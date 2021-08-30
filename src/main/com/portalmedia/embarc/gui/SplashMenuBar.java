package com.portalmedia.embarc.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * SplashMenuBar
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class SplashMenuBar {
	
	public static MenuBar createMenuBar() {
    	MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem importFilesItem = new MenuItem("Import Files");
		MenuItem importDirectoryItem = new MenuItem("Import Folder");
		MenuItem aboutItem = new MenuItem("About embARC");
		MenuItem exitItem = new MenuItem("Exit");
		
		importFilesItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	FileProcessController.getInstance().chooseFile(Main.getSplashStage());
		    }
		});
		importDirectoryItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	FileProcessController.getInstance().chooseDirectory(Main.getSplashStage());
		    }
		});
		aboutItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	AboutModalController.getInstance().showAboutModal(Main.getSplashStage());
		    }
		});
		exitItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	Stage splashStage = Main.getSplashStage();
		    	splashStage.fireEvent(new WindowEvent(splashStage, WindowEvent.WINDOW_CLOSE_REQUEST));
		    }
		});

		fileMenu.getItems().addAll(importFilesItem, importDirectoryItem, aboutItem, exitItem);
		menuBar.getMenus().addAll(fileMenu);
		return menuBar;
	}
	
}
