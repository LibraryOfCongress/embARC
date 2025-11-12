package com.portalmedia.embarc.gui.dpx;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.helper.CleanInputPathHelper;
import com.portalmedia.embarc.system.UserPreferencesService;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * Controls write files modal
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-03-11
 */
public class WriteFilesModalController {

	private static class ControllerHolder {
		private static final WriteFilesModalController INSTANCE = new WriteFilesModalController();
	}

	public static WriteFilesModalController getInstance() {
		return ControllerHolder.INSTANCE;
	}

	private WriteFilesModalController() {}

	// Present the user with a dialog allowing them to select options for writing files
	public void showWriteFilesDialog() {
		final UserPreferencesService userPreferences = new UserPreferencesService();
		final ChoiceDialog<ButtonData> dialog = new ChoiceDialog<>();
		final FontIcon icon = new FontIcon(FontAwesomeSolid.DOWNLOAD);
		icon.getStyleClass().add("write-files-icon");
		icon.setIconSize(20);
		dialog.setGraphic(icon);
		dialog.getDialogPane().setPrefSize(525, 320);
		dialog.setTitle("Write Files");
		dialog.setHeaderText("Write Files to Disk");
		dialog.initOwner(Main.getPrimaryStage());

		// Set the button types.
		final ButtonType loginButtonType = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
		final ButtonType writeFilesButtonType = new ButtonType("Write Files", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().setAll(loginButtonType, writeFilesButtonType);

		dialog.setResultConverter((ButtonType type) -> {
			final ButtonBar.ButtonData data = type == null ? null : type.getButtonData();
			return data;
		});

		final GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 10, 10, 10));

		final CheckBox writeEditedCb = new CheckBox("Write only edited files");
		// Set the checked value based on user preferences
		writeEditedCb.setSelected(userPreferences.getWriteOnlyEditedFiles());

		final CheckBox reportCb = new CheckBox("Create an image data checksum report");
		// Set the value based on user preferences
		reportCb.setSelected(userPreferences.getSaveImageChecksums());

		final CheckBox saveAsCb = new CheckBox("Save Files to a separate directory");
		// Set the value based on user preferences
		saveAsCb.setSelected(userPreferences.getSaveToSeparateDirectory());

		// Set report path based on user preferences or user home
		String reportDir = userPreferences.getImageChecksumValidationReportPath();
		String cleanHomePath = CleanInputPathHelper.cleanString(System.getProperty("user.home"));

		if (reportDir == null || reportDir.isEmpty()) {
			reportDir = cleanHomePath;
		}

		final Label reportPath = new Label(reportDir + "/" + "ImageDataChecksumComparisonReport_"
				+ new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".csv");
		final Button selectReportPathButton = new Button();
		selectReportPathButton.setText("Save Checksums To...");
		selectReportPathButton.setPrefWidth(250);

		selectReportPathButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				final FileChooser fileChooser = new FileChooser();
				String cleanHomePath = CleanInputPathHelper.cleanString(System.getProperty("user.home"));
				fileChooser.setInitialDirectory(new File(cleanHomePath));
				fileChooser.setInitialFileName("ChecksumReport" + "_" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".csv");
				fileChooser.setTitle("Select a file");
				final File file = fileChooser.showSaveDialog(null);
				if (file != null) {
					reportPath.setText(file.getAbsolutePath());
				}
			}
		});

		// Set path for saving written files to
		String outputDir = userPreferences.getSaveToPath();
		if (outputDir == null || outputDir.isEmpty()) {
			outputDir = cleanHomePath;
		}

		final Label writeFilesPath = new Label(outputDir);
		final Button chooseOutputDirButton = new Button();
		chooseOutputDirButton.setText("Save Files To...");
		chooseOutputDirButton.setPrefWidth(250);
		chooseOutputDirButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				final DirectoryChooser dirChooser = new DirectoryChooser();
				String cleanHomePath = CleanInputPathHelper.cleanString(System.getProperty("user.home"));
				dirChooser.setInitialDirectory(new File(cleanHomePath));
				dirChooser.setTitle("Select a directory");
				final File dir = dirChooser.showDialog(null);
				if (dir != null) {
					writeFilesPath.setText(dir.getAbsolutePath());
				}
			}
		});
		selectReportPathButton.setDisable(!reportCb.isSelected());
		chooseOutputDirButton.setDisable(!saveAsCb.isSelected());
		reportPath.setDisable(true);
		writeFilesPath.setDisable(true);
		reportCb.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				selectReportPathButton.setDisable(!newValue);
				reportPath.setDisable(!newValue);
			}
		});
		saveAsCb.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				chooseOutputDirButton.setDisable(!newValue);
				writeFilesPath.setDisable(!newValue);
			}
		});
		grid.add(writeEditedCb, 0, 0, 2, 1);
		grid.add(reportCb, 0, 1, 2, 1);
		grid.add(selectReportPathButton, 0, 2);
		grid.add(reportPath, 1, 2);
		grid.add(saveAsCb, 0, 3, 2, 1);
		grid.add(chooseOutputDirButton, 0, 4);
		grid.add(writeFilesPath, 1, 4);

		dialog.getDialogPane().setContent(grid);
		final Optional<ButtonData> result = dialog.showAndWait();
		if (result.isPresent()) {
			if (result.get() == ButtonData.OK_DONE) {
				// Set user preferences
				userPreferences.setSaveImageChecksums(reportCb.isSelected());
				userPreferences.setWriteOnlyEditedFiles(writeEditedCb.isSelected());
				userPreferences.setSaveToSeparateDirectory(saveAsCb.isSelected());
				if (reportCb.isSelected()) {
					final File p = new File(reportPath.getText());
					final String newReportDir = p.getParent();
					userPreferences.setImageChecksumValidationReportPath(newReportDir);
				}
				if (saveAsCb.isSelected()) {
					final String newOutputDir = writeFilesPath.getText();
					userPreferences.setSaveToPath(newOutputDir);
				}

				final String tmpWriteFilesPath = saveAsCb.isSelected() ? writeFilesPath.getText() : "";
				final String tmpReportPath = reportCb.isSelected() ? reportPath.getText() : "";

				final WriteFilesDialog d = new WriteFilesDialog(tmpWriteFilesPath, tmpReportPath,
						writeEditedCb.isSelected());

				d.initOwner(Main.getPrimaryStage());
			}
		}

	}
	
}
