package com.github.monkey.analyzer.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.monkey.analyzer.analyze.Constants;

/**
 * Read the file that stores Monkey result and Device information and save some
 * important key<br/>
 * 
 * @author Alex Chen (apack1001@gmail.com)
 * 
 */
public class AbnormalityProperties {
	private static final String REGEX_PACKAGE = "apk.package=";
	private static final String REGEX_PACKAGE_VERSION = "apk.version=";
	private static final String REGEX_SERIAL = "ro.serialno=";
	private static final String REGEX_START = "test.start=";
	private static final String REGEX_END = "test.finish=";
	private static final String REGEX_FINGERPRINT = "ro.build.fingerprint=";

	private Map<String, String> properties = new HashMap<String, String>();

	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * open a file from filePath then get all the necessary information
	 * 
	 * @param filePath
	 *            the path of properties file
	 */
	public AbnormalityProperties(final String filePath) {
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			InputStreamReader inputStreamReader;
			try {
				inputStreamReader = new InputStreamReader(new FileInputStream(
						file));
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);

				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					if (line.contains(REGEX_PACKAGE)) {
						String[] splits = line.split(REGEX_PACKAGE);
						if (splits != null && splits.length == 2) {
							properties
									.put(Constants.AbnormalityProperties.KEY_PACKAGE_NAME,
											splits[1]);
						}
					}

					if (line.contains(REGEX_START)) {
						String[] splits = line.split(REGEX_START);
						if (splits != null && splits.length == 2) {
							properties
									.put(Constants.AbnormalityProperties.KEY_START_TIME,
											splits[1]);
						}
					}

					if (line.contains(REGEX_END)) {
						String[] splits = line.split(REGEX_END);
						if (splits != null && splits.length == 2) {
							properties
									.put(Constants.AbnormalityProperties.KEY_END_TIME,
											splits[1]);
						}
					}

					if (line.contains(REGEX_SERIAL)) {
						String[] splits = line.split(REGEX_SERIAL);
						if (splits != null && splits.length == 2) {
							properties.put(
									Constants.AbnormalityProperties.KEY_SERIAL,
									splits[1]);
						}
					}

					if (line.contains(REGEX_FINGERPRINT)) {
						String[] splits = line.split(REGEX_FINGERPRINT);
						if (splits != null && splits.length == 2) {
							properties
									.put(Constants.AbnormalityProperties.KEY_PLATFORM,
											splits[1]);
						}
					}

					if (line.contains(REGEX_PACKAGE_VERSION)) {
						String[] splits = line.split(REGEX_PACKAGE_VERSION);
						if (splits != null && splits.length == 2) {
							properties
									.put(Constants.AbnormalityProperties.KEY_PACKAGE_VERSION,
											splits[1]);
						}
					}
				}

				bufferedReader.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (properties == null)
				return;

			String start = properties
					.get(Constants.AbnormalityProperties.KEY_START_TIME);
			String end = properties
					.get(Constants.AbnormalityProperties.KEY_END_TIME);

			if (start == null || end == null)
				return;

			String duration = getDurationString(start, end);
			properties.put(Constants.AbnormalityProperties.KEY_DURATION_LONG,
					Long.toString(getDurationLong(start, end)));
			properties.put(Constants.AbnormalityProperties.KEY_DURATION_STRING,
					duration);
		}
	}

	private String getDurationString(String start, String end) {
		long lDuration = getDurationLong(start, end);
		long hour = lDuration / 1000 / 3600;
		long minite = (lDuration - hour * 1000 * 3600) / 1000 / 60;
		long seconds = lDuration / 1000 % 60;
		String duration = "" + hour + "小时" + minite + "分钟" + seconds + "秒";
		return duration;
	}

	private long getDurationLong(final String start, final String end) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ParsePosition pos = new ParsePosition(0);
		ParsePosition pos1 = new ParsePosition(0);
		Date dt1 = formatter.parse(start, pos);
		Date dt2 = formatter.parse(end, pos1);
		long duration = dt2.getTime() - dt1.getTime();
		return duration;
	}

	public void setProperties(final Map<String, String> properties) {
		this.properties.clear();
		this.properties = properties;
	}

	/**
	 * Get one property with given key
	 * 
	 * 
	 * @param key
	 * @return
	 */
	public String get(final String key) {
		if (properties == null)
			return null;

		return properties.get(key);
	}
}
