package com.portalmedia.embarc.gui;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Component that displays Image Source Information Header Border Validity array
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class BorderValidityField extends AnchorPane implements IEditorField {
	@FXML
	private HBox editorTextFieldContainer;
	@FXML
	private Label editorTextFieldLabel;
	@FXML
	private HBox editorTextFieldLabelInfoIcon;
	@FXML
	private IntegerInputControl editorArrayValue1;
	@FXML
	private IntegerInputControl editorArrayValue2;
	@FXML
	private IntegerInputControl editorArrayValue3;
	@FXML
	private IntegerInputControl editorArrayValue4;
	@FXML
	private HBox editorArrayValue1ValidationInfo;
	@FXML
	private HBox editorArrayValue2ValidationInfo;
	@FXML
	private HBox editorArrayValue3ValidationInfo;
	@FXML
	private HBox editorArrayValue4ValidationInfo;
	
	private DPXColumn column;
	private String originalValue;
	private MXFColumn mxfColumn;

	public BorderValidityField() {
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BorderValidityField.fxml"));
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
		this.setInvalidRuleSets(new HashSet<>());
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
		return "[" + editorArrayValue1.getText().trim() + "," + editorArrayValue2.getText().trim() + ","
				+ editorArrayValue3.getText().trim() + "," + editorArrayValue4.getText().trim() + "]";

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
		editorArrayValue1.setEditable(editable);
		editorArrayValue2.setEditable(editable);
		editorArrayValue3.setEditable(editable);
		editorArrayValue4.setEditable(editable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.portalmedia.embarc.gui.IEditorField#setInvalidRuleSets(java.util.Set)
	 */
	@Override
	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet) {	
		boolean hasErrors = ValidationWarningHelper.getInvalidRuleSetsAndUpdateErrorIcons(editorArrayValue1ValidationInfo, invalidRuleSet, column);
		String validationWarning = hasErrors ? "Contains errors" : "";
		
		editorArrayValue1.setAccessibleHelp(validationWarning);
		
		hasErrors = ValidationWarningHelper.getInvalidRuleSetsAndUpdateErrorIcons(editorArrayValue2ValidationInfo, invalidRuleSet, column);
		editorArrayValue2.setAccessibleHelp(validationWarning);
		
		hasErrors = ValidationWarningHelper.getInvalidRuleSetsAndUpdateErrorIcons(editorArrayValue3ValidationInfo, invalidRuleSet, column);
		editorArrayValue3.setAccessibleHelp(validationWarning);
		
		hasErrors = ValidationWarningHelper.getInvalidRuleSetsAndUpdateErrorIcons(editorArrayValue4ValidationInfo, invalidRuleSet, column);
		editorArrayValue4.setAccessibleHelp(validationWarning);		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String text) {
		editorTextFieldLabel.setText(text);
		editorArrayValue1.getText();
		editorTextFieldLabel.setLabelFor(editorArrayValue1);
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
	 * @see com.portalmedia.embarc.gui.IEditorField#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String labelText, String helpText, String labelColor) {
		editorTextFieldLabel.setTextFill(Color.web(labelColor));
		setLabel(labelText, helpText);
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
		final String v = value.replace("[", "").replace("]", "");
		final String[] list = v.split(",");
		if (list.length == 4) {
			editorArrayValue1.setText(list[0]);
			editorArrayValue2.setText(list[1]);
			editorArrayValue3.setText(list[2]);
			editorArrayValue4.setText(list[3]);
		}
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
			// TODO: if(originalValue.equals(EditorTextField.getText())) return false;
		}

		return false;
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
