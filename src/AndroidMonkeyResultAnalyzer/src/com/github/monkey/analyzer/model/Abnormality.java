package com.github.monkey.analyzer.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class define the interface of an abnormality.<br/>
 * <br/>
 * For example, running monkey test may introduce CRASH or ANR in the target
 * application to be tested.<br/>
 * <br/>
 * 
 * <b>Developer or maintainer of this tools should inherit this abstract
 * class</b>
 * 
 * @author Alex Chen (apack1001@gmail.com)
 * 
 */
public abstract class Abnormality {
	protected String message = "";
	protected AbnormalityType type = AbnormalityType.UNKNOWN;
	protected HashMap<String, String> extraInfo = new HashMap<String, String>();
	protected boolean isValid = false;
	
	public static final String EXTRAS_KEY_BEFORE_START_PATTERN = "before";
	public static final String EXTRAS_KEY_AFTER_END_PATTERN = "after";
	public static final String EXTRAS_KEY_PATH = "path";
	/**
	 * set the type of abnormality
	 * 
	 */
	public abstract void setType(final AbnormalityType type);

	/**
	 * get the type of abnormality
	 * 
	 * @return instance of FilterType
	 */
	public abstract AbnormalityType getType();

	/**
	 * set whether this Abnormality is Valid
	 * 
	 * @param isValid
	 *            whether this Abnormality is Valid
	 */

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	/**
	 * tell user whether the abnormality is valid
	 * 
	 * @return true if valid, else return invalid
	 */
	public abstract boolean isValid();

	/**
	 * set the main message of this abnormality file, it is usually monkey log
	 * 
	 * @return abnormality message
	 */
	final public void setMessage(StringBuilder sb) {
		if (sb == null)
			message = "";
		else
			message = sb.toString();
	}

	/**
	 * the main message of this abnormality file, it is usually monkey log
	 * 
	 * @return abnormality message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * extra message of this abnormality file, not in use now. it is used for
	 * extension
	 * 
	 * @return extra information of this abnormality
	 */
	final public HashMap<String, String> getExtras() {
		return extraInfo;
	}

	/**
	 * put one entry to extras container
	 */
	final public String put(String key, String value) {
		return extraInfo.put(key, value);
	}
	/**
	 * copy from an
	 */
	final public void copyFrom(Map<String, String> extras) {
		if (extras == null || extras.isEmpty())
			return;

		Set<Entry<String, String>> entries = extras.entrySet();

		if (entries == null || entries.isEmpty())
			return;

		for (Entry<String, String> entry : entries) {
			extraInfo.put(entry.getKey(), entry.getValue());
		}
	}
}