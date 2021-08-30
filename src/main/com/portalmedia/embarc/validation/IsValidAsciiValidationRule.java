package com.portalmedia.embarc.validation;

import java.io.Serializable;

import org.dizitart.no2.mapper.Mappable;

/**
 * Validation rule to check if a value is ascii text (char values between 0-127)
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class IsValidAsciiValidationRule extends ValidationRuleBase implements IValidationRule, Mappable, Serializable {
	private static final long serialVersionUID = 1L;
	String rule = "Must be valid ASCII characters.";

	@Override
	public boolean isValid(String value) {
		final byte[] bytes = value.getBytes();
		for (final byte b : bytes) {
			final char c = (char) (b & 0xFF);
			if (c < 0 || c > 127) {
				return false;
			}
		}
		return true;
	}
}
