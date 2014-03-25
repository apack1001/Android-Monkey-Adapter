package com.github.monkey.analyzer.report;

import java.io.File;
import org.json.JSONObject;

/**
 * This class aims at generation the JSON value which is used to fill a HTML
 * report or other format. <br/>
 * Any way! You konw what to do!<br/>
 * 
 * @author Alex Chen (apack1001@gmail.com)
 * 
 */
final public class JSONReportProvider {

	/**
	 * the report file will stored in this object
	 */
	private JSONObject json = new JSONObject();

	/**
	 * this Override the default implementation of {@link Object}.
	 * 
	 * @return return the json's {@link }String} value
	 */
	@Override
	public String toString() {
		if (json == null)
			return "";
		return json.toString();
	}
	
	public void setJSONObject(final JSONObject json) {
		this.json = json;
	}
	
	/**
	 * This will write the JSON Value to file. <br/>
	 * This must be an not exists file and its parent folder exist<br/>
	 * This won't create folder recursively<br/>
	 * 
	 * @param path
	 *            JSON report file
	 * 
	 */
	public void toFile(final String path) {
		if (path == null || path == "")
			throw new RuntimeException("Invalid path");

		File file = new File(path);

		if (file.isDirectory() && file.exists() == true) {
			throw new RuntimeException("Not exisiting file supported only!");
		}
		throw new RuntimeException("not yet implemented");
	}
}
