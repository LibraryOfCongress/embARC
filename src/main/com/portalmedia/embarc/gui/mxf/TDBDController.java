package com.portalmedia.embarc.gui.mxf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;

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
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import tv.amwa.maj.model.impl.AS07DMSIdentifierSetImpl;

/**
 * Text data and binary data pane view
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-07-13
 */
public class TDBDController extends AnchorPane {

	@FXML
	private Label sectionLabel;
    @FXML
    private VBox tdbdVBox;

	SectionDef section;

	public TDBDController() {
		ControllerMediatorMXF.getInstance().registerTDBDView(this);
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
		if (section == MXFSection.TD) sectionLabel.setText("Text Data");
		else sectionLabel.setText("Binary Data");
		setContent(section == MXFSection.TD);
	}

	private void setContent(boolean isTD) {
		final MXFSelectedFilesSummary summary = ControllerMediatorMXF.getInstance().getSelectedFilesSummary();
		HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> elements;

		if (isTD) elements = summary.getAllTDElements();
		else elements = summary.getAllBDElements();

		String message = "";
		if ((summary.getFileCount() > 1) || (elements == null || elements.size() == 0)) {
			BorderPane bp = new BorderPane();
			bp.setPadding(new Insets(10,10,10,10));
			if (summary.getFileCount() > 1) message = "Multiple files selected, please select only one file.";
			else message = isTD ? "No text data present" : "No binary data present";
			bp.setTop(new Label(message));
			tdbdVBox.getChildren().add(bp);
			tdbdVBox.setStyle("-fx-background-color: #d1d9de;");
			return;
		}

		ListView<BorderPane> list = new ListView<BorderPane>();
		int index = 1;
		for (final String key : elements.keySet()) {
			final LinkedHashMap<MXFColumn, MetadataColumnDef> element = elements.get(key);
			BorderPane bp = createElementCards(element, isTD, index, summary.getFilePath(), summary.getFileName());
			list.getItems().add(bp);
			index += 1;
		}
		list.setPrefHeight(725);
		list.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) { event.consume(); }
		});
		tdbdVBox.getChildren().add(list);
	}

	private BorderPane createElementCards(LinkedHashMap<MXFColumn, MetadataColumnDef> element, boolean isTD, int index, String filePath, String fileName) {
		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10,0,10,0));
		GridPane topGrid = new GridPane();
		HBox header = new HBox();
		header.setSpacing(20);
		topGrid.setPadding(new Insets(10, 10, 10, 10));
		String title = isTD ? String.format("Text Data Element #%s", index) : String.format("Binary Data Element #%s", index);
		Label label = new Label(title);
		label.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
		header.getChildren().add(label);
		FontAwesomeIconView downloadIcon = getDownloadIcon(element.get(MXFColumn.AS_07_Object_GenericStreamID).getCurrentValue(), filePath, fileName, isTD);
		header.getChildren().add(downloadIcon);
		topGrid.add(header, 0, 0);
		bp.setTop(topGrid);

		GridPane centerGrid = new GridPane();
		centerGrid.setPadding(new Insets(0,10,10,10));
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPrefWidth(205);
		centerGrid.getColumnConstraints().add(cc);
		int identifierCount = 0;
		
		for (MXFColumn key : element.keySet()) {
			if (key == MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier ||
				key == MXFColumn.AS_07_TD_DMS_PrimaryRFC5646LanguageCode ||
				key == MXFColumn.AS_07_BD_DMS_PrimaryRFC5646LanguageCode) continue;

			int sortOrder = element.get(key).getColumnDef().getSortOrder();

			if (key == MXFColumn.AS_07_Object_Identifiers) {
				IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
				ArrayList<AS07DMSIdentifierSetImpl> identifiers = idSetHelper.createIdentifierListFromString(element.get(key).toString());
				for (int i = 0; i < identifiers.size(); i++) {
					AS07DMSIdentifierSetImpl id = identifiers.get(i);
					int row = identifierCount + sortOrder;
					centerGrid.add(getDescriptorLabel("Identifier " + (i+1) +": ", "#e3e3e3", 400, ""), 0, row);

					String value = id.getIdentifierValue();
					value = value.replace("urn:uuid:", "");
					centerGrid.add(getDescriptorLabel("Value: " + value, "#e3e3e3", 400, ""), 1, row);
					row += 1;
					centerGrid.add(getDescriptorLabel("", "#e3e3e3", 400, ""), 0, row);
					centerGrid.add(getDescriptorLabel("Role: " + id.getIdentifierRole(), "#e3e3e3", 400, ""), 1, row);
					row += 1;
					centerGrid.add(getDescriptorLabel("", "#e3e3e3", 400, ""), 0, row);
					centerGrid.add(getDescriptorLabel("Type: " + id.getIdentifierType(), "#e3e3e3", 400, ""), 1, row);
					row += 1;
					centerGrid.add(getDescriptorLabel("", "#e3e3e3", 400, ""), 0, row);
					centerGrid.add(getDescriptorLabel("Comment: " + id.getIdentifierComment(), "#e3e3e3", 400, ""), 1, row);
					row += 1;
				}
				identifierCount ++;
				continue;
			}

			String val = element.get(key).toString();
			centerGrid.add(getDescriptorLabel(key.getDisplayName() + ": ", "#e3e3e3", 400, ""), 0, sortOrder);
			centerGrid.add(getDescriptorLabel(val, "#e3e3e3", 400, ""), 1, sortOrder);
		}

		bp.setCenter(centerGrid);
		return bp;
	}

	private Label getDescriptorLabel(String title, String color, int width, String borderClass) {
		Label label = new Label(title);
		if (color != "") label.setStyle("-fx-background-color: " + color);
		if (borderClass != "") label.getStyleClass().add(borderClass);
		label.getStyleClass().add("descriptor-rounded");
		label.setPrefWidth(width);
		label.setPadding(new Insets(2,5,2,5));
		label.setTooltip(new Tooltip(title));
		return label;
	}

	private FontAwesomeIconView getDownloadIcon(String streamId, String filePath, String fileName, Boolean isTD) {
		FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);
		icon.setStyleClass("download-icon");
		icon.setSize("18");
		Tooltip.install(icon, new Tooltip("Download data"));
		icon.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				showDownloadDataDialog(streamId, filePath, fileName, isTD);
			}
		});
		return icon;
	}

	public void showDownloadDataDialog(String streamId, String filePath, String fileName, Boolean isTD) {
		final ChoiceDialog<ButtonData> dialog = new ChoiceDialog<>();
		final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);
		icon.setStyleClass("write-files-icon");
		icon.setSize("20");
		dialog.setGraphic(icon);
		dialog.getDialogPane().setPrefSize(525, 120);
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

		final GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));

		String cleanHomePath = CleanInputPathHelper.cleanString(System.getProperty("user.home"));
		final Label writeFilesPath = new Label(cleanHomePath);
		final Button chooseOutputDirButton = new Button();
		chooseOutputDirButton.setText("Save File To...");
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
		writeFilesPath.setDisable(true);
		grid.add(chooseOutputDirButton, 0, 4);
		grid.add(writeFilesPath, 1, 4);

		dialog.getDialogPane().setContent(grid);
		final Optional<ButtonData> result = dialog.showAndWait();
		if (result.isPresent()) {
			if (result.get() == ButtonData.OK_DONE) {
				MXFService mxfService;
				try {
					mxfService = new MXFServiceImpl(filePath);
					int streamInt = Integer.parseInt(streamId);
					try {
						ByteBuffer bb = mxfService.GetGenericStream(Integer.parseInt(streamId));
						String ext = FileFormatDetection.getExtension(bb);
						String fileType = "DATA_DOWNLOAD" + ext;
//						String fileType = isTD ? "TEXT_DATA_DOWNLOAD.xml" : "BINARY_DATA_DOWNLOAD.tiff";
						String outputPath = String.format("%s/%s_%s_%s", writeFilesPath.getText(), fileName, streamId, fileType);
						mxfService.DownloadGenericStream(streamInt, outputPath);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
