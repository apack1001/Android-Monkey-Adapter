package com.github.monkey.analyzer.analyze;

final public class Constants {
	/**
	 * This class defines the keys of major displaying information
	 * 
	 * @author herongtian
	 * @author Alex Chen (apack1001@gmail.com)
	 * 
	 */
	public class JSONReport {
		public static final String KEY_PRODUCT_NAME = "productName";
		public static final String KEY_VERSION = "version";
		public static final String KEY_START_TIME = "startTime";
		public static final String KEY_END_TIME = "endTime";
		public static final String KEY_DURATION = "duration";
		public static final String KEY_AVERAGE = "average";
		public static final String KEY_EXECUTOR = "executor";
		public static final String KEY_MOBILE_PLATFORM = "mobilePlatform";
		public static final String KEY_MOBILE_PHONE_TYPE = "mobile";
		public static final String KEY_ANR_NUMBER = "anrNumber";
		public static final String KEY_CRASH_NUMBER = "crashNumber";
		public static final String KEY_NATIVE_CRASH_NUMBER = "nativeCrashNumber";
		
		public static final String KEY_ABNORMALITIS = "abnormalitis";
		public static final String KEY_ABNORMALITIS_TYPE = "type";
		public static final String KEY_ABNORMALITIS_MSG = "msg";
		public static final String KEY_ABNORMALITIS_SHORT_MSG = "shortMsg";
		public static final String KEY_ABNORMALITIS_TRACE_URL = "traceUrl";
		public static final String KEY_MONKEY_LOG_URL = "monkeyLogUrl";
		public static final String KEY_ABNORMALITIS_LOGCAT_URL = "logcatUrl";
		public static final String KEY_ABNORMALITIS_BUGREPORT_URL = "bugreportUrl";
		public static final String KEY_ABNORMALITIES_ZIP_URL = "zipUrl";
		public static final String KEY_ABNORMALITIS_COUNT = "count";
		public static final String KEY_ABNORMALITIS_DURATIONS = "durations";
		
	}

	/**
	 * This class defines the keys of an abnormality
	 * 
	 * @author Alex Chen (apack1001@gmail.com)
	 * 
	 */
	public class AbnormalityProperties {
		public static final String KEY_SERIAL = "serialNumber";
		public static final String KEY_START_TIME = "startTime";
		public static final String KEY_END_TIME = "endTime";
		public static final String KEY_DURATION_STRING = "duration";
		public static final String KEY_DURATION_LONG = "longTypeDuration";
		public static final String KEY_PLATFORM = "platform";
		public static final String KEY_PACKAGE_NAME = "pkgName";
		public static final String KEY_PACKAGE_VERSION = "pkgVersion";
		public static final String KEY_PATHS = "path";
	}
}
