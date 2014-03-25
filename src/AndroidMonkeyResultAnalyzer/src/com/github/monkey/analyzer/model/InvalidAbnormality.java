package com.github.monkey.analyzer.model;

/**
 * A default implementation of <b>Invalid<b/> abnormality.<br/>
 * <br/>
 * If one instance of {@link IAnalyzer} cannot parse the monkey log, it will be
 * in use.<br/>
 * <br/>
 * This is <b>Null Object Pattern</b>
 * 
 * @author Alex Chen (apack1001@gmail.com)
 * 
 */
public class InvalidAbnormality extends Abnormality {
	@Override
	public AbnormalityType getType() {
		return AbnormalityType.UNKNOWN;
	}
	
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public void setType(AbnormalityType type) {
		
	}

}
