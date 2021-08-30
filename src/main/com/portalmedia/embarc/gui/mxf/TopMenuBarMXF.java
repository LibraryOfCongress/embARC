package com.portalmedia.embarc.gui.mxf;

import com.portalmedia.embarc.gui.AboutModalController;
import com.portalmedia.embarc.gui.FileProcessController;
import com.portalmedia.embarc.gui.Main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
public class TopMenuBarMXF {
	
	public static MenuBar createMenuBar() {
    	MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem importFilesItem = new MenuItem("Import Files");
		MenuItem importDirectoryItem = new MenuItem("Import Folder");
		MenuItem writeFilesItem = new MenuItem("Write Files");
		MenuItem aboutItem = new MenuItem("About embARC");
		MenuItem exitItem = new MenuItem("Exit");
		
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
				WriteFilesModalControllerMXF.getInstance().showWriteFilesDialog();
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

		fileMenu.getItems().addAll(importFilesItem, importDirectoryItem, writeFilesItem, aboutItem, exitItem);

		menuBar.getMenus().addAll(fileMenu);
		return menuBar;
	}
	
}
