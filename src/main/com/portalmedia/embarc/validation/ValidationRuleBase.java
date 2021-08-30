package com.portalmedia.embarc.validation;

import java.io.Serializable;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;

/**
 * Base validation rule class used by each of the field validation rules.
 *
 * @author PortalMedia
 * @since 2019-05-01
 */
public class ValidationRuleBase implements IValidationRule, Mappable, Serializable {

	protected static final long serialVersionUID = 1L;
	String rule = "";

	@Override
	public String getRule() {
		return rule;
	}

	@Override
	public boolean isValid(String value) {
		return false;
	}

	@Override
	public void read(NitriteMapper mapper, Document document) {
		if (document != null) {
			setRule((String) document.get("rule"));
		}
	}

	@Override
	public void setRule(String newRule) {
		rule = newRule;
	}

	@Override
	public Document write(NitriteMapper mapper) {
		final Document document = new Document();
		document.put("rule", getRule());
		return document;
	}
}
