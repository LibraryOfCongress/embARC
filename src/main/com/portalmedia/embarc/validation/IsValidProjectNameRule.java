package com.portalmedia.embarc.validation;

import org.dizitart.no2.mapper.Mappable;

/**
 * Validation rule to check if the project name follows a specific pattern: If
 * labeled: Identifier [comma space] type [comma space] comment [semicolon-space
 * if more than one identifier]. If no labeling: Identifier
 *
 * @author PortalMedia
 * @since 2019-05-01
 */
public class IsValidProjectNameRule extends ValidationRuleBase implements IValidationRule, Mappable {
	private static final long serialVersionUID = 1L;
	String rule = "Project Name Custom Rule";

	@Override
	public boolean isValid(String value) {
		if (value.length() < 1) {
			return false;
		}

		// first check if there are multiple identifiers
		final String[] arr1 = value.split("; ");
		if (arr1.length > 1) {
			for (final String element : arr1) {
				// if multiple identifiers, split on commas
				final String[] arr = element.split(", ");
				if (arr.length == 1) {
					// no comma was found with multiple identifiers, return false
					return false;
				} else if (arr.length == 2) {
					// only 1 comma found
					return false;
				} else {
					// at least one comma found, iterate through and make sure values exist
					for (final String element2 : arr) {
						if (!element2.matches(".*")) {
							return false;
						}
					}
				}
			}
		} else if (arr1.length == 1) {
			// if one identifier, check for commas and values
			final String[] arr = arr1.toString().split(", ");
			if (arr.length == 1) {
				// no comma found, check if value exists
				if (!arr[0].matches(".*")) {
					return false;
				}
			} else if (arr.length == 2) {
				return false;
			} else {
				// at least one comma found, iterate through and make sure values exist
				for (final String element : arr) {
					if (!element.matches(".*")) {
						return false;
					}
				}
			}
		}

		return true;
	}

}
