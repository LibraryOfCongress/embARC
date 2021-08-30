package com.portalmedia.embarc.validation;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;

/**
 * Validation rule to check if a short value falls within a range
 *
 * @author PortalMedia
 * @since 2019-05-01
 */
public class IsValidShortRangeRule extends ValidationRuleBase implements IValidationRule, Mappable {
	private static final long serialVersionUID = 1L;
	String rule = "Generic Short Range Rule";
	short min;
	short max;

	public IsValidShortRangeRule(short min, short max) {
		this.min = min;
		this.max = max;

	}

	public short getMax() {
		return max;
	}

	public short getMin() {
		return min;
	}

	@Override
	public boolean isValid(String value) {
		try {
			final short v = Short.parseShort(value);
			if (v >= min && v <= max) {
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
			setMin((short) document.get("min"));
			setMax((short) document.get("max"));
		}
	}

	public void setMax(short max) {
		this.max = max;
	}

	public void setMin(short min) {
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
