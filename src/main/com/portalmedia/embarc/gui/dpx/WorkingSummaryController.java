package com.portalmedia.embarc.gui.dpx;

import java.net.URL;
import java.util.ResourceBundle;

import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleRole;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

/**
 * UI component, bottom strip containing file count, error count, etc
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
@SuppressWarnings({ "rawtypes" })
public class WorkingSummaryController implements Initializable {
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
	@FXML
	private CheckBox errorsOnlyCheckBox;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediatorDPX.getInstance().registerWorkingSummaryController(this);
		removeSelectedFilesLabel.setVisible(false);

		errorsOnlyCheckBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				final Boolean isChecked = errorsOnlyCheckBox.selectedProperty().get();
				final int numFilesWithErrors = ControllerMediatorDPX.getInstance().toggleErrorsOnlyFilter(isChecked);
				if (isChecked) {
					errorsOnlyCheckBox.setText("Errors Only: " + numFilesWithErrors);
				} else {
					errorsOnlyCheckBox.setText("Errors Only");
				}
				DPXFileListHelper.setSelectAll(false);
				setSelectedFileList(null);
			}
		});

		totalFilesLabel.setAccessibleText("Total number of files imported.");
		totalFilesLabel.setFocusTraversable(true);

		filesSelectedLabel.setAccessibleText("Total number of files currently selected.");
		filesSelectedLabel.setFocusTraversable(true);

		selectAllFilesLabel.setAccessibleText("Select all files.");
		selectAllFilesLabel.setAccessibleRole(AccessibleRole.BUTTON);
		selectAllFilesLabel.setFocusTraversable(true);
		selectAllFilesLabel.getStyleClass().add("hover-hand");
		selectAllFilesLabel.setOnMouseClicked(event -> {
			ControllerMediatorDPX.getInstance().selectAllFiles();
		});
		selectAllFilesLabel.setOnKeyPressed(event -> {
			if (event.getCode() != KeyCode.SPACE) {
				return;
			}
			ControllerMediatorDPX.getInstance().selectAllFiles();
		});

		deselectAllFilesLabel.setAccessibleText("Deselect all files.");
		deselectAllFilesLabel.setAccessibleRole(AccessibleRole.BUTTON);
		deselectAllFilesLabel.setFocusTraversable(true);
		deselectAllFilesLabel.getStyleClass().add("hover-hand");
		deselectAllFilesLabel.setOnMouseClicked(event -> {
			ControllerMediatorDPX.getInstance().deselectAllFiles();
		});
		deselectAllFilesLabel.setOnKeyPressed(event -> {
			if (event.getCode() != KeyCode.SPACE) {
				return;
			}
			ControllerMediatorDPX.getInstance().deselectAllFiles();
		});
		
		removeSelectedFilesLabel.setAccessibleText("Remove all selected files.");
		removeSelectedFilesLabel.setAccessibleRole(AccessibleRole.BUTTON);
		removeSelectedFilesLabel.setFocusTraversable(true);
		removeSelectedFilesLabel.getStyleClass().add("hover-hand");
		removeSelectedFilesLabel.setOnMouseClicked(event -> {
			ControllerMediatorDPX.getInstance().deleteSelectedFiles();
		});
		removeSelectedFilesLabel.setOnKeyPressed(event -> {
			if (event.getCode() != KeyCode.SPACE) {
				return;
			}
			ControllerMediatorDPX.getInstance().deleteSelectedFiles();
		});
		
		errorsOnlyCheckBox.setAccessibleText("Only show files with errors.");
		errorsOnlyCheckBox.setFocusTraversable(true);
	}

	public void resetErrorCount() {
		Platform.runLater(() -> {
			final Boolean isChecked = errorsOnlyCheckBox.selectedProperty().get();
			if (isChecked) {
				final int numFilesWithErrors = ControllerMediatorDPX.getInstance().getCurrentFileCount();
				errorsOnlyCheckBox.setText("Errors Only: " + numFilesWithErrors);
			} else {
				errorsOnlyCheckBox.setText("Errors Only");
			}
		});
	}

	public <T> void setFiles() {
		final long size = DPXFileListHelper.getTotalFiles();
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

	public <T> void setSelectedFileList(ObservableList<DPXFileInformationViewModel> list) {
		final long size = list == null ? 0
				: DPXFileListHelper.getSelectedFileCount(list, ControllerMediatorDPX.getInstance().getCurrentRuleSets(),
						errorsOnlyCheckBox.isSelected());
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
