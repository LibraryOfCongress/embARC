package com.portalmedia.embarc.gui.dpx;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.report.DPXReportService;
import com.portalmedia.embarc.system.UserPreferencesService;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

/**
 * Controls main UI, handles file drags, various modals, file processing
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class MainViewController implements Initializable {

	@FXML
	private AnchorPane mainViewPane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediatorDPX.getInstance().registerMainViewController(this);
	}

	// Creates a report containing the CRC32 checksum of each file
	public void createImageChecksumReport() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a file");

		final UserPreferencesService userPreferences = new UserPreferencesService();
		final String initialDirectory = userPreferences.getImageChecksumReportPath();

		if (initialDirectory != null && !initialDirectory.isEmpty()) {
			fileChooser.setInitialDirectory(new File(initialDirectory));
		}

		fileChooser.setInitialFileName(
				"ImageDataChecksumReport_" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".csv");
		final File file = fileChooser.showSaveDialog(mainViewPane.getScene().getWindow());
		try {
			if (file != null) {
				DPXReportService.WriteImageHashCsv(ControllerMediatorDPX.getInstance().getTable(), file.getAbsolutePath());
				userPreferences.setImageChecksumReportPath(file.getParent());
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	// Creates a csv with all dpx rule violations for each file
	public void createValidationReport() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a file");
		final UserPreferencesService userPreferences = new UserPreferencesService();
		final String initialDirectory = userPreferences.getValidationReportPath();

		if (initialDirectory != null && !initialDirectory.isEmpty()) {
			fileChooser.setInitialDirectory(new File(initialDirectory));
		}
		fileChooser.setInitialFileName(
				"DPXValidationReport_" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".csv");
		final File file = fileChooser.showSaveDialog(mainViewPane.getScene().getWindow());
		try {
			if (file != null) {
				DPXReportService.WriteValidationCsv(ControllerMediatorDPX.getInstance().getTable(),
						file.getAbsolutePath());
				userPreferences.setValidationReportPath(file.getParent());
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteSelectedFiles() {
		final ObservableList<DPXFileInformationViewModel> toDelete = ControllerMediatorDPX.getInstance()
				.getSelectedFileList();

		DPXFileListHelper.deleteSelectedRows(toDelete);
		final List<DPXFileInformationViewModel> newList = new LinkedList<>();
		for (final DPXFileInformationViewModel m : toDelete) {
			newList.add(m);
		}
		for (final DPXFileInformationViewModel m : newList) {
			DPXFileListHelper.deleteFileFromDB(m.getId());
		}
	}
	
}
