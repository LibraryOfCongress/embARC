package com.portalmedia.embarc.gui;

import java.io.File;
import java.util.List;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

/**
 * Reusable DragBoard/BorderPane component
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-03-11
 */
public class DragBoard extends BorderPane {

	public DragBoard() {
		DragBoard self = this;

		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
			}
		});
		
		this.setOnDragOver(new EventHandler <DragEvent>() {
		    public void handle(DragEvent event) {
		    	event.acceptTransferModes(TransferMode.ANY);
				event.consume();
		    }
		});

		this.setOnDragEntered(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				self.setMouseTransparent(false);
				event.consume();
			}
		});

		this.setOnDragExited(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				event.consume();
			}
		});

		this.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				final Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					final List<File> files = db.getFiles();
					Platform.runLater(() -> FileProcessController.getInstance().processFiles(files, event));
				}
			}
		});
	}

}
