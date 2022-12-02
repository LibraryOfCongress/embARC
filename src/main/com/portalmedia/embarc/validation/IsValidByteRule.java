package com.portalmedia.embarc.validation;

import org.dizitart.no2.mapper.Mappable;

/**
 * Validation rule to check if a value is a valid byte
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class IsValidByteRule extends ValidationRuleBase implements IValidationRule, Mappable {

	private static final long serialVersionUID = 1L;
	String rule = "Generic Byte Rule";

	@Override
	public boolean isValid(String value) {
		try {
			if (value.length() == 0) {
				return true;
			}
			final byte v = Byte.parseByte(value);
			if (v >= 0) {
				return true;
			}
		} catch (final NumberFormatException ex) {
			System.out.println("Invalid number in IsValidByteRule");
		}
		return false;
	}
}
