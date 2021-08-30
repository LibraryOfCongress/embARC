package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.dpx.DPXColumn;

/**
 * For each of the specified image elements, verify the BitDepth is in
 * {1,8,10,12,16,32,64}
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXImageBitDepthRule implements IFileValidationRule {
	DPXColumn targetColumn;
	boolean isValid;
	int imageElement = 1;
	private final String valid8 = "Must be valid unsigned 8bit integer";

	private final String smc = "SMPTE-C \n\n";

	public DPXImageBitDepthRule(DPXColumn column, int imageElement) {
		this.targetColumn = column;
		this.imageElement = imageElement;
	}

	@Override
	public String getRule() {
		return smc + valid8 + "; can be values of 1, 8, 10, 12, 16, 32, 64 only; any other value is invalid";
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
		return new IsValidByteListRule(new byte[] { 1, 8, 10, 12, 16, 32, 64 });
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
