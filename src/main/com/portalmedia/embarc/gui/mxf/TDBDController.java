package com.portalmedia.embarc.gui.mxf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.helper.CleanInputPathHelper;
import com.portalmedia.embarc.gui.model.MXFSelectedFilesSummary;
import com.portalmedia.embarc.parser.FileFormatDetection;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.SectionDef;
import com.portalmedia.embarc.parser.mxf.IdentifierSetHelper;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.parser.mxf.MXFSection;
import com.portalmedia.embarc.parser.mxf.MXFService;
import com.portalmedia.embarc.parser.mxf.MXFServiceImpl;
import com.portalmedia.embarc.parser.mxf.ManifestParser;
import com.portalmedia.embarc.parser.mxf.ManifestParserImpl;
import com.portalmedia.embarc.parser.mxf.ManifestType;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import tv.amwa.maj.model.impl.AS07DMSIdentifierSetImpl;

/**
 * Text data and binary data pane view
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-07-13
 */
public class TDBDController extends AnchorPane {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getClass().getName());
	@FXML
	private Label sectionLabel;
	@FXML
	private Label selectedFilesLabel;
	@FXML
	private VBox tdbdVBox;

	public TDBDController() {
		ControllerMediatorMXF.getInstance().registerTDBDController(this);
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TDBDView.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setContent(SectionDef section) {
		if (section == MXFSection.TD) {
			sectionLabel.setText("Text DMS");
		} else {
			sectionLabel.setText("Binary DMS");
		}
		try {
			setContent(section == MXFSection.TD);
		} catch (FileNotFoundException e) {
			System.out.println("File not found error");
		}
	}

	private void setContent(boolean isTD) throws FileNotFoundException {
		final MXFSelectedFilesSummary summary = ControllerMediatorMXF.getInstance().getSelectedFilesSummary();
		HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> elements;

		if (isTD) {
			elements = summary.getAllTDElements();
		} else {
			elements = summary.getAllBDElements();
		}

		String message = "";
		if ((summary.getFileCount() > 1) || (elements == null || elements.size() == 0)) {
			BorderPane bp = new BorderPane();
			bp.setPadding(new Insets(10,10,10,10));
			if (summary.getFileCount() > 1) {
				message = "Multiple files selected, please select only one file.";
			} else {
				message = isTD ? "No text data present" : "No binary data present";
			}
			
			Label messageLabel = new Label(message);
			messageLabel.setStyle("-fx-text-fill: black;");
			bp.setTop(messageLabel);
			tdbdVBox.getChildren().add(bp);
			return;
		}

		int index = 1;
		SortedSet<String> keys = new TreeSet<>(elements.keySet());
		for (String key : keys) {
			final LinkedHashMap<MXFColumn, MetadataColumnDef> element = elements.get(key);
			VBox vbox = createElementCards(element, isTD, index, summary.getFilePath(), summary.getFileName());
			tdbdVBox.getChildren().add(vbox);
			index += 1;
		}
		setNumberOfSelectedFiles(summary.getFileCount());
	}

	private void setNumberOfSelectedFiles(int num) {
		selectedFilesLabel.setText(Integer.toString(num) + " file" + (num > 1 ? "s " : " ") + "selected");
	}

	private VBox createElementCards(
			LinkedHashMap<MXFColumn, MetadataColumnDef> element,
			boolean isTD,
			int index,
			String filePath,
			String fileName
	) throws FileNotFoundException {
		String streamId = element.get(MXFColumn.AS_07_Object_GenericStreamID).getCurrentValue();
		int streamIdInt = Integer.parseInt(streamId);
		ManifestType mfType = getMfType(streamIdInt, filePath);
		boolean isManifest = mfType == ManifestType.VALID_MANIFEST || mfType == ManifestType.INVALID_MANIFEST;

		GridPane centerGrid = new GridPane();
		centerGrid.setPadding(new Insets(0,10,10,10));
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPrefWidth(205);
		centerGrid.getColumnConstraints().add(cc);
		int identifierCount = 0;
		int lastIndex = 0;
		String isManifestValid = "";

		for (MXFColumn key : element.keySet()) {
			if (isManifest && key == MXFColumn.AS_07_Manifest_Valid) {
				isManifestValid = element.get(key).toString();
			}

			if (key == MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier ||
				key == MXFColumn.AS_07_TD_DMS_PrimaryRFC5646LanguageCode ||
				key == MXFColumn.AS_07_BD_DMS_PrimaryRFC5646LanguageCode ||
				key == MXFColumn.AS_07_Manifest ||
				key == MXFColumn.AS_07_Manifest_Valid) {
				continue;
			}

			int sortOrder = element.get(key).getColumnDef().getSortOrder();

			if (key == MXFColumn.AS_07_Object_Identifiers) {
				IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
				ArrayList<AS07DMSIdentifierSetImpl> identifiers = idSetHelper.createIdentifierListFromString(element.get(key).toString());
				for (int i = 0; i < identifiers.size(); i++) {
					AS07DMSIdentifierSetImpl id = identifiers.get(i);

					int row = identifierCount + sortOrder;
					centerGrid.add(getDescriptorLabel("Identifier " + (i+1), "Identifier", false), 0, row);

					String idValue = id.getIdentifierValue().replace("urn:uuid:", "");
					centerGrid.add(getDescriptorLabel("Value: " + idValue, "Identifier " + (i+1) + " Value is " + idValue, true), 1, row);
					row += 1;

					centerGrid.add(getDescriptorLabel("Role: " + id.getIdentifierRole(), "Identifier " + (i+1) + " Role is " + id.getIdentifierRole(), true), 1, row);
					row += 1;
					
					centerGrid.add(getDescriptorLabel("Type: " + id.getIdentifierType(), "Identifier " + (i+1) + " Type is " + id.getIdentifierType(), true), 1, row);
					row += 1;
					
					centerGrid.add(getDescriptorLabel("Comment: " + id.getIdentifierComment(), "Identifier " + (i+1) + " Comment is " + id.getIdentifierComment(), true), 1, row);
					row += 1;
				}
				identifierCount ++;
				continue;
			}

			String val = element.get(key).toString();
			centerGrid.add(getDescriptorLabel(key.getDisplayName(), "", false), 0, sortOrder);
			centerGrid.add(getDescriptorLabel(val, key.getDisplayName() + " is " + val, true), 1, sortOrder);
			if (sortOrder > lastIndex) {
				lastIndex = sortOrder;
			}
		}

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10,0,10,0));
		HBox header = getHeader(isTD, isManifest, isManifestValid, index, filePath, fileName, streamIdInt, streamId);
		vbox.getChildren().add(header);
		vbox.getChildren().add(centerGrid);
		return vbox;
	}
	
	private HBox getHeader(
			boolean isTD,
			boolean isManifest,
			String isManifestValid,
			int index,
			String filePath,
			String fileName,
			int streamIdInt,
			String streamId
	) throws FileNotFoundException {
		HBox header = new HBox();
		header.setAlignment(Pos.CENTER);
		header.setSpacing(20);
		header.setStyle("-fx-padding: 0 0 10 0;");
		String titlePrefix = isManifest ? "RDD 48 Manifest" : "";
		if (isManifest) {
			if (isManifestValid.equals("true")) {
				titlePrefix += " [VALID] - ";
			} else if (isManifestValid.equals("false")) {
				titlePrefix += " [INVALID] - ";
			} else {
				titlePrefix += " - ";
			}
		}
		String title = isTD ? String.format("%sText Data Element #%s", titlePrefix, index) : String.format("Binary Data Element #%s", index);
		Label label = new Label(title);
		label.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: black;");
		label.setFocusTraversable(true);
		header.getChildren().add(label);
		MXFService mxfService = new MXFServiceImpl(filePath);
		ByteBuffer bb = mxfService.GetGenericStream(streamIdInt);
		if (bb != null) {
			Button downloadIcon = getDownloadButton(streamId, filePath, fileName, isTD, title);
			header.getChildren().add(downloadIcon);
		}
		return header;
	}

	private Label getDescriptorLabel(String title, String accessibleText, boolean isTraversable) {
		Label label = new Label(title);
		label.setPadding(new Insets(2,0,2,5));
		label.setFocusTraversable(isTraversable);
		label.setAccessibleText(accessibleText);
		label.setStyle("-fx-text-fill: black;");
		return label;
	}

	private Button getDownloadButton(String streamId, String filePath, String fileName, Boolean isTD, String title) {
		FontIcon icon = new FontIcon(FontAwesomeSolid.DOWNLOAD);
		icon.setIconCode(FontAwesomeSolid.DOWNLOAD);
		icon.setAccessibleText("Select to download data for " + title);
		
		Button button = new Button();
		button.setGraphic(icon);
		button.getStyleClass().add("light-element-base");
		button.setOnAction(event -> {
			showDownloadDataDialog(streamId, filePath, fileName, isTD);
		});

		return button;
	}

	private ManifestType getMfType(int streamInt, String filePath) {
		ManifestType mfType = ManifestType.NOT_MANIFEST;
		try {
			MXFService mxfService = new MXFServiceImpl(filePath);
			ManifestParser mfParser = new ManifestParserImpl();
			ByteBuffer bb = mxfService.GetGenericStream(streamInt);
			mfType = mfParser.isManifest(bb);
		} catch (FileNotFoundException e) {
			System.out.println("File not found error thrown in TDBDController.getMfType");
		}
		return mfType;
	}

	private String getFileExt(int streamInt, String filePath) {
		String fileExt = "";
		try {
			MXFService mxfService = new MXFServiceImpl(filePath);
			ByteBuffer bb = mxfService.GetGenericStream(streamInt);
			fileExt = FileFormatDetection.getExtension(bb);
		} catch (Exception e) {
			System.out.println("Exception thrown in TDBDController.getFileExt");
		}
		return fileExt;
	}

	private void showDownloadDataDialog(String streamId, String filePath, String fileName, Boolean isTD) {
		int streamInt = Integer.parseInt(streamId);
		ManifestType mfType = getMfType(streamInt, filePath);
		String fileExt = getFileExt(streamInt, filePath);
		String cleanHomePath = CleanInputPathHelper.cleanString(System.getProperty("user.home"));
		String defaultOutputFileNameNonManifest = String.format("%s_%s_%s", fileName, streamId, "DATA_DOWNLOAD" + fileExt);
		String defaultOutputFileNameManifest = "manifest.xml";

		String fullPath = "";
		if (mfType == ManifestType.NOT_MANIFEST) {
			fullPath = String.format("%s%s%s", cleanHomePath, File.separator, defaultOutputFileNameNonManifest);
		} else {
			fullPath = String.format("%s%s%s", cleanHomePath, File.separator, defaultOutputFileNameManifest);
		}

		final Label writeFilesPathLabel = new Label(fullPath);
		writeFilesPathLabel.setStyle("-fx-text-fill: black;");
		final ChoiceDialog<ButtonData> dialog = new ChoiceDialog<>();
		final FontIcon icon = new FontIcon(FontAwesomeSolid.DOWNLOAD);
		icon.getStyleClass().add("write-files-icon");
		icon.setIconSize(20);
		dialog.setGraphic(icon);
		dialog.getDialogPane().setPrefSize(525, 150);
		String tdbd = isTD ? "Text Data" : "Binary Data";
		dialog.setTitle("Download " + tdbd);
		dialog.setHeaderText("Write " + tdbd + " File to Disk");
		dialog.initOwner(Main.getPrimaryStage());

		final ButtonType closeButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		final ButtonType writeFilesButtonType = new ButtonType("Download " + tdbd, ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().setAll(closeButtonType, writeFilesButtonType);

		dialog.setResultConverter((ButtonType type) -> {
			final ButtonBar.ButtonData data = type == null ? null : type.getButtonData();
			return data;
		});

		final Button chooseOutputDirButton = new Button();
		chooseOutputDirButton.setText("Save File To...");
		chooseOutputDirButton.setPrefWidth(125);

		if (mfType == ManifestType.NOT_MANIFEST) {
			chooseOutputDirButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					final FileChooser fileChooser = new FileChooser();
					fileChooser.setInitialDirectory(new File(cleanHomePath));
					fileChooser.setInitialFileName(defaultOutputFileNameNonManifest);
					fileChooser.setTitle("Save");
					final File file = fileChooser.showSaveDialog(null);
					if (file != null) {
						writeFilesPathLabel.setText(file.getAbsolutePath());
					}
				}
			});
		} else {
			chooseOutputDirButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					final DirectoryChooser dirChooser = new DirectoryChooser();
					dirChooser.setInitialDirectory(new File(cleanHomePath));
					dirChooser.setTitle("Select a directory");
					final File dir = dirChooser.showDialog(null);
					if (dir != null) {
						writeFilesPathLabel.setText(String.format("%s%s%s", dir.getAbsolutePath(), File.separator, "manifest.xml"));
					}
				}
			});
		}

		TextFlow textFlow = new TextFlow();
		VBox vbox = new VBox(2);
		vbox.setPadding(new Insets(10));
		textFlow.getChildren().add(writeFilesPathLabel);
		writeFilesPathLabel.setWrapText(true);
		writeFilesPathLabel.setPadding(new Insets(10,0,10,0));
		writeFilesPathLabel.setPrefWidth(500);
		chooseOutputDirButton.setPrefWidth(150);
		vbox.getChildren().add(chooseOutputDirButton);
		vbox.getChildren().add(textFlow);
		dialog.getDialogPane().setContent(vbox);

		final Optional<ButtonData> result = dialog.showAndWait();
		if (result.isPresent()) {
			if (result.get() == ButtonData.OK_DONE) {
				MXFService mxfService;
				try {
					mxfService = new MXFServiceImpl(filePath);
					mxfService.DownloadGenericStream(streamInt, writeFilesPathLabel.getText());
				} catch (FileNotFoundException e) {
					System.out.println("File not found error thrown in TDBDController.showDownloadDataDialog");
				}
			}
		}
	}
}
