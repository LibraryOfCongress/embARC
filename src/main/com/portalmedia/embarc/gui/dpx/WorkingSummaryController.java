package com.portalmedia.embarc.gui.dpx;

import java.net.URL;
import java.util.ResourceBundle;

import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

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
	private AnchorPane workingSummaryContainer;
	@FXML
	private Label totalNumberOfFiles;
	@FXML
	private Label totalFilesText;
	@FXML
	private Label numberOfSelectedFiles;
	@FXML
	private Label filesSelectedText;
	@FXML
	private Label selectAllFiles;
	@FXML
	private Label deselectAllFiles;
	@FXML
	private Label removeSelectedFiles;
	@FXML
	private CheckBox errorsOnlyCheckBox;

	DPXFileListHelper fileListHelper = new DPXFileListHelper();

	public void deselectAllFiles() {
		ControllerMediatorDPX.getInstance().deselectAllFiles();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediatorDPX.getInstance().registerWorkingSummaryController(this);
		removeSelectedFiles.setVisible(false);
		ControllerMediatorDPX.getInstance().isEditingProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue o, Object ov, Object nv) {
				selectAllFiles.setDisable((Boolean) nv);
				deselectAllFiles.setDisable((Boolean) nv);
				removeSelectedFiles.setDisable((Boolean) nv);
			}
		});

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
	}

	public void removeSelectedFiles() {
		ControllerMediatorDPX.getInstance().deleteSelectedFiles();
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

	public void selectAllFiles() {
		ControllerMediatorDPX.getInstance().selectAllFiles();
	}

	public <T> void setFiles() {
		final long size = DPXFileListHelper.getTotalFiles();
		selectAllFiles.setVisible(true);
		deselectAllFiles.setVisible(true);
		if (size > 1) {
			totalFilesText.setText("files imported");
		} else if (size == 1) {
			totalFilesText.setText("file imported");
		} else if (size == 0) {
			selectAllFiles.setVisible(false);
			deselectAllFiles.setVisible(false);
		}
		totalNumberOfFiles.setText(Long.toString(size));
	}

	public <T> void setSelectedFileList(ObservableList<DPXFileInformationViewModel> list) {
		final long size = list == null ? 0
				: DPXFileListHelper.getSelectedFileCount(list, ControllerMediatorDPX.getInstance().getCurrentRuleSets(),
						errorsOnlyCheckBox.isSelected());
		deselectAllFiles.setVisible(true);
		removeSelectedFiles.setVisible(true);
		filesSelectedText.setText("files selected");
		if (size == 1) {
			filesSelectedText.setText("file selected");
		} else if (size == 0) {
			selectAllFiles.setVisible(true);
			deselectAllFiles.setVisible(false);
			removeSelectedFiles.setVisible(false);
		}
		numberOfSelectedFiles.setText(Long.toString(size));
	}
}
