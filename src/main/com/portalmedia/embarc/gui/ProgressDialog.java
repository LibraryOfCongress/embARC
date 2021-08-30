package com.portalmedia.embarc.gui;

import java.util.List;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Progress dialog modal to display process completion
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ProgressDialog {
	private final Alert dialogAlert;
	private final ProgressIndicator progressIndicator = new ProgressIndicator();
	private Label processCount;
	private final VBox vbox;
	private final GridPane grid;

	public ProgressDialog() {
		dialogAlert = new Alert(AlertType.NONE);
		dialogAlert.initOwner(Main.getPrimaryStage());
		dialogAlert.setHeight(400);
		dialogAlert.setWidth(400);
		dialogAlert.setResizable(false);
		dialogAlert.setTitle("Processing Files");

		progressIndicator.setProgress(-1F);
		progressIndicator.setVisible(true);

		grid = new GridPane();
		grid.setPrefWidth(300);
		grid.setPrefHeight(300);
		grid.setHgap(20);
		grid.setVgap(20);
		grid.setPadding(new Insets(40, 40, 40, 40));
		grid.setAlignment(Pos.CENTER);
		grid.add(progressIndicator, 0, 0);

		vbox = new VBox();
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.getChildren().add(grid);

		dialogAlert.getDialogPane().setContent(vbox);
	}

	public void activateProgressBar(final Task<?> task) {
		progressIndicator.progressProperty().bind(task.progressProperty());
		dialogAlert.show();
	}

	public void cancelProgressBar() {
		if (progressIndicator.progressProperty().isBound()) {
			progressIndicator.progressProperty().unbind();
		}
	}

	public Alert getDialogAlert() {
		return dialogAlert;
	}

	public void setCountLabel(Label processText) {
		processCount = processText;
		final int size = grid.getChildren().size();
		grid.add(processCount, 0, size);
	}

	public void showCloseButton() {
		dialogAlert.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
	}

	public void showLabels(List<Label> labels) {
		final int size = grid.getChildren().size();
		for (int i = 0; i < labels.size(); i++) {
			grid.add(labels.get(i), 0, size + i);
		}
	}
}
