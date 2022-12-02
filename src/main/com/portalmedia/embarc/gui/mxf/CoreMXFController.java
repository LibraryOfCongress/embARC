package com.portalmedia.embarc.gui.mxf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import com.portalmedia.embarc.gui.ASCIIArea;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
	private VBox dataContainer;
	@FXML
	private Label editFormLabel;
	@FXML
	private Label selectedFilesLabel;
	@FXML
	private Button toggleEditButton;
	@FXML
	private Button applyChangesButton;
	@FXML
	private Label editingSummary;
	@FXML
	private VBox editableFieldsVBox;

	private HashSet<IEditorField> fields;
	private IntegerProperty editedFieldsCount = new SimpleIntegerProperty(0);
	private int numSelected;

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

		ControllerMediatorMXF.getInstance().isEditingProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue o, Object ov, Object nv) {
				setIsEditingMode((Boolean) nv);
			}
		});

		setIsEditingMode(ControllerMediatorMXF.getInstance().isEditingProperty().get());

		toggleEditButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				final Boolean isEditing = ControllerMediatorMXF.getInstance().isEditingProperty().get();
				ControllerMediatorMXF.getInstance().isEditingProperty().set(!isEditing);
			}
		});

		editedFieldsCount.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> obs, Number ov, Number nv) {
				if (nv.intValue() == 0) {
					editingSummary.setText("");
					applyChangesButton.setDisable(true);
					applyChangesButton.setStyle("-fx-background-color: #7EFFFE;");
					return;
				}
				String text = "Change ";
				if (nv.intValue() == 1) text += nv + " field in ";
				else text += nv + " fields in ";
				if (numSelected == 1) text += numSelected + " file?";
				else text += numSelected + " files?";
				editingSummary.setText(text);
				applyChangesButton.setDisable(false);
				applyChangesButton.setStyle("-fx-background-color: #AED581");
			}
		});

		applyChangesButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				final HashMap<MXFColumn, MetadataColumnDef> changedValsNew = new LinkedHashMap<MXFColumn, MetadataColumnDef>();
				for (final IEditorField field : fields) {
					if (field.valueChanged()) {
						changedValsNew.put(field.getMXFColumn(), new StringMetadataColumn(field.getMXFColumn(), field.getValue()));
					}
				}
				ControllerMediatorMXF.getInstance().updateChangedValues(changedValsNew);
			}
		});

		editableFieldsVBox.setPadding(new Insets(10, 10, 10, 10));
	}

	public void setTitle(String title) {
		editFormLabel.setText(title);
	}

	private void calculateEditedFields() {
		int count = 0;
		for (final IEditorField field : fields) {
			if (field.valueChanged()) count++;
		}
		editedFieldsCount.set(count);
	}

	public int getEditedFieldsCount() {
		return editedFieldsCount.get();
	}

	public void setEditedFieldsCount(int count) {
		if (editedFieldsCount != null) editedFieldsCount.set(count);
	}

	private void setNumberOfSelectedFiles(int num) {
		numSelected = num;
		if (num == 1) {
			selectedFilesLabel.setText(Integer.toString(num) + " file selected");
		} else {
			selectedFilesLabel.setText(Integer.toString(num) + " files selected");
		}
	}

	public void setSection(Boolean resetValues) {
		if (resetValues) {}

		final MXFSelectedFilesSummary summary = ControllerMediatorMXF.getInstance().getSelectedFilesSummary();

		if (summary.getFilesAreMissingAS07CoreDMSFramework()) {
			toggleEditButton.setDisable(true);
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

			// Default to ascii area
			final ASCIIArea area = new ASCIIArea();
			area.setMXFColumn(col);
			area.setVisible(true);
			String label = col.getDisplayName();
			if (col.isRequired()) label += " *";
			area.setLabel(label, MXFColumnHelpText.getInstance().getHelpText(col));
			MetadataColumnDef columnDef = summary.getCoreData().get(col);
			if (columnDef != null && columnDef.getCurrentValue() != null) {
				area.setValue(columnDef.getCurrentValue());
			} else {
				area.setValue("");
			}
			area.setEditable(col.getEditable());
			area.managedProperty().bind(area.visibleProperty());
			area.setPopoutIcon();
			area.setMaxHeight(50);
			area.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> obs, String ov, String nv) {
					calculateEditedFields();
				}
			});
			fields.add(area);
			AnchorPane.setLeftAnchor(area, 0.00);
			AnchorPane.setRightAnchor(area, 0.00);
			editableFieldsVBox.getChildren().add(area);
		}

		setNumberOfSelectedFiles(summary.getFileCount());
		editableFieldsVBox.setVisible(true);
	}

	private void setIsEditingMode(Boolean isEditing) {
		if (isEditing) {
			toggleEditButton.setText("Stop Editing");
			toggleEditButton.setStyle("-fx-background-color: #FFAB91");
			dataContainer.setDisable(false);
			editingSummary.setVisible(true);
		} else {
			toggleEditButton.setText("Start Editing");
			toggleEditButton.setStyle("-fx-background-color: #7EFFFE");
			applyChangesButton.setStyle("-fx-background-color: #7EFFFE");
			applyChangesButton.setDisable(true);
			dataContainer.setDisable(true);
			editingSummary.setVisible(false);
			editingSummary.setText("");
			editedFieldsCount.set(0);
		}
	}

	private void setDropDownField(MXFColumn col, MXFSelectedFilesSummary summary, Collection<String> strVals) {
		final DropDownField dropDownField = new DropDownField();
		dropDownField.setComboBoxValues(strVals);
		dropDownField.setEditable(col.getEditable());
		dropDownField.setMXFColumn(col);
		dropDownField.setVisible(true);
		String label = col.getDisplayName();
		if (col.isRequired()) label += " *";
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
		HBox hbox = new HBox();
		String label = "Devices";
		if (col.isRequired()) label += " *";
		Label devicesLabel = new Label(label);
		devicesLabel.setLayoutX(14.0);
		devicesLabel.setLayoutY(14.0);
		devicesLabel.setPrefHeight(26.0);
		devicesLabel.setPrefWidth(225.0);
		devicesLabel.setMinWidth(100.0);
		devicesLabel.setMaxWidth(225.0);
		HBox.setHgrow(devicesLabel, Priority.ALWAYS);
		hbox.getChildren().add(devicesLabel);
		editableFieldsVBox.getChildren().add(hbox);
		GridPane grid = new GridPane();
		hbox.getChildren().add(grid);
		LabelField field = new LabelField();
		field.setMXFColumn(col);
		field.setValue(summary.getCoreData().get(col).getCurrentValue());
		fields.add(field);
		if (devices.size() == 0) {
			grid.add(new Label("No Devices"), 0, 0);
			Label newDeviceLabel = createNewDeviceLabel(devices, field);
			grid.add(newDeviceLabel, 0, 1);
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
			deviceLabel.setPrefWidth(300.0);
			deviceLabel.setMaxWidth(300.0);
			deviceLabel.setLayoutX(184.0);
			deviceLabel.setLayoutY(9.0);
			deviceLabel.setPrefHeight(26.0);
			deviceLabel.setMinWidth(100.0);
			HBox.setHgrow(deviceLabel, Priority.NEVER);
			HBox iconBox = new HBox();
			FontAwesomeIconView popoutIcon = new FontAwesomeIconView(FontAwesomeIcon.EXTERNAL_LINK);
			popoutIcon.setSize("16px");
			popoutIcon.setStyleClass("popout-icon");
			popoutIcon.setVisible(true);
			iconBox.getChildren().add(popoutIcon);
			grid.add(deviceLabel, 0, i);
			grid.add(popoutIcon, 1, i);
			popoutIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
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

					Button deleteButton = new Button("Delete Identifier");
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
			});
		}
		Label newDeviceLabel = createNewDeviceLabel(devices, field);
		grid.add(newDeviceLabel, 0, devices.size()+1);
	}

	private Label createNewDeviceLabel(ArrayList<AS07CoreDMSDeviceObjectsImpl> devices, LabelField field) {
		// create plus button to add identifier
		Label newDeviceLabel = new Label("Add Device");
		FontAwesomeIconView plusCircle = new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
		plusCircle.setStyleClass("popout-icon");
		newDeviceLabel.setGraphic(plusCircle);
		newDeviceLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
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
		});
		return newDeviceLabel;
	}

	private void createIdentifiersDisplay(MXFSelectedFilesSummary summary, MXFColumn col) {
		IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
		ArrayList<AS07DMSIdentifierSetImpl> identifiers = idSetHelper.createIdentifierListFromString(summary.getCoreData().get(col).getCurrentValue());
		HBox hbox = new HBox();
		String label = "Identifiers";
		if (col.isRequired()) label += " *";
		Label identifierLabel = new Label(label);
		final Tooltip tt = new Tooltip("Identifiers" + "\n\n" + MXFColumnHelpText.getInstance().getHelpText(col));
		tt.setStyle("-fx-text-fill: white; -fx-font-size: 12px");
		tt.setPrefWidth(500);
		tt.setWrapText(true);
		tt.setAutoHide(false);
		identifierLabel.setTooltip(tt);
		identifierLabel.setLayoutX(14.0);
		identifierLabel.setLayoutY(14.0);
		identifierLabel.setPrefHeight(26.0);
		identifierLabel.setPrefWidth(225.0);
		identifierLabel.setMinWidth(100.0);
		identifierLabel.setMaxWidth(225.0);
		HBox.setHgrow(identifierLabel, Priority.ALWAYS);
		hbox.getChildren().add(identifierLabel);
		editableFieldsVBox.getChildren().add(hbox);
		GridPane grid = new GridPane();
		hbox.getChildren().add(grid);
		LabelField field = new LabelField();
		field.setMXFColumn(col);
		field.setValue(summary.getCoreData().get(col).getCurrentValue());
		fields.add(field);
		if (identifiers.size() == 0) {
			grid.add(new Label("No Identifiers"), 0, 0);
			Label newDeviceLabel = createNewIdentifierLabel(identifiers, field);
			grid.add(newDeviceLabel, 0, 1);
			return;
		}
		for (int i = 0; i < identifiers.size(); i++) {
			try {
				AS07DMSIdentifierSetImpl id = identifiers.get(i);
				String idValue = id.getIdentifierValue();
				String idRole = id.getIdentifierRole();
				Label idLabel = new Label(idValue +" ("+ idRole +")");
				idLabel.setPrefWidth(300.0);
				idLabel.setMaxWidth(300.0);
				idLabel.setLayoutX(184.0);
				idLabel.setLayoutY(9.0);
				idLabel.setPrefHeight(26.0);
				idLabel.setMinWidth(100.0);
				hbox.setPadding(new Insets(5, 0, 5, 0));
				HBox iconBox = new HBox();
				FontAwesomeIconView popoutIcon = new FontAwesomeIconView(FontAwesomeIcon.EXTERNAL_LINK);
				popoutIcon.setSize("16px");
				popoutIcon.setStyleClass("popout-icon");
				popoutIcon.setVisible(true);
				iconBox.getChildren().add(popoutIcon);
				grid.add(idLabel, 0, i);
				grid.add(popoutIcon, 1, i);
				popoutIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
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
				});
			} catch (PropertyNotPresentException e) {
				System.out.println("Property not found exception in createIdentifiersDisplay");
			}
		}

		Label newIdentifierLabel = createNewIdentifierLabel(identifiers, field);
		grid.add(newIdentifierLabel, 0, identifiers.size()+1);
	}

	private Label createNewIdentifierLabel(ArrayList<AS07DMSIdentifierSetImpl> identifiers, LabelField field) {
		// create plus button to add identifier
		Label newIdentifierLabel = new Label("Add New Identifier");
		FontAwesomeIconView plusCircle = new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
		plusCircle.setStyleClass("popout-icon");
		newIdentifierLabel.setGraphic(plusCircle);
		newIdentifierLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
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
}
