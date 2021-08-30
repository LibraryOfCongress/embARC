package com.portalmedia.embarc.validation;

import org.dizitart.no2.mapper.Mappable;

/**
 * Validation rule to check if a string field matches a specific value
 *
 * @author PortalMedia
 * @since 2019-05-01
 */
public class IsValidMatchesTextRule extends ValidationRuleBase implements IValidationRule, Mappable {
	private static final long serialVersionUID = 1L;
	String rule = "Generic MatchesText Rule";

	private final String[] validValues;

	public IsValidMatchesTextRule(String[] validValues) {
		this.validValues = validValues;
	}

	@Override
	public boolean isValid(String value) {
		for (final String s : validValues) {
			if (s.equals(value)) {
				return true;
			}
		}
		return false;
	}
}
