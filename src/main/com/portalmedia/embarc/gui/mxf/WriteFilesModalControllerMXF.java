package com.portalmedia.embarc.gui.mxf;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.helper.CleanInputPathHelper;
import com.portalmedia.embarc.system.UserPreferencesService;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
public class WriteFilesModalControllerMXF {
	
	// this is almost identical to the WriteFilesModalController in gui.dpx
	// TODO: extract differences and combine into one to get rid of duplicate code

	private static class ControllerHolder {
		private static final WriteFilesModalControllerMXF INSTANCE = new WriteFilesModalControllerMXF();
	}

	public static WriteFilesModalControllerMXF getInstance() {
		return ControllerHolder.INSTANCE;
	}

	private WriteFilesModalControllerMXF() {}
	

	// Present the user with a dialog allowing them to select options for writing files
	public void showWriteFilesDialog() {
		final UserPreferencesService userPreferences = new UserPreferencesService();
		final ChoiceDialog<ButtonData> dialog = new ChoiceDialog<>();
		final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);
		icon.setStyleClass("write-files-icon");
		icon.setSize("20");
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

		// Create the username and password labels and fields.
		final GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 10, 10, 10));

		final CheckBox writeEditedCb = new CheckBox("Write only edited files");
		// Set the checked value based on user preferences
		writeEditedCb.setSelected(userPreferences.getWriteOnlyEditedFiles());

		final CheckBox saveAsCb = new CheckBox("Save files to a separate directory");
		// Set the value based on user preferences
		saveAsCb.setSelected(userPreferences.getSaveToSeparateDirectory());

		// Set report path based on user preferences or user home
		String reportDir = userPreferences.getImageChecksumValidationReportPath();
		String cleanHomePath = CleanInputPathHelper.cleanString(System.getProperty("user.home"));

		if (reportDir == null || reportDir.isEmpty()) {
			reportDir = cleanHomePath;
		}

		// Set path for saving written files to
		String outputDir = userPreferences.getSaveToPath();
		if (outputDir == null || outputDir.isEmpty()) {
			outputDir = cleanHomePath;
		}

		final Label writeFilesPath = new Label(outputDir);
		final Button chooseOutputDirButton = new Button();
		chooseOutputDirButton.setText("Save Files To...");
		chooseOutputDirButton.setPrefWidth(125);
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
		chooseOutputDirButton.setDisable(!saveAsCb.isSelected());
		writeFilesPath.setDisable(true);

		saveAsCb.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				chooseOutputDirButton.setDisable(!newValue);
				writeFilesPath.setDisable(!newValue);
			}
		});
		grid.add(writeEditedCb, 0, 0, 2, 1);
		grid.add(saveAsCb, 0, 1, 2, 1);
		grid.add(chooseOutputDirButton, 0, 2);
		grid.add(writeFilesPath, 1, 2);

		dialog.getDialogPane().setContent(grid);
		final Optional<ButtonData> result = dialog.showAndWait();
		if (result.isPresent()) {
			if (result.get() == ButtonData.OK_DONE) {
				// Set user preferences
				userPreferences.setWriteOnlyEditedFiles(writeEditedCb.isSelected());
				userPreferences.setSaveToSeparateDirectory(saveAsCb.isSelected());
				if (saveAsCb.isSelected()) {
					final String newOutputDir = writeFilesPath.getText();
					userPreferences.setSaveToPath(newOutputDir);
				}

				final String tmpWriteFilesPath = saveAsCb.isSelected() ? writeFilesPath.getText() : "";

				WriteFilesDialogMXF d;
				try {
					d = new WriteFilesDialogMXF(tmpWriteFilesPath, writeEditedCb.isSelected());
					d.initOwner(Main.getPrimaryStage());
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}
	
}
