package com.github.monkey.analyzer.statistics;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.monkey.analyzer.analyze.Constants;
import com.github.monkey.analyzer.model.Abnormality;
import com.github.monkey.analyzer.model.AbnormalityType;
import com.github.monkey.analyzer.model.AnrOrCrashAbnormality;

/**
 * A wrapper class which is to strengthen the function of the Analyze module
 * 
 * @author Alex Chen (apack1001@gmail.com)
 * 
 */
final public class AbnormalitiesAnalyzerWrapper {
	private static Map<AbnormalityType, Integer> statistics = Collections
			.synchronizedMap(new HashMap<AbnormalityType, Integer>());

	/**
	 * Get the count of some given kind of abnormalities
	 * 
	 * @param abnormalitiesRef
	 *            instance of {@link WeakReference
	 *            <ArrayList<Abnormality>>}
	 * @param type
	 *            Type of Abnormality
	 * @return the count
	 */
	public final static int getCount(
			final WeakReference<ArrayList<Abnormality>> abnormalitiesRef,
			final AbnormalityType type) {
		statistics.clear();
		if (abnormalitiesRef == null || abnormalitiesRef.get() == null)
			throw new RuntimeException();

		for (Abnormality abnormality : abnormalitiesRef.get()) {
			if (abnormality.getType() == type) {
				if (statistics.containsKey(type))
					statistics.put(type, statistics.get(type) + 1);
				else
					statistics.put(type, 1);
			}
		}

		if (statistics.get(type) == null)
			return 0;

		return Integer.valueOf(statistics.get(type));
	}

	/**
	 * Print all the abnormalities
	 * 
	 * @param abnormalities
	 *            all the abnormalities
	 */
	public static void printAbnormalities(
			ArrayList<Abnormality> abnormalities) {

		HashMap<String, Tuple> hm = removeDulplicatedCrashes(abnormalities);

		for (Abnormality abnormality : abnormalities) {
			if (abnormality.getType() == AbnormalityType.ANR) {
				System.out.println("出现ANR\n" + abnormality.getMessage() + "1次");
				String temp = abnormality.getExtras().get(
						AnrOrCrashAbnormality.EXTRAS_KEY_AFTER_END_PATTERN);
				System.out.println(temp);
				System.out.println("--\n");
			}
		}

		for (String key : hm.keySet()) {
			System.out.println("出现" + hm.get(key).abnormalities.size() + "次"
					+ hm.get(key).crashAbnormalities.getShortMessage() + "异常");
			System.out.println(""
					+ hm.get(key).abnormalities.get(0).get().getMessage());
			// System.out.println(""
			// + hm.get(key).abnormality.getExtras().toString());
			System.out.println("--\n");
		}
	}

	/**
	 * Remove all the duplicated crash abnormalities, duplicate carsh
	 * abnormalities will save in an instance of tuple
	 * 
	 * @param abnormalities
	 *            all the abnormalities to be processed
	 * @return an instance of {@code HashMap<String, Tuple>} which store all the
	 *         processed abnormalities
	 */
	public static HashMap<String, Tuple> removeDulplicatedCrashes(
			ArrayList<Abnormality> abnormalities) {
		HashMap<String, Tuple> theProcessed = new HashMap<String, Tuple>();

		for (Abnormality abnormality : abnormalities) {
			if (abnormality.getType() == AbnormalityType.CRASH) {
				CrashAbnormalityAdapter abnormalityAdapter = new CrashAbnormalityAdapter(
						abnormality);

				if (theProcessed.containsKey(abnormalityAdapter.getCodeLines())) {
					Tuple tuple = theProcessed.get(abnormalityAdapter
							.getCodeLines());
					tuple.abnormalities
							.add(new WeakReference<Abnormality>(
									abnormality));
					theProcessed.put(abnormalityAdapter.getCodeLines(), tuple);
				} else {
					Tuple tuple = new Tuple();
					tuple.crashAbnormalities = abnormalityAdapter;
					tuple.abnormalities
							.add(new WeakReference<Abnormality>(
									abnormality));
					theProcessed.put(abnormalityAdapter.getCodeLines(), tuple);
				}
			}
		}
		return theProcessed;
	}

	/**
	 * Calculate the average duration of this monkey test
	 * 
	 * @param knownAbnormalities
	 *            all the known abnormalities
	 * @param unknownAbnormalities
	 *            all the unknown abnormalities
	 * @param monkeyTestDuration
	 *            expected one monkey test duration
	 * @return the average duration of the monkey test
	 */
	public static double getAverage(
			ArrayList<Abnormality> knownAbnormalities,
			ArrayList<Abnormality> unknownAbnormalities,
			int monkeyTestDuration) {
		if (monkeyTestDuration <= 0)
			throw new IllegalArgumentException("Some arguments maybe wrong!");

		if (knownAbnormalities == null || knownAbnormalities.size() == 0) {
			return monkeyTestDuration;
		} else {
			long durationSum = 0;
			for (Abnormality abnormality : knownAbnormalities) {
				if (abnormality.getExtras().containsKey(
						Constants.AbnormalityProperties.KEY_DURATION_LONG) == false)
					return monkeyTestDuration / knownAbnormalities.size();
				try {
					durationSum += Long.parseLong(abnormality.getExtras().get(
							Constants.AbnormalityProperties.KEY_DURATION_LONG));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 保留小数点后2位，4舍5入
			long temp = durationSum / 3600 / knownAbnormalities.size();
			temp = (temp + (temp % 10 >= 5 ? 10 : 0) - temp % 10) / 10 ;
			return (double) temp / 100;
		}
	}

}
