package com.github.monkey.analyzer.analyze;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

import com.github.monkey.analyzer.model.Abnormality;
import com.github.monkey.analyzer.model.AbnormalityFactory;
import com.github.monkey.analyzer.model.AbnormalityProperties;
import com.github.monkey.analyzer.model.AnrOrCrashAbnormality;

/**
 * @brief This is an Analyzer template for abnormality analyzing which type is
 *        Crash、ANR、native Crash
 * @author Alex Chen (apack1001@gmail.com)
 * @since 2013/03/20
 * 
 */
public abstract class Analyzer implements IAnalyzer {

	protected AnalyzerConfiguration mConfig;
	protected String filePath;

	public Analyzer(AnalyzerConfiguration anrConfig) {
		mConfig = anrConfig;
	}

	public Analyzer() {
		super();
	}

	@Override
	public void setPath(String path) {
		filePath = path;
	}

	@Override
	public Abnormality toAbnormality() {
		if (mConfig == null)
			throw new IllegalStateException("No analyzing configuration set");
		LinkedList<String> messageBefore = new LinkedList<String>();
		LinkedList<String> messageAfter = new LinkedList<String>();
		Abnormality abnormality = AbnormalityFactory.newInstance(mConfig.getType());

		StringBuilder message = new StringBuilder();

		abnormality.setType(mConfig.getType());
		if (filePath == null || abnormality == null) {
			throw new RuntimeException(
					"AnalyzerConfiguration or filePath not set");
		}

		final Pattern startPattern = Pattern.compile(mConfig.getStartPattern());
		final Pattern endPattern = Pattern.compile(mConfig.getEndPattern());
		final String monkeyFilePath = filePath + mConfig.getMonkeyLogFileName();
		File file = new File(monkeyFilePath);
		final int beforeSize = mConfig.getBeforeSize();
		final int afterSize = mConfig.getAfterSize();
		boolean matching = false;
		if (file.exists() && file.isFile()) {
			// StringBuilder message = new StringBuilder();
			// message = new StringBuilder("");
			matching = parse(file, messageBefore, messageAfter, message,
					startPattern, endPattern, beforeSize, afterSize);

			AbnormalityProperties properties = new AbnormalityProperties(
					filePath + mConfig.getPropertiesFileName());
			// System.out.println(properties.getProperties().toString());
			final Map<String, String> map = properties.getProperties();
			abnormality.copyFrom(map);

			// 匹配完成，输出匹配结果
			if (matching) {

				abnormality.setMessage(message);
				if (new File(filePath).isDirectory())
					filePath = filePath.substring(0, filePath.length() - 1);
				abnormality.put(AnrOrCrashAbnormality.EXTRAS_KEY_PATH, filePath
						+ ".zip");

				onMatch(abnormality, message, messageBefore, messageAfter);
				// System.out.println(abnormality.getExtras().toString());
				if (isPackageDefinedConfiguration() == false)
					abnormality.setValid(true);
				else {
					System.out.println("=================" + mConfig.getPackageName());
					System.out.println(message.toString());
					
					if (message.toString().contains(mConfig.getPackageName())) {
						abnormality.setValid(true);
//						System.out.println(abnormality.getMessage());
					}
				}
			}

		}
		return abnormality;
	}

	/**
	 * Parse a file via given <b>IN</b> parameter(file, startPattern,
	 * endPattern, beforeSize, afterSize) <br/>
	 * After parsing, results will be put into
	 * <b>out</b>parameter(messageBefore, messageAfter, message) <br/>
	 * 
	 * @param file
	 *            log file
	 * @param messageBefore
	 *            log line String before startPattern
	 * @param messageAfter
	 *            log line String after endPattern
	 * @param message
	 *            the message of this log
	 * @param startPattern
	 *            the pattern which the log start with
	 * @param endPattern
	 *            the pattern which the log end with
	 * @param beforeSize
	 *            log line number before startPattern
	 * @param afterSize
	 *            log line number after endPattern
	 * @return {@code true} if matching the <b>Patterns</b>
	 */
	final protected boolean parse(File file, LinkedList<String> messageBefore,
			LinkedList<String> messageAfter, StringBuilder message,
			final Pattern startPattern, final Pattern endPattern,
			final int beforeSize, final int afterSize) {
		boolean matching = false;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(
					new FileInputStream(file));
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String line = null;
			boolean matchStart = false;
			boolean matchEnd = false;

			while ((line = bufferedReader.readLine()) != null) {
				// 先匹配开始选项
				if (startPattern.matcher(line).find()) {
					matchStart = true;
				}
				// 拿到beforePattern之前n行的数据
				if (!matchStart && beforeSize > 0) {
					if (messageBefore.size() == beforeSize)
						messageBefore.pollFirst();
					messageBefore.addLast(line);
				}

				// 填充真正的Message
				if (matchStart && !matchEnd) {
					message.append(line);
					message.append("\n");
				}

				// 匹配结束选项
				if (matchStart && endPattern.matcher(line).find()) {
					matchEnd = true;
					continue;
				}
				if (matchEnd && afterSize < 0) {
					if (messageAfter.size() < 50)
						messageAfter.addLast(line);
				} else if (matchEnd && messageAfter.size() < afterSize) {
					if (messageAfter.size() < 50)
						messageAfter.addLast(line);
				}
			}
			matching = matchStart && matchEnd && message != null;
			bufferedReader.close();
			inputStreamReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matching;
	}

	/**
	 * It is a Template pattern, toAbnormality will called to convert from
	 * matching result to an abnormality
	 * 
	 * @param abnormality
	 *            abnormality to be filled
	 * @param message
	 *            the message passed to the abnormality
	 * @param messageBefore
	 *            lines before message
	 * @param messageAfter
	 *            lines after message
	 */
	protected abstract void onMatch(Abnormality abnormality,
			StringBuilder message, LinkedList<String> messageBefore,
			LinkedList<String> messageAfter);

	/**
	 * Get whether the target package under analyzing is defined in the
	 * configuration
	 * 
	 * @return whether the package is defined or not
	 */
	private boolean isPackageDefinedConfiguration() {
		if (mConfig == null)
			throw new RuntimeException("Analyzing Configuration is null");

		final String pkgName = mConfig.getPackageName();
		if (pkgName != null && pkgName != "") {
			return true;
		}
		return false;
	}
}