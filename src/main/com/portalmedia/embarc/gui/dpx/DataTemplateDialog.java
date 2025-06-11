package com.portalmedia.embarc.gui.dpx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Objects;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import com.portalmedia.embarc.gui.AccessibleAlertHelper;
import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXDataTemplate;
import com.portalmedia.embarc.system.UserPreferencesService;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * DPX data template
 *
 * @author PortalMedia
 * @version 0.1.4+
 * @since 2019-07-24
 */
public class DataTemplateDialog extends Dialog {
	@FXML
	private TextField nameField;
	@FXML
	private Label dateCreated;
	@FXML
	private ChoiceBox<DPXColumn> columnChoiceBox;
	@FXML
	private Button addButton;
	@FXML
	private Button removeButton;
	@FXML
	private Button deleteTemplateButton;
	@FXML
	private Button saveButton;
	@FXML
	private Button cancelButton;
	@FXML
	private TextField valueField;
	@FXML
	private Pane listPane;

	Stage dataTemplateStage;

	ObservableList<DPXColumn> columnList;
	ListView<String> listView = new ListView<>();
	ObservableMap<DPXColumn, String> backingMap = FXCollections.observableHashMap();
	ObservableMap<DPXColumn, String> setMap = FXCollections.observableHashMap();

	DPXDataTemplate template;
	final UserPreferencesService userPreferences = new UserPreferencesService();
	ValidationSupport validationSupport = new ValidationSupport();
	
	public DataTemplateDialog() {
		template = new DPXDataTemplate();
		template.setValues(new HashMap<DPXColumn, String>());
		template.setName("");
		createDataTemplateStage();
		dataTemplateStage.setTitle("Create Data Template");
	}
	
	public DataTemplateDialog(DPXDataTemplate savedTemplate) {
		template = savedTemplate;
		createDataTemplateStage();
		dataTemplateStage.setTitle("Edit Data Template");
	}
	
	public void createDataTemplateStage() {
		dataTemplateStage = new Stage(); 
		dataTemplateStage.initOwner(Main.getPrimaryStage());
		dataTemplateStage.setHeight(450);
		dataTemplateStage.setWidth(610);
		dataTemplateStage.initStyle(StageStyle.UTILITY);
		dataTemplateStage.setResizable(false);
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DPXDataTemplate.fxml"));
		fxmlLoader.setController(this);
		Scene scene;
		try {
			scene = new Scene(fxmlLoader.load());
			scene.getStylesheets().add(getClass().getResource("/com/portalmedia/embarc/gui/application.css").toExternalForm());
			setContent();
			dataTemplateStage.setScene(scene);
			dataTemplateStage.show();
		} catch (IOException e) {
			System.out.println("Error creating data template stage");
		}
	}
	
	public void setContent() {
		setMap.putAll(template.getValues());
		removeButton.setVisible(false);

		for (Map.Entry<DPXColumn, String> entry : setMap.entrySet()) {
	        backingMap.put(entry.getKey(), entry.getKey().getDisplayName() + ": " + entry.getValue());
	    }

		setMap.addListener((MapChangeListener<DPXColumn, String>) change -> {
			if (change.wasRemoved()) backingMap.remove(change.getKey());
		    if (change.wasAdded()) {
		    	backingMap.put(change.getKey(), change.getKey().getDisplayName() + ": " + change.getValueAdded());
		    }
		});

		listView.getItems().setAll(backingMap.values());
		listView.setPrefHeight(194);
		listView.setPrefWidth(560);

		SelectionModel<String> listSelection = listView.getSelectionModel();
		listSelection.selectedItemProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if (newValue != null) removeButton.setVisible(true);
		        else removeButton.setVisible(false);
		    }
		});

		listPane.getChildren().add(0, listView);

		if (!template.getName().isEmpty()) {
			nameField.setText(template.getName());
			nameField.setDisable(true);
			deleteTemplateButton.setVisible(true);
		} else {
			deleteTemplateButton.setVisible(false);
			validationSupport.setErrorDecorationEnabled(false);
			validationSupport.registerValidator(nameField, Validator.createEmptyValidator("Data template name is required"));
		}

		dateCreated.setText(template.getInsertDate().toString());

		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				DPXColumn col = columnChoiceBox.getValue();
				String val = valueField.getText();
				if (val.isEmpty()) return;
				setMap.put(col, val);
				listView.getItems().setAll(backingMap.values());
				valueField.clear();
			}
		});

		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String selected = listSelection.getSelectedItem();
				if (backingMap.containsValue(selected)) {
					ColumnDef toRemove = null;
					for (Entry<DPXColumn, String> entry : backingMap.entrySet()) {
				        if (Objects.equals(selected, entry.getValue())) {
				            toRemove = entry.getKey();
				        }
				    }
					if (toRemove != null) {
						setMap.remove(toRemove);
						listView.getItems().setAll(backingMap.values());
					}
				}
			}
		});
		
		deleteTemplateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				final String message = "Are you sure you want to delete this template?";
				final Alert alert = AccessibleAlertHelper.CreateAccessibleAlert(
						"Delete Template", 
						AlertType.CONFIRMATION, 
						message);
				
				
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.initOwner(Main.getPrimaryStage());
	        	alert.setGraphic(null);
	        	alert.setHeaderText(null);
				 
	        	final DialogPane dialogPane =  alert.getDialogPane();
	        	dialogPane.lookupButton(ButtonType.OK).setAccessibleHelp(message);
				 	
				dialogPane.getStylesheets().add(getClass().getResource("/com/portalmedia/embarc/gui/application.css").toExternalForm());
				dialogPane.getStyleClass().add("alertDialog");	 
	        	
	        	Optional<ButtonType> result = alert.showAndWait();
	        	if (result.get() == ButtonType.OK){
		        	userPreferences.removeDpxDataTemplate(template.getName());
					dataTemplateStage.close();
					ControllerMediatorDPX.getInstance().refreshDataTemplateList();
	        	} else {
	        		e.consume();
	        	}
			}
		});
		
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				validationSupport.setErrorDecorationEnabled(true);
				if (nameField.getText().isEmpty()) return;
				template.setName(nameField.getText());
				HashMap<DPXColumn, String> map = new HashMap<>(setMap);
				template.setValues(map);
				
				userPreferences.addDpxDataTemplate(template);
				dataTemplateStage.close();
				ControllerMediatorDPX.getInstance().refreshDataTemplateList();
			}
		});
		
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				dataTemplateStage.close();
			}
		});
		
		columnList = FXCollections.observableArrayList();
		for (final DPXColumn c : DPXColumn.values()) {
			if (c.getEditable()) {
				columnList.add(c);
			}
		}
		columnChoiceBox.getItems().addAll(columnList);
	}

}
