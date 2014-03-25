package com.github.monkey.analyzer.model;

/**
 * @brief This is an Abnormality Factory which is used to create derived, empty
 *        Abnormality
 * @author Alex Chen (apack1001@gmail.com)
 * 
 */
public class AbnormalityFactory {
	public static synchronized Abnormality newInstance(AbnormalityType type) {
		Abnormality abnormality = null;
		switch (type) {
		case ANR:
		case CRASH:
			abnormality = new AnrOrCrashAbnormality();
			break;
		case NATIVE:
			abnormality = new NativeCrashAbnormality();
			break;
		default:
			abnormality = new InvalidAbnormality();
			break;
		}
		return abnormality;
	}
}
