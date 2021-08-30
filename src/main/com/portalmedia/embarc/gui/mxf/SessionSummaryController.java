package com.portalmedia.embarc.gui.mxf;

import java.net.URL;
import java.util.ResourceBundle;

import com.portalmedia.embarc.gui.dpx.ControllerMediatorDPX;
import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.helper.MXFFileList;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.gui.model.MXFFileInformationViewModel;
import com.portalmedia.embarc.gui.model.MXFSelectedFilesSummary;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediatorMXF.getInstance().registerSessionSummaryController(this);
		removeSelectedFiles.setVisible(false);
		ControllerMediatorMXF.getInstance().isEditingProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue o, Object ov, Object nv) {
				selectAllFiles.setDisable((Boolean) nv);
				deselectAllFiles.setDisable((Boolean) nv);
				removeSelectedFiles.setDisable((Boolean) nv);
			}
		});
		setFiles();
	}

	public void removeSelectedFiles() {
		ControllerMediatorMXF.getInstance().deleteSelectedFiles();
		setFiles();
	}

	public void selectAllFiles() {
		ControllerMediatorMXF.getInstance().selectAllFiles();
	}

	public void deselectAllFiles() {
		ControllerMediatorMXF.getInstance().deselectAllFiles();
	}

	public <T> void setFiles() {
		MXFFileList.getInstance();
		final long size = MXFFileList.getList().size();
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

	public <T> void setSelectedFileList(MXFSelectedFilesSummary list) {
		deselectAllFiles.setVisible(true);
		removeSelectedFiles.setVisible(true);
		filesSelectedText.setText("files selected");
		if (list.getFileCount() == 1) {
			filesSelectedText.setText("file selected");
		} else if (list.getFileCount() == 0) {
			selectAllFiles.setVisible(true);
			deselectAllFiles.setVisible(false);
			removeSelectedFiles.setVisible(false);
		}
		numberOfSelectedFiles.setText(Long.toString(list.getFileCount()));
	}
}
