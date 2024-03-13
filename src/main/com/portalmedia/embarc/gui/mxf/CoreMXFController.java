package com.portalmedia.embarc.gui.mxf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import com.portalmedia.embarc.gui.ASCIIField;
import com.portalmedia.embarc.gui.DataFieldInfoAlert;
import com.portalmedia.embarc.gui.DropDownField;
import com.portalmedia.embarc.gui.IEditorField;
import com.portalmedia.embarc.gui.LabelField;
import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.RFC5646LanguageTags;
import com.portalmedia.embarc.gui.model.MXFSelectedFilesSummary;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.StringMetadataColumn;
import com.portalmedia.embarc.parser.mxf.AudioTrackLayoutValues;
import com.portalmedia.embarc.parser.mxf.DeviceSetHelper;
import com.portalmedia.embarc.parser.mxf.IdentifierSetHelper;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.parser.mxf.MXFColumnHelpText;
import com.portalmedia.embarc.parser.mxf.MXFSection;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import tv.amwa.maj.exception.PropertyNotPresentException;
import tv.amwa.maj.model.impl.AS07CoreDMSDeviceObjectsImpl;
import tv.amwa.maj.model.impl.AS07DMSIdentifierSetImpl;

/**
 * UI component that sorts, displays, and allows editing of metadata
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-07-02
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CoreMXFController extends AnchorPane {
	@FXML
	private Label sectionLabel;
	@FXML
	private Label selectedFilesLabel;
	@FXML
	private VBox editableFieldsVBox;
	@FXML
	private Text editingSummary;
	@FXML
	private Button applyChangesButton;
	@FXML
	private Button discardChangesButton;

	private HashSet<IEditorField> fields;
	private IntegerProperty editedFieldsCount = new SimpleIntegerProperty(0);

	public CoreMXFController() {
		ControllerMediatorMXF.getInstance().registerCoreMXFController(this);
		fields = new HashSet<>();
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CoreMXFView.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}

		editedFieldsCount.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> obs, Number ov, Number nv) {
				final String[] numFiles = selectedFilesLabel.getText().split(" ");
				if (nv.intValue() == 0) {
					editingSummary.setText("0 edited fields");
					return;
				}
				String filesString = " files";
				if ("1".equals(numFiles[0])) {
					filesString = " file";
				}
				String text = nv.intValue() + (nv.intValue() == 1 ? " edit in " : " edits in ") + numFiles[0] + filesString;
				editingSummary.setText(text);
			}
		});

		applyChangesButton.setOnAction(e -> {
			if (editedFieldsCount.get() == 0) {
				showAlert("", "There are no edits to apply.");
				return;
			}
			final HashMap<MXFColumn, MetadataColumnDef> changedValsNew = new LinkedHashMap<MXFColumn, MetadataColumnDef>();
			for (final IEditorField field : fields) {
				if (field.valueChanged()) {
					changedValsNew.put(field.getMXFColumn(), new StringMetadataColumn(field.getMXFColumn(), field.getValue()));
				}
			}
			ControllerMediatorMXF.getInstance().updateChangedValues(changedValsNew);
		});

		discardChangesButton.setOnAction(e -> {
			if (editedFieldsCount.get() == 0) {
				showAlert("", "There are no edits to discard.");
			} else {
				showConfirmation("Are you sure?", "Press OK to discard current changes. Press cancel to keep changes.");
			}
		});
	}

	public void setTitle(String title) {
		sectionLabel.setText(title);
	}

	private void calculateEditedFields() {
		int count = 0;
		for (final IEditorField field : fields) {
			if (field.valueChanged()) {
				count++;
			}
		}
		editedFieldsCount.set(count);
	}

	private void setNumberOfSelectedFiles(int num) {
		selectedFilesLabel.setText(Integer.toString(num) + " file" + (num > 1 ? "s " : " ") + "selected");
	}
	
	public void setSection(boolean resetValues) {
		if (resetValues) {
			for (final IEditorField field : fields) {
				field.resetValueChanged();
			}
			editableFieldsVBox.getChildren().clear();
			fields.clear();
		}

		final MXFSelectedFilesSummary summary = ControllerMediatorMXF.getInstance().getSelectedFilesSummary();

		if (summary.getFilesAreMissingAS07CoreDMSFramework()) {
			// TODO: prevent editing?
		}

		for (final MXFColumn col : MXFColumn.values()) {
			if (col.getSection() != MXFSection.CORE) continue;

			if (col == MXFColumn.AS_07_Core_DMS_Identifiers) {
				createIdentifiersDisplay(summary, col);
				continue;
			}
			if (col == MXFColumn.AS_07_Core_DMS_Devices) {
				createDevicesDisplay(summary, col);
				continue;
			}
			if (col == MXFColumn.AS_07_Core_DMS_ShimName) {
				HashMap<String, String> vals = new HashMap<String, String>();
				vals.put("RDD 48 Baseband Shim", "RDD 48 Baseband Shim");
				vals.put("", "");
				setDropDownField(col, summary, vals.values());
				continue;
			}
			if (col == MXFColumn.AS_07_Core_DMS_Captions) {
				HashMap<String, String> vals = new HashMap<String, String>();
				vals.put("Y", "Y");
				vals.put("N", "N");
				setDropDownField(col, summary, vals.values());
				continue;
			}
			if (col == MXFColumn.AS_07_Core_DMS_AudioTrackPrimaryLanguage) {
				RFC5646LanguageTags langTags = new RFC5646LanguageTags();
				setDropDownField(col, summary, langTags.getTreeMap().values()); // tree map orders alphabetically
				continue;
			}
			if (col == MXFColumn.AS_07_Core_DMS_AudioTrackSecondaryLanguage) {
				RFC5646LanguageTags langTags = new RFC5646LanguageTags();
				setDropDownField(col, summary, langTags.getTreeMap().values()); // tree map orders alphabetically
				continue;
			}
			if (col == MXFColumn.AS_07_Core_DMS_AudioTrackLayout) {
				AudioTrackLayoutValues vals = new AudioTrackLayoutValues();
				Collection<String> strVals = vals.getDescriptions();
				Set<String> set = new LinkedHashSet<>(strVals); // gets rid of duplicates
				setDropDownField(col, summary, set);
				continue;
			}

			// Default to ascii field
			final ASCIIField field = new ASCIIField();
			field.setMXFColumn(col);
			field.setVisible(true);
			String label = col.getDisplayName();
			if (col.isRequired()) label += " *";
			field.setLabel(label, MXFColumnHelpText.getInstance().getHelpText(col));
			MetadataColumnDef columnDef = summary.getCoreData().get(col);
			if (columnDef != null && columnDef.getCurrentValue() != null) {
				field.setValue(columnDef.getCurrentValue());
			} else {
				field.setValue("");
			}
			field.setEditable(col.getEditable());
			field.managedProperty().bind(field.visibleProperty());
			field.setPopoutIcon();
			field.setMXFMissingRequiredFieldRules();
			field.setMaxHeight(50);
			field.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> obs, String ov, String nv) {
					calculateEditedFields();
				}
			});
			fields.add(field);
			AnchorPane.setLeftAnchor(field, 0.00);
			AnchorPane.setRightAnchor(field, 0.00);
			editableFieldsVBox.getChildren().add(field);
		}

		setNumberOfSelectedFiles(summary.getFileCount());
		calculateEditedFields();
	}

	private void setDropDownField(MXFColumn col, MXFSelectedFilesSummary summary, Collection<String> strVals) {
		final DropDownField dropDownField = new DropDownField();
		dropDownField.setComboBoxValues(strVals);
		dropDownField.setEditable(false);
		dropDownField.setMXFColumn(col);
		dropDownField.setVisible(true);
		String label = col.getDisplayName();
		if (col.isRequired()) {
			label += " *";
		}
		dropDownField.setLabel(label, MXFColumnHelpText.getInstance().getHelpText(col));
		MetadataColumnDef columnDef = summary.getCoreData().get(col);
		if (columnDef != null) {
			dropDownField.setValue(columnDef.getCurrentValue());
		} else {
			dropDownField.setValue("");
		}
		dropDownField.managedProperty().bind(dropDownField.visibleProperty());
		dropDownField.getComboBoxField().getSelectionModel().selectedItemProperty().addListener((ops, ov, nv) -> {
			calculateEditedFields();
		});
		fields.add(dropDownField);
		AnchorPane.setLeftAnchor(dropDownField, 0.00);
		AnchorPane.setRightAnchor(dropDownField, 0.00);
		editableFieldsVBox.getChildren().add(dropDownField);
	}
	
	private void createDevicesDisplay(MXFSelectedFilesSummary summary, MXFColumn col) {
		DeviceSetHelper deviceSetHelper = new DeviceSetHelper();
		ArrayList<AS07CoreDMSDeviceObjectsImpl> devices = deviceSetHelper.createDeviceListFromString(summary.getCoreData().get(col).getCurrentValue());

		String label = "Devices";
		if (col.isRequired()) {
			label += " *";
		}

		HBox hbox = new HBox();
		HBox labelIconHbox = new HBox();
		labelIconHbox.setSpacing(5);
		Label devicesLabel = new Label(label);
		HBox iconHbox = new HBox();
		labelIconHbox.getChildren().addAll(devicesLabel, iconHbox);
		FontAwesomeIconView infoIcon = new FontAwesomeIconView();
		infoIcon.setGlyphName("INFO_CIRCLE");
		infoIcon.getStyleClass().add("popout-icon");
		infoIcon.setSize("12px");
		iconHbox.idProperty().set("editorTextFieldLabelInfoIcon");
		iconHbox.setFocusTraversable(true);
		iconHbox.getChildren().add(infoIcon);
		iconHbox.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.SPACE) {
				DataFieldInfoAlert.showFieldInfoAlert("Devices", MXFColumnHelpText.getInstance().getHelpText(col));
			}
		});
		iconHbox.setOnMouseClicked(event -> {
			DataFieldInfoAlert.showFieldInfoAlert("Devices", MXFColumnHelpText.getInstance().getHelpText(col));
		});
		iconHbox.setAccessibleRole(AccessibleRole.BUTTON);
		iconHbox.setAccessibleText("Open modal with Devices specification.");
		hbox.getChildren().addAll(labelIconHbox);
		
		labelIconHbox.setPrefWidth(285.0);
		labelIconHbox.setMaxWidth(285.0);
		labelIconHbox.setMinWidth(100.0);
		editableFieldsVBox.getChildren().add(hbox);
		VBox vbox = new VBox();
		vbox.setSpacing(5.0);
		hbox.getChildren().add(vbox);
		LabelField field = new LabelField();
		field.setMXFColumn(col);
		field.setValue(summary.getCoreData().get(col).getCurrentValue());
		fields.add(field);
		if (devices.size() == 0) {
			vbox.getChildren().add(new Label("No Devices"));
			Label newDeviceLabel = createNewDeviceLabel(devices, field);
			newDeviceLabel.setFocusTraversable(true);
			vbox.getChildren().add(newDeviceLabel);
			return;
		}
		for (int i = 0; i < devices.size(); i++) {
			AS07CoreDMSDeviceObjectsImpl device = devices.get(i);
			String manu = "";
			String type = "";
			try {
				manu = device.getManufacturer();
			} catch(PropertyNotPresentException pex) {}
			try {
				type = device.getDeviceType();
			} catch(PropertyNotPresentException pex) {}
			Label deviceLabel = new Label(manu + " " + type);
			deviceLabel.setPrefWidth(306.0);
			deviceLabel.setMaxWidth(306.0);
			deviceLabel.setMinWidth(100.0);
			deviceLabel.setFocusTraversable(true);
			HBox iconBox = new HBox();
			FontAwesomeIconView popoutIcon = new FontAwesomeIconView(FontAwesomeIcon.EXTERNAL_LINK);
			popoutIcon.setSize("16px");
			popoutIcon.setStyleClass("popout-icon");
			popoutIcon.setVisible(true);
			iconBox.setAccessibleText("Open modal to edit or delete " + deviceLabel.getText() + " device");
			iconBox.setAccessibleRole(AccessibleRole.BUTTON);
			iconBox.getChildren().add(popoutIcon);
			iconBox.setFocusTraversable(true);
			iconBox.idProperty().set("popoutIconContainer");
			HBox labelPopoutHbox = new HBox();
			labelPopoutHbox.getChildren().addAll(deviceLabel, iconBox);
			vbox.getChildren().add(labelPopoutHbox);
			iconBox.setOnKeyPressed(e -> {
				if (e.getCode() == KeyCode.SPACE) {
					devicesPopout(device, devices, field, deviceSetHelper);
				}
			});
			iconBox.setOnMouseClicked(e -> {
				devicesPopout(device, devices, field, deviceSetHelper);
			});
		}
		Label newDeviceLabel = createNewDeviceLabel(devices, field);
		newDeviceLabel.setFocusTraversable(true);
		vbox.getChildren().add(newDeviceLabel);
	}

	private Label createNewDeviceLabel(ArrayList<AS07CoreDMSDeviceObjectsImpl> devices, LabelField field) {
		Label newDeviceLabel = new Label("Add New Device");
		newDeviceLabel.setAccessibleRole(AccessibleRole.BUTTON);
		FontAwesomeIconView plusCircle = new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
		plusCircle.setStyleClass("popout-icon");
		newDeviceLabel.setGraphic(plusCircle);
		newDeviceLabel.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.SPACE) {
				newDevice(devices, field);
			}
		});
		newDeviceLabel.setOnMouseClicked(e -> {
			newDevice(devices, field);
		});
		return newDeviceLabel;
	}

	private void createIdentifiersDisplay(MXFSelectedFilesSummary summary, MXFColumn col) {
		IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
		ArrayList<AS07DMSIdentifierSetImpl> identifiers = idSetHelper.createIdentifierListFromString(summary.getCoreData().get(col).getCurrentValue());

		String label = "Identifiers";
		if (col.isRequired()) {
			label += " *";
		}

		HBox hbox = new HBox();
		HBox labelIconHbox = new HBox();
		labelIconHbox.setSpacing(5);
		Label identifierLabel = new Label(label);
		HBox iconHbox = new HBox();
		labelIconHbox.getChildren().addAll(identifierLabel, iconHbox);
		FontAwesomeIconView infoIcon = new FontAwesomeIconView();
		infoIcon.setGlyphName("INFO_CIRCLE");
		infoIcon.getStyleClass().add("popout-icon");
		infoIcon.setSize("12px");
		iconHbox.idProperty().set("editorTextFieldLabelInfoIcon");
		iconHbox.setFocusTraversable(true);
		iconHbox.getChildren().add(infoIcon);
		iconHbox.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.SPACE) {
				DataFieldInfoAlert.showFieldInfoAlert("Identifiers", MXFColumnHelpText.getInstance().getHelpText(col));
			}
		});
		iconHbox.setOnMouseClicked(event -> {
			DataFieldInfoAlert.showFieldInfoAlert("Identifiers", MXFColumnHelpText.getInstance().getHelpText(col));
		});
		iconHbox.setAccessibleRole(AccessibleRole.BUTTON);
		iconHbox.setAccessibleText("Open modal with Identifiers specification.");
		hbox.getChildren().addAll(labelIconHbox);

		labelIconHbox.setPrefWidth(285.0);
		labelIconHbox.setMaxWidth(285.0);
		labelIconHbox.setMinWidth(100.0);
		editableFieldsVBox.getChildren().add(hbox);
		VBox vbox = new VBox();
		vbox.setSpacing(5.0);
		hbox.getChildren().add(vbox);
		LabelField field = new LabelField();
		field.setMXFColumn(col);
		field.setValue(summary.getCoreData().get(col).getCurrentValue());
		fields.add(field);
		if (identifiers.size() == 0) {
			vbox.getChildren().add(new Label("No Identifiers"));
			Label newIdentifierLabel = createNewIdentifierLabel(identifiers, field);
			newIdentifierLabel.setFocusTraversable(true);
			vbox.getChildren().add(newIdentifierLabel);
			return;
		}
		for (int i = 0; i < identifiers.size(); i++) {
			try {
				AS07DMSIdentifierSetImpl id = identifiers.get(i);
				String idValue = id.getIdentifierValue();
				String idRole = id.getIdentifierRole();
				Label idLabel = new Label(idValue +" ("+ idRole +")");
				idLabel.setPrefWidth(306.0);
				idLabel.setMaxWidth(306.0);
				idLabel.setFocusTraversable(true);
				HBox iconBox = new HBox();
				FontAwesomeIconView popoutIcon = new FontAwesomeIconView(FontAwesomeIcon.EXTERNAL_LINK);
				popoutIcon.setSize("16px");
				popoutIcon.setStyleClass("popout-icon");
				iconBox.setAccessibleText("Open modal to edit or delete " + idLabel.getText() + " identifier");
				iconBox.setAccessibleRole(AccessibleRole.BUTTON);
				iconBox.setFocusTraversable(true);
				iconBox.getChildren().add(popoutIcon);
				iconBox.idProperty().set("popoutIconContainer");
				HBox labelPopoutHbox = new HBox();
				labelPopoutHbox.getChildren().addAll(idLabel, iconBox);
				vbox.getChildren().add(labelPopoutHbox);
				iconBox.setOnKeyPressed(e -> {
					if (e.getCode() != KeyCode.SPACE) {
						return;
					}
					identifierPopout(id, identifiers, field, idSetHelper);
				});
				iconBox.setOnMouseClicked(e -> {
					identifierPopout(id, identifiers, field, idSetHelper);
				});
			} catch (PropertyNotPresentException e) {
				System.out.println("Property not found exception in createIdentifiersDisplay");
			}
		}

		Label newIdentifierLabel = createNewIdentifierLabel(identifiers, field);
		newIdentifierLabel.setFocusTraversable(true);
		vbox.getChildren().add(newIdentifierLabel);
	}

	private Label createNewIdentifierLabel(ArrayList<AS07DMSIdentifierSetImpl> identifiers, LabelField field) {
		Label newIdentifierLabel = new Label("Add New Identifier");
		newIdentifierLabel.setAccessibleRole(AccessibleRole.BUTTON);
		FontAwesomeIconView plusCircle = new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
		plusCircle.setStyleClass("popout-icon");
		newIdentifierLabel.setGraphic(plusCircle);
		newIdentifierLabel.setOnKeyPressed(e -> {
			if (e.getCode() != KeyCode.SPACE) {
				return;
			}
			newIdentifier(identifiers, field);
		});
		newIdentifierLabel.setOnMouseClicked(e -> {
			newIdentifier(identifiers, field);
		});
		return newIdentifierLabel;
	}

	private Alert createIdDevicePopup(String title) {
		final Alert alert = new Alert(AlertType.NONE);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(null);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(Main.getPrimaryStage());
		final ButtonType[] buttonList = new ButtonType[2];
		buttonList[0] = ButtonType.APPLY;
		buttonList[1] = ButtonType.CLOSE;
		alert.getButtonTypes().setAll(buttonList);
		return alert;
	}

	private TextArea createTextArea(String value) {
		final TextArea textArea = new TextArea(value);
		textArea.setEditable(true);
		textArea.setWrapText(true);
		textArea.setMaxWidth(550.0);
		textArea.setPrefWidth(550.0);
		textArea.setPrefHeight(50.0);
		textArea.setMinHeight(50.0);
		return textArea;
	}

	private Label createLabel(String name) {
		final Label label = new Label(name);
		label.setPadding(new Insets(10, 0, 0, 0));
		return label;
	}

	private Tooltip createTooltip(String text) {
		final Tooltip tt = new Tooltip(text);
		tt.setStyle("-fx-text-fill: white; -fx-font-size: 12px");
		tt.setPrefWidth(500);
		tt.setWrapText(true);
		tt.setAutoHide(false);
		return tt;
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
			setSection(true);
		}
	}
	
	private void identifierPopout(AS07DMSIdentifierSetImpl id, ArrayList<AS07DMSIdentifierSetImpl> identifiers, LabelField field, IdentifierSetHelper idSetHelper) {
		final Alert alert = createIdDevicePopup("Edit " + id.getIdentifierValue() +" ("+ id.getIdentifierRole() +")");

		final Label typeLabel = createLabel("Identifier Type");
		final TextArea typeTextArea = createTextArea(id.getIdentifierType());
		typeLabel.setTooltip(createTooltip("Identifier Type\n\nControlled vocabulary string value identifying the type of identifier: UUID - UUID encoded as a URN according to IETF RFC 4122; UMID - Unique Material Identifier (UMID) defined by SMPTE ST 330:2004, represented as a URN per ST 2029:2009; UL – Universal Label as defined by SMPTE ST 298:2009, represented as a URN per ST 2029:2009; Other –A value not included in the controlled list, including archive specific values."));

		final Label roleLabel = createLabel("Identifier Role");
		final TextArea roleTextArea = createTextArea(id.getIdentifierRole());
		roleLabel.setTooltip(createTooltip("Identifier Role\n\nControlled vocabulary string value identifying the role of identifier: Main (universally unique primary identifier for the entire RDD 48 file) Additional (additional, possibly local, identifier for the entire RDD 48 file. Additional identifiers are not required to be universally unique) GSP (universally unique identifier for GSP payload)"));

		final Label valueLabel = createLabel("Identifier Value");
		final TextArea valueTextArea = createTextArea(id.getIdentifierValue());
		valueLabel.setTooltip(createTooltip("Identifier Value\n\nIdentifier value."));

		final Label commentLabel = createLabel("Identifier Comment");
		final TextArea commentTextArea = createTextArea(id.getIdentifierComment());
		commentLabel.setTooltip(createTooltip("Identifier Comment\n\nIdentifier comment."));

		final GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(typeLabel, 0, 0);
		expContent.add(typeTextArea, 0, 1);
		expContent.add(roleLabel, 0, 2);
		expContent.add(roleTextArea, 0, 3);
		expContent.add(valueLabel, 0, 4);
		expContent.add(valueTextArea, 0, 5);
		expContent.add(commentLabel, 0, 6);
		expContent.add(commentTextArea, 0, 7);
		expContent.add(new Label(""), 0, 8);

		Button deleteButton = new Button("Delete Identifier");
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				identifiers.remove(id);
				field.setValue(idSetHelper.identifiersToString(identifiers));
				field.setValueChanged();
				calculateEditedFields();
				alert.close();
			}
		});
		expContent.add(deleteButton, 0, 9);

		alert.getDialogPane().setContent(expContent);

		alert.showAndWait();
		if (alert.getResult() == ButtonType.APPLY) {
			id.setIdentifierType(typeTextArea.getText());
			id.setIdentifierRole(roleTextArea.getText());
			id.setIdentifierValue(valueTextArea.getText());
			id.setIdentifierComment(commentTextArea.getText());
			field.setValue(idSetHelper.identifiersToString(identifiers));
			field.setValueChanged();
			calculateEditedFields();
			alert.close();
		} else if (alert.getResult() == ButtonType.CLOSE) {
			alert.close();
		}
	}
	
	private void newIdentifier(ArrayList<AS07DMSIdentifierSetImpl> identifiers, LabelField field) {
		Alert popup = createIdDevicePopup("Add Identifier");

		final Label typeLabel = createLabel("Identifier Type");
		final TextArea typeTextArea = createTextArea("");
		typeLabel.setTooltip(createTooltip("Identifier Type\n\nControlled vocabulary string value identifying the type of identifier: UUID - UUID encoded as a URN according to IETF RFC 4122; UMID - Unique Material Identifier (UMID) defined by SMPTE ST 330:2004, represented as a URN per ST 2029:2009; UL – Universal Label as defined by SMPTE ST 298:2009, represented as a URN per ST 2029:2009; Other –A value not included in the controlled list, including archive specific values."));

		final Label roleLabel = createLabel("Identifier Role");
		final TextArea roleTextArea = createTextArea("");
		roleLabel.setTooltip(createTooltip("Identifier Role\n\nControlled vocabulary string value identifying the role of identifier: Main (universally unique primary identifier for the entire RDD 48 file) Additional (additional, possibly local, identifier for the entire RDD 48 file. Additional identifiers are not required to be universally unique) GSP (universally unique identifier for GSP payload)"));

		final Label valueLabel = createLabel("Identifier Value");
		final TextArea valueTextArea = createTextArea("");
		valueLabel.setTooltip(createTooltip("Identifier Value\n\nIdentifier value."));

		final Label commentLabel = createLabel("Identifier Comment");
		final TextArea commentTextArea = createTextArea("");
		commentLabel.setTooltip(createTooltip("Identifier Comment\n\nIdentifier comment."));

		final GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(typeLabel, 0, 0);
		expContent.add(typeTextArea, 0, 1);
		expContent.add(roleLabel, 0, 2);
		expContent.add(roleTextArea, 0, 3);
		expContent.add(valueLabel, 0, 4);
		expContent.add(valueTextArea, 0, 5);
		expContent.add(commentLabel, 0, 6);
		expContent.add(commentTextArea, 0, 7);

		popup.getDialogPane().setContent(expContent);
		popup.showAndWait();

		if (popup.getResult() == ButtonType.APPLY) {
			AS07DMSIdentifierSetImpl newIdSet = new AS07DMSIdentifierSetImpl();
			newIdSet.setIdentifierType(typeTextArea.getText());
			newIdSet.setIdentifierRole(roleTextArea.getText());
			newIdSet.setIdentifierValue(valueTextArea.getText());
			newIdSet.setIdentifierComment(commentTextArea.getText());
			identifiers.add(newIdSet);
			IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
			field.setValue(idSetHelper.identifiersToString(identifiers));
			field.setValueChanged();
			calculateEditedFields();
			popup.close();
		} else if (popup.getResult() == ButtonType.CLOSE) {
			popup.close();
		}
	}

	private void devicesPopout(AS07CoreDMSDeviceObjectsImpl device, ArrayList<AS07CoreDMSDeviceObjectsImpl> devices, LabelField field, DeviceSetHelper deviceSetHelper) {
		Alert alert = createIdDevicePopup("Edit Device");

		final Label deviceTypeLabel = createLabel("Device Type");
		String type = "";
		try { type = device.getDeviceType(); }
		catch(PropertyNotPresentException pex) {}
		final TextArea deviceTypeTextArea = createTextArea(type);
		deviceTypeLabel.setTooltip(createTooltip("Device Type\n\nThe kind of device used to capture or create the content (as either a commonly known name or as a locally defined name; e.g., Radio-camera)"));

		final Label manufacturerLabel = createLabel("Manufacturer");
		String manu = "";
		try { manu = device.getManufacturer(); }
		catch(PropertyNotPresentException pex) {}
		final TextArea manufacturerTextArea = createTextArea(manu);
		manufacturerLabel.setTooltip(createTooltip("Manufacturer\n\nThe manufacturer or maker of the device"));

		final Label modelLabel = createLabel("Model");
		String model = "";
		try { model = device.getModel(); }
		catch(PropertyNotPresentException pex) {}
		final TextArea modelTextArea = createTextArea(model);
		modelLabel.setTooltip(createTooltip("Model\n\nIdentifies the device model used in capturing or generating the essence."));

		final Label serialNumberLabel = createLabel("Serial Number");
		String serial = "";
		try { serial = device.getSerialNumber(); }
		catch(PropertyNotPresentException pex) {}
		final TextArea serialNumberTextArea = createTextArea(serial);
		serialNumberLabel.setTooltip(createTooltip("Serial Number\n\nAlphanumeric serial number identifying the individual device"));

		final Label usageDescriptionLabel = createLabel("Usage Description");
		String usage = "";
		try { usage = device.getUsageDescription(); }
		catch(PropertyNotPresentException pex) {}
		final TextArea usageDescriptionTextArea = createTextArea(usage);
		usageDescriptionLabel.setTooltip(createTooltip("UsageDescription\n\nFree text description of the function or use of the device in the production of a specific content item"));

		final GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(deviceTypeLabel, 0, 0);
		expContent.add(deviceTypeTextArea, 0, 1);
		expContent.add(manufacturerLabel, 0, 2);
		expContent.add(manufacturerTextArea, 0, 3);
		expContent.add(modelLabel, 0, 4);
		expContent.add(modelTextArea, 0, 5);
		expContent.add(serialNumberLabel, 0, 6);
		expContent.add(serialNumberTextArea, 0, 7);
		expContent.add(usageDescriptionLabel, 0, 8);
		expContent.add(usageDescriptionTextArea, 0, 9);
		expContent.add(new Label(""), 0, 10);

		Button deleteButton = new Button("Delete Device");
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				devices.remove(device);
				field.setValue(deviceSetHelper.devicesToString(devices));
				field.setValueChanged();
				calculateEditedFields();
				alert.close();
			}
		});
		expContent.add(deleteButton, 0, 11);

		alert.getDialogPane().setContent(expContent);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.APPLY) {
			device.setDeviceType(deviceTypeTextArea.getText());
			device.setManufacturer(manufacturerTextArea.getText());
			device.setModel(modelTextArea.getText());
			device.setSerialNumber(serialNumberTextArea.getText());
			device.setUsageDescription(usageDescriptionTextArea.getText());
			field.setValue(deviceSetHelper.devicesToString(devices));
			field.setValueChanged();
			calculateEditedFields();
			alert.close();
		} else if (alert.getResult() == ButtonType.CLOSE) {
			alert.close();
		}
	}

	private void newDevice(ArrayList<AS07CoreDMSDeviceObjectsImpl> devices, LabelField field) {
		Alert popup = createIdDevicePopup("Add New Device");
		final Label deviceTypeLabel = createLabel("Device Type");
		final TextArea deviceTypeTextArea = createTextArea("");
		deviceTypeLabel.setTooltip(createTooltip("Device Type\n\nThe kind of device used to capture or create the content (as either a commonly known name or as a locally defined name; e.g., Radio-camera)"));

		final Label manufacturerLabel = createLabel("Manufacturer");
		final TextArea manufacturerTextArea = createTextArea("");
		manufacturerLabel.setTooltip(createTooltip("Manufacturer\n\nThe manufacturer or maker of the device"));

		final Label modelLabel = createLabel("Model");
		final TextArea modelTextArea = createTextArea("");
		modelLabel.setTooltip(createTooltip("Model\n\nIdentifies the device model used in capturing or generating the essence."));

		final Label serialNumberLabel = createLabel("Serial Number");
		final TextArea serialNumberTextArea = createTextArea("");
		serialNumberLabel.setTooltip(createTooltip("Serial Number\n\nAlphanumeric serial number identifying the individual device"));

		final Label usageDescriptionLabel = createLabel("Usage Description");
		final TextArea usageDescriptionTextArea = createTextArea("");
		usageDescriptionLabel.setTooltip(createTooltip("UsageDescription\n\nFree text description of the function or use of the device in the production of a specific content item"));

		final GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(deviceTypeLabel, 0, 0);
		expContent.add(deviceTypeTextArea, 0, 1);
		expContent.add(manufacturerLabel, 0, 2);
		expContent.add(manufacturerTextArea, 0, 3);
		expContent.add(modelLabel, 0, 4);
		expContent.add(modelTextArea, 0, 5);
		expContent.add(serialNumberLabel, 0, 6);
		expContent.add(serialNumberTextArea, 0, 7);
		expContent.add(usageDescriptionLabel, 0, 8);
		expContent.add(usageDescriptionTextArea, 0, 9);

		popup.getDialogPane().setContent(expContent);
		popup.showAndWait();

		if (popup.getResult() == ButtonType.APPLY) {
			AS07CoreDMSDeviceObjectsImpl newDevice = new AS07CoreDMSDeviceObjectsImpl();
			newDevice.setDeviceType(deviceTypeTextArea.getText());
			newDevice.setManufacturer(manufacturerTextArea.getText());
			newDevice.setModel(modelTextArea.getText());
			newDevice.setSerialNumber(serialNumberTextArea.getText());
			newDevice.setUsageDescription(usageDescriptionTextArea.getText());
			devices.add(newDevice);
			DeviceSetHelper deviceSetHelper = new DeviceSetHelper();
			field.setValue(deviceSetHelper.devicesToString(devices));
			field.setValueChanged();
			calculateEditedFields();
			popup.close();
		} else if (popup.getResult() == ButtonType.CLOSE) {
			popup.close();
		}
	}
}
