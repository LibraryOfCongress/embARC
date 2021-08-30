package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.dpx.DPXColumn;

/**
 * For each of the specified image elements, validate the image encoding field
 * is in {0,1,2}
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXImageEncodingRule implements IFileValidationRule {
	DPXColumn targetColumn;
	boolean isValid;
	int imageElement = 1;
	private final String valid16 = "Must be valid unsigned 16bit integer";

	private final String partUndefWarn = "= warning that a non-defined value is in use; ";
	private final String smc = "SMPTE-C \n\n";

	public DPXImageEncodingRule(DPXColumn column, int imageElement) {
		this.targetColumn = column;
		this.imageElement = imageElement;
	}

	@Override
	public String getRule() {
		return smc + valid16 + "; can be values of 0-1, 2-7 " + partUndefWarn + " cannot = 8-255";
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
		return new IsValidShortListRule(new short[] { 0, 1 });
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
