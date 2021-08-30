package com.portalmedia.embarc.validation;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;

/**
 * Validation rule to check if a value is a valid byte within a specified range
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class IsValidByteRangeRule extends ValidationRuleBase implements IValidationRule, Mappable {
	private static final long serialVersionUID = 1L;
	String rule = "Generic Byte Range Rule";
	int min;
	int max;

	public IsValidByteRangeRule(byte min, byte max) {
		this.min = min & 0xFF;
		this.max = max & 0xFF;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	@Override
	public boolean isValid(String value) {
		try {
			if (value.length() == 0) {
				return true;
			}
			final byte v = Byte.parseByte(value);
			if (v >= min && v <= max && v >= 0) {
				return true;
			}
		} catch (final NumberFormatException ex) {
		}
		return false;
	}

	@Override
	public void read(NitriteMapper mapper, Document document) {
		if (document != null) {
			setRule((String) document.get("rule"));
			setMin((int) document.get("min"));
			setMax((int) document.get("max"));
		}
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setMin(int min) {
		this.min = min;
	}

	@Override
	public Document write(NitriteMapper mapper) {
		final Document document = new Document();
		document.put("rule", getRule());
		document.put("min", getMin());
		document.put("max", getMax());
		return document;
	}
}
