package com.portalmedia.embarc.gui.mxf;

import java.net.URL;
import java.util.ResourceBundle;

import com.portalmedia.embarc.gui.helper.MXFFileList;
import com.portalmedia.embarc.gui.model.MXFSelectedFilesSummary;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

/**
 * UI component, bottom strip containing file count, error count, etc
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-07-09
 */
@SuppressWarnings({ "rawtypes" })
public class SessionSummaryController implements Initializable {
	@FXML
	private Label totalFilesLabel;
	@FXML
	private Label filesSelectedLabel;
	@FXML
	private Label selectAllFilesLabel;
	@FXML
	private Label deselectAllFilesLabel;
	@FXML
	private Label removeSelectedFilesLabel;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediatorMXF.getInstance().registerSessionSummaryController(this);

		removeSelectedFilesLabel.setVisible(false);

		totalFilesLabel.setAccessibleText("Total number of files imported.");
		totalFilesLabel.setFocusTraversable(true);

		filesSelectedLabel.setAccessibleText("Total number of files currently selected.");
		filesSelectedLabel.setFocusTraversable(true);
		
		selectAllFilesLabel.setAccessibleText("Select all files.");
		selectAllFilesLabel.setAccessibleRole(AccessibleRole.BUTTON);
		selectAllFilesLabel.setFocusTraversable(true);
		selectAllFilesLabel.getStyleClass().add("hover-hand");
		selectAllFilesLabel.setOnMouseClicked(event -> {
			ControllerMediatorMXF.getInstance().selectAllFiles();
		});
		selectAllFilesLabel.setOnKeyPressed(event -> {
			if (event.getCode() != KeyCode.SPACE) {
				return;
			}
			ControllerMediatorMXF.getInstance().selectAllFiles();
		});
		
		deselectAllFilesLabel.setAccessibleText("Deselect all files.");
		deselectAllFilesLabel.setAccessibleRole(AccessibleRole.BUTTON);
		deselectAllFilesLabel.setFocusTraversable(true);
		deselectAllFilesLabel.getStyleClass().add("hover-hand");
		deselectAllFilesLabel.setOnMouseClicked(event -> {
			ControllerMediatorMXF.getInstance().deselectAllFiles();
		});
		deselectAllFilesLabel.setOnKeyPressed(event -> {
			if (event.getCode() != KeyCode.SPACE) {
				return;
			}
			ControllerMediatorMXF.getInstance().deselectAllFiles();
		});
		
		removeSelectedFilesLabel.setAccessibleText("Remove all selected files.");
		removeSelectedFilesLabel.setAccessibleRole(AccessibleRole.BUTTON);
		removeSelectedFilesLabel.setFocusTraversable(true);
		removeSelectedFilesLabel.getStyleClass().add("hover-hand");
		removeSelectedFilesLabel.setOnMouseClicked(event -> {
			ControllerMediatorMXF.getInstance().deleteSelectedFiles();
		});
		removeSelectedFilesLabel.setOnKeyPressed(event -> {
			if (event.getCode() != KeyCode.SPACE) {
				return;
			}
			ControllerMediatorMXF.getInstance().deleteSelectedFiles();
		});
		
		setFiles();
	}

	public <T> void setFiles() {
		MXFFileList.getInstance();
		final long size = MXFFileList.getList().size();
		if (size >= 1) {
			String text = Long.toString(size) + " file" + (size == 1 ? " " : "s ") + "imported";
			totalFilesLabel.setText(text);
			totalFilesLabel.setAccessibleText(text);
			selectAllFilesLabel.setVisible(true);
			deselectAllFilesLabel.setVisible(true);
		} else {
			totalFilesLabel.setText(Long.toString(size) + " files imported");
			totalFilesLabel.setAccessibleText(Long.toString(size) + " files imported");
			selectAllFilesLabel.setVisible(false);
			deselectAllFilesLabel.setVisible(false);
		}
	}

	public <T> void setSelectedFileList(MXFSelectedFilesSummary list) {
		final long size = list.getFileCount();
		if (size >= 1) {
			String text = Long.toString(size) + " file" + (size == 1 ? " " : "s ") + "selected";
			filesSelectedLabel.setText(text);
			filesSelectedLabel.setAccessibleText(text);
			deselectAllFilesLabel.setVisible(true);
			removeSelectedFilesLabel.setVisible(true);
		} else {
			filesSelectedLabel.setText(Long.toString(size) + " files selected");
			filesSelectedLabel.setAccessibleText(Long.toString(size) + " files selected");
			selectAllFilesLabel.setVisible(true);
			deselectAllFilesLabel.setVisible(false);
			removeSelectedFilesLabel.setVisible(false);
		}
	}
}
