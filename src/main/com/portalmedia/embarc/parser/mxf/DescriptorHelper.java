package com.portalmedia.embarc.parser.mxf;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
import com.portalmedia.embarc.gui.mxf.MXFPictureEncodingMap;
import com.portalmedia.embarc.gui.mxf.MXFSoundEncodingMap;

import tv.amwa.maj.model.AAFFileDescriptor;
import tv.amwa.maj.model.InterchangeObject;
import tv.amwa.maj.model.Locator;
import tv.amwa.maj.model.SubDescriptor;
import tv.amwa.maj.model.impl.AS07DateTimeDescriptorImpl;
import tv.amwa.maj.model.impl.AncillaryPacketsDescriptorImpl;
import tv.amwa.maj.model.impl.CDCIDescriptorImpl;
//import tv.amwa.maj.model.impl.FFV1PictureSubDescriptorImpl;
import tv.amwa.maj.model.impl.TimedTextDescriptorImpl;
import tv.amwa.maj.model.impl.WAVEPCMDescriptorImpl;
import tv.amwa.maj.record.Rational;

public class DescriptorHelper {
	public ArrayList<LinkedHashMap<String, String>> getPictureDescriptors(MXFFileDescriptorResult descriptors) {
		List<CDCIDescriptorImpl> cdciDescriptors = descriptors.getCDCIDescriptor();

		ArrayList<LinkedHashMap<String, String>> parsedDescriptors = new ArrayList<LinkedHashMap<String, String>>();
		if (cdciDescriptors.size() > 0) {
			for (CDCIDescriptorImpl cdci : cdciDescriptors) {
				parsedDescriptors.add(getCDCIProperties(cdci));
			}
		}

		return parsedDescriptors;
	}

	public ArrayList<LinkedHashMap<String, String>> getSoundDescriptors(MXFFileDescriptorResult descriptors) {
		List<WAVEPCMDescriptorImpl> wavePCMDescriptors = descriptors.getWavePCMDescriptors();

		ArrayList<LinkedHashMap<String, String>> parsedDescriptors = new ArrayList<LinkedHashMap<String, String>>();
		if (wavePCMDescriptors.size() > 0) {
			for (WAVEPCMDescriptorImpl wave : wavePCMDescriptors) {
				parsedDescriptors.add(getWaveProperties(wave));
			}
		}

		return parsedDescriptors;
	}

	public ArrayList<LinkedHashMap<String, String>> getOtherDescriptors(MXFFileDescriptorResult descriptors) {
		List<AncillaryPacketsDescriptorImpl> ancillaryDescriptors = descriptors.getAncillaryPacketsDescriptors();
		List<TimedTextDescriptorImpl> timedTextDescriptors = descriptors.getTimedTextDescriptor();
		List<AS07DateTimeDescriptorImpl> dateTimeDescriptors = descriptors.getAS07DateTimeDescriptor();

		ArrayList<LinkedHashMap<String, String>> parsedDescriptors = new ArrayList<LinkedHashMap<String, String>>();

		if (ancillaryDescriptors.size() > 0) {
			for (AncillaryPacketsDescriptorImpl ancillary : ancillaryDescriptors) {
				parsedDescriptors.add(getAncillaryProperties(ancillary));
			}
		}

		if (timedTextDescriptors.size() > 0) {
			for (TimedTextDescriptorImpl timedText : timedTextDescriptors) {
				parsedDescriptors.add(getTimedTextProperties(timedText));
			}
		}

		if (dateTimeDescriptors.size() > 0) {
			for (AS07DateTimeDescriptorImpl dateTime : dateTimeDescriptors) {
				parsedDescriptors.add(getDateTimeProperties(dateTime));
			}
		}

		return parsedDescriptors;
	}

	private LinkedHashMap<String, String> getCDCIProperties(CDCIDescriptorImpl cdci) {
		LinkedHashMap<String, String> cdciProps = new LinkedHashMap<String, String>();
		
		cdciProps.put("\nCDCI Descriptor", "");
		
		LinkedHashMap<String, String> fileDescriptors = getFileDescriptors(cdci, cdci);
		
		cdciProps.putAll(fileDescriptors);

		// Picture Essence Descriptors
		String signalStandard = "";
		String pictureEncoding = "";
		String storedHeight = "";
		String storedWidth = "";
		String sampledHeight = "";
		String sampledWidth = "";
		String sampledXOffset = "";
		String sampledYOffset = "";
		String displayHeight = "";
		String displayWidth = "";
		String displayXOffset = "";
		String displayYOffset = "";
		String frameLayout = "";
		String videoLineMap = "";
		String imageAspectRatio = "";
		String alphaTransparency = "";
		String imageAlignmentOffset = "";
		String imageStartOffset = "";
		String imageEndOffset = "";
		String fieldDominance = "";
		String displayF2Offset = "";
		String storedF2Offset = "";
		
		try {
			signalStandard = cdci.getSignalStandard().toString();
		} catch(Exception e) {
			signalStandard = "PROPERTY NOT PRESENT";
		}
		try {
			String picEncodingStr = cdci.getPictureCompression().toString();
			String stripped = picEncodingStr.replace("urn:smpte:ul:", "").toUpperCase();
			HashMap<String, String> picEncodingMap = new MXFPictureEncodingMap().getMap();
			String value = picEncodingMap.get(stripped);
			if (value == null || "".equals(value)) {
				pictureEncoding = picEncodingStr;
			} else {
				pictureEncoding = value + " (" + stripped + ")";
			}
		} catch(Exception e) {
			pictureEncoding = "PROPERTY NOT PRESENT";
		}
		try {
			storedHeight = "" + cdci.getStoredHeight();
		} catch(Exception e) {
			storedHeight = "PROPERTY NOT PRESENT";
		}
		try {
			storedWidth = "" + cdci.getStoredWidth();
		} catch(Exception e) {}
			storedWidth = "PROPERTY NOT PRESENT";
		try {
			sampledHeight = "" + cdci.getSampledHeight();
		} catch(Exception e) {
			sampledHeight = "PROPERTY NOT PRESENT";
		}
		try {
			sampledWidth = "" + cdci.getSampledWidth();
		} catch(Exception e) {
			sampledWidth = "PROPERTY NOT PRESENT";
		}
		try {
			sampledXOffset = "" + cdci.getSampledXOffset();
		} catch(Exception e) {
			sampledXOffset = "PROPERTY NOT PRESENT";
		}
		try {
			sampledYOffset = "" + cdci.getSampledYOffset();
		} catch(Exception e) {
			sampledYOffset = "PROPERTY NOT PRESENT";
		}
		try {
			displayHeight = "" + cdci.getDisplayHeight();
		} catch(Exception e) {
			displayHeight = "PROPERTY NOT PRESENT";
		}
		try {
			displayWidth = "" + cdci.getDisplayWidth();
		} catch(Exception e) {
			displayWidth = "PROPERTY NOT PRESENT";
		}
		try {
			displayXOffset = "" + cdci.getDisplayXOffset();
		} catch(Exception e) {
			displayXOffset = "PROPERTY NOT PRESENT";
		}
		try {
			displayYOffset = "" + cdci.getDisplayYOffset();
		} catch(Exception e) {
			displayYOffset = "PROPERTY NOT PRESENT";
		}
		try {
			frameLayout = "" + cdci.getFrameLayout();
		} catch(Exception e) {
			frameLayout = "PROPERTY NOT PRESENT";
		}
		try {
			String vlm = "";
			int[] arr = cdci.getVideoLineMap();
			if (arr != null && arr.length > 0) {
				for (int i = 0; i < arr.length; i++) {
					vlm += arr[i];
					if (i < arr.length -1) vlm += ", ";
				}
			}
			videoLineMap = vlm;
		} catch(Exception e) {
			videoLineMap = "PROPERTY NOT PRESENT";
		}
		try {
			imageAspectRatio = "" + cdci.getImageAspectRatio();
		} catch(Exception e) {
			imageAspectRatio = "PROPERTY NOT PRESENT";
		}
		try {
			alphaTransparency = "" + cdci.getAlphaTransparency();
		} catch(Exception e) {
			alphaTransparency = "PROPERTY NOT PRESENT";
		}
		try {
			imageAlignmentOffset = "" + cdci.getImageAlignmentFactor();
		} catch(Exception e) {
			imageAlignmentOffset = "PROPERTY NOT PRESENT";
		}
		try {
			imageStartOffset = "" + cdci.getImageStartOffset();
		} catch(Exception e) {
			imageStartOffset = "PROPERTY NOT PRESENT";
		}
		try {
			imageEndOffset = "" + cdci.getImageEndOffset();
		} catch(Exception e) {
			imageEndOffset = "PROPERTY NOT PRESENT";
		}
		try {
			fieldDominance = "" + cdci.getFieldDominance().value();
		} catch(Exception e) {
			fieldDominance = "PROPERTY NOT PRESENT";
		}
		try {
			displayF2Offset = "" + cdci.getDisplayF2Offset();
		} catch(Exception e) {
			displayF2Offset = "PROPERTY NOT PRESENT";
		}
		try {
			storedF2Offset = "" + cdci.getStoredF2Offset();
		} catch(Exception e) {
			storedF2Offset = "PROPERTY NOT PRESENT";
		}

		cdciProps.put("--- Picture Essence Descriptors ---", "");
		cdciProps.put("Signal Standard", signalStandard);
		cdciProps.put("Picture Encoding", pictureEncoding);
		cdciProps.put("Stored Height", storedHeight);
		cdciProps.put("Stored Width", storedWidth);
		cdciProps.put("Sampled Height", sampledHeight);
		cdciProps.put("Sampled Width", sampledWidth);
		cdciProps.put("Sampled X Offset", sampledXOffset);
		cdciProps.put("Sampled Y Offset", sampledYOffset);
		cdciProps.put("Display Height", displayHeight);
		cdciProps.put("Display Width", displayWidth);
		cdciProps.put("Display X Offset", displayXOffset);
		cdciProps.put("Display Y Offset", displayYOffset);
		cdciProps.put("Frame Layout", frameLayout);
		cdciProps.put("Video Line Map", videoLineMap);
		cdciProps.put("Image Aspect Ratio", imageAspectRatio);
		cdciProps.put("Alpha Transparency", alphaTransparency);
		cdciProps.put("Image Alignment Offset", imageAlignmentOffset);
		cdciProps.put("Image Start Offset", imageStartOffset);
		cdciProps.put("Image End Offset", imageEndOffset);
		cdciProps.put("Field Dominance", fieldDominance);
		cdciProps.put("Display F2 Offset", displayF2Offset);
		cdciProps.put("Stored F2 Offset", storedF2Offset);

		// CDCI Descriptors
		String activeFormat = "";
		String alphaSampleDepth = "";
		String blackRefLevel = "";
		String colorRange = "";
		String colorSiting = "";
		String componentDepth = "";
		String horizontalSubsampling = "";
		String paddingBits = "";
		String reversedByteOrder = "";
		String verticalSubsampling = "";
		String whiteRefLevel = "";

		try {
			activeFormat = "" + cdci.getActiveFormatDescriptor();
		} catch(Exception ex) {
			activeFormat = "PROPERTY NOT PRESENT";
		}
		try {
			alphaSampleDepth = "" + cdci.getAlphaSampleDepth();
		} catch(Exception ex) {
			alphaSampleDepth = "PROPERTY NOT PRESENT";
		}
		try {
			blackRefLevel = "" + cdci.getBlackRefLevel();
		} catch(Exception ex) {
			blackRefLevel = "PROPERTY NOT PRESENT";
		}
		try {
			colorRange = "" + cdci.getColorRange();
		} catch(Exception ex) {
			colorRange = "PROPERTY NOT PRESENT";
		}
		try {
			colorSiting = "" + cdci.getColorSiting();
		} catch(Exception ex) {
			colorSiting = "PROPERTY NOT PRESENT";
		}
		try {
			componentDepth = "" + cdci.getComponentDepth();
		} catch(Exception ex) {
			componentDepth = "PROPERTY NOT PRESENT";
		}
		try {
			horizontalSubsampling = "" + cdci.getHorizontalSubsampling();
		} catch(Exception ex) {
			horizontalSubsampling = "PROPERTY NOT PRESENT";
		}
		try {
			paddingBits = "" + cdci.getPaddingBits();
		} catch(Exception ex) {
			paddingBits = "PROPERTY NOT PRESENT";
		}
		try {
			reversedByteOrder = "" + cdci.getReversedByteOrder();
		} catch(Exception ex) {
			reversedByteOrder = "PROPERTY NOT PRESENT";
		}
		try {
			verticalSubsampling = "" + cdci.getVerticalSubsampling();
		} catch(Exception ex) {
			verticalSubsampling = "PROPERTY NOT PRESENT";
		}
		try {
			whiteRefLevel = "" + cdci.getWhiteRefLevel();
		} catch(Exception ex) {
			whiteRefLevel = "PROPERTY NOT PRESENT";
		}

		cdciProps.put("--- CDCI Descriptors ---", "");
		cdciProps.put("Active Format", activeFormat);
		cdciProps.put("Alpha Sample Depth", alphaSampleDepth);
		cdciProps.put("Black Ref Level", blackRefLevel);
		cdciProps.put("Color Range", colorRange);
		cdciProps.put("Color Siting", colorSiting);
		cdciProps.put("Component Depth", componentDepth);
		cdciProps.put("Horizontal Subsampling", horizontalSubsampling);
		cdciProps.put("Padding Bits", paddingBits);
		cdciProps.put("Reversed Byte Order", reversedByteOrder);
		cdciProps.put("Vertical Subsampling", verticalSubsampling);
		cdciProps.put("White Reference Level", whiteRefLevel);

		// FFV1
		/*
		FFV1PictureSubDescriptorImpl ffv1 = null;
		try {
			List<SubDescriptor> subdescriptors = cdci.getSubDescriptors();
			ffv1 = GetFFV1Subdescriptor(subdescriptors);
		} catch(Exception ex) {}

		if (ffv1 != null) {

			String ffv1InitMetadata = "";
			String ffv1IdenticalGop = "";
			String ffv1MaxGop = "";
			String ffv1MaxBitRate = "";
			String ffv1Version = "";
			String ffv1MicroVersion = "";

			try {
				byte[] initMetadata = ffv1.getFFV1InitializationMetadata();
				List<String> intMetadataUnsigned = new ArrayList<String>();
				for(Byte b : initMetadata) intMetadataUnsigned.add(String.format("%02X", Byte.toUnsignedInt(b)));
				ffv1InitMetadata = "" + intMetadataUnsigned;
			} catch(Exception ex) {
				ffv1InitMetadata = "PROPERTY NOT PRESENT";
			}
			try {
				ffv1IdenticalGop = "" + ffv1.getFFV1IdenticalGOP();
			} catch(Exception ex) {
				ffv1IdenticalGop = "PROPERTY NOT PRESENT";
			}
			try {
				ffv1MaxGop = "" + ffv1.getFFV1MaxGOP();
			} catch(Exception ex) {
				ffv1MaxGop = "PROPERTY NOT PRESENT";
			}
			try {
				ffv1MaxBitRate = "" + ffv1.getFFV1MaximumBitRate();
			} catch(Exception ex) {
				ffv1MaxBitRate = "PROPERTY NOT PRESENT";
			}
			try {
				ffv1Version = "" + ffv1.getFFV1Version();
			} catch(Exception ex) {
				ffv1Version = "PROPERTY NOT PRESENT";
			}
			try {
				ffv1MicroVersion = "" + ffv1.getFFV1MicroVersion();
			} catch(Exception ex) {
				ffv1MicroVersion = "PROPERTY NOT PRESENT";
			}

			cdciProps.put("--- FFV1 Descriptors ---", "");
			cdciProps.put("FFV1 Initialization Metadata", ffv1InitMetadata);
			cdciProps.put("FFV1 Identical GOP", ffv1IdenticalGop);
			cdciProps.put("FFV1 Max GOP", ffv1MaxGop);
			cdciProps.put("FFV1 Maximum Bit Rate", ffv1MaxBitRate);
			cdciProps.put("FFV1 Version", ffv1Version);
			cdciProps.put("FFV1 Micro Version", ffv1MicroVersion);
		}
		*/

		// Calculated Values
		String duration = "";
		String samplesPerSecond = "";

		try {
			long el = cdci.getEssenceLength();
			Rational sr = cdci.getSampleRate();
			duration = "" + el/sr.doubleValue();
		} catch(Exception ex) {
			duration = "N/A";
		}

		try {
			samplesPerSecond = "" + cdci.getSampleRate().doubleValue();
		} catch(Exception ex) {
			samplesPerSecond = "N/A";
		}

		cdciProps.put("--- Calculated Values ---", "");
		cdciProps.put("Duration", duration);
		cdciProps.put("Samples Per Second", samplesPerSecond);

		return cdciProps;
	}

	private LinkedHashMap<String, String> getWaveProperties(WAVEPCMDescriptorImpl wave) {
		LinkedHashMap<String, String> waveProps = new LinkedHashMap<String, String>();
		
		waveProps.put("\nWAVE Descriptor", "");
		
		LinkedHashMap<String, String> fileDescriptors = getFileDescriptors(wave, wave);
		
		waveProps.putAll(fileDescriptors);

		// Sound Descriptors
		String audioSampleRate = "";
		String channelCount = "";
		String soundEncoding = "";
		String bitDepth = "";
		String locked = "";
		String audioRefLevel = "";
		String electroForm = "";
		String dialNorm = "";
		
		try {
			audioSampleRate = wave.getAudioSampleRateString();
		} catch(Exception e) {
			audioSampleRate = "PROPERTY NOT PRESENT";
		}
		try {
			channelCount = "" + wave.getChannelCount();
		} catch(Exception e) {
			channelCount = "PROPERTY NOT PRESENT";
		}
		try {
			String soundEncodingStr = wave.getSoundCompression().toString();
			String stripped = soundEncodingStr.replace("urn:smpte:ul:", "").toUpperCase();
			HashMap<String, String> soundEncodingMap = new MXFSoundEncodingMap().getMap();
			String value = soundEncodingMap.get(stripped);
			if (value == null || "".equals(value)) {
				soundEncoding = soundEncodingStr;
			} else {
				soundEncoding = value + " (" + stripped + ")";
			}
		} catch(Exception e) {
			soundEncoding = "PROPERTY NOT PRESENT";
		}
		try {
			bitDepth = "" + wave.getQuantizationBits();
		} catch(Exception e) {
			bitDepth = "PROPERTY NOT PRESENT";
		}
		try {
			locked = "" + wave.getLocked();
		} catch(Exception e) {
			locked = "PROPERTY NOT PRESENT";
		}
		try {
			audioRefLevel = "" + wave.getAudioReferenceLevel();
		} catch(Exception e) {
			audioRefLevel = "PROPERTY NOT PRESENT";
		}
		try {
			electroForm = "" + wave.getElectrospatialFormulation();
		} catch(Exception e) {
			electroForm = "PROPERTY NOT PRESENT";
		}
		try {
			dialNorm = "" + wave.getDialNorm();
		} catch(Exception e) {
			dialNorm = "PROPERTY NOT PRESENT";
		}

		waveProps.put("--- Sound Descriptors ---", "");
		waveProps.put("Audio Sample Rate", audioSampleRate);
		waveProps.put("Channel Count", channelCount);
		waveProps.put("Sound Encoding", soundEncoding);
		waveProps.put("Bit Depth", bitDepth);
		waveProps.put("Locked", locked);
		waveProps.put("Audio Reference Level", audioRefLevel);
		waveProps.put("Electro-Spatial Formulation", electroForm);
		waveProps.put("Dial Norm", dialNorm);
		
		// WAVE Descriptors
		String averageBytesPerSecond = "";
		String blockAlign = "";
		
		try {
			averageBytesPerSecond = "" + wave.getAverageBytesPerSecond();
		} catch(Exception e) {
			averageBytesPerSecond = "PROPERTY NOT PRESENT";
		}
		try {
			blockAlign = "" + wave.getBlockAlign();
		} catch(Exception e) {
			blockAlign = "PROPERTY NOT PRESENT";
		}

		waveProps.put("--- WAVE Descriptors ---", "");
		waveProps.put("Average Bytes Per Second", averageBytesPerSecond);
		waveProps.put("Block Align", blockAlign);
		
		return waveProps;
	}

	private LinkedHashMap<String, String> getAncillaryProperties(AncillaryPacketsDescriptorImpl ancillary) {
		LinkedHashMap<String, String> ancillaryProps = new LinkedHashMap<String, String>();
		
		ancillaryProps.put("\nAncillary Descriptor", "");
		
		LinkedHashMap<String, String> fileDescriptors = getFileDescriptors(ancillary, ancillary);
		
		ancillaryProps.putAll(fileDescriptors);

		// Ancillary Descriptors
		String dataEssenceCoding = "";
		try {
			//dataEssenceCoding = "" + ancillary.??
		} catch(Exception e) {
			dataEssenceCoding = "PROPERTY NOT PRESENT";
		}

		ancillaryProps.put("--- Generic Data Essence Descriptor ---", "");
		ancillaryProps.put("Data Essence Coding", dataEssenceCoding);

		return ancillaryProps;
	}

	private LinkedHashMap<String, String> getTimedTextProperties(TimedTextDescriptorImpl timedText) {
		LinkedHashMap<String, String> timedTextProps = new LinkedHashMap<String, String>();
		
		timedTextProps.put("\nTimed Text Descriptor", "");
		
		LinkedHashMap<String, String> fileDescriptors = getFileDescriptors(timedText, timedText);
		
		timedTextProps.putAll(fileDescriptors);

		// Timed Text Descriptors
		String textEncodingFormat = "";
		String namespaceUri = "";
		String resourceId = "";

		try {
			textEncodingFormat = "" + timedText.getUcsEncoding();
		} catch(Exception e) {
			textEncodingFormat = "PROPERTY NOT PRESENT";
		}
		try {
			namespaceUri = "" + timedText.getNamespaceURI();
		} catch(Exception e) {
			namespaceUri = "PROPERTY NOT PRESENT";
		}
		try {
			resourceId = "" + timedText.getResourceId();
		} catch(Exception e) {
			resourceId = "PROPERTY NOT PRESENT";
		}

		timedTextProps.put("--- Timed Text Descriptors ---", "");
		timedTextProps.put("Text Encoding Format", textEncodingFormat);
		timedTextProps.put("Namespace URI", namespaceUri);
		timedTextProps.put("Resource ID", resourceId);

		return timedTextProps;
	}

	private LinkedHashMap<String, String> getDateTimeProperties(AS07DateTimeDescriptorImpl dateTime) {
		LinkedHashMap<String, String> dateTimeProps = new LinkedHashMap<String, String>();
		
		dateTimeProps.put("\nAS07 DateTime Descriptor", "");
		
		LinkedHashMap<String, String> fileDescriptors = getFileDescriptors(dateTime, dateTime);
		
		dateTimeProps.putAll(fileDescriptors);

		// DateTime Descriptors
		String dateTimeRate = "";
		String dateTimeDropFrame = "";
		String dateTimeEmbedded = "";
		String dateTimeKind = "";

		try {
			dateTimeRate = "" + dateTime.getDateTimeRate();
		} catch(Exception e) {
			dateTimeRate = "PROPERTY NOT PRESENT";
		}
		try {
			dateTimeDropFrame = "" + dateTime.getDateTimeDropFrame();
		} catch(Exception e) {
			dateTimeDropFrame = "PROPERTY NOT PRESENT";
		}
		try {
			dateTimeEmbedded = "" + dateTime.getDateTimeEmbedded();
		} catch(Exception e) {
			dateTimeEmbedded = "PROPERTY NOT PRESENT";
		}
		try {
			dateTimeKind = "" + dateTime.getDateTimeKind();
		} catch(Exception e) {
			dateTimeKind = "PROPERTY NOT PRESENT";
		}

		dateTimeProps.put("--- DateTime Descriptors ---", "");
		dateTimeProps.put("DateTime Rate", dateTimeRate);
		dateTimeProps.put("DateTime Drop Frame", dateTimeDropFrame);
		dateTimeProps.put("DateTime Embedded", dateTimeEmbedded);
		dateTimeProps.put("DateTime Kind", dateTimeKind);

		// DateTime Subdescriptors
		for (SubDescriptor sub : dateTime.getSubDescriptors()) {
			AS07TimecodeLabelSubdescriptor parsedSub = parseDateTimeSubDescriptor(sub.toString());

			String dateTimeSymbol = "";
			String dateTimeEssenceTrackID = "";

			try {
				dateTimeSymbol = "" + parsedSub.getDateTimeSymbol();
			} catch(Exception e) {
				dateTimeSymbol = "PROPERTY NOT PRESENT";
			}
			try {
				dateTimeEssenceTrackID = "" + dateTime.getDateTimeDropFrame();
			} catch(Exception e) {
				dateTimeEssenceTrackID = "PROPERTY NOT PRESENT";
			}

			dateTimeProps.put("--- DateTime Subdescriptor ---", "");
			dateTimeProps.put("DateTime Symbol", dateTimeSymbol);
			dateTimeProps.put("DateTime Essence Track ID", dateTimeEssenceTrackID);
		}

		return dateTimeProps;
	}

	private LinkedHashMap<String, String> getFileDescriptors(InterchangeObject interchangeObj, AAFFileDescriptor aafFileDescriptor) {
		LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();

		String instanceUID = "";
		String generation = "";
		String linkedTrackID = "";
		String sampleRate = "";
		String essenceLength = "";
		//String essenceContainer = "";
		String containerFormat = "";
		String locators = "";

		try {
			instanceUID = "" + interchangeObj.getOriginalAUID();
		} catch(Exception e) {
			instanceUID = "PROPERTY NOT PRESENT";
		}
		try {
			generation = "" + interchangeObj.getLinkedGenerationID();
		} catch(Exception e) {
			generation = "PROPERTY NOT PRESENT";
		}
		try {
			linkedTrackID = "" + aafFileDescriptor.getLinkedTrackID();
		} catch(Exception e) {
			linkedTrackID = "PROPERTY NOT PRESENT";
		}
		try {
			sampleRate = "" + aafFileDescriptor.getSampleRate();
		} catch(Exception e) {
			sampleRate = "PROPERTY NOT PRESENT";
		}
		try {
			essenceLength = "" + aafFileDescriptor.getEssenceLength();
		} catch(Exception e) {
			essenceLength = "PROPERTY NOT PRESENT";
		}
		/*
		try {
			essenceContainer = "" + aafFileDescriptor.getContainerFormat();
		} catch(Exception e) {
			essenceContainer = "PROPERTY NOT PRESENT";
		}
		*/
		try {
			containerFormat = "" + aafFileDescriptor.getContainerFormat().getDescription();
		} catch(Exception e) {
			containerFormat = "PROPERTY NOT PRESENT";
		}
		try {
			String locString = "";
			List<Locator> locs = (List<Locator>) aafFileDescriptor.getLocators();
			if (locs.size() > 0) {
				for (int i = 0; i < locs.size(); i++) {
					locString += locs.get(i);
					if (i < locs.size() -1) locString += ", ";
				}
			}
			locators = locString;
		} catch(Exception e) {
			locators = "PROPERTY NOT PRESENT";
		}

		props.put("--- File Descriptors ---", "");
		props.put("Instance UID", instanceUID);
		props.put("Generation UID", generation);
		props.put("Linked Track ID", linkedTrackID);
		props.put("Sample Rate", sampleRate);
		props.put("Container Duration", essenceLength);
		//props.put("Essence Container", essenceContainer);
		props.put("Codec", containerFormat);
		props.put("Locators", locators);

		return props;
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

/*
	private FFV1PictureSubDescriptorImpl GetFFV1Subdescriptor(List<SubDescriptor> subdescriptors) {
		if (subdescriptors == null || subdescriptors.size() == 0) {
			return null;
		}
		FFV1PictureSubDescriptorImpl ffv1 = null;
		for (int i = 0; i < subdescriptors.size(); i++) {
			try {
				ffv1 = (FFV1PictureSubDescriptorImpl)subdescriptors.get(i);
			} catch(Exception ex) {
				continue;
			}
			if (ffv1 != null) return ffv1;
		}
		return null;
	}
*/
}
