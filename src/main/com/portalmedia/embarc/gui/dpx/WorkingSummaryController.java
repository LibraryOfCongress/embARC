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
	private Label totalNumberOfFilesLabel;
	@FXML
	private Label totalFilesLabel;
	@FXML
	private Label numberOfSelectedFilesLabel;
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
		ControllerMediatorDPX.getInstance().isEditingProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue o, Object ov, Object nv) {
				selectAllFilesLabel.setDisable((Boolean) nv);
				deselectAllFilesLabel.setDisable((Boolean) nv);
				removeSelectedFilesLabel.setDisable((Boolean) nv);
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

	@SuppressWarnings("ucd") // referenced in WorkingSummary.fxml
	public void deselectAllFiles() {
		ControllerMediatorDPX.getInstance().deselectAllFiles();
	}

	@SuppressWarnings("ucd") // referenced in WorkingSummary.fxml
	public void removeSelectedFiles() {
		ControllerMediatorDPX.getInstance().deleteSelectedFiles();
	}

	@SuppressWarnings("ucd") // referenced in WorkingSummary.fxml
	public void selectAllFiles() {
		ControllerMediatorDPX.getInstance().selectAllFiles();
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
		selectAllFilesLabel.setVisible(true);
		deselectAllFilesLabel.setVisible(true);
		if (size > 1) {
			totalFilesLabel.setText("files imported");
		} else if (size == 1) {
			totalFilesLabel.setText("file imported");
		} else if (size == 0) {
			selectAllFilesLabel.setVisible(false);
			deselectAllFilesLabel.setVisible(false);
		}
		totalNumberOfFilesLabel.setText(Long.toString(size));
	}

	public <T> void setSelectedFileList(ObservableList<DPXFileInformationViewModel> list) {
		final long size = list == null ? 0
				: DPXFileListHelper.getSelectedFileCount(list, ControllerMediatorDPX.getInstance().getCurrentRuleSets(),
						errorsOnlyCheckBox.isSelected());
		deselectAllFilesLabel.setVisible(true);
		removeSelectedFilesLabel.setVisible(true);
		filesSelectedLabel.setText("files selected");
		if (size == 1) {
			filesSelectedLabel.setText("file selected");
		} else if (size == 0) {
			selectAllFilesLabel.setVisible(true);
			deselectAllFilesLabel.setVisible(false);
			removeSelectedFilesLabel.setVisible(false);
		}
		numberOfSelectedFilesLabel.setText(Long.toString(size));
	}
}
