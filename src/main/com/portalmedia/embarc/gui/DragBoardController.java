package com.portalmedia.embarc.gui;

import java.io.File;
import java.util.List;

import com.portalmedia.embarc.gui.dpx.ControllerMediatorDPX;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;

/**
 * Controls DragBoard component
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-03-11
 */
public class DragBoardController {
	
	@FXML
	private Pane dragPane;

	boolean greyDragOver = false;

	public DragBoardController(Pane pane) {
		dragPane = pane;
		dragPane.setMouseTransparent(true);

		// safety check for app grey out bug
		dragPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				if (greyDragOver) {
					dragPane.setStyle("-fx-background-color: transparent");
					dragPane.setMouseTransparent(true);
					greyDragOver = false;
				}
			}
		});
	}
	
	@FXML
	private void handleOnDragDropped(DragEvent event) {
		if (ControllerMediatorDPX.getInstance().isEditingProperty().get()) {
			final String info = "Cannot add files to workspace while editing.";
			final Alert alert = new Alert(AlertType.WARNING, info, ButtonType.OK);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.initOwner(Main.getPrimaryStage());
			alert.show();
			if (alert.getResult() == ButtonType.OK) {
				alert.close();
			}
			dragPane.setStyle("-fx-background-color: transparent;");
			dragPane.setMouseTransparent(true);
			greyDragOver = false;
			event.consume();
		} else {
			final Dragboard db = event.getDragboard();
			if (db.hasFiles()) {
				final List<File> files = db.getFiles();
				if (files.size() > 0) {
					if (files.get(0).isDirectory()) {
						int count = 0;
						for (final File file : files) {
							count += FileProcessController.getInstance().countFiles(file);
						}
						//totalItemsCount = count;
					} else {
						//totalItemsCount = files.size();
					}
				}
				FileProcessController.getInstance().processFiles(files, event);
			}
		}
	}

	@FXML
	private void handleOnDragEntered(DragEvent event) {
		dragPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
		dragPane.setMouseTransparent(false);
		greyDragOver = true;
		event.consume();
	}

	@FXML
	private void handleOnDragExited(DragEvent event) {
		event.consume();
		dragPane.setStyle("-fx-background-color: transparent");
		dragPane.setMouseTransparent(true);
		greyDragOver = false;
	}

	@FXML
	private void handleOnDragOver(DragEvent event) {
		event.acceptTransferModes(TransferMode.ANY);
		event.consume();
	}
	

}
