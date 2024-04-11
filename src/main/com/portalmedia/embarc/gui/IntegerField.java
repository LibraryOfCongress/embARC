package com.portalmedia.embarc.gui;

import java.io.IOException;
import java.util.Set;

import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * Field for displaying integer data
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class IntegerField extends AnchorPane implements IEditorField {
	@FXML
	private HBox editorTextFieldContainer;
	@FXML
	private Label editorTextFieldLabel;
	@FXML
	private HBox editorTextFieldLabelInfoIcon;
	@FXML
	private IntegerInputControl editorTextField;

	private DPXColumn column;
	private String originalValue;
	private MXFColumn mxfColumn;

	public IntegerField() {
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("IntegerField.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#clearValidation()
	 */
	@Override
	public void clearValidation() {
		if(editorTextField!=null) {
			editorTextField.setRight(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#getColumn()
	 */
	@Override
	public DPXColumn getColumn() {
		return column;
	}

	@Override
	public String getValue() {
		return editorTextField.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#valueChanged()
	 */
	@Override
	public void resetValueChanged() {
		originalValue = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.portalmedia.embarc.gui.IEditorField#setColumn(DPXColumn)
	 */
	@Override
	public void setColumn(DPXColumn column) {
		this.column = column;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#setEditable(boolean)
	 */
	@Override
	public void setEditable(boolean editable) {
		editorTextField.setEditable(editable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.portalmedia.embarc.gui.IEditorField#setInvalidRuleSets(java.util.Set)
	 */
	@Override
	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet) {
		final ValidationWarningIcons icons = new ValidationWarningIcons();
		icons.setInvalidRuleSets(invalidRuleSet);
		AnchorPane.setBottomAnchor(icons, 0.0);
		AnchorPane.setTopAnchor(icons, 0.0);
		editorTextField.setRight(icons);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String text) {
		editorTextFieldLabel.setText(text);
		editorTextFieldLabel.setLabelFor(editorTextField);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String labelText, String helpText) {
		editorTextFieldLabelInfoIcon.setOnKeyPressed(event -> {
			if (event.getCode() != KeyCode.SPACE) {
				return;
			}
			DataFieldInfoAlert.showFieldInfoAlert(labelText, helpText);
		});
		editorTextFieldLabelInfoIcon.setOnMouseClicked(event -> {
			DataFieldInfoAlert.showFieldInfoAlert(labelText, helpText);
		});
		setLabel(labelText);
		editorTextFieldLabelInfoIcon.setAccessibleRole(AccessibleRole.BUTTON);
		editorTextFieldLabelInfoIcon.setAccessibleText("Open modal with field specification.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#setPopoutIcon()
	 */
	@Override
	public void setPopoutIcon() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		if (originalValue == null) {
			originalValue = value;
		}
		editorTextField.setText(value);
	}

	public StringProperty textProperty() {
		return editorTextField.textProperty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#valueChanged()
	 */
	@Override
	public boolean valueChanged() {
		if (originalValue == null) {
			return false;
		}
		if (originalValue.equals(editorTextField.getText())) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.portalmedia.embarc.gui.IEditorField#setMXFColumn(MXFColumn)
	 */
	public void setMXFColumn(MXFColumn col) {
		this.mxfColumn = col;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#getMXFColumn()
	 */
	public MXFColumn getMXFColumn() {
		return mxfColumn;
	}
}
