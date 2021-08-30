package com.portalmedia.embarc.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Error dialog modal to display error information
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ErrorDialog {
	private final Stage dialogStage;
	private final VBox vbox;

	public ErrorDialog(String errorMessage) {
		dialogStage = new Stage();
		dialogStage.initOwner(Main.getPrimaryStage());
		dialogStage.setHeight(200);
		dialogStage.setWidth(250);
		dialogStage.initStyle(StageStyle.UTILITY);
		dialogStage.setResizable(false);
		dialogStage.setTitle("Error");
		
		
		Label label = new Label(errorMessage);
		label.setWrapText(true);
		label.setAlignment(Pos.CENTER);
		label.setTextAlignment(TextAlignment.CENTER);

		vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.setSpacing(10);
		vbox.getChildren().add(label);
		
		final Button closeButton = new Button("Close");
		vbox.getChildren().add(closeButton);
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				dialogStage.close();
			}
		});

		final Scene scene = new Scene(vbox);
		dialogStage.setScene(scene);
	}

	public Stage getDialogStage() {
		return dialogStage;
	}
}
