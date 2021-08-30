package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.dpx.DPXColumn;

/**
 * For each of the specified image elements, verify the image data sign is 0 or
 * 1
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXImageDataSignRule implements IFileValidationRule {
	DPXColumn targetColumn;
	boolean isValid;
	int imageElement = 1;
	private final String valid32 = "Must be valid unsigned 32bit integer";

	private final String smc = "SMPTE-C \n\n";

	public DPXImageDataSignRule(DPXColumn column, int imageElement) {
		this.targetColumn = column;
		this.imageElement = imageElement;
	}

	@Override
	public String getRule() {
		return smc + valid32 + "; can be values of 0 or 1 only; values 2-255 are not allowed";
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
		return new IsValidIntRangeRule(0, 1);
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
