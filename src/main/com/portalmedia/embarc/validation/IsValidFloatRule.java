package com.portalmedia.embarc.validation;

import org.dizitart.no2.mapper.Mappable;

/**
 * Validation rule to check if a value is a valid float
 *
 * @author PortalMedia
 * @since 2019-05-01
 */
public class IsValidFloatRule extends ValidationRuleBase implements IValidationRule, Mappable {

	private static final long serialVersionUID = 1L;
	String rule = "Generic Float Rule";

	@Override
	public boolean isValid(String value) {
		if (value.equals("")) {
			return true;
		}
		try {
			final float v = Float.parseFloat(value);
			if (v >= 0) {
				return true;
			}
		} catch (final NumberFormatException ex) {
		}
		return false;
	}
}
