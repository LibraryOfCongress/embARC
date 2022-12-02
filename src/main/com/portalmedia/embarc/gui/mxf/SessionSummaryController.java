package com.portalmedia.embarc.gui.mxf;

import java.net.URL;
import java.util.ResourceBundle;

import com.portalmedia.embarc.gui.helper.MXFFileList;
import com.portalmedia.embarc.gui.model.MXFSelectedFilesSummary;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

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
	private AnchorPane sessionSummaryContainer;
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

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediatorMXF.getInstance().registerSessionSummaryController(this);
		removeSelectedFilesLabel.setVisible(false);
		ControllerMediatorMXF.getInstance().isEditingProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue o, Object ov, Object nv) {
				selectAllFilesLabel.setDisable((Boolean) nv);
				deselectAllFilesLabel.setDisable((Boolean) nv);
				removeSelectedFilesLabel.setDisable((Boolean) nv);
			}
		});
		setFiles();
	}

	@SuppressWarnings("ucd") // referenced in SessionSummary.fxml
	public void removeSelectedFiles() {
		ControllerMediatorMXF.getInstance().deleteSelectedFiles();
		setFiles();
	}

	@SuppressWarnings("ucd") // referenced in SessionSummary.fxml
	public void selectAllFiles() {
		ControllerMediatorMXF.getInstance().selectAllFiles();
	}

	@SuppressWarnings("ucd") // referenced in SessionSummary.fxml
	public void deselectAllFiles() {
		ControllerMediatorMXF.getInstance().deselectAllFiles();
	}

	public <T> void setFiles() {
		MXFFileList.getInstance();
		final long size = MXFFileList.getList().size();
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

	public <T> void setSelectedFileList(MXFSelectedFilesSummary list) {
		deselectAllFilesLabel.setVisible(true);
		removeSelectedFilesLabel.setVisible(true);
		filesSelectedLabel.setText("files selected");
		if (list.getFileCount() == 1) {
			filesSelectedLabel.setText("file selected");
		} else if (list.getFileCount() == 0) {
			selectAllFilesLabel.setVisible(true);
			deselectAllFilesLabel.setVisible(false);
			removeSelectedFilesLabel.setVisible(false);
		}
		numberOfSelectedFilesLabel.setText(Long.toString(list.getFileCount()));
	}
}
