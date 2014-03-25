package com.github.monkey.analyzer.model;

/**
 * This class is the default implementation of Abnormality. <br/>
 * <br/>
 * It can parse CRASH and ANR abnormality in a monkey log 
 * 
 * @author Alex Chen (apack1001@gmail.com)
 * 
 */
public class AnrOrCrashAbnormality extends Abnormality {

	@Override
	public void setType(AbnormalityType type) {
		this.type = type;
	}

	@Override
	public AbnormalityType getType() {
		return type;
	}
	
	@Override
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	@Override
	public boolean isValid() {
		return isValid;
	}
}
