package com.github.monkey.analyzer.analyze;

import java.io.File;
import java.util.LinkedList;
import java.util.regex.Pattern;

import com.github.monkey.analyzer.model.Abnormality;
import com.github.monkey.analyzer.model.AbnormalityType;

class NativeCrashAnalyzer extends Analyzer {
	public NativeCrashAnalyzer(AnalyzerConfiguration config) {
		super(config);
	}

	@Override
	protected void onMatch(Abnormality abnormality,
			StringBuilder monkeyMsg, LinkedList<String> logcatMsgBefore,
			LinkedList<String> logcatMsgAfter) {
		if (monkeyMsg != null && !monkeyMsg.equals("")) {
			abnormality.setValid(true);
		} else {
			abnormality.setValid(false);
		}

		String logcatFilePath = filePath + File.separator
				+ mConfig.getLogcatFileName();
		File file = new File(logcatFilePath);

		if (file == null || false == file.exists())
			return;

		final Pattern startPattern = Pattern.compile("I DEBUG   : \\*\\*\\*");
		final Pattern endPattern = Pattern.compile("I DEBUG   : \\*\\*\\*");
		final int beforeSize = 50;
		final int afterSize = -1;
		StringBuilder logcatMsg = new StringBuilder();
		boolean matching = parse(file, logcatMsgBefore, logcatMsgAfter,
				logcatMsg, startPattern, endPattern, beforeSize, afterSize);
		if (matching) {
			StringBuilder msgAfter = new StringBuilder();
			for (String msg : logcatMsgAfter) {
				msgAfter.append(msg);
				msgAfter.append("\n");
			}
			StringBuilder msgBefore = new StringBuilder();
			for (String msg : logcatMsgBefore) {
				msgBefore.append(msg);
				msgBefore.append("\n");
			}
			StringBuilder message = new StringBuilder();
			message.append(monkeyMsg.toString());
			message.append('\n');
			message.append(msgBefore.toString());
			message.append(logcatMsg.toString());
			message.append(msgAfter.toString());

			abnormality.setType(AbnormalityType.NATIVE);
			abnormality.setMessage(message);
		}
	}
}