package com.portalmedia.embarc.gui;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * UI spinner component 
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ProgressSpinner {
	private final Stage dialogStage;
	private final ProgressIndicator progressIndicator = new ProgressIndicator();
	private final VBox vbox;
	private final GridPane grid;

	public ProgressSpinner() {
		dialogStage = new Stage();
		dialogStage.initOwner(Main.getPrimaryStage());
		dialogStage.setHeight(90);
		dialogStage.setWidth(100);
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.APPLICATION_MODAL);
		dialogStage.initStyle(StageStyle.UNDECORATED);

		progressIndicator.setProgress(-1F);

		grid = new GridPane();
		grid.setPrefWidth(90);
		grid.setVgap(15);
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.setAlignment(Pos.CENTER);
		grid.add(new Label("Working..."), 0, 0);
		grid.add(progressIndicator, 0, 1);

		vbox = new VBox();
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.getChildren().add(grid);

		final Scene scene = new Scene(vbox);
		dialogStage.setScene(scene);
	}

	public void activateProgressBar(final Task<?> task) {
		progressIndicator.progressProperty().bind(task.progressProperty());
		dialogStage.show();
	}

	public void cancelProgressBar() {
		if (progressIndicator.progressProperty().isBound()) {
			progressIndicator.progressProperty().unbind();
		}
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

}
