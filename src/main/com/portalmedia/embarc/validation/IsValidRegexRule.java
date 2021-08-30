package com.portalmedia.embarc.validation;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;

/**
 * Validation rule to check if a string value matches a regular expression
 *
 * @author PortalMedia
 * @since 2019-05-01
 */
public class IsValidRegexRule extends ValidationRuleBase implements IValidationRule, Mappable {
	private static final long serialVersionUID = 1L;
	String rule = "Generic Regex Rule";
	private String regexExpression;

	public IsValidRegexRule(String regexExpression) {
		this.regexExpression = regexExpression;
	}

	public String getRegexExpression() {
		return regexExpression;
	}

	@Override
	public boolean isValid(String value) {

		return value.trim().matches(regexExpression);
	}

	@Override
	public void read(NitriteMapper mapper, Document document) {
		if (document != null) {
			setRule((String) document.get("rule"));
			setRegexExpression((String) document.get("regexExpression"));
		}
	}

	public void setRegexExpression(String regexExpression) {
		this.regexExpression = regexExpression;
	}

	@Override
	public Document write(NitriteMapper mapper) {
		final Document document = new Document();
		document.put("rule", getRule());
		document.put("regexExpression", getRegexExpression());
		return document;
	}
}
