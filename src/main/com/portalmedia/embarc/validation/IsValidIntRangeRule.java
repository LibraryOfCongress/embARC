package com.portalmedia.embarc.validation;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;

/**
 * Validation rule to check if a value is an integer falls within a specific
 * range
 *
 * @author PortalMedia
 * @since 2019-05-01
 */
public class IsValidIntRangeRule extends ValidationRuleBase implements IValidationRule, Mappable {

	private static final long serialVersionUID = 1L;
	String rule = "Generic Int Range Rule";
	long min;
	long max;

	public IsValidIntRangeRule(int min, int max) {
		this.min = min & 0xFF;
		this.max = max & 0xFF;
	}

	public long getMax() {
		return max;
	}

	public long getMin() {
		return min;
	}

	@Override
	public boolean isValid(String value) {
		if (value.length() == 0) {
			return true;
		}
		try {
			final long v = Long.parseLong(value);
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
			setMin((short) document.get("min"));
			setMax((short) document.get("max"));
		}
	}

	public void setMax(long max) {
		this.max = max;
	}

	public void setMin(long min) {
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
