package com.portalmedia.embarc.gui.dpx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.portalmedia.embarc.gui.model.SelectedFilesSummary;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * UI component for displaying filename and file path
 * Also allows user to view an image
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class GeneralForm extends AnchorPane {
	@FXML
	private Label sectionLabel;
	@FXML
	private Label selectedFilesLabel;
	@FXML
	private AnchorPane generalInfo;

	String filePathString;

	final Logger logger = Logger.getLogger(this.getClass());

	public GeneralForm() {
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GeneralForm.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}

		final SelectedFilesSummary summary = ControllerMediatorDPX.getInstance().getSelectedFilesSummary();
		setNumberOfSelectedFiles(summary.getFileCount());

		final HBox gridBox = new HBox();
		gridBox.setPadding(new Insets(10, 0, 0, 10));
		final GridPane grid = new GridPane();
		gridBox.getChildren().add(grid);
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(15);
		grid.setVgap(15);

		generalInfo.getChildren().add(gridBox);

		final Label fileNameText = new Label(summary.getFileName());
		fileNameText.setFocusTraversable(false);
		grid.add(fileNameText, 1, 0);
		final Label fileNameLabel = new Label("Filename ");
		fileNameLabel.setStyle("-fx-font-size: 12.0");
		fileNameLabel.setAccessibleText("File name is " + fileNameText.getText());
		fileNameLabel.setFocusTraversable(true);
		grid.add(fileNameLabel, 0, 0);

		filePathString = summary.getFilePath();
		final Label filePathText = new Label(filePathString);
		filePathText.setFocusTraversable(false);
		grid.add(filePathText, 1, 1);
		final Label filePath = new Label("File Path ");
		filePath.setStyle("-fx-font-size: 12.0");
		filePath.setAccessibleText("File path is " + filePathString);
		filePath.setFocusTraversable(true);
		grid.add(filePath, 0, 1);

		if (filePathString != null && !filePathString.equals("{multiple values}")) {
			final Label viewImage = new Label("View Image ");
			viewImage.setStyle("-fx-font-size: 12.0");
			viewImage.setAccessibleText("View image");
			viewImage.setFocusTraversable(false);
			grid.add(viewImage, 0, 2);

			final FontIcon viewImageIcon = new FontIcon();
			viewImageIcon.setIconCode(FontAwesomeSolid.EXTERNAL_LINK_ALT);
			viewImageIcon.setAccessibleText("Select to open image viewer");
			final Button b = new Button();
			b.setStyle("-fx-text-fill: black; -fx-base: #E5E5E5;");
			b.setGraphic(viewImageIcon);
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					showDiaglog();
				}
			});
			grid.add(b, 1, 2);
		}

	}

	public void setTitle(String title) {
		sectionLabel.setText(title);
		sectionLabel.setAccessibleText(title);
		sectionLabel.setFocusTraversable(true);
	}

	public void showDiaglog() {
		Path tempPath;
		try {

			final String os = System.getProperty("os.name");
			if (os != null && os.startsWith("Mac")) {
				tempPath = Files.createTempFile("ffplay", "");

				try (InputStream in = getClass().getResourceAsStream("/resources/ffplay_mac")) {
					Files.copy(in, tempPath, StandardCopyOption.REPLACE_EXISTING);
				}

				final PosixFileAttributeView view = Files.getFileAttributeView(tempPath, PosixFileAttributeView.class);
				if (view != null) {
					final Set<PosixFilePermission> perms = view.readAttributes().permissions();
					if (perms.add(PosixFilePermission.OWNER_EXECUTE)) {
						view.setPermissions(perms);
					}
				}

				final List<String> params = java.util.Arrays.asList(tempPath.toString(), filePathString, "-x", "500",
						"-y", "500");
				final ProcessBuilder b = new ProcessBuilder(params);
				logger.error("Starting show image mac");
				try {
					final Process p = b.start();
					p.getOutputStream().close();

				} catch (final IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getStackTrace());
				}

			} else {
				tempPath = Files.createTempFile("ffplay", "exe");

				try (InputStream in = getClass().getResourceAsStream("/resources/ffplay_win.exe")) {
					Files.copy(in, tempPath, StandardCopyOption.REPLACE_EXISTING);
				}

				final List<String> params = java.util.Arrays.asList(tempPath.toString(), filePathString, "-x", "500",
						"-y", "500");
				final ProcessBuilder b = new ProcessBuilder(params);
				try {
					final Process p = b.start();
					p.getOutputStream().close();

				} catch (final IOException e) {
					System.out.println("Error displaying DPX sequence");
				}
			}

		} catch (final IOException e1) {
			System.out.println("Error displaying DPX sequence final");
		}
		return;
	}

	private void setNumberOfSelectedFiles(int num) {
		selectedFilesLabel.setText(Integer.toString(num) + " file" + (num > 1 ? "s " : " ") + "selected");
		selectedFilesLabel.setFocusTraversable(true);
	}
}
