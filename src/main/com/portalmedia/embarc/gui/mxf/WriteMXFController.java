package com.portalmedia.embarc.gui.mxf;

import java.io.IOException;

import com.portalmedia.embarc.parser.SectionDef;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

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

	SectionDef section;

	public WriteMXFController() {
		ControllerMediatorMXF.getInstance().registerWriteView(this);
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
		final VBox header = new VBox();
		header.getStyleClass().add("editor-header-box");
		header.setPrefWidth(70);
		AnchorPane.setTopAnchor(header, 0.00);
		AnchorPane.setLeftAnchor(header, 0.00);
		AnchorPane.setRightAnchor(header, 0.00);
		final Label l = new Label("Select one or more files to view and edit data");
		l.prefHeight(25);
		l.getStyleClass().add("editor-header");
		header.getChildren().add(l);

		writeFilesSummaryPane.getChildren().add(header);
		final GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(75, 25, 25, 25));

		writeFilesSummaryPane.getChildren().add(grid);

//		final Label totalFilesLabel = new Label("Total Files:");
//		final FontAwesomeIconView icon0 = new FontAwesomeIconView(FontAwesomeIcon.FILE_IMAGE_ALT);
//		totalFilesLabel.setGraphic(icon0);
//		grid.add(totalFilesLabel, 1, 0);
//
//		final Label totalFilesText = new Label(String.valueOf(DatabaseSummary.getFileCount()));
//		grid.add(totalFilesText, 2, 0);
	}

}
