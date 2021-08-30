package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.dpx.DPXColumn;

/**
 * Interface for validation rules that require validation across multiple
 * columns
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public interface IFileValidationRule {
	String getRule();

	ValidationRuleSetEnum getRuleSet();

	DPXColumn getTargetColumn();

	IValidationRule getValidationRule();

	boolean isValid(DPXFileInformationViewModel file);
}
