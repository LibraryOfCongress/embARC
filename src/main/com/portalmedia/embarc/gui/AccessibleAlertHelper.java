package com.portalmedia.embarc.gui;

import org.apache.commons.lang3.StringUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Functions to create Alert with screen reader support. Intended primarily for simple text alerts with default styling.
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2025-02-25
 */
public class AccessibleAlertHelper {
	public static Alert CreateAccessibleAlert(String title, AlertType alertType, String contentText, ButtonType...buttons) {
		final Alert alert = new Alert(alertType, "", buttons);
		if(StringUtils.isNotBlank(title)) {
			alert.setTitle(title);
		}
		
		//Block required for screen reader support
		 final GridPane grid = new GridPane();
		 final Text text = new Text(contentText);
		 text.setStyle("-fx-font-size: 14.0");
		 text.setFocusTraversable(true);
		 grid.add(text, 0, 0);
		 grid.setVgap(15);
		 alert.getDialogPane().setContent(grid);

		 return alert;
	}
}
