package com.github.monkey.analyzer.analyze;

import java.util.LinkedList;

import com.github.monkey.analyzer.model.Abnormality;

/**
 * Default implementation of {@link IAnalyzer}<br/>
 * This is a business related class so coupling may occur during the following
 * upgrading<br/>
 * 
 * @author Alex Chen (apack1001@gmail.com)
 * 
 * @since 2013/01/14
 */
class CrashOrAnrAnalyzer extends Analyzer {

	public CrashOrAnrAnalyzer(AnalyzerConfiguration config) {
		super(config);
	}

	@Override
	protected void onMatch(Abnormality abnormality,
			StringBuilder message,
			LinkedList<String> messageBefore,
			LinkedList<String> messageAfter
			) {
		StringBuilder msgAfter = new StringBuilder();
		for (String msg : messageAfter) {
			msgAfter.append(msg);
			msgAfter.append("\n");
		}
		abnormality.put(Abnormality.EXTRAS_KEY_AFTER_END_PATTERN,
				msgAfter.toString());
		
		StringBuilder msgBefore = new StringBuilder();
		for (String msg : messageBefore) {
			msgBefore.append(msg);
			msgBefore.append("\n");
		}
		abnormality.put(Abnormality.EXTRAS_KEY_BEFORE_START_PATTERN,
				msgBefore.toString());
	}
}
