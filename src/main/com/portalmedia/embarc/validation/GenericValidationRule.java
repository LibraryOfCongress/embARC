package com.portalmedia.embarc.validation;

import java.io.Serializable;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;

/**
 * Generic validation rule allowing FileValidationRule to be mapped to
 * IValidationRule interface.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class GenericValidationRule extends ValidationRuleBase implements IValidationRule, Mappable, Serializable {

	private static final long serialVersionUID = 1L;
	boolean isValid;
	String rule;

	public GenericValidationRule(String rule, boolean isValid) {
		this.rule = rule;
		this.isValid = isValid;
	}

	public boolean isValid() {
		return isValid;
	}

	@Override
	public boolean isValid(String value) {
		return isValid;
	}

	@Override
	public void read(NitriteMapper mapper, Document document) {
		if (document != null) {
			setRule((String) document.get("rule"));
			setValid((boolean) document.get("isValid"));
		}
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	@Override
	public Document write(NitriteMapper mapper) {
		final Document document = new Document();
		document.put("rule", getRule());
		document.put("isValid", isValid());
		return document;
	}

}
