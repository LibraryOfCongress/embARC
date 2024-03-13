package com.portalmedia.embarc.gui.dpx;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.portalmedia.embarc.gui.ASCIIArea;
import com.portalmedia.embarc.gui.ASCIIField;
import com.portalmedia.embarc.gui.BorderValidityField;
import com.portalmedia.embarc.gui.DatePickerField;
import com.portalmedia.embarc.gui.DropDownField;
import com.portalmedia.embarc.gui.FloatField;
import com.portalmedia.embarc.gui.IEditorField;
import com.portalmedia.embarc.gui.IntegerField;
import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.PixelAspectRatioField;
import com.portalmedia.embarc.gui.model.SelectedFilesSummary;
import com.portalmedia.embarc.parser.DisplayType;
import com.portalmedia.embarc.parser.ImageElementDef;
import com.portalmedia.embarc.parser.SectionDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXColumnHelpText;
import com.portalmedia.embarc.parser.dpx.DPXImageElement;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;

/**
 * UI component that sorts, displays, and allows editing of metadata
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EditorForm extends AnchorPane {
	@FXML
	private Label sectionLabel;
	@FXML
	private Label selectedFilesLabel;
	@FXML
	private VBox sectionEditableFields;
	@FXML
	private VBox sectionNotEditableFields;
	@FXML
	private ScrollPane sectionEditableFieldsContainer;
	@FXML
	private ScrollPane sectionNotEditableFieldsContainer;
	@FXML
	private TitledPane editableFieldsAccordion;
	@FXML
	private TitledPane notEditableFieldsAccordion;
	@FXML
	private Accordion editorAccordion;
	@FXML
	private ComboBox subsectionDropDown;
	@FXML
	private HBox subsectionBox;
	@FXML
	private Button applyChangesButton;
	@FXML
	private Button discardChangesButton;
	@FXML
	private Label editingSummary;

	private SectionDef section;
	private ImageElementDef selectedSubsection;
	private HashSet<IEditorField> textFields;
	private IntegerProperty editedFieldsCount = new SimpleIntegerProperty(0);

	public EditorForm() {
		ControllerMediatorDPX.getInstance().registerEditorForm(this);
		textFields = new HashSet<>();
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EditorForm.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}

		selectedSubsection = DPXImageElement.IMAGE_ELEMENT_1;
		subsectionDropDown.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				if (t != null && !t.equals(t1)) {
					setVisibility(t1);
				}
			}
		});

		applyChangesButton.setOnAction(event -> {
			if (editedFieldsCount.get() == 0) {
				showAlert("", "There are no edits to apply.");
				return;
			}
			final HashMap<DPXColumn, String> changedValues = new HashMap<>();
			for (final IEditorField field : textFields) {
				if (field.getColumn().getEditable() && field.valueChanged()) {
					changedValues.put(field.getColumn(), field.getValue());
				}
			}
			ControllerMediatorDPX.getInstance().updateChangedValues(changedValues);
		});

		discardChangesButton.setOnAction(event -> {
			if (editedFieldsCount.get() == 0) {
				showAlert("", "There are no edits to discard.");
			} else {
				showConfirmation("Are you sure?", "Press OK to discard current changes. Press cancel to keep changes.");
			}
		});

		editedFieldsCount.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> obs, Number ov, Number nv) {
				final String[] numFiles = selectedFilesLabel.getText().split(" ");
				String filesString = " files";
				if ("1".equals(numFiles[0])) {
					filesString = " file";
				}
				if (nv.intValue() == 0) {
					editingSummary.setText("0 edited fields");
				} else if (nv.intValue() == 1) {
					editingSummary.setText(nv + " edit in " + numFiles[0] + filesString);
				} else {
					editingSummary.setText(nv + " edits in " + numFiles[0] + filesString);
				}
			}
		});

		notEditableFieldsAccordion.setAccessibleText("List of non editable fields");
		editableFieldsAccordion.setAccessibleText("List of editable fields");
		editingSummary.setFocusTraversable(true);
	}

	private void calculateEditedFields() {
		int count = 0;
		for (final IEditorField field : textFields) {
			if (field.valueChanged() && field.getColumn().getEditable()) {
				count++;
			}
		}
		editedFieldsCount.set(count);
	}

	public int getEditedFieldsCount() {
		return editedFieldsCount.get();
	}

	public void refreshValidation() {
		final SelectedFilesSummary summary = ControllerMediatorDPX.getInstance().getSelectedFilesSummary();

		for (final IEditorField tf : textFields) {
			if (summary == null) {
				tf.setInvalidRuleSets(null);
			}
			final Set<ValidationRuleSetEnum> invalidRules = summary.getRuleSetViolations(tf.getColumn());
			tf.setInvalidRuleSets(invalidRules);
		}
	}

	public void setEditedFieldsCount(int count) {
		if (editedFieldsCount != null) {
			editedFieldsCount.set(count);
		}
	}

	private void setNumberOfEditableFields(int num) {
		editableFieldsAccordion.setText(Integer.toString(num) + " Editable Field" + (num != 1 ? "s" : ""));
	}

	private void setNumberOfNotEditableFields(int num) {
		notEditableFieldsAccordion.setText(Integer.toString(num) + " Not Editable Field" + (num != 1 ? "s" : ""));
	}

	private void setNumberOfSelectedFiles(int num) {
		selectedFilesLabel.setText(Integer.toString(num) + " file" + (num != 1? "s " : " ") + "selected");
		selectedFilesLabel.setFocusTraversable(true);
	}

	public void setSection(SectionDef section, boolean resetValues) {
		if (resetValues) {
			for (final IEditorField field : textFields) {
				field.resetValueChanged();
			}
		}
		calculateEditedFields();
		this.section = section;
		final SelectedFilesSummary summary = ControllerMediatorDPX.getInstance().getSelectedFilesSummary();
		int editableCount = 0;
		int notEditableCount = 0;
		final List<String> subsections = new LinkedList<>();

		sectionNotEditableFields.getChildren().clear();
		sectionEditableFields.getChildren().clear();
		for (final DPXColumn c : DPXColumn.values()) {
			if (c.getSection() != this.section) {
				continue;
			}
			if (c.hasSubsection() && !subsections.contains(c.getSubsection().getDisplayName())) {
				subsections.add(c.getSubsection().getDisplayName());
			}

			boolean subsectionVisible = true;
			if (c.hasSubsection() && !selectedSubsection.getDisplayName().equals(c.getSubsection().getDisplayName())) {
				continue;
			}

			if (c.getDisplayType() == DisplayType.ASCII || c.getDisplayType() == DisplayType.DATEPICKER) {
				if (c.getDisplayName().equals("User Defined Data")) {
					final ASCIIArea area = new ASCIIArea();
					area.setColumn(c);
					area.setVisible(subsectionVisible);
					area.setValue(summary.getDisplayValues(c));
					area.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
					area.setEditable(c.getEditable());
					area.setInvalidRuleSets(summary.getRuleSetViolations(c));
					area.managedProperty().bind(area.visibleProperty());
					area.setPopoutIcon();

					area.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> obs, String ov, String nv) {
							calculateEditedFields();
						}
					});

					textFields.add(area);

					AnchorPane.setLeftAnchor(area, 0.00);
					AnchorPane.setRightAnchor(area, 0.00);

					if (c.getEditable()) {
						sectionEditableFields.getChildren().add(area);
						editableCount++;
					} else {
						sectionNotEditableFields.getChildren().add(area);
						notEditableCount++;
					}
				} else {
					final ASCIIField field = new ASCIIField();
					field.setColumn(c);
					field.setVisible(subsectionVisible);
					field.setValue(summary.getDisplayValues(c));
					field.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
					field.setEditable(c.getEditable());
					field.setInvalidRuleSets(summary.getRuleSetViolations(c));
					field.managedProperty().bind(field.visibleProperty());
					if (c.getLength() >= 20) {
						field.setPopoutIcon();
					}

					field.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> obs, String ov, String nv) {
							calculateEditedFields();
						}
					});

					textFields.add(field);

					AnchorPane.setLeftAnchor(field, 0.00);
					AnchorPane.setRightAnchor(field, 0.00);

					if (c.getEditable()) {
						sectionEditableFields.getChildren().add(field);
						editableCount++;
					} else {
						sectionNotEditableFields.getChildren().add(field);
						notEditableCount++;
					}
				}
			} else if (c.getDisplayType() == DisplayType.INTEGER) {
				final IntegerField field = new IntegerField();
				field.setColumn(c);
				field.setVisible(subsectionVisible);
				field.setValue(summary.getDisplayValues(c));
				field.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
				field.setEditable(c.getEditable());
				field.setInvalidRuleSets(summary.getRuleSetViolations(c));
				field.managedProperty().bind(field.visibleProperty());

				field.textProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> obs, String ov, String nv) {
						calculateEditedFields();
					}
				});

				textFields.add(field);

				AnchorPane.setLeftAnchor(field, 0.00);
				AnchorPane.setRightAnchor(field, 0.00);

				if (c.getEditable()) {
					sectionEditableFields.getChildren().add(field);
					editableCount++;
				} else {
					sectionNotEditableFields.getChildren().add(field);
					notEditableCount++;
				}
			} else if (c.getDisplayType() == DisplayType.FLOAT) {
				final FloatField textField = new FloatField();
				textField.setColumn(c);
				textField.setValue(summary.getDisplayValues(c));
				textField.setVisible(subsectionVisible);
				textField.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
				textField.setEditable(c.getEditable());
				textField.setInvalidRuleSets(summary.getRuleSetViolations(c));
				textField.managedProperty().bind(textField.visibleProperty());

				textField.textProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> obs, String ov, String nv) {
						calculateEditedFields();
					}
				});

				textFields.add(textField);

				AnchorPane.setLeftAnchor(textField, 0.00);
				AnchorPane.setRightAnchor(textField, 0.00);

				if (c.getEditable()) {
					sectionEditableFields.getChildren().add(textField);
					editableCount++;
				} else {
					sectionNotEditableFields.getChildren().add(textField);
					notEditableCount++;
				}
			} else if (c.getDisplayType() == DisplayType.BORDER_VALIDITY) {
				final BorderValidityField textField = new BorderValidityField();
				textField.setColumn(c);
				textField.setVisible(subsectionVisible);
				textField.setValue(summary.getDisplayValues(c));
				textField.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
				textField.setEditable(c.getEditable());
				textField.setInvalidRuleSets(summary.getRuleSetViolations(c));
				textField.managedProperty().bind(textField.visibleProperty());

				textFields.add(textField);

				AnchorPane.setLeftAnchor(textField, 0.00);
				AnchorPane.setRightAnchor(textField, 0.00);

				if (c.getEditable()) {
					sectionEditableFields.getChildren().add(textField);
					editableCount++;
				} else {
					sectionNotEditableFields.getChildren().add(textField);
					notEditableCount++;
				}
			} else if (c.getDisplayType() == DisplayType.PIXEL_ASPECT_RATIO) {
				final PixelAspectRatioField textField = new PixelAspectRatioField();
				textField.setColumn(c);
				textField.setVisible(subsectionVisible);
				textField.setValue(summary.getDisplayValues(c));
				textField.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
				textField.setEditable(c.getEditable());
				textField.setInvalidRuleSets(summary.getRuleSetViolations(c));
				textField.managedProperty().bind(textField.visibleProperty());

				textFields.add(textField);

				AnchorPane.setLeftAnchor(textField, 0.00);
				AnchorPane.setRightAnchor(textField, 0.00);

				if (c.getEditable()) {
					sectionEditableFields.getChildren().add(textField);
					editableCount++;
				} else {
					sectionNotEditableFields.getChildren().add(textField);
					notEditableCount++;
				}
			}

			sectionNotEditableFieldsContainer.setFitToWidth(true);
			sectionEditableFieldsContainer.setFitToWidth(true);
		}
		editorAccordion.setExpandedPane(editableFieldsAccordion);
		editableFieldsAccordion.setDisable(false);
		if (notEditableCount == 0) {
			notEditableFieldsAccordion.setVisible(false);
		} else if (editableCount == 0) {
			// TODO: fix style bug when there are no editable fields
			editableFieldsAccordion.setVisible(true);
//			editableFieldsAccordion.setDisable(true);
			editorAccordion.setExpandedPane(notEditableFieldsAccordion);
		}

		if (subsections.size() > 0) {
			for (final String s : subsections) {
				subsectionDropDown.getItems().add(s);
			}
			subsectionDropDown.getSelectionModel().selectFirst();
		} else {
			subsectionBox.setVisible(false);
			subsectionBox.managedProperty().bind(subsectionBox.visibleProperty());
		}

		setNumberOfSelectedFiles(summary.getFileCount());
		setNumberOfEditableFields(editableCount);
		setNumberOfNotEditableFields(notEditableCount);
	}

	public void setSectionSecond(SectionDef section) {
		this.section = section;
		final SelectedFilesSummary summary = ControllerMediatorDPX.getInstance().getSelectedFilesSummary();
		int editableCount = 0;
		int notEditableCount = 0;

		sectionNotEditableFields.getChildren().clear();
		sectionEditableFields.getChildren().clear();
		for (final DPXColumn c : DPXColumn.values()) {
			if (c.getSection() != this.section) {
				continue;
			}

			boolean subsectionVisible = true;
			if (!selectedSubsection.getDisplayName().equals(c.getSubsection().getDisplayName())) {
				continue;
			}

			if (c.getDisplayType() == DisplayType.ASCII) {
				// TODO: Check for display type
				final ASCIIField field = new ASCIIField();
				field.setColumn(c);
				field.setVisible(subsectionVisible);
				field.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
				field.setValue(summary.getDisplayValues(c));
				field.setEditable(c.getEditable());
				field.setInvalidRuleSets(summary.getRuleSetViolations(c));
				field.managedProperty().bind(field.visibleProperty());
				textFields.add(field);

				AnchorPane.setLeftAnchor(field, 0.00);
				AnchorPane.setRightAnchor(field, 0.00);

				if (c.getEditable()) {
					sectionEditableFields.getChildren().add(field);
					editableCount++;
				} else {
					sectionNotEditableFields.getChildren().add(field);
					notEditableCount++;
				}
			} else if (c.getDisplayType() == DisplayType.INTEGER) {
				final IntegerField field = new IntegerField();
				field.setColumn(c);
				field.setVisible(subsectionVisible);
				field.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
				field.setValue(summary.getDisplayValues(c));
				field.setEditable(c.getEditable());
				field.setInvalidRuleSets(summary.getRuleSetViolations(c));
				field.managedProperty().bind(field.visibleProperty());
				textFields.add(field);

				AnchorPane.setLeftAnchor(field, 0.00);
				AnchorPane.setRightAnchor(field, 0.00);

				if (c.getEditable()) {
					sectionEditableFields.getChildren().add(field);
					editableCount++;
				} else {
					sectionNotEditableFields.getChildren().add(field);
					notEditableCount++;
				}
			} else if (c.getDisplayType() == DisplayType.DROPDOWN) {
				final DropDownField textField = new DropDownField();
				textField.setColumn(c);
				textField.setVisible(subsectionVisible);
				textField.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
				textField.setValue(summary.getDisplayValues(c));
				textField.setEditable(c.getEditable());
				textField.setInvalidRuleSets(summary.getRuleSetViolations(c));
				textField.managedProperty().bind(textField.visibleProperty());
				textFields.add(textField);

				AnchorPane.setLeftAnchor(textField, 0.00);
				AnchorPane.setRightAnchor(textField, 0.00);

				if (c.getEditable()) {
					sectionEditableFields.getChildren().add(textField);
					editableCount++;
				} else {
					sectionNotEditableFields.getChildren().add(textField);
					notEditableCount++;
				}
			} else if (c.getDisplayType() == DisplayType.DATEPICKER) {
				final DatePickerField textField = new DatePickerField();
				textField.setColumn(c);
				textField.setVisible(subsectionVisible);
				textField.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
				textField.setValue(summary.getDisplayValues(c));
				textField.setEditable(c.getEditable());
				textField.setInvalidRuleSets(summary.getRuleSetViolations(c));
				textField.managedProperty().bind(textField.visibleProperty());
				textFields.add(textField);

				AnchorPane.setLeftAnchor(textField, 0.00);
				AnchorPane.setRightAnchor(textField, 0.00);

				if (c.getEditable()) {
					sectionEditableFields.getChildren().add(textField);
					editableCount++;
				} else {
					sectionNotEditableFields.getChildren().add(textField);
					notEditableCount++;
				}
			} else if (c.getDisplayType() == DisplayType.FLOAT) {
				final FloatField textField = new FloatField();
				textField.setColumn(c);
				textField.setVisible(subsectionVisible);
				textField.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
				textField.setValue(summary.getDisplayValues(c));
				textField.setEditable(c.getEditable());
				textField.setInvalidRuleSets(summary.getRuleSetViolations(c));
				textField.managedProperty().bind(textField.visibleProperty());
				textFields.add(textField);

				AnchorPane.setLeftAnchor(textField, 0.00);
				AnchorPane.setRightAnchor(textField, 0.00);

				if (c.getEditable()) {
					sectionEditableFields.getChildren().add(textField);
					editableCount++;
				} else {
					sectionNotEditableFields.getChildren().add(textField);
					notEditableCount++;
				}
			} else if (c.getDisplayType() == DisplayType.BORDER_VALIDITY) {
				final BorderValidityField textField = new BorderValidityField();
				textField.setColumn(c);
				textField.setVisible(subsectionVisible);
				textField.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
				textField.setValue(summary.getDisplayValues(c));
				textField.setEditable(c.getEditable());
				textField.setInvalidRuleSets(summary.getRuleSetViolations(c));
				textField.managedProperty().bind(textField.visibleProperty());
				textFields.add(textField);

				AnchorPane.setLeftAnchor(textField, 0.00);
				AnchorPane.setRightAnchor(textField, 0.00);

				if (c.getEditable()) {
					sectionEditableFields.getChildren().add(textField);
					editableCount++;
				} else {
					sectionNotEditableFields.getChildren().add(textField);
					notEditableCount++;
				}
			} else if (c.getDisplayType() == DisplayType.PIXEL_ASPECT_RATIO) {
				final PixelAspectRatioField textField = new PixelAspectRatioField();
				textField.setColumn(c);
				textField.setVisible(subsectionVisible);
				textField.setLabel(c.getDisplayName(), DPXColumnHelpText.getInstance().getHelpText(c));
				textField.setValue(summary.getDisplayValues(c));
				textField.setEditable(c.getEditable());
				textField.setInvalidRuleSets(summary.getRuleSetViolations(c));
				textField.managedProperty().bind(textField.visibleProperty());
				textFields.add(textField);

				AnchorPane.setLeftAnchor(textField, 0.00);
				AnchorPane.setRightAnchor(textField, 0.00);

				if (c.getEditable()) {
					sectionEditableFields.getChildren().add(textField);
					editableCount++;
				} else  {
					sectionNotEditableFields.getChildren().add(textField);
					notEditableCount++;
				}
			}

			sectionNotEditableFieldsContainer.setFitToWidth(true);
			sectionEditableFieldsContainer.setFitToWidth(true);
		}
		editorAccordion.setExpandedPane(editableFieldsAccordion);
		if (notEditableCount == 0) {
			notEditableFieldsAccordion.setVisible(false);
		} else if (editableCount == 0) {
			// TODO: fix style bug when there are no editable fields
			editableFieldsAccordion.setVisible(false);
			editorAccordion.setExpandedPane(notEditableFieldsAccordion);
		}

		setNumberOfSelectedFiles(summary.getFileCount());
		setNumberOfEditableFields(editableCount);
		setNumberOfNotEditableFields(notEditableCount);
	}

	public void setTitle(String title) {
		sectionLabel.setText(title);
		sectionLabel.setAccessibleText(title);
		sectionLabel.setFocusTraversable(true);
	}

	public void setVisibility(String subsectionDisplayName) {
		for (final ImageElementDef ss : DPXImageElement.values()) {
			if (subsectionDisplayName.equals(ss.getDisplayName())) {
				selectedSubsection = ss;
				setSectionSecond(section);
				break;
			}
		}
	}
	
	private void showAlert(String modalTitle, String alertText) {
		final Alert alert = new Alert(AlertType.NONE);
		alert.setTitle(modalTitle);
		alert.setHeaderText(null);
		alert.setContentText(null);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(Main.getPrimaryStage());

		final ButtonType[] buttonList = new ButtonType[1];
		buttonList[0] = ButtonType.CLOSE;
		alert.getButtonTypes().setAll(buttonList);

		final GridPane grid = new GridPane();
		final Text text = new Text(alertText);
		text.setStyle("-fx-font-size: 14.0");
		text.setFocusTraversable(true);
		grid.add(text, 0, 0);
		grid.setVgap(15);
		alert.getDialogPane().setContent(grid);

		alert.showAndWait();
		if (alert.getResult() == ButtonType.CLOSE) {
			alert.close();
		}
	}
	
	private void showConfirmation(String modalTitle, String confirmationText) {
		final Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(modalTitle);
		alert.setHeaderText(null);
		alert.setContentText(null);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(Main.getPrimaryStage());

		final ButtonType[] buttonList = new ButtonType[2];
		buttonList[0] = ButtonType.CANCEL;
		buttonList[1] = ButtonType.OK;
		alert.getButtonTypes().setAll(buttonList);

		final GridPane grid = new GridPane();
		final Text text = new Text(confirmationText);
		text.setStyle("-fx-font-size: 14.0");
		text.setFocusTraversable(true);
		grid.add(text, 0, 0);
		grid.setVgap(15);
		alert.getDialogPane().setContent(grid);

		alert.showAndWait();
		if (alert.getResult() == ButtonType.CANCEL) {
			alert.close();
		} else if (alert.getResult() == ButtonType.OK) {
			setSection(section, true);
		}
	}
}
