package com.portalmedia.embarc.validation;

import org.dizitart.no2.mapper.Mappable;

/**
 * Validation rule to check if a value is a valid unsigned integer (0 to
 * 4294967295).
 *
 * @author PortalMedia
 * @since 2019-05-01
 */
public class IsValidIntRule extends ValidationRuleBase implements IValidationRule, Mappable {
	private static final long serialVersionUID = 1L;
	String rule = "Must be valid unsigned 32 bit integer.";

	@Override
	public boolean isValid(String value) {
		if (value.equals("")) {
			return true;
		}
		try {
			final Integer v = Integer.parseUnsignedInt(value);
			final Long l = Integer.toUnsignedLong(v);
			// Hack here. If we allow the max value (4294967295) it is the same as a null
			// value (FF FF FF FF).
			if (l.compareTo(4294967294L) <= 0) {
				return true;
			}
		} catch (final NumberFormatException ex) {
		}
		return false;
	}
}
