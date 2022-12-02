package com.portalmedia.embarc.gui;

import org.controlsfx.control.textfield.CustomTextField;

import com.portalmedia.embarc.validation.IsValidIntRule;

/**
 * Class for validating and replacing integer field data
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class IntegerInputControl extends CustomTextField {

	public IntegerInputControl() {
		super();
	}

	@Override
	public void replaceSelection(String text) {
		System.out.println("Replacing selection");
		final String oldValue = getText();
		if (validate(text)) {
			super.replaceSelection(text);
			final String newText = super.getText();
			if (!validate(newText)) {
				super.setText(oldValue);
			}
		}
	}

	@Override
	public void replaceText(int start, int end, String text) {
		System.out.println("Replacing text");
		final String oldValue = getText();
		if ((validate(text))) {
			super.replaceText(start, end, text);
			final String newText = super.getText();
			if (!validate(newText)) {
				super.setText(oldValue);
			}
		}
	}

	private boolean validate(String text) {
		if (text.length() == 0) {
			return true;
		}

		try {
			return new IsValidIntRule().isValid(text);
		} catch (final NumberFormatException exception) {
			System.out.println("NumberFormatException Error");
		}

		return false;
	}

}
