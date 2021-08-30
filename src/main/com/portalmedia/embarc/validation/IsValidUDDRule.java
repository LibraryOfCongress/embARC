package com.portalmedia.embarc.validation;

import org.dizitart.no2.mapper.Mappable;

/**
 * User defined data validation rule. Each line must start with 0=
 *
 * @author PortalMedia
 * @since 2019-05-01
 *
 */
public class IsValidUDDRule extends ValidationRuleBase implements IValidationRule, Mappable {
	private static final long serialVersionUID = 1L;
	String rule = "User Defined Data Custom Rule";

	@Override
	public boolean isValid(String value) {
		if (value.length() < 2) {
			return false;
		}
		if (value.charAt(0) != 'O' || value.charAt(1) != '=') {
			return false;
		}
		final String[] arr = value.split("\n");
		for (final String element : arr) {
			if (element.length() > 1) {
				if (element.charAt(0) != 'O' || element.charAt(1) != '=') {
					return false;
				}
			}
		}
		return true;
	}
}
