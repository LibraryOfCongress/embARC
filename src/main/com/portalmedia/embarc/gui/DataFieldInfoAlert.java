package com.portalmedia.embarc.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;

public class DataFieldInfoAlert {
	public static void showFieldInfoAlert(String labelText, String helpText) {
		final Alert alert = new Alert(AlertType.NONE);
		alert.setTitle(labelText + " Information");
		alert.setHeaderText(null);
		alert.setContentText(null);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(Main.getPrimaryStage());

		final ButtonType[] buttonList = new ButtonType[1];
		buttonList[0] = ButtonType.CLOSE;
		alert.getButtonTypes().setAll(buttonList);

		final GridPane grid = new GridPane();
		final ScrollPane scrollPane = new ScrollPane();
		final Text text = new Text(helpText);
		text.setWrappingWidth(375);
		scrollPane.setContent(text);
		scrollPane.setPrefWidth(400);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setAccessibleText(helpText);
		scrollPane.setFocusTraversable(true);
		scrollPane.setStyle("-fx-background-color: transparent;");
		final Label labelTextText = new Label(labelText);
		labelTextText.setStyle("-fx-font-size: 14.0");
		labelTextText.setAccessibleText(labelText);
		labelTextText.setFocusTraversable(true);
		grid.add(labelTextText, 0, 0);
		grid.add(scrollPane, 0, 1);
		grid.setVgap(10);
		alert.getDialogPane().setContent(grid);

		alert.showAndWait();
		if (alert.getResult() == ButtonType.CLOSE) {
			alert.close();
		}
	}
}
