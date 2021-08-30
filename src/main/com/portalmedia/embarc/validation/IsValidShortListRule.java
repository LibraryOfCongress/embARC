package com.portalmedia.embarc.validation;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;

/**
 * Validation rule to check if a short value is in a list of values
 *
 * @author PortalMedia
 * @since 2019-05-01
 */
public class IsValidShortListRule extends ValidationRuleBase implements IValidationRule, Mappable {

	private static final long serialVersionUID = 1L;
	String rule = "Generic ShortList Rule";

	short[] validValuesList;

	public IsValidShortListRule(short[] validValues) {
		validValuesList = validValues;
	}

	public short[] getValidValuesList() {
		return validValuesList;
	}

	@Override
	public boolean isValid(String value) {
		try {
			final short v = Short.parseShort(value);
			for (final short i : validValuesList) {
				if (v == i) {
					return true;
				}
			}
		} catch (final NumberFormatException ex) {
		}
		return false;
	}

	@Override
	public void read(NitriteMapper mapper, Document document) {
		if (document != null) {
			setRule((String) document.get("rule"));
			setValidValuesList((short[]) document.get("validValuesList"));
		}
	}

	public void setValidValuesList(short[] validValuesList) {
		this.validValuesList = validValuesList;
	}

	@Override
	public Document write(NitriteMapper mapper) {
		final Document document = new Document();
		document.put("rule", getRule());
		document.put("validValuesList", getValidValuesList());
		return document;
	}
}
