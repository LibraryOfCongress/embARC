package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.dpx.DPXColumn;

/**
 * Checks to see if each of the specified image elements has an offset to image
 * data specified.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXImageOffsetToImageDataRule implements IFileValidationRule {
	DPXColumn targetColumn;
	boolean isValid;
	int imageElement = 1;
	private final String imgEl = "Must be present for each image element; must be valid u32bit integer";

	private final String smc = "SMPTE-C \n\n";

	public DPXImageOffsetToImageDataRule(DPXColumn column, int imageElement) {
		this.targetColumn = column;
		this.imageElement = imageElement;
	}

	@Override
	public String getRule() {
		return smc + imgEl;
	}

	@Override
	public ValidationRuleSetEnum getRuleSet() {
		return ValidationRuleSetEnum.SMPTE_C;
	}

	@Override
	public DPXColumn getTargetColumn() {
		return targetColumn;
	}

	@Override
	public IValidationRule getValidationRule() {
		return new IsRequired();
	}

	@Override
	public boolean isValid(DPXFileInformationViewModel file) {

		final String imageElements = file.getProp(DPXColumn.NUMBER_OF_IMAGE_ELEMENTS);
		if (imageElements.isEmpty()) {
			return true;
		}
		try {
			if (Integer.parseInt(imageElements) < imageElement) {
				return true;
			}
		} catch (final NumberFormatException ex) {
			return true;
		}

		final String value = file.getProp(targetColumn);

		final IValidationRule rule = getValidationRule();
		return rule.isValid(value);
	}
}
