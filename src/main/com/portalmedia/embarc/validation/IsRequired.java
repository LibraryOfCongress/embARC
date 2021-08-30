package com.portalmedia.embarc.validation;

import org.dizitart.no2.mapper.Mappable;

/**
 * Validation rule to check if a value exists (non-whitespace with a length
 * greater than 0)
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class IsRequired extends ValidationRuleBase implements IValidationRule, Mappable {
	private static final long serialVersionUID = 1L;
	String rule = "Is Required Rule";

	@Override
	public boolean isValid(String value) {
		if (value == null || value.trim().length() == 0) {
			return false;
		}
		return true;
	}
}
