package com.portalmedia.embarc.gui.mxf;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * Editor pane view when no files are selected. Displays rule set check boxes
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-07-13
 */
public class WriteMXFController extends AnchorPane {
	@FXML
	private AnchorPane writeFilesSummaryPane;
	@FXML
	private Label sectionLabel;
	@FXML
	private Label selectedFilesLabel;

	public WriteMXFController() {
		ControllerMediatorMXF.getInstance().registerWriteMXFController(this);
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WriteMXFView.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setContent() {
		sectionLabel.setText("File Summary");
		selectedFilesLabel.setText("Select one or more files to view and edit data");
	}
}
