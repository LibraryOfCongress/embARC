package com.portalmedia.embarc.gui.mxf;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.portalmedia.embarc.gui.model.AS07TimecodeLabelSubdescriptor;
import com.portalmedia.embarc.gui.model.MXFSelectedFilesSummary;
import com.portalmedia.embarc.parser.mxf.MXFFileDescriptorResult;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import tv.amwa.maj.enumeration.AlphaTransparencyType;
import tv.amwa.maj.enumeration.ColorSitingType;
import tv.amwa.maj.enumeration.ElectroSpatialFormulation;
import tv.amwa.maj.enumeration.FieldNumber;
import tv.amwa.maj.enumeration.SignalStandardType;
import tv.amwa.maj.exception.PropertyNotPresentException;
import tv.amwa.maj.model.CodecDefinition;
import tv.amwa.maj.model.ContainerDefinition;
import tv.amwa.maj.model.Locator;
import tv.amwa.maj.model.SubDescriptor;
import tv.amwa.maj.model.impl.AS07DateTimeDescriptorImpl;
import tv.amwa.maj.model.impl.AncillaryPacketsDescriptorImpl;
import tv.amwa.maj.model.impl.CDCIDescriptorImpl;
import tv.amwa.maj.model.impl.FFV1PictureSubDescriptorImpl;
import tv.amwa.maj.model.impl.TimedTextDescriptorImpl;
import tv.amwa.maj.model.impl.WAVEPCMDescriptorImpl;
import tv.amwa.maj.record.AUID;
import tv.amwa.maj.record.Rational;

/**
 * UI component that displays MXF file descriptor data
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-07-02
 */
public class DescriptorMXFController extends AnchorPane {
	@FXML
	private Label sectionLabel;
	@FXML
	private Label selectedFilesLabel;
	@FXML
	private Accordion descriptorsAccordion;
	@FXML
	private TitledPane pictureTitledPane;
	@FXML
	private TitledPane soundTitledPane;
	@FXML
	private TitledPane otherTitledPane;
	@FXML
	private VBox pictureVBox;
	@FXML
	private VBox soundVBox;
	@FXML
	private VBox otherVBox;

	private String pnp = "PROPERTY NOT PRESENT";

	public DescriptorMXFController() {
		ControllerMediatorMXF.getInstance().registerDescriptorMXFController(this);
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DescriptorMXF.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
		final MXFSelectedFilesSummary summary = ControllerMediatorMXF.getInstance().getSelectedFilesSummary();
		setFileDescriptors(summary);
		setNumberOfSelectedFiles(summary.getFileCount());
		descriptorsAccordion.setExpandedPane(pictureTitledPane);
	}

	private void setNumberOfSelectedFiles(int num) {
		selectedFilesLabel.setText(Integer.toString(num) + " file" + (num == 1 ? " " : "s ") + "selected");
	}

	public void setTitle(String title) {
		sectionLabel.setText(title);
	}

	private void setFileDescriptors(MXFSelectedFilesSummary summary) {
		MXFFileDescriptorResult descriptors = summary.getFileDescriptors();
		if (!Objects.isNull(descriptors)) {
			setPictureDescriptors(descriptors);
			setSoundDescriptors(descriptors);
			setOtherDescriptors(descriptors);
		} else {
			Label label = new Label();
			int fileCount = summary.getFileCount();
			if (fileCount == 0) {
				label.setText("No files selected, select a file to view descriptors.");
			} else if (fileCount > 1) {
				label.setText("Multiple files selected, please select only one file.");
			}
			// TODO: Set this label somewhere
		}
	}

	private void setPictureDescriptors(MXFFileDescriptorResult descriptors) {
		List<CDCIDescriptorImpl> cdciDescriptors = descriptors.getCDCIDescriptor();
		//List<RGBADescriptorImpl> rgbaDescriptors = descriptors.getRGBADescriptors();
		//List<PictureDescriptorImpl> pictureDescriptors = descriptors.getPictureDescriptors();

		int index = 1;
		if (cdciDescriptors.size() > 0) {
			for (CDCIDescriptorImpl cdci : cdciDescriptors) {
				VBox card = createCDCICard(cdci, index);
				//card.setStyle("-fx-background-color: #e6ecf0;");
				pictureVBox.getChildren().add(card);
				index++;
			}
		} else {
			VBox noPicturePane = new VBox();
			noPicturePane.getChildren().add(new Text("No Picture Descriptors Present"));
			pictureVBox.getChildren().add(noPicturePane);
		}

		pictureTitledPane.setText("Picture Descriptors (" + (index - 1) + ")");

		// TODO: needed?
		pictureVBox.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) { event.consume(); }
		});
	}

	private void setSoundDescriptors(MXFFileDescriptorResult descriptors) {
		List<WAVEPCMDescriptorImpl> wavePCMDescriptors = descriptors.getWavePCMDescriptors();
		//List<SoundDescriptorImpl> soundDescriptors = descriptors.getSoundDescriptors();

		int index = 1;
		if (wavePCMDescriptors.size() > 0) {
			for (WAVEPCMDescriptorImpl wave : wavePCMDescriptors) {
				VBox card = createWaveCard(wave, index);
				soundVBox.getChildren().add(card);
				index++;
			}
		} else {
			BorderPane noWavePane = new BorderPane();
			noWavePane.getChildren().add(new Text("No Sound Descriptors Present"));
			soundVBox.getChildren().add(noWavePane);
		}
		
		soundTitledPane.setText("Sound Descriptors (" + (index - 1) + ")");

		// TODO: needed?
		soundVBox.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) { event.consume(); }
		});
	}

	private void setOtherDescriptors(MXFFileDescriptorResult descriptors) {
		List<AncillaryPacketsDescriptorImpl> ancillaryDescriptors = descriptors.getAncillaryPacketsDescriptors();
		List<TimedTextDescriptorImpl> timedTextDescriptors = descriptors.getTimedTextDescriptor();
//		List<STLDescriptorImpl> stlDescriptors = descriptors.getSTLDescriptors();
//		List<VBIDescriptorImpl> vbiDescriptors = descriptors.getVBIDescriptors();
		List<AS07DateTimeDescriptorImpl> dateTimeDescriptors = descriptors.getAS07DateTimeDescriptor();

		int totalOtherDescriptors = (int)ancillaryDescriptors.size() + (int)dateTimeDescriptors.size() + (int)timedTextDescriptors.size();

		int index = 1;
		if (totalOtherDescriptors > 0) {
			for (AS07DateTimeDescriptorImpl dateTime : dateTimeDescriptors) {
				VBox card = createDateTimeCard(dateTime, index);
				otherVBox.getChildren().add(card);
				index++;
			}
			for (AncillaryPacketsDescriptorImpl ancillary : ancillaryDescriptors) {
				VBox card = createAncillaryCard(ancillary, index);
				otherVBox.getChildren().add(card);
				index++;
			}
			for (TimedTextDescriptorImpl timedText : timedTextDescriptors) {
				VBox card = createTimedTextCard(timedText, index);
				otherVBox.getChildren().add(card);
				index++;
			}
		} else {
			BorderPane noOtherPane = new BorderPane();
			noOtherPane.getChildren().add(new Text("No Other Descriptors Present"));
			otherVBox.getChildren().add(noOtherPane);
		}

		otherTitledPane.setText("Other Descriptors (" + (index - 1) + ")");

		// TODO: needed?
		otherVBox.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) { event.consume(); }
		});
	}

	/* 
	 * CDCI 
	 */
	private VBox createCDCICard(CDCIDescriptorImpl cdci, int index) {
		VBox vbox = new VBox();
		
		Label cardTitle = new Label("CDCI Descriptor");
		cardTitle.setFocusTraversable(true);
		cardTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14.0;");
		vbox.getChildren().add(cardTitle);

		BorderPane filePane = new BorderPane();
		Label filePaneTitle = new Label("File Descriptors");
		filePaneTitle.setUnderline(true);
		filePaneTitle.setFocusTraversable(true);
		filePaneTitle.setStyle("-fx-font-size: 13.0;");
		GridPane fileGridPane = new GridPane();
		populateCDCIFileDescriptorsGrid(fileGridPane, cdci);
		filePane.setTop(filePaneTitle);
		filePane.setCenter(fileGridPane);
		filePane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(filePane);

		BorderPane pictureEssencePane = new BorderPane();
		Label pictureEssenceTitle = new Label("Picture Essence Descriptors");
		pictureEssenceTitle.setUnderline(true);
		pictureEssenceTitle.setFocusTraversable(true);
		pictureEssenceTitle.setStyle("-fx-font-size: 13.0;");
		GridPane pictureEssenceGridPane = new GridPane();
		populateCDCIPictureEssenceGrid(pictureEssenceGridPane, cdci);
		pictureEssencePane.setTop(pictureEssenceTitle);
		pictureEssencePane.setCenter(pictureEssenceGridPane);
		pictureEssencePane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(pictureEssencePane);

		BorderPane cdciPane = new BorderPane();
		Label cdciTitle = new Label("CDCI Descriptors");
		cdciTitle.setUnderline(true);
		cdciTitle.setFocusTraversable(true);
		cdciTitle.setStyle("-fx-font-size: 13.0;");
		GridPane cdciGridPane = new GridPane();
		populateCDCIGrid(cdciGridPane, cdci);
		cdciPane.setTop(cdciTitle);
		cdciPane.setCenter(cdciGridPane);
		cdciPane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(cdciPane);

		BorderPane ffv1Pane = new BorderPane();
		Label ffv1Title = new Label("FFV1");
		ffv1Title.setUnderline(true);
		ffv1Title.setFocusTraversable(true);
		ffv1Title.setStyle("-fx-font-size: 13.0;");
		GridPane ffv1GridPane = new GridPane();
		populateCDCIFFV1Grid(ffv1GridPane, cdci);
		ffv1Pane.setTop(ffv1Title);
		ffv1Pane.setCenter(ffv1GridPane);
		ffv1Pane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(ffv1Pane);

		BorderPane calculatedPane = new BorderPane();
		Label calculatedTitle = new Label("Calculated");
		calculatedTitle.setUnderline(true);
		calculatedTitle.setFocusTraversable(true);
		calculatedTitle.setStyle("-fx-font-size: 13.0;");
		GridPane calculatedGridPane = new GridPane();
		populateCDCICalculatedGrid(calculatedGridPane, cdci);
		calculatedPane.setTop(calculatedTitle);
		calculatedPane.setCenter(calculatedGridPane);
		calculatedPane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(calculatedPane);

		return vbox;
	}

	private void populateCDCIFileDescriptorsGrid(GridPane gp, CDCIDescriptorImpl cdci) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");

		gp.add(getKeyLabel("Instance UID *"), 1, row);
		try {
			AUID instanceUID = cdci.getOriginalAUID();
			gp.add(getValueLabel("" + instanceUID, "Instance UID"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Instance UID"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Generation UID"), 1, row);
		try {
			AUID generation = cdci.getLinkedGenerationID();
			gp.add(getValueLabel("" + generation, "Generation UID"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Generation UID"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Linked Track ID"), 1, row);
		gp.add(getValueLabel("" + cdci.getLinkedTrackID(), "Linked Track ID"), 2, row);
		row += 1;

		try {
			gp.add(getKeyLabel("Essence Length"), 1, row);
			gp.add(getValueLabel("" + cdci.getEssenceLength(), "Essence Length"), 2, row);
			row += 1;
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Essence Length"), 2, row);
		}

		gp.add(getKeyLabel("Sample Rate *"), 1, row);
		gp.add(getValueLabel("" + cdci.getSampleRateString(), "Sample Rate"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Container Duration"), 1, row);
		gp.add(getValueLabel("" + cdci.getEssenceLength(), "Container Duration"), 2, row);
		row += 1;
		
		/* TODO: essence container
			gp.add(getDescriptorLabel("Essence Container *", "#e3e3e3", 200, "descriptor-border-bottom"), 1, row);
			gp.add(getDescriptorLabel("" + cdci.??, "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
			row += 1;
		*/
	}

	private void populateCDCIPictureEssenceGrid(GridPane gp, CDCIDescriptorImpl cdci) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");

		gp.add(getKeyLabel("Signal Standard"), 1, row);
		try {
			SignalStandardType signalStandard = cdci.getSignalStandard();
			gp.add(getValueLabel("" + signalStandard, "Signal Standard"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Signal Standard"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Picture Encoding"), 1, row);
		try {
			String picEncodingStr = cdci.getPictureCompression().toString();
			String stripped = picEncodingStr.replace("urn:smpte:ul:", "").toUpperCase();
			HashMap<String, String> picEncodingMap = new MXFPictureEncodingMap().getMap();
			String value = picEncodingMap.get(stripped);
			if (value == null || "".equals(value)) {
				value = picEncodingStr;
				gp.add(getValueLabel(value, "Picture Encoding"), 2, row);
			} else {
				gp.add(getValueLabel(value, "Picture Encoding"), 2, row);
				row += 1;
				gp.add(getValueLabel("", "Picture Encoding"), 1, row);
				gp.add(getValueLabel("(" + stripped + ")", "Picture Encoding"), 2, row);
			}
		} catch(PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Picture Encoding"), 2, row);
		} catch (Exception ex) {
			System.out.println("Error getting crc32");
		}
		row += 1;

		gp.add(getKeyLabel("Stored Height"), 1, row);
		gp.add(getValueLabel("" + cdci.getStoredHeight(), "Stored Height"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Stored Width"), 1, row);
		gp.add(getValueLabel("" + cdci.getStoredWidth(), "Stored Width"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Sampled Height"), 1, row);
		gp.add(getValueLabel("" + cdci.getSampledHeight(), "Sampled Height"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Sampled Width"), 1, row);
		gp.add(getValueLabel("" + cdci.getSampledWidth(), "Sampled Width"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Sampled X Offset"), 1, row);
		try {
			int sampledXOffset = cdci.getSampledXOffset();
			gp.add(getValueLabel("" + sampledXOffset, "Sampled X Offset"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Sampled X Offset"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Sampled Y Offset"), 1, row);
		try {
			int sampledYOffset = cdci.getSampledYOffset();
			gp.add(getValueLabel("" + sampledYOffset, "Sampled Y Offset"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Sampled Y Offset"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Display Height"), 1, row);
		gp.add(getValueLabel("" + cdci.getDisplayHeight(), "Display Height"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Display Width"), 1, row);
		gp.add(getValueLabel("" + cdci.getDisplayWidth(), "Display Width"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Display X Offset"), 1, row);
		try {
			int displayXOffset = cdci.getDisplayXOffset();
			gp.add(getValueLabel("" + displayXOffset, "Display X Offset"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Display X Offset"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Display Y Offset"), 1, row);
		try {
			int displayYOffset = cdci.getDisplayYOffset();
			gp.add(getValueLabel("" + displayYOffset, "Display Y Offset"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Display Y Offset"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Frame Layout"), 1, row);
		gp.add(getValueLabel("" + cdci.getFrameLayout(), "Frame Layout"), 2, row);
		row += 1;

		String videoLineMap = "";
		int[] arr = cdci.getVideoLineMap();
		if (arr != null && arr.length > 0) {
			for (int i = 0; i < arr.length; i++) {
				videoLineMap += arr[i];
				if (i < arr.length -1) videoLineMap += ", ";
			}
		}

		gp.add(getKeyLabel("Video Line Map"), 1, row);
		gp.add(getValueLabel("" + videoLineMap, "Video Line Map"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Image Aspect Ratio"), 1, row);
		gp.add(getValueLabel("" + cdci.getImageAspectRatioString(), "Image Aspect Ratio"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Alpha Transparency"), 1, row);
		try {
			AlphaTransparencyType alphaTransparency = cdci.getAlphaTransparency();
			gp.add(getValueLabel("" + alphaTransparency, "Alpha Transparency"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Alpha Transparency"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Image Alignment Offset"), 1, row);
		try {
			int alignmentFactor = cdci.getImageAlignmentFactor();
			gp.add(getValueLabel("" + alignmentFactor, "Image Alignment Offset"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Image Alignment Offset"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Image Start Offset"), 1, row);
		try {
			int imageStartOffset = cdci.getImageStartOffset();
			gp.add(getValueLabel("" + imageStartOffset, "Image Start Offset"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Image Start Offset"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Image End Offset"), 1, row);
		try {
			int imageEndOffset = cdci.getImageEndOffset();
			gp.add(getValueLabel("" + imageEndOffset, "Image End Offset"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Image End Offset"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Field Dominance"), 1, row);
		try {
			FieldNumber fieldDominance = cdci.getFieldDominance();
			gp.add(getValueLabel("" + fieldDominance.value(), "Field Dominance"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Field Dominance"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Display F2 Offset"), 1, row);
		try {
			int displayOffset = cdci.getDisplayF2Offset();
			gp.add(getValueLabel("" + displayOffset, "Display F2 Offset"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Display F2 Offset"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Stored F2 Offset"), 1, row);
		try {
			int storedOffset = cdci.getStoredF2Offset();
			gp.add(getValueLabel("" + storedOffset, "Stored F2 Offset"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Stored F2 Offset"), 2, row);
		}
		row += 1;
	}

	private void populateCDCIGrid(GridPane gp, CDCIDescriptorImpl cdci) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");
		
		gp.add(getKeyLabel("Active Format Descriptor"), 1, row);
		try {
			byte activeFormatDescriptor = cdci.getActiveFormatDescriptor();
			gp.add(getValueLabel("" + activeFormatDescriptor, "Active Format Descriptor"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Active Format Descriptor"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Alpha Sample Depth"), 1, row);
		try {
			int alphaSampleDepth = cdci.getAlphaSampleDepth();
			gp.add(getValueLabel("" + alphaSampleDepth, "Alpha Sample Depth"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Alpha Sample Depth"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Black Reference Level"), 1, row);
		try {
			int blackRefLevel = cdci.getBlackRefLevel();
			gp.add(getValueLabel("" + blackRefLevel, "Black Reference Level"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Black Reference Level"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Color Range"), 1, row);
		try {
			int colorRange = cdci.getColorRange();
			gp.add(getValueLabel("" + colorRange, "Color Range"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Color Range"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Color Siting"), 1, row);
		try {
			ColorSitingType colorSiting = cdci.getColorSiting();
			gp.add(getValueLabel("" + colorSiting, "Color Siting"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Color Siting"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Component Depth"), 1, row);
		try {
			int componentDepth = cdci.getComponentDepth();
			gp.add(getValueLabel("" + componentDepth, "Component Depth"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Component Depth"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Horizontal Subsampling"), 1, row);
		try {
			int horizontalSubsampling = cdci.getHorizontalSubsampling();
			gp.add(getValueLabel("" + horizontalSubsampling, "Horizontal Subsampling"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Horizontal Subsampling"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Padding Bits"), 1, row);
		try {
			int paddingBits = cdci.getPaddingBits();
			gp.add(getValueLabel("" + paddingBits, "Padding Bits"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Padding Bits"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Reversed Byte Order"), 1, row);
		gp.add(getValueLabel("" + cdci.getReversedByteOrder(), "Reversed Byte Order"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Vertical Subsampling"), 1, row);
		gp.add(getValueLabel("" + cdci.getVerticalSubsampling(), "Vertical Subsampling"), 2, row);
		row += 1;

		gp.add(getKeyLabel("White Reference Level"), 1, row);
		try {
			int whiteRefLevel = cdci.getWhiteRefLevel();
			gp.add(getValueLabel("" + whiteRefLevel, "White Reference Level"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "White Reference Level"), 2, row);
		}
		row += 1;
	}

	private void populateCDCIFFV1Grid(GridPane gp, CDCIDescriptorImpl cdci) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");
		
		FFV1PictureSubDescriptorImpl ffv1 = null;
		try {
			List<SubDescriptor> subdescriptors = cdci.getSubDescriptors();
			ffv1 = GetFFV1Subdescriptor(subdescriptors);
		} catch(Exception ex) {}

		if (ffv1 != null) {
			gp.add(getKeyLabel("FFV1 Initialization Metadata *"), 1, row);
			try {
				byte[] initMetadata = ffv1.getFFV1InitializationMetadata();
				List<String> intMetadataUnsigned = new ArrayList<String>();
				for(Byte b : initMetadata) intMetadataUnsigned.add(String.format("%02X", Byte.toUnsignedInt(b)));
				
				Label initMetadataLabel = getValueLabel("" + intMetadataUnsigned, "FFV1 Initialization Metadata");
				initMetadataLabel.setWrapText(true);
				gp.add(initMetadataLabel, 2, row);
			} catch (PropertyNotPresentException e) {
				gp.add(getValueLabel(pnp, "FFV1 Initialization Metadata"), 2, row);
			}
			row += 1;

			gp.add(getKeyLabel("FFV1 Identical GOP"), 1, row);
			try {
				boolean isIdenticalGOP = ffv1.getFFV1IdenticalGOP();
				gp.add(getValueLabel("" + isIdenticalGOP, "FFV1 Identical GOP"), 2, row);
			} catch (PropertyNotPresentException e) {
				gp.add(getValueLabel(pnp, "FFV1 Identical GOP"), 2, row);
			}
			row += 1;

			gp.add(getKeyLabel("FFV1 Max GOP"), 1, row);
			try {
				short maxGOP = ffv1.getFFV1MaxGOP();
				gp.add(getValueLabel("" + maxGOP, "FFV1 Max GOP"), 2, row);
				row += 1;
			} catch (PropertyNotPresentException e) {
				gp.add(getValueLabel(pnp, "FFV1 Max GOP"), 2, row);
			}
			row += 1;

			gp.add(getKeyLabel("FFV1 Maximum Bit Rate"), 1, row);
			try {
				int maxBitRate = ffv1.getFFV1MaximumBitRate();
				gp.add(getValueLabel("" + maxBitRate, "FFV1 Maximum Bit Rate"), 2, row);
				row += 1;
			} catch (PropertyNotPresentException e) {
				gp.add(getValueLabel(pnp, "FFV1 Maximum Bit Rate"), 2, row);
			}
			row += 1;

			gp.add(getKeyLabel("FFV1 Version"), 1, row);
			try {
				short version = ffv1.getFFV1Version();
				gp.add(getValueLabel("" + version, "FFV1 Version"), 2, row);
				row += 1;
			} catch (PropertyNotPresentException e) {
				gp.add(getValueLabel(pnp, "FFV1 Version"), 2, row);
			}
			row += 1;

			gp.add(getKeyLabel("FFV1 Micro Version"), 1, row);
			try {
				short microVersion = ffv1.getFFV1MicroVersion();
				gp.add(getValueLabel("" + microVersion, "FFV1 Micro Version"), 2, row);
				row += 1;
			} catch (PropertyNotPresentException e) {
				gp.add(getValueLabel(pnp, "FFV1 Micro Version"), 2, row);
			}
			row += 1;
		}
	}

	private void populateCDCICalculatedGrid(GridPane gp, CDCIDescriptorImpl cdci) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");

		gp.add(getKeyLabel("Duration (seconds)"), 1, row);
		try {
			long essenceLength = cdci.getEssenceLength();
			Rational sampleRate = cdci.getSampleRate();
			gp.add(getValueLabel("" + essenceLength/sampleRate.doubleValue(), "Duration (seconds)"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel("N/A", "Duration (seconds)"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Samples Per Second"), 1, row);
		gp.add(getValueLabel("" + cdci.getSampleRate().doubleValue(), "Samples Per Second"), 2, row);
	}

	/* 
	 * WAVE 
	 */
	private VBox createWaveCard(WAVEPCMDescriptorImpl wave, int index) {
		VBox vbox = new VBox();
		
		Label cardTitle = new Label("WAVE Descriptor");
		cardTitle.setFocusTraversable(true);
		cardTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14.0;");
		vbox.getChildren().add(cardTitle);

		BorderPane filePane = new BorderPane();
		Label filePaneTitle = new Label("File Descriptors");
		filePaneTitle.setUnderline(true);
		filePaneTitle.setFocusTraversable(true);
		filePaneTitle.setStyle("-fx-font-size: 13.0;");
		GridPane fileGridPane = new GridPane();
		populateWaveFileDescriptorsGrid(fileGridPane, wave);
		filePane.setTop(filePaneTitle);
		filePane.setCenter(fileGridPane);
		filePane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(filePane);

		BorderPane soundPane = new BorderPane();
		Label soundTitle = new Label("Sound Descriptors");
		soundTitle.setUnderline(true);
		soundTitle.setFocusTraversable(true);
		soundTitle.setStyle("-fx-font-size: 13.0;");
		GridPane soundGridPane = new GridPane();
		populateWaveSoundGrid(soundGridPane, wave);
		soundPane.setTop(soundTitle);
		soundPane.setCenter(soundGridPane);
		soundPane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(soundPane);

		BorderPane wavePane = new BorderPane();
		Label waveTitle = new Label("Wave Descriptors");
		waveTitle.setUnderline(true);
		waveTitle.setFocusTraversable(true);
		waveTitle.setStyle("-fx-font-size: 13.0;");
		GridPane waveGridPane = new GridPane();
		populateWaveGrid(waveGridPane, wave);
		wavePane.setTop(waveTitle);
		wavePane.setCenter(waveGridPane);
		wavePane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(wavePane);

		return vbox;
	}

	private void populateWaveFileDescriptorsGrid(GridPane gp, WAVEPCMDescriptorImpl wave) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");
		
		gp.add(getKeyLabel("Instance UID"), 1, row);
		try {
			AUID instanceUID = wave.getOriginalAUID();
			gp.add(getValueLabel("" + instanceUID, "Instance UID"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Instance UID"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Generation UID"), 1, row);
		try {
			AUID generation = wave.getLinkedGenerationID();
			gp.add(getValueLabel("" + generation, "Generation UID"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Generation UID"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Linked Track ID"), 1, row);
		try {
			int linkedTrackID = wave.getLinkedTrackID();
			gp.add(getValueLabel("" + linkedTrackID, "Linked Track ID"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Linked Track ID"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Sample Rate *"), 1, row);
		gp.add(getValueLabel("" + wave.getSampleRateString(), "Sample Rate"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Container Duration"), 1, row);
		try {
			long essenceLength = wave.getEssenceLength();
			gp.add(getValueLabel("" + essenceLength, "Container Duration"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Container Duration"), 2, row);
		}
		row += 1;

		/* TODO: essence container
			centerGrid.add(getDescriptorLabel("Essence Container *", "#e3e3e3", 200, ""), 1, row);
			centerGrid.add(getDescriptorLabel("" + wave.??, "#e3e3e3", 400, ""), 2, row);
			row += 1;
		 */

		gp.add(getKeyLabel("Codec"), 1, row);
		try {
			ContainerDefinition containerFormat = wave.getContainerFormat();
			gp.add(getValueLabel("" + containerFormat.getDescription(), "Codec"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Codec"), 2, row);
		}
		row += 1;
	}

	private void populateWaveSoundGrid(GridPane gp, WAVEPCMDescriptorImpl wave) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");

		gp.add(getKeyLabel("Audio Sample Rate"), 1, row);
		gp.add(getValueLabel("" + wave.getAudioSampleRateString(), "Audio Sample Rate"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Channel Count"), 1, row);
		gp.add(getValueLabel("" + wave.getChannelCount(), "Channel Count"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Sound Encoding"), 1, row);
		try {
			String soundEncodingStr = wave.getSoundCompression().toString();
			String stripped = soundEncodingStr.replace("urn:smpte:ul:", "").toUpperCase();
			HashMap<String, String> soundEncodingMap = new MXFSoundEncodingMap().getMap();
			String value = soundEncodingMap.get(stripped);
			if (value == null || "".equals(value)) {
				value = soundEncodingStr;
				gp.add(getValueLabel(value, "Sound Encoding"), 2, row);
			} else {
				gp.add(getValueLabel(value, "Sound Encoding"), 2, row);
				row += 1;
				gp.add(getValueLabel("", "Sound Encoding"), 1, row);
				gp.add(getValueLabel("(" + stripped + ")", "Sound Encoding"), 2, row);
			}
		} catch(PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Sound Encoding"), 2, row);
		} catch (Exception ex) {
			System.out.println("Error adding sound encoding");
		}
		row += 1;

		gp.add(getKeyLabel("Bit Depth"), 1, row);
		gp.add(getValueLabel("" + wave.getQuantizationBits(), "Bit Depth"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Locked *"), 1, row);
		gp.add(getValueLabel("" + wave.getLocked(), "Locked"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Audio Reference Level"), 1, row);
		try {
			byte audioRefLevel = wave.getAudioReferenceLevel();
			gp.add(getValueLabel("" + audioRefLevel, "Audio Reference Level"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Audio Reference Level"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Electro-Spatial Formulation"), 1, row);
		try {
			ElectroSpatialFormulation electroForm = wave.getElectrospatialFormulation();
			gp.add(getValueLabel("" + electroForm, "Electro-Spatial Formulation"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Electro-Spatial Formulation"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Dial Norm"), 1, row);
		try {
			byte dialNorm = wave.getDialNorm();
			gp.add(getValueLabel("" + dialNorm, "Dial Norm"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Dial Norm"), 2, row);
		}
		row += 1;
	}

	private void populateWaveGrid(GridPane gp, WAVEPCMDescriptorImpl wave) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");
		
		gp.add(getKeyLabel("Average Bytes Per Second"), 1, row);
		gp.add(getValueLabel("" + wave.getAverageBytesPerSecond(), "Average Bytes Per Second"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Block Align"), 1, row);
		gp.add(getValueLabel("" + wave.getBlockAlign(), "Block Align"), 2, row);
	}

	/* 
	 * DATETIME 
	 */
	private VBox createDateTimeCard(AS07DateTimeDescriptorImpl dateTime, int index) {
		VBox vbox = new VBox();
		
		Label cardTitle = new Label("AS07 DateTime Descriptor");
		cardTitle.setFocusTraversable(true);
		cardTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14.0;");
		vbox.getChildren().add(cardTitle);

		BorderPane filePane = new BorderPane();
		Label filePaneTitle = new Label("File Descriptors");
		filePaneTitle.setUnderline(true);
		filePaneTitle.setFocusTraversable(true);
		filePaneTitle.setStyle("-fx-font-size: 13.0;");
		GridPane fileGridPane = new GridPane();
		populateDateTimeFileDescriptorsGrid(fileGridPane, dateTime);
		filePane.setTop(filePaneTitle);
		filePane.setCenter(fileGridPane);
		filePane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(filePane);

		BorderPane dateTimePane = new BorderPane();
		Label dateTimeTitle = new Label("DateTime Descriptors");
		dateTimeTitle.setUnderline(true);
		dateTimeTitle.setFocusTraversable(true);
		dateTimeTitle.setStyle("-fx-font-size: 13.0;");
		GridPane dateTimeGridPane = new GridPane();
		populateDateTimeDescriptorGrid(dateTimeGridPane, dateTime);
		dateTimePane.setTop(dateTimeTitle);
		dateTimePane.setCenter(dateTimeGridPane);
		dateTimePane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(dateTimePane);

		BorderPane dateTimeSubdescriptorsPane = new BorderPane();
		Label dateTimeSubdescriptorsTitle = new Label("DateTime Subdescriptors");
		dateTimeSubdescriptorsTitle.setUnderline(true);
		dateTimeSubdescriptorsTitle.setFocusTraversable(true);
		dateTimeSubdescriptorsTitle.setStyle("-fx-font-size: 13.0;");
		GridPane dateTimeSubdescriptorsGridPane = new GridPane();
		populateDateTimeSubdescriptorsGrid(dateTimeSubdescriptorsGridPane, dateTime);
		dateTimeSubdescriptorsPane.setTop(dateTimeSubdescriptorsTitle);
		dateTimeSubdescriptorsPane.setCenter(dateTimeSubdescriptorsGridPane);
		dateTimeSubdescriptorsPane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(dateTimeSubdescriptorsPane);

		return vbox;
	}

	private void populateDateTimeFileDescriptorsGrid(GridPane gp, AS07DateTimeDescriptorImpl dateTime) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");
		
		gp.add(getKeyLabel("Instance UID *"), 1, row);
		try {
			AUID originalAUID = dateTime.getOriginalAUID();
			gp.add(getValueLabel("" + originalAUID, "Instance UID"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Instance UID"), 2, row);
		}
		row += 1;
		
		gp.add(getKeyLabel("Generation UID"), 1, row);
		try {
			AUID linkedGenerationID = dateTime.getLinkedGenerationID();
			gp.add(getValueLabel("" + linkedGenerationID, "Generation UID"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Generation UID"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Linked Track ID"), 1, row);
		gp.add(getValueLabel("" + dateTime.getLinkedTrackID(), "Linked Track ID"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Sample Rate *"), 1, row);
		gp.add(getValueLabel("" + dateTime.getSampleRateString(), "Sample Rate"), 2, row);
		row += 1;
		
		gp.add(getKeyLabel("Container Duration"), 1, row);
		try {
			long essenceLength = dateTime.getEssenceLength();
			gp.add(getValueLabel("" + essenceLength, "Container Duration"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Container Duration"), 2, row);
		}
		row += 1;

		/*
		centerGrid.add(getDescriptorLabel("Essence Container *", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + dateTime.getEssenceContainer(), "#e3e3e3", 400, ""), 2, row);
		row += 1;
		*/

		gp.add(getKeyLabel("Codec"), 1, row);
		try {
			ContainerDefinition containerFormat = dateTime.getContainerFormat();
			gp.add(getValueLabel("" + containerFormat, "Codec"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Codec"), 2, row);
		}
		row += 1;

		// TODO: iterate locators
		gp.add(getKeyLabel("Locators"), 1, row);
		try {
			List<Locator> locators = dateTime.getLocators();
			gp.add(getValueLabel("" + locators, "Locators"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Locators"), 2, row);
		}
		row += 1;
	}

	private void populateDateTimeDescriptorGrid(GridPane gp, AS07DateTimeDescriptorImpl dateTime) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");

		gp.add(getKeyLabel("DateTime Rate"), 1, row);
		gp.add(getValueLabel("" + dateTime.getDateTimeRate(), "DateTime Rate"), 2, row);
		row += 1;

		gp.add(getKeyLabel("DateTime Drop Frame"), 1, row);
		gp.add(getValueLabel("" + dateTime.getDateTimeDropFrame(), "DateTime Drop Frame"), 2, row);
		row += 1;

		gp.add(getKeyLabel("DateTime Embedded"), 1, row);
		gp.add(getValueLabel("" + dateTime.getDateTimeEmbedded(), "DateTime Embedded"), 2, row);
		row += 1;

		gp.add(getKeyLabel("DateTime Kind *"), 1, row);
		gp.add(getValueLabel("" + dateTime.getDateTimeKind(), "DateTime Kind"), 2, row);
		row += 1;
	}

	private void populateDateTimeSubdescriptorsGrid(GridPane gp, AS07DateTimeDescriptorImpl dateTime) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");

		if (dateTime.getSubDescriptors().size() == 0) {
			return;
		}

		for (SubDescriptor sub : dateTime.getSubDescriptors()) {
			AS07TimecodeLabelSubdescriptor parsedSub = parseDateTimeSubDescriptor(sub.toString());

			if (parsedSub == null) {
				continue;
			}

			gp.add(getKeyLabel("DateTime Symbol *"), 1, row);
			gp.add(getValueLabel("" + parsedSub.getDateTimeSymbol(), "DateTime Symbol"), 2, row);
			row += 1;
			
			gp.add(getKeyLabel("DateTime Essence Track ID"), 1, row);
			gp.add(getValueLabel("" + parsedSub.getDateTimeEssenceTrackID(), "DateTime Essence Track ID"), 2, row);
			row += 1;
		}
	}

	/* 
	 * ANCILLARY 
	 */
	private VBox createAncillaryCard(AncillaryPacketsDescriptorImpl ancillary, int index) {
		VBox vbox = new VBox();
		
		Label cardTitle = new Label("Ancillary Packets Descriptor (ANC)");
		cardTitle.setFocusTraversable(true);
		cardTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14.0;");
		vbox.getChildren().add(cardTitle);

		BorderPane filePane = new BorderPane();
		Label filePaneTitle = new Label("File Descriptors");
		filePaneTitle.setUnderline(true);
		filePaneTitle.setFocusTraversable(true);
		filePaneTitle.setStyle("-fx-font-size: 13.0;");
		GridPane fileGridPane = new GridPane();
		populateAncillaryFileDescriptorsGrid(fileGridPane, ancillary);
		filePane.setTop(filePaneTitle);
		filePane.setCenter(fileGridPane);
		filePane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(filePane);

		BorderPane ancillaryPane = new BorderPane();
		Label ancillaryTitle = new Label("Generic Data Essence Descriptor");
		ancillaryTitle.setUnderline(true);
		ancillaryTitle.setFocusTraversable(true);
		ancillaryTitle.setStyle("-fx-font-size: 13.0;");
		GridPane ancillaryGridPane = new GridPane();
		populateAncillaryDescriptorGrid(ancillaryGridPane, ancillary);
		ancillaryPane.setTop(ancillaryTitle);
		ancillaryPane.setCenter(ancillaryGridPane);
		ancillaryPane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(ancillaryPane);

		return vbox;
	}

	private void populateAncillaryFileDescriptorsGrid(GridPane gp, AncillaryPacketsDescriptorImpl ancillary) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");
		
		gp.add(getKeyLabel("Instance UID *"), 1, row);
		try {
			AUID originalAUID = ancillary.getOriginalAUID();
			gp.add(getValueLabel("" + originalAUID, "Instance UID"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Instance UID"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Generation UID"), 1, row);
		try {
			AUID generationID = ancillary.getLinkedGenerationID();
			gp.add(getValueLabel("" + generationID, "Generation UID"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Generation UID"), 2, row);
		}
		row += 1;
		
		gp.add(getKeyLabel("Linked Track ID"), 1, row);
		gp.add(getValueLabel("" + ancillary.getLinkedTrackID(), "Linked Track ID"), 2, row);
		row += 1;
		
		gp.add(getKeyLabel("Sample Rate *"), 1, row);
		gp.add(getValueLabel("" + ancillary.getSampleRate(), "Sample Rate"), 2, row);
		row += 1;
		
		gp.add(getKeyLabel("Container Duration"), 1, row);
		try {
			long essenceLength = ancillary.getEssenceLength();
			gp.add(getValueLabel("" + essenceLength, "Container Duration"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Container Duration"), 2, row);
		}
		row += 1;

		/* TODO: essence container
			centerGrid.add(getDescriptorLabel("Essence Container *", "#e3e3e3", 200, ""), 1, row);
			centerGrid.add(getDescriptorLabel("" + ancillary.??, "#e3e3e3", 400, ""), 2, row);
			row += 1;
		 */

		gp.add(getKeyLabel("Codec"), 1, row);
		try {
			CodecDefinition codec = ancillary.getCodec();
			gp.add(getValueLabel("" + codec, "Codec"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Codec"), 2, row);
		}
		row += 1;
	}

	private void populateAncillaryDescriptorGrid(GridPane gp, AncillaryPacketsDescriptorImpl ancillary) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");

		gp.add(getKeyLabel("Data Essence Coding"), 1, row);
		gp.add(getValueLabel(pnp, "Data Essence Coding"), 2, row);
		//gp.add(getDescriptorLabel("" + ancillary.???, "#e3e3e3", 400, ""), 2, row); // data essence coding ??
	}

	/* 
	 * TIMED TEXT 
	 */
	private VBox createTimedTextCard(TimedTextDescriptorImpl timedText, int index) {
		VBox vbox = new VBox();
		
		Label cardTitle = new Label("Timed Text Descriptor");
		cardTitle.setFocusTraversable(true);
		cardTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14.0;");
		vbox.getChildren().add(cardTitle);

		BorderPane filePane = new BorderPane();
		Label filePaneTitle = new Label("File Descriptors");
		filePaneTitle.setUnderline(true);
		filePaneTitle.setFocusTraversable(true);
		filePaneTitle.setStyle("-fx-font-size: 13.0;");
		GridPane fileGridPane = new GridPane();
		populateTimedTextFileDescriptorsGrid(fileGridPane, timedText);
		filePane.setTop(filePaneTitle);
		filePane.setCenter(fileGridPane);
		filePane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(filePane);

		BorderPane timedTextPane = new BorderPane();
		Label timedTextTitle = new Label("Timed Text Descriptors");
		timedTextTitle.setUnderline(true);
		timedTextTitle.setFocusTraversable(true);
		timedTextTitle.setStyle("-fx-font-size: 13.0;");
		GridPane timedTextGridPane = new GridPane();
		populateTimedTextGrid(timedTextGridPane, timedText);
		timedTextPane.setTop(timedTextTitle);
		timedTextPane.setCenter(timedTextGridPane);
		timedTextPane.setPadding(new Insets(0, 0, 10, 5));
		vbox.getChildren().add(timedTextPane);

		return vbox;
	}

	private void populateTimedTextFileDescriptorsGrid(GridPane gp, TimedTextDescriptorImpl timedText) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");
		
		gp.add(getKeyLabel("Instance UID *"), 1, row);
		try {
			AUID originalAUID = timedText.getOriginalAUID();
			gp.add(getValueLabel("" + originalAUID, "Instance UID"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Instance UID"), 2, row);
		}
		row += 1;
		
		gp.add(getKeyLabel("Generation UID"), 1, row);
		gp.add(getValueLabel("" + timedText.getLinkedGenerationID(), "Generation UID"), 2, row);
		row += 1;
		
		gp.add(getKeyLabel("Linked Track ID"), 1, row);
		gp.add(getValueLabel("" + timedText.getLinkedTrackID(), "Linked Track ID"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Sample Rate *"), 1, row);
		gp.add(getValueLabel("" + timedText.getSampleRate(), "Sample Rate"), 2, row);
		row += 1;

		gp.add(getKeyLabel("Container Duration"), 1, row);
		gp.add(getValueLabel("" + timedText.getEssenceLength(), "Container Duration"), 2, row);
		row += 1;

		/* TODO: essence container
			centerGrid.add(getDescriptorLabel("Essence Container *", "#e3e3e3", 200, ""), 1, row);
			centerGrid.add(getDescriptorLabel("" + timedText.??, "#e3e3e3", 400, ""), 2, row);
			row += 1;
		 */

		gp.add(getKeyLabel("Codec"), 1, row);
		try {
			ContainerDefinition containerFormat = timedText.getContainerFormat();
			gp.add(getValueLabel("" + containerFormat.getDescription(), "Codec"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Codec"), 2, row);
		}
		row += 1;

		// need to iterate locators array
		gp.add(getKeyLabel("Locators"), 1, row);
		try {
			List<Locator> locators = timedText.getLocators();
			gp.add(getValueLabel("" + locators, "Locators"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Locators"), 2, row);
		}
		row += 1;
	}

	private void populateTimedTextGrid(GridPane gp, TimedTextDescriptorImpl timedText) {
		int row = 0;
		gp.setStyle("-fx-background-color: transparent;");

		gp.add(getKeyLabel("Text Encoding Format"), 1, row);
		try {
			String ucsEncoding = timedText.getUcsEncoding();
			gp.add(getValueLabel("" + ucsEncoding, "Text Encoding Format"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Text Encoding Format"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Namespace URI"), 1, row);
		try {
			String namespaceURI = timedText.getNamespaceURI();
			gp.add(getValueLabel("" + namespaceURI, "Namespace URI"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Namespace URI"), 2, row);
		}
		row += 1;

		gp.add(getKeyLabel("Resource ID"), 1, row);
		try {
			AUID resourceID = timedText.getResourceId();
			gp.add(getValueLabel("" + resourceID, "Resource ID"), 2, row);
		} catch (PropertyNotPresentException e) {
			gp.add(getValueLabel(pnp, "Resource ID"), 2, row);
		}
		row += 1;
	}

	private Label getKeyLabel(String key) {
		Label label = new Label(key);
		label.setPrefWidth(200);
		label.setPadding(new Insets(2,0,2,5));
		label.setMaxHeight(Double.MAX_VALUE);
		label.setMaxWidth(Double.MAX_VALUE);
		label.setAlignment(Pos.TOP_LEFT);
		return label;
	}

	private Label getValueLabel(String value, String key) {
		Label label = new Label(value);
		label.setPrefWidth(400);
		label.setPadding(new Insets(2,0,2,5));
		label.setMaxHeight(Double.MAX_VALUE);
		label.setMaxWidth(Double.MAX_VALUE);
		label.setAlignment(Pos.TOP_LEFT);
		label.setFocusTraversable(true);
		label.setAccessibleText(key + " is " + value);
		return label;
	}

	private AS07TimecodeLabelSubdescriptor parseDateTimeSubDescriptor(String sub) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(AS07TimecodeLabelSubdescriptor.class);
		    Unmarshaller u = jaxbContext.createUnmarshaller();
			String d = sub.replace("aaf:", "");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(new StringReader(d)));
			AS07TimecodeLabelSubdescriptor subdescriptor = (AS07TimecodeLabelSubdescriptor)u.unmarshal(document);
		    return subdescriptor;
		} catch (JAXBException e)  {
			System.out.println("Error parsing date time subdescriptor");
		} catch (ParserConfigurationException e) {
			System.out.println("Error parsing date time subdescriptor");
		} catch (SAXException e) {
			System.out.println("Error parsing date time subdescriptor");
		} catch (IOException e) {
			System.out.println("Error parsing date time subdescriptor");
		}
		return null;
	}

	private FFV1PictureSubDescriptorImpl GetFFV1Subdescriptor(List<SubDescriptor> subdescriptors) {
		if (subdescriptors == null || subdescriptors.size() == 0) {
			return null;
		}
		FFV1PictureSubDescriptorImpl ffv1 = null;
		for (int i = 0; i < subdescriptors.size(); i++) {
			try {
				ffv1 = (FFV1PictureSubDescriptorImpl)subdescriptors.get(i);
			} catch(Exception ex) {
				System.out.println("Caught exception in GetFFV1Subdescriptor: " + ex.getMessage());
				continue;
			}
			if (ffv1 != null) return ffv1;
		}
		return null;
	}
}
