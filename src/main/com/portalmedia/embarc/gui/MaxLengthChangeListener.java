package com.portalmedia.embarc.gui;

import org.controlsfx.control.textfield.CustomTextField;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;

/**
 * Change listener for max size of length limited fields
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class MaxLengthChangeListener implements javafx.beans.value.ChangeListener<String> {

	private final int maxLength;
	private CustomTextField textField;
	private TextArea textArea;

	public MaxLengthChangeListener(CustomTextField textField, int maxLength) {
		this.textField = textField;
		this.maxLength = maxLength;
	}

	public MaxLengthChangeListener(TextArea textArea, int maxLength) {
		this.textArea = textArea;
		this.maxLength = maxLength;
	}

	@Override
	public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {

		if (newValue == null) {
			return;
		}

		if (newValue.length() > maxLength) {
			if (textField != null) {
				textField.setText(oldValue);
			} else if (textArea != null) {
				textArea.setText(oldValue);
			}
		} else {
			if (textField != null) {
				textField.setText(newValue);
			} else if (textArea != null) {
				textArea.setText(newValue);
			}
		}
	}

	public int getMaxLength() {
		return maxLength;
	}
}
