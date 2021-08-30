package com.portalmedia.embarc.gui;

import org.controlsfx.control.textfield.CustomTextField;

import com.portalmedia.embarc.gui.model.DPXMetadataColumnViewModel;

/**
 * Class for validating and replacing float field data
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class FloatInputControl extends CustomTextField {
	DPXMetadataColumnViewModel column;

	public FloatInputControl() {
		super();
	}

	@Override
	public void replaceSelection(String text) {
		final String oldValue = getText();
		if (validate(text)) {
			if(text.toLowerCase().indexOf("e")>0) {
				try {
					final Float i = Float.parseFloat(text);
					super.replaceSelection(String.valueOf(i));
				}
				catch(NumberFormatException ex) {
					super.replaceSelection(text);
				}
			}
			else {
				super.replaceSelection(text);
			}
			
			final String newText = super.getText();
			if (!validate(newText)) {
				super.setText(oldValue);
			}
		}
	}

	@Override
	public void replaceText(int start, int end, String text) {
		final String oldValue = getText();
		if ((validate(text))) {
			if(text.toLowerCase().indexOf("e")>0) {
				try {
					final Float i = Float.parseFloat(text);
					super.replaceText(start, end, String.valueOf(i));
				}
				catch(NumberFormatException ex) {
					super.replaceText(start, end, text);
				}
			}
			else {
				super.replaceText(start, end, text);
			}
			final String newText = super.getText();
			if (!validate(newText)) {
				super.setText(oldValue);
			}
		}
	}

	private boolean validate(String text) {
		// Empty string = valid float
		if (text.length() == 0) {
			return true;
		}
		
		// Only '.', then valid
		if (text.length() == 1 && text.equals(".")) {
			return true;
		}
		try {
			final Float i = Float.parseFloat(text);

			if (i >= 0) {
				return true;
			}
		} catch (final NumberFormatException exception) {
			exception.printStackTrace();
		}

		return false;
	}

}
