package com.portalmedia.embarc.gui.dpx;

import java.util.HashMap;
import java.util.Optional;

import com.portalmedia.embarc.gui.AboutModalController;
import com.portalmedia.embarc.gui.AccessibleAlertHelper;
import com.portalmedia.embarc.gui.FileProcessController;
import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.parser.dpx.DPXDataTemplate;
import com.portalmedia.embarc.system.UserPreferencesService;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * MenuBar
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class TopMenuBarDPX {
	
	public static MenuBar createMenuBar() {
    	MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		Menu optionsMenu = new Menu("Options");
		Menu dataTemplates = new Menu("Data Templates");
		Menu reportsMenu = new Menu("Reports");
		MenuItem importFilesItem = new MenuItem("Import Files");
		MenuItem importDirectoryItem = new MenuItem("Import Folder");
		MenuItem writeFilesItem = new MenuItem("Write Files");
		MenuItem aboutItem = new MenuItem("About embARC");
		MenuItem exitItem = new MenuItem("Exit");
		MenuItem toggleColumns = new MenuItem("Toggle Column Visibility");
		MenuItem ruleSets = new MenuItem("Rule Sets");
		MenuItem createReport = new MenuItem("Download Rule Violations");
		MenuItem createImageChecksumReport = new MenuItem("Download Image Data Checksums");
		MenuItem createSequenceGapAnalysisReport = new MenuItem("Download Sequence Gap Analysis Report (Simple)");
		MenuItem createSequenceGapAnalysisReportVerbose = new MenuItem("Download Sequence Gap Analysis Report (Full)");
		MenuItem createCSVExport = new MenuItem("Download Metadata as CSV");
		
		importFilesItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	FileProcessController.getInstance().chooseFile(Main.getPrimaryStage());
		    }
		});
		importDirectoryItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	FileProcessController.getInstance().chooseDirectory(Main.getPrimaryStage());
		    }
		});
		writeFilesItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	WriteFilesModalController.getInstance().showWriteFilesDialog();
		    }
		});
		aboutItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	AboutModalController.getInstance().showAboutModal(Main.getPrimaryStage());
		    }
		});
		exitItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	Stage primaryStage = Main.getPrimaryStage();
		    	primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
		    }
		});
		toggleColumns.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediatorDPX.getInstance().showColumnVisibilityDialogue();
		    }
		});
		ruleSets.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	RuleSetModalController.getInstance().showRuleSetsDialog();
		    }
		});
		createReport.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediatorDPX.getInstance().createReport();
		    }
		});
		createImageChecksumReport.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediatorDPX.getInstance().createImageChecksumReport();
		    }
		});
		createSequenceGapAnalysisReport.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediatorDPX.getInstance().createSequenceGapAnalysisReport(false);
		    }
		});
		createSequenceGapAnalysisReportVerbose.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediatorDPX.getInstance().createSequenceGapAnalysisReport(true);
		    }
		});
		createCSVExport.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				ControllerMediatorDPX.getInstance().createCSVMetadataExport();
			}
		});

		fileMenu.getItems().addAll(importFilesItem, importDirectoryItem, writeFilesItem, aboutItem, exitItem);
		populateSavedDataTemplates(dataTemplates);
		optionsMenu.getItems().addAll(ruleSets, toggleColumns, dataTemplates);
		reportsMenu.getItems().addAll(createReport, createImageChecksumReport, createSequenceGapAnalysisReport, createSequenceGapAnalysisReportVerbose, createCSVExport);
		menuBar.getMenus().addAll(fileMenu, optionsMenu, reportsMenu);
		menuBar.setStyle("-fx-background-color: #eceff1;");
		return menuBar;
	}
	
	public static void populateSavedDataTemplates(Menu dataTemplateMenu) {
		final UserPreferencesService userPreferences = new UserPreferencesService();
		HashMap<String, DPXDataTemplate> templates = userPreferences.getDPXDataTemplates();
		templates.forEach((name, template) -> {
			Menu templateMenu = new Menu(name);
			MenuItem editTemplate = new MenuItem("Edit");
			MenuItem applyTemplate = new MenuItem("Apply");
			
			editTemplate.setOnAction(new EventHandler<ActionEvent>() {
			    public void handle(ActionEvent t) {
			    	DataTemplatesModalController.getInstance().showDataTemplatesDialogue(template);
			    }
			});
			
			applyTemplate.setOnAction(new EventHandler<ActionEvent>() {
			    public void handle(ActionEvent t) {
			    	ControllerMediatorDPX cm = ControllerMediatorDPX.getInstance();
			    	int count = cm.getSelectedFilesSummary().getFileCount();
			    	String contentText = "Are you sure you want to apply this template to ";
			    	if (count == 0) contentText += "all files?";
			    	else if (count == 1) contentText += count + " file?";
			    	else contentText += count + " files?";
			    	
			    	final Alert alert = AccessibleAlertHelper.CreateAccessibleAlert(
			    			"Apply Data Template", 
							AlertType.CONFIRMATION, 
							contentText
						);
					alert.initModality(Modality.APPLICATION_MODAL);
					alert.initOwner(Main.getPrimaryStage());
		        	alert.setGraphic(null);
		        	alert.setHeaderText(null);

					DialogPane dialogPane = alert.getDialogPane();
					 
					alert.getDialogPane().lookupButton(ButtonType.OK).setAccessibleHelp(contentText);
					 	
					dialogPane.getStylesheets().add(getClass().getResource("/com/portalmedia/embarc/gui/application.css").toExternalForm());
					dialogPane.getStyleClass().add("alertDialog");	 
					
		        	
		        	Optional<ButtonType> result = alert.showAndWait();
		        	if (result.get() == ButtonType.OK) {
		        		if (count == 0) cm.updateChangedValuesAllFiles(template.getValues());
		        		cm.updateChangedValues(template.getValues());
		        	} else {
		        		t.consume();
		        	}
			    }
			});
			
			templateMenu.getItems().addAll(editTemplate, applyTemplate);
			dataTemplateMenu.getItems().add(templateMenu);
		});
		MenuItem newDataTemplate = new MenuItem("New Data Template");
		newDataTemplate.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	DataTemplatesModalController.getInstance().showDataTemplatesDialogue();
		    }
		});
		dataTemplateMenu.getItems().add(newDataTemplate);
	}
}
