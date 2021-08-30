package com.portalmedia.embarc.validation;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;

/**
 * Validation rule to check if a value is a valid byte and is contained within a
 * list of bytes
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class IsValidByteListRule extends ValidationRuleBase implements IValidationRule, Mappable {
	private static final long serialVersionUID = 1L;
	String rule = "Generic ByteList Rule";
	int[] validValuesList;

	public IsValidByteListRule(byte[] validValues) {
		this.validValuesList = new int[validValues.length];
		for (int i = 0; i < validValues.length; i++) {
			validValuesList[i] = validValues[i] & 0xFF;
		}
	}

	public int[] getValidValuesList() {
		return validValuesList;
	}

	@Override
	public boolean isValid(String value) {
		try {
			final byte v = Byte.parseByte(value);
			for (final int i : validValuesList) {
				final int vValue = v & 0xFF;
				if (vValue == i) {
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
			setValidValuesList((int[]) document.get("validValuesList"));
		}
	}

	public void setValidValuesList(int[] validValuesList) {
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
