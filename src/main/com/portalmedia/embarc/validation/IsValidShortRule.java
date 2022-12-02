package com.portalmedia.embarc.validation;

import org.dizitart.no2.mapper.Mappable;

/**
 * Short data value validation rule. Must be a valid short value.
 *
 * @author PortalMedia
 * @since 2019-05-01
 */
public class IsValidShortRule extends ValidationRuleBase implements IValidationRule, Mappable {
	private static final long serialVersionUID = 1L;
	String rule = "Generic Short Rule";

	@Override
	public boolean isValid(String value) {
		if (value.equals("")) {
			return true;
		}
		try {
			final Integer v = Integer.parseUnsignedInt(value);
			if (v < 65535) {
				return true;
			}
		} catch (final NumberFormatException ex) {
			System.out.println("Invalid number in IsValidShortRule");
		}
		return false;
	}

}
