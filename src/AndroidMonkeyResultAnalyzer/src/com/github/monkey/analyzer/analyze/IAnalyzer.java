/**
 * 
 */
package com.github.monkey.analyzer.analyze;

import com.github.monkey.analyzer.model.Abnormality;

/**
 * define Filter Operations
 * @author Alex Chen (apack1001@gmail.com)
 *
 */
public interface IAnalyzer {
	/**
	 * perform filter
	 * @param path abnormal file path
	 * @return Model or Bean of Abnormality
	 */
	public Abnormality toAbnormality();
	
	/**
	 * set the monkey result path of, must be called before toAbnormality
	 * @param path
	 */
	public void setPath(final String path);
}
