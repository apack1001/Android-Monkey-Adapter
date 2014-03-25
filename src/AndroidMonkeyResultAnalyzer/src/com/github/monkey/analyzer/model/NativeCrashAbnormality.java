package com.github.monkey.analyzer.model;

/**
 * @brief This is an implement of native crash abnormality
 * 
 * @author Alex Chen (apack1001@gmail.com)
 *
 * @since 2013/03/19
 */
public class NativeCrashAbnormality extends Abnormality {
	
	@Override
	public void setType(final AbnormalityType type) {
		// do noting
	}

	@Override
	public AbnormalityType getType() {
		return AbnormalityType.NATIVE;
	}

	@Override
	public void setValid(final boolean isValid) {
		this.isValid = isValid;
	}

	@Override
	public boolean isValid() {
		return isValid;
	}
}
