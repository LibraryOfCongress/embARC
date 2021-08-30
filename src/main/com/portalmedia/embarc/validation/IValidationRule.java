package com.portalmedia.embarc.validation;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.NitriteMapper;

/**
 * Validation rule interface for all field based validation rules
 *
 * @author PortalMedia
 * @since 2019-05-01
 *
 */
public interface IValidationRule {
	String getRule();

	boolean isValid(String value);

	void read(NitriteMapper mapper, Document document);

	void setRule(String newRule);

	Document write(NitriteMapper mapper);
}
