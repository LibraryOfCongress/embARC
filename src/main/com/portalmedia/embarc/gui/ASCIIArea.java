package com.portalmedia.embarc.gui;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.portalmedia.embarc.gui.dpx.ValidationChangeListener;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

/**
 * Small text input area for editable ASCII fields
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ASCIIArea extends AnchorPane implements IEditorField {
	@FXML
	private HBox editorTextAreaContainer;
	@FXML
	private Label editorTextAreaLabel;
	@FXML
	private HBox editorTextAreaLabelInfoIcon;
	@FXML
	private TextArea editorTextArea;
	@FXML
	private FontAwesomeIconView popoutIcon;
	@FXML
	private HBox popoutIconContainer;
	@FXML
	private HBox editorTextAreaValidationInfo;

	private DPXColumn column;
	private String originalValue;
	private MXFColumn mxfColumn;

	public ASCIIArea() {
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ASCIIArea.fxml"));
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
		return editorTextArea.getText();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#resetValueChanged()
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
		if (this.column == null) {
			editorTextArea.textProperty().addListener(new MaxLengthChangeListener(editorTextArea, column.getLength()));
			editorTextArea.textProperty().addListener(new ValidationChangeListener(editorTextArea, column, editorTextAreaValidationInfo));
		}
		this.column = column;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#setEditable(boolean)
	 */
	@Override
	public void setEditable(boolean editable) {
		editorTextArea.setEditable(editable);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.portalmedia.embarc.gui.IEditorField#setInvalidRuleSets(java.util.Set)
	 */
	@Override
	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet) {		
		final boolean hasErrors = ValidationWarningHelper.getInvalidRuleSetsAndUpdateErrorIcons(editorTextAreaValidationInfo, invalidRuleSet, getColumn());
		String validationWarning = hasErrors ? "Contains errors" : "";
		editorTextArea.setAccessibleHelp(validationWarning);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String text) {
		editorTextAreaLabel.setText(text);
		editorTextAreaLabel.setLabelFor(editorTextArea);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String labelText, String helpText, String labelColor) {
		editorTextAreaLabel.setTextFill(Color.web(labelColor));
		setLabel(labelText, helpText);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String labelText, String helpText) {
		editorTextAreaLabelInfoIcon.setOnKeyPressed(event -> {
			if (event.getCode() != KeyCode.SPACE) {
				return;
			}
			DataFieldInfoAlert.showFieldInfoAlert(labelText, helpText);
		});
		editorTextAreaLabelInfoIcon.setOnMouseClicked(event -> {
			DataFieldInfoAlert.showFieldInfoAlert(labelText, helpText);
		});
		setLabel(labelText);
		editorTextAreaLabelInfoIcon.setAccessibleRole(AccessibleRole.BUTTON);
		editorTextAreaLabelInfoIcon.setAccessibleText("Open modal with " + labelText + " specification.");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#setPopoutIcon()
	 */
	@Override
	public void setPopoutIcon() {
		if (!editorTextArea.isEditable()) {
			return;
		}
		popoutIconContainer.setVisible(true);
		popoutIconContainer.setOnKeyPressed(event -> {
			if (event.getCode() != KeyCode.SPACE) {
				return;
			}
			showPopoutAlert();
		});
		popoutIconContainer.setOnMouseClicked(event -> {
			showPopoutAlert();
		});
		popoutIconContainer.setAccessibleRole(AccessibleRole.BUTTON);
		popoutIconContainer.setAccessibleText("Open modal to edit " + editorTextAreaLabel.getText());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		editorTextArea.setText(value);
		if (originalValue == null) {
			originalValue = editorTextArea.getText();
		}
	}

	public StringProperty textProperty() {
		return editorTextArea.textProperty();
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

		if (originalValue.equals(editorTextArea.getText())) {
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
		if (this.mxfColumn == null) {
			this.mxfColumn = col;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#getMXFColumn()
	 */
	public MXFColumn getMXFColumn() {
		return mxfColumn;
	}

	private void showPopoutAlert() {
		final Alert alert = new Alert(AlertType.NONE);
		alert.setTitle("Edit " + editorTextAreaLabel.getText());
		alert.setHeaderText(null);
		alert.setContentText(null);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(Main.getPrimaryStage());
		final ButtonType[] buttonList = new ButtonType[2];
		if (column != null) {
			if (column.getEditable()) {
				buttonList[0] = ButtonType.APPLY;
				buttonList[1] = ButtonType.CLOSE;
			} else {
				buttonList[0] = ButtonType.CLOSE;
			}
		} else if (mxfColumn != null) {
			if (mxfColumn.getEditable()) {
				buttonList[0] = ButtonType.APPLY;
				buttonList[1] = ButtonType.CLOSE;
			} else {
				buttonList[0] = ButtonType.CLOSE;
			}
		}
		alert.getButtonTypes().setAll(buttonList);
		final Label label = new Label(editorTextAreaLabel.getText());
		final TextArea textArea = new TextArea(editorTextArea.getText());
		textArea.setEditable(true);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		final GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		alert.getDialogPane().setContent(expContent);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.APPLY) {
			final String value = textArea.getText();
			editorTextArea.setText(value);
			alert.close();
		} else if (alert.getResult() == ButtonType.CLOSE) {
			alert.close();
		}
	}
}
