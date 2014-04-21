package com.github.monkey.analyzer.report;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.monkey.analyzer.analyze.Constants;
import com.github.monkey.analyzer.model.Abnormality;
import com.github.monkey.analyzer.model.AbnormalityType;
import com.github.monkey.analyzer.model.AnrOrCrashAbnormality;
import com.github.monkey.analyzer.statistics.AbnormalitiesAnalyzerWrapper;
import com.github.monkey.analyzer.statistics.Tuple;

final public class Abnormalities2JSONReport {

	/**
	 * 
	 * Convert all the abnormalities to
	 * 
	 * TODO This kind of implement is not very good. JSON report should not
	 * contain HTML
	 * 
	 * @param knownAbnormalities
	 *            abnormalities which type is known
	 * @param testingInfo
	 *            Extra testing information which store device information and
	 *            monkey executing result
	 * @param duration
	 *            monkey test duration (hour)
	 * @param abnormalitiesCount
	 *            all the abnormalities count
	 * @return a JSON-format String value which represent the Monkey Report
	 */
	public static String toJSONFormatStringReport(
			ArrayList<Abnormality> knownAbnormalities,
			ArrayList<Abnormality> unknownAbnormalities,
			HashMap<String, String> testingInfo, 
			final double duration,
			final int abnormalitiesCount) {
		// 0. handle input data
		if ((knownAbnormalities == null || knownAbnormalities.size() == 0)
				&& (unknownAbnormalities == null || unknownAbnormalities.size() == 0))
			return "{}";

		JSONReportProvider report = new JSONReportProvider();
		JSONObject reportJSON = new JSONObject();

		JSONArray abnormalitiesJsonArray = new JSONArray();

		// 1. fill each crash abnormalities
		handleCrashes(knownAbnormalities, abnormalitiesJsonArray);

		// 2. fill ANR Info
		handleANRs(knownAbnormalities, abnormalitiesJsonArray);
		// 3. fill native crash 
		handleNativeCrashes(knownAbnormalities, abnormalitiesJsonArray);

		// 4. fill summary
		fillSummary(knownAbnormalities, unknownAbnormalities,
				duration, testingInfo, reportJSON);

		// 5. file abnormalities to JSON report
		try {
			reportJSON.put(Constants.JSONReport.KEY_ABNORMALITIS,
					abnormalitiesJsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		report.setJSONObject(reportJSON);
		return report.toString();
	}

	/**
	 * 
	 * @param knownAbnormalities
	 *            abnormalities with known AbnormalityType
	 * @param unknownAbnormalities
	 *            abnormalities with unknown AbnormalityType
	 * @param testingInfo
	 * @param reportJSON
	 */
	private static void fillSummary(
			final ArrayList<Abnormality> knownAbnormalities,
			final ArrayList<Abnormality> unknownAbnormalities,
			final double monkeyTestDuration,
			final HashMap<String, String> testingInfo,
			final JSONObject reportJSON) {
		int anrCount = AbnormalitiesAnalyzerWrapper.getCount(
				new WeakReference<ArrayList<Abnormality>>(
						knownAbnormalities), AbnormalityType.ANR);
		int crashCount = AbnormalitiesAnalyzerWrapper.getCount(
				new WeakReference<ArrayList<Abnormality>>(
						knownAbnormalities), AbnormalityType.CRASH);
		int nativeCrashCount = AbnormalitiesAnalyzerWrapper.getCount(
				new WeakReference<ArrayList<Abnormality>>(
						knownAbnormalities), AbnormalityType.NATIVE);
		
		try {
			reportJSON.put(Constants.JSONReport.KEY_ANR_NUMBER, anrCount);
			reportJSON.put(Constants.JSONReport.KEY_CRASH_NUMBER, crashCount);
			reportJSON.put(Constants.JSONReport.KEY_NATIVE_CRASH_NUMBER, nativeCrashCount);
			
			// TODO
			reportJSON.put(Constants.JSONReport.KEY_START_TIME, "-");

			String duration = testingInfo
					.get(Constants.JSONReport.KEY_DURATION);
			if (duration == null || duration.isEmpty())
				duration = "-";
			reportJSON.put(Constants.JSONReport.KEY_DURATION, duration);
			
			
			String average = testingInfo
					.get(Constants.JSONReport.KEY_AVERAGE);
			if (average == null || duration.isEmpty())
				average = "-";
			reportJSON.put(Constants.JSONReport.KEY_AVERAGE, average + "小时");

			reportJSON.put(Constants.JSONReport.KEY_END_TIME, "-");
			reportJSON.put(Constants.JSONReport.KEY_EXECUTOR, "-");
			reportJSON.put(Constants.JSONReport.KEY_MONKEY_LOG_URL, "-");

			String fingerprint = null;
			if (hasMoreThanOne(knownAbnormalities)) {
				fingerprint = knownAbnormalities.get(0).getExtras()
						.get(Constants.AbnormalityProperties.KEY_PLATFORM);
			} else if (hasMoreThanOne(unknownAbnormalities)) {
				fingerprint = unknownAbnormalities.get(0).getExtras()
						.get(Constants.AbnormalityProperties.KEY_PLATFORM);
			}

			if (fingerprint == null)
				fingerprint = "";
			reportJSON.put(Constants.JSONReport.KEY_MOBILE_PLATFORM,
					fingerprint);
			String pkgName = null;
			if (hasMoreThanOne(knownAbnormalities)) {
				pkgName = knownAbnormalities.get(0).getExtras()
						.get(Constants.AbnormalityProperties.KEY_PACKAGE_NAME);
			} else if (hasMoreThanOne(unknownAbnormalities)) {
				pkgName = unknownAbnormalities.get(0).getExtras()
						.get(Constants.AbnormalityProperties.KEY_PACKAGE_NAME);
			}
			if (pkgName == null)
				pkgName = "";

			reportJSON.put(Constants.JSONReport.KEY_PRODUCT_NAME, pkgName);
			String pkgVersion = null;
			if (hasMoreThanOne(knownAbnormalities)) {
				pkgVersion = knownAbnormalities
						.get(0)
						.getExtras()
						.get(Constants.AbnormalityProperties.KEY_PACKAGE_VERSION);
			} else if (hasMoreThanOne(unknownAbnormalities)) {
				pkgVersion = unknownAbnormalities
						.get(0)
						.getExtras()
						.get(Constants.AbnormalityProperties.KEY_PACKAGE_VERSION);
			}

			if (pkgVersion == null)
				pkgVersion = "";
			reportJSON.put(Constants.JSONReport.KEY_VERSION, pkgVersion);

			reportJSON.put(Constants.JSONReport.KEY_MOBILE_PHONE_TYPE, "-");

		} catch (JSONException e2) {
			e2.printStackTrace();
		}

		try {
			reportJSON.put(Constants.JSONReport.KEY_CRASH_NUMBER, crashCount);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Whether the ArrayList of Abnormality has more than one entry
	 * 
	 * @param abnormalities
	 *            all the abnormalities
	 * @return whether the ArrayList of Abnormality has more than one
	 *         entry
	 */
	private static boolean hasMoreThanOne(
			final ArrayList<Abnormality> abnormalities) {
		return abnormalities != null && abnormalities.size() > 0;
	}
	
	/**
	 * handle Native Crashes
	 * 
	 * @param abnormalities
	 * @param abnormalitiesJsonArray
	 */
	private static void handleNativeCrashes(
			ArrayList<Abnormality> abnormalities,
			JSONArray abnormalitiesJsonArray) {
		for (Abnormality abnormality : abnormalities) {
			if (abnormality.getType() != AbnormalityType.NATIVE)
				continue;

			JSONObject nativeCrash = new JSONObject();
			try {
				nativeCrash.put(Constants.JSONReport.KEY_ABNORMALITIS_TYPE, "Native&nbsp;Crash");

				String shortMessage = null;
				if (shortMessage == null || shortMessage == "")
					shortMessage = "-";
				nativeCrash.put(Constants.JSONReport.KEY_ABNORMALITIS_SHORT_MSG,
						shortMessage.replace("\n", "<br/>"));

				String message = abnormality.getMessage();
				message = trimNewLine(message);
				String path = abnormality.getExtras().get(AnrOrCrashAbnormality.EXTRAS_KEY_PATH);
				nativeCrash.put(Constants.JSONReport.KEY_ABNORMALITIS_MSG, message);
				final String duration = abnormality.getExtras().get(
						Constants.AbnormalityProperties.KEY_DURATION_STRING);
				nativeCrash.put(Constants.JSONReport.KEY_ABNORMALITIS_COUNT, 1);
				nativeCrash.put(Constants.JSONReport.KEY_ABNORMALITIS_DURATIONS,
						trimNewLine(duration));
				nativeCrash.put(Constants.JSONReport.KEY_MONKEY_LOG_URL, "-");
				nativeCrash.put(Constants.JSONReport.KEY_ABNORMALITIS_TRACE_URL, "-");
				nativeCrash.put(Constants.JSONReport.KEY_ABNORMALITIS_BUGREPORT_URL,
						"-");
				nativeCrash.put(Constants.JSONReport.KEY_ABNORMALITIS_LOGCAT_URL, "-");
				path = toHyperLink(path);
				nativeCrash.put(Constants.JSONReport.KEY_ABNORMALITIES_ZIP_URL, path);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			abnormalitiesJsonArray.put(nativeCrash);

		}
	}

	/**
	 * handle ANRs
	 * 
	 * @param abnormalities
	 * @param abnormalitiesJsonArray
	 */
	private static void handleANRs(
			ArrayList<Abnormality> abnormalities,
			JSONArray abnormalitiesJsonArray) {
		for (Abnormality abnormality : abnormalities) {
			if (abnormality.getType() != AbnormalityType.ANR)
				continue;

			JSONObject anr = new JSONObject();
			try {
				anr.put(Constants.JSONReport.KEY_ABNORMALITIS_TYPE, "ANR");

				String shortMessage = abnormality.getMessage();
				if (shortMessage == null || shortMessage == "")
					shortMessage = "-";
				anr.put(Constants.JSONReport.KEY_ABNORMALITIS_SHORT_MSG,
						shortMessage.replace("\n", "<br/>"));

				String messageAfter = abnormality.getExtras().get(
						AnrOrCrashAbnormality.EXTRAS_KEY_AFTER_END_PATTERN);
				String message = shortMessage + messageAfter;
				message = trimNewLine(message);
				String path = abnormality.getExtras().get(AnrOrCrashAbnormality.EXTRAS_KEY_PATH);
				anr.put(Constants.JSONReport.KEY_ABNORMALITIS_MSG, message);
				final String duration = abnormality.getExtras().get(
						Constants.AbnormalityProperties.KEY_DURATION_STRING);
				anr.put(Constants.JSONReport.KEY_ABNORMALITIS_COUNT, 1);
				anr.put(Constants.JSONReport.KEY_ABNORMALITIS_DURATIONS,
						trimNewLine(duration));
				anr.put(Constants.JSONReport.KEY_MONKEY_LOG_URL, "-");
				anr.put(Constants.JSONReport.KEY_ABNORMALITIS_TRACE_URL, "-");
				anr.put(Constants.JSONReport.KEY_ABNORMALITIS_BUGREPORT_URL,
						"-");
				anr.put(Constants.JSONReport.KEY_ABNORMALITIS_LOGCAT_URL, "-");
				path = toHyperLink(path);
				anr.put(Constants.JSONReport.KEY_ABNORMALITIES_ZIP_URL, path);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			abnormalitiesJsonArray.put(anr);
		}
	}

	/**
	 * handle CRASH
	 * 
	 * @param abnormalities
	 *            all the abnormalities
	 * @param abnormalitiesJsonArray
	 */
	private static void handleCrashes(
			ArrayList<Abnormality> abnormalities,
			JSONArray abnormalitiesJsonArray) {
		final HashMap<String, Tuple> crashesAfterDuplicatedRemoving = AbnormalitiesAnalyzerWrapper
				.removeDulplicatedCrashes(abnormalities);
		for (String key : crashesAfterDuplicatedRemoving.keySet()) {
			JSONObject crash = new JSONObject();
			try {
				crash.put(Constants.JSONReport.KEY_ABNORMALITIS_TYPE, "crash");
				String shortMessage = crashesAfterDuplicatedRemoving.get(key).crashAbnormalities
						.getShortMessage();
				crash.put(Constants.JSONReport.KEY_ABNORMALITIS_SHORT_MSG,
						shortMessage.replace("\n", "<br/>"));
				String messageItself = crashesAfterDuplicatedRemoving.get(key).abnormalities
						.get(0).get().getMessage();
				String message = trimNewLine(messageItself);
				String durations = "";
				String paths = "";
				for (WeakReference<Abnormality> abnormalityRef : crashesAfterDuplicatedRemoving
						.get(key).abnormalities) {
					String duration = "-";
					String path = "-";
					if (abnormalityRef
							.get()
							.getExtras()
							.containsKey(
									Constants.AbnormalityProperties.KEY_DURATION_STRING)) {
						duration = abnormalityRef
								.get()
								.getExtras()
								.get(Constants.AbnormalityProperties.KEY_DURATION_STRING);
					}
					// System.out.println(duration + "##\n");
					durations += duration + "\n";
					
					if (abnormalityRef
							.get()
							.getExtras()
							.containsKey(
									Constants.AbnormalityProperties.KEY_PATHS)) {
						path = abnormalityRef
								.get()
								.getExtras()
								.get(Constants.AbnormalityProperties.KEY_PATHS);
					}
					// System.out.println(duration + "##\n");
					paths += path + "\n";
				}
				durations = trimNewLine(durations);
				paths = toHyperLink(paths);
				// durations = "共出现" +
				// crashesAfterDuplicatedRemoving.get(key).abnormalities.size()
				// + "次.<br/><br/>每次持续时间为:<br/>" + durations;
				crash.put(Constants.JSONReport.KEY_ABNORMALITIS_COUNT,
						crashesAfterDuplicatedRemoving.get(key).abnormalities
								.size());
				crash.put(Constants.JSONReport.KEY_ABNORMALITIS_DURATIONS,
						durations);
				crash.put(Constants.JSONReport.KEY_ABNORMALITIS_MSG, message
						+ "");
				crash.put(Constants.JSONReport.KEY_MONKEY_LOG_URL, "-");
				crash.put(Constants.JSONReport.KEY_ABNORMALITIS_TRACE_URL, "-");
				crash.put(Constants.JSONReport.KEY_ABNORMALITIS_BUGREPORT_URL,
						"-");
				crash.put(Constants.JSONReport.KEY_ABNORMALITIS_LOGCAT_URL, "-");
				
				crash.put(Constants.JSONReport.KEY_ABNORMALITIES_ZIP_URL, paths);
				System.out.println(paths);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			abnormalitiesJsonArray.put(crash);
		}
	}

	/**
	 * trim the source String to remove all {@code \n}
	 * 
	 * @param source
	 *            a String to be handled
	 * @return a String after handling
	 */
	private static String trimNewLine(final String source) {
		if (source == null || source == "")
			return "-";
		String message = source.replace("\n", "<br/>");
		String[] messages = message.split("<br/>");
		if (messages == null || messages.length == 0) {
			message = "-";
		}
		return message;
	}
	
	private static String toHyperLink(final String source) {
		if (source == null || source == "")
			return "-";
		String[] messages = source.split("\\n");
		if (messages == null || messages.length == 0) {
			return "-";
		}
		StringBuilder sb = new StringBuilder();
		for (String message : messages) {
			sb.append("<a href=\"" + message + "\">下载</a><br/>");
		}
		return sb.toString();
	}

}