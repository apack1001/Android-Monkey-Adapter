package com.github.monkey.analyzer.analyze;

import com.github.monkey.analyzer.model.AbnormalityType;

/**
 * Configuration of an abnormality analyzer(implemented using a <b>Builder
 * pattern<b/>)
 * 
 * @author Alex Chen (apack1001@gmail.com)
 * 
 */
class AnalyzerConfiguration {
	private String startPattern = "";
	private String endPattern = "";
	private AbnormalityType type = null;
	private int beforeSize = 0;
	private int afterSize = 0;
	private String packageName;
	private String monkeyLogFileName;
	private String bugreportFileName;
	private String tracesFileName;
	private String logcatFileName;
	private String propertiesFileName;

	private AnalyzerConfiguration(String startPattern, String endPattern,
			AbnormalityType type, int beforeSize, int afterSize,
			String packageName, String monkeyLogFielName,
			String bugreportFileName, String tracesFileName,
			String logcatFileName, String propertiesName) {
		this.startPattern = startPattern;
		this.endPattern = endPattern;
		this.type = type;
		this.beforeSize = beforeSize;
		this.afterSize = afterSize;
		this.packageName = packageName;
		this.monkeyLogFileName = monkeyLogFielName;
		this.bugreportFileName = bugreportFileName;
		this.tracesFileName = tracesFileName;
		this.logcatFileName = logcatFileName;
		this.propertiesFileName = propertiesName;
	}

	public String getStartPattern() {
		return startPattern;
	}

	public String getEndPattern() {
		return endPattern;
	}

	public AbnormalityType getType() {
		return type;
	}

	public int getBeforeSize() {
		return beforeSize;
	}

	public int getAfterSize() {
		return afterSize;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getBugreportFileName() {
		return bugreportFileName;
	}

	public String getTracesFileName() {
		return tracesFileName;
	}

	public String getLogcatFileName() {
		return logcatFileName;
	}

	public String getMonkeyLogFileName() {
		return monkeyLogFileName;
	}

	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	public static class Builder {
		private String startPattern = "";
		private String endPattern = "";
		private AbnormalityType type = null;
		private int beforeSize = 0;
		private int afterSize = 0;
		private String monkeyLogFileName;
		private String bugreportFileName;
		private String tracesFileName;
		private String logcatFileName;
		private String packageName;
		private String propertiesName;

		public Builder setStartPattern(String pattern) {
			startPattern = pattern;
			return this;
		}

		public Builder setEndPattern(String pattern) {
			endPattern = pattern;
			return this;
		}

		public Builder setType(final AbnormalityType type) {
			this.type = type;
			return this;
		}

		public Builder setContentLengthBeforeStartPattern(int size) {
			beforeSize = size;
			return this;
		}

		public Builder setContentLengthAfterEndPattern(int size) {
			afterSize = size;
			return this;
		}

		public Builder setPackageName(final String packageName) {
			this.packageName = packageName;
			return this;
		}

		public Builder setBugReportFileName(final String bugreportFileName) {
			this.bugreportFileName = bugreportFileName;
			return this;
		}

		public Builder setTracesFileName(final String tracesFileName) {
			this.tracesFileName = tracesFileName;
			return this;
		}

		public Builder setMonkeyLogFileName(final String monkeyLogFileName) {
			this.monkeyLogFileName = monkeyLogFileName;
			return this;
		}

		public Builder setLogcatFileName(final String logcatFileName) {
			this.logcatFileName = logcatFileName;
			return this;
		}

		public Builder setPropertiesFileName(final String propertiesName) {
			this.propertiesName = propertiesName;
			return this;
		}

		public AnalyzerConfiguration build() {
			return new AnalyzerConfiguration(startPattern, endPattern, type,
					beforeSize, afterSize, packageName, monkeyLogFileName,
					bugreportFileName, tracesFileName, logcatFileName,
					propertiesName);
		}

	}

}
