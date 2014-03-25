package com.github.monkey.analyzer.statistics;

import com.github.monkey.analyzer.model.Abnormality;

/**
 * This is an adapter Object of CRASH abnormality which split the abnormality
 * message.<br/>
 * <br/>
 * The code lines of a CRASH abnormality is chosen as the main key of an
 * abnormality.
 * 
 * @author Alex Chen (apack1001@gmail.com)
 * 
 */
public class CrashAbnormalityAdapter {
	/**
	 * a brief of Crash Abnormality
	 */
	private String shortMessage = null;
	/**
	 * code lines of a Crash Abnormality
	 */
	private String codeLines = null;

	/**
	 * instantiate from an abnormality
	 * 
	 * @param abnormality
	 *            instance of an abnormality
	 */
	public CrashAbnormalityAdapter(final Abnormality abnormality) {
		final String rawMessage = abnormality.getMessage();
		String[] messages = rawMessage.split("\\n");
		if (messages == null || messages.length == 0)
			throw new RuntimeException();

		StringBuilder lines = new StringBuilder();
		for (String message : messages) {
			if (message.contains("Short Msg:"))
				setShortMessage(message.substring(14));
			else if (message.contains("at")) {
				lines.append(message);
				lines.append("\n");
			}

			setCodeLines(lines.toString());
		}
	}

	public void setShortMessage(final String shortMessage) {
		this.shortMessage = shortMessage;
	}

	public void setCodeLines(final String codeLines) {
		this.codeLines = codeLines;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public String getCodeLines() {
		return codeLines;
	}
}
