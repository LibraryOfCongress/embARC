package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.dpx.DPXColumn;

/**
 * Validate that the actual filename matches the specified ImageFileName column
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXFileNameMatchesRule implements IFileValidationRule {
	DPXColumn targetColumn = DPXColumn.IMAGE_FILE_NAME;
	boolean isValid;

	@Override
	public String getRule() {
		return "Must match the filename of the file as stored in the file system.";
	}

	@Override
	public ValidationRuleSetEnum getRuleSet() {
		return ValidationRuleSetEnum.FADGI_SR;
	}

	@Override
	public DPXColumn getTargetColumn() {
		return targetColumn;
	}

	@Override
	public IValidationRule getValidationRule() {
		return new GenericValidationRule(getRule(), isValid);
	}

	@Override
	public boolean isValid(DPXFileInformationViewModel file) {
		final String imageFileName = file.getProp(DPXColumn.IMAGE_FILE_NAME).trim();

		final String fileName = file.getProp("name");

		isValid = imageFileName.equals(fileName.trim());
		return isValid;
	}
}
