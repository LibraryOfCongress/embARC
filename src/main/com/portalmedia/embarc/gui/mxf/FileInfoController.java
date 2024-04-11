package com.portalmedia.embarc.gui.mxf;

import java.io.IOException;

import com.portalmedia.embarc.gui.model.MXFSelectedFilesSummary;
import com.portalmedia.embarc.parser.mxf.MXFFileInfo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * File info pane view
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-07-13
 */
public class FileInfoController extends AnchorPane {
	@FXML
	private Label sectionLabel;
	@FXML
	private Label selectedFilesLabel;
	@FXML
	private Label fileNameLabel;
	@FXML
	private Label filePathLabel;
	@FXML
	private Label formatLabel;
	@FXML
	private Label versionLabel;
	@FXML
	private Label profileLabel;
	@FXML
	private Label fileSizeLabel;
	@FXML
	private Label pictureTrackCountLabel;
	@FXML
	private Label soundTrackCountLabel;
	@FXML
	private Label otherTrackCountLabel;
	@FXML
	private Label tdCountLabel;
	@FXML
	private Label bdCountLabel;

	public FileInfoController() {
		ControllerMediatorMXF.getInstance().registerFileInfoController(this);
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FileInfoView.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setContent() {
		sectionLabel.setText("File Information");
		final MXFSelectedFilesSummary summary = ControllerMediatorMXF.getInstance().getSelectedFilesSummary();

		fileNameLabel.setText(summary.getFileName());
		fileNameLabel.setFocusTraversable(true);
		fileNameLabel.setAccessibleText("Filename is " + summary.getFileName());

		filePathLabel.setText(summary.getFilePath());
		filePathLabel.setFocusTraversable(true);
		filePathLabel.setAccessibleText("File path is " + summary.getFilePath());

		formatLabel.setText(summary.getFileInfoDisplayValues(MXFFileInfo.FORMAT));
		formatLabel.setFocusTraversable(true);
		formatLabel.setAccessibleText("Format is " + summary.getFileInfoDisplayValues(MXFFileInfo.FORMAT));

		versionLabel.setText(summary.getFileInfoDisplayValues(MXFFileInfo.VERSION));
		versionLabel.setFocusTraversable(true);
		versionLabel.setAccessibleText("Version is " + summary.getFileInfoDisplayValues(MXFFileInfo.VERSION));

		profileLabel.setText(summary.getFileInfoDisplayValues(MXFFileInfo.PROFILE));
		profileLabel.setFocusTraversable(true);
		profileLabel.setAccessibleText("Profile is " + summary.getFileInfoDisplayValues(MXFFileInfo.PROFILE));

		fileSizeLabel.setText(summary.getFileInfoDisplayValues(MXFFileInfo.FILE_SIZE));
		fileSizeLabel.setFocusTraversable(true);
		fileSizeLabel.setAccessibleText("File size is " + summary.getFileInfoDisplayValues(MXFFileInfo.FILE_SIZE));

		pictureTrackCountLabel.setText(summary.getFileInfoDisplayValues(MXFFileInfo.PICTURE_TRACK_COUNT));
		pictureTrackCountLabel.setFocusTraversable(true);
		pictureTrackCountLabel.setAccessibleText("Picture track count " + summary.getFileInfoDisplayValues(MXFFileInfo.PICTURE_TRACK_COUNT));

		soundTrackCountLabel.setText(summary.getFileInfoDisplayValues(MXFFileInfo.SOUND_TRACK_COUNT));
		soundTrackCountLabel.setFocusTraversable(true);
		soundTrackCountLabel.setAccessibleText("Sound track count " + summary.getFileInfoDisplayValues(MXFFileInfo.SOUND_TRACK_COUNT));

		otherTrackCountLabel.setText(summary.getFileInfoDisplayValues(MXFFileInfo.OTHER_TRACK_COUNT));
		otherTrackCountLabel.setFocusTraversable(true);
		otherTrackCountLabel.setAccessibleText("Other track count " + summary.getFileInfoDisplayValues(MXFFileInfo.OTHER_TRACK_COUNT));

		tdCountLabel.setText(summary.getFileInfoDisplayValues(MXFFileInfo.TD_COUNT));
		tdCountLabel.setFocusTraversable(true);
		tdCountLabel.setAccessibleText("Text data count " + summary.getFileInfoDisplayValues(MXFFileInfo.TD_COUNT));

		bdCountLabel.setText(summary.getFileInfoDisplayValues(MXFFileInfo.BD_COUNT));
		bdCountLabel.setFocusTraversable(true);
		bdCountLabel.setAccessibleText("Binary data count " + summary.getFileInfoDisplayValues(MXFFileInfo.BD_COUNT));

		setNumberOfSelectedFiles(summary.getFileCount());
	}

	private void setNumberOfSelectedFiles(int num) {
		selectedFilesLabel.setText(Integer.toString(num) + " file" + (num > 1 ? "s " : " ") + "selected");
	}

}