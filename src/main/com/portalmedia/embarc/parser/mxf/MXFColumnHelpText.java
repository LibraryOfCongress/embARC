package com.portalmedia.embarc.parser.mxf;

import java.util.HashMap;

import com.portalmedia.embarc.parser.ColumnDef;

/**
 * Singleton containing help text for mxf columns.
 *
 * @author PortalMedia
 * @since 2020-07-16
 **/
public class MXFColumnHelpText {

	private static MXFColumnHelpText instance = null;

	public static MXFColumnHelpText getInstance() {
		if (instance == null) instance = new MXFColumnHelpText();
		return instance;
	}

	private HashMap<MXFColumn, String> helpTextSet;

	private MXFColumnHelpText() {
		BuildHelpTextSet();
	}

	private void AddHelpText(MXFColumn column, String helpText) {
		helpTextSet.put(column, helpText);
	}

	private void BuildHelpTextSet() {
		helpTextSet = new HashMap<>();

		// CORE
		AddHelpText(MXFColumn.AS_07_Core_DMS_ShimName, "Controlled vocabulary string value indicating the AS-07 Shim Name, e.g., Derived-from-video-serial-interface");
		AddHelpText(MXFColumn.AS_07_Core_DMS_Identifiers, "Unordered list of strong references to all AS_07_DMS_Identifier sets. At least one AS_07_DMS_Identifier Objects set is required with the IdentifierRole = Main. Other AS_07_DMSIdentifierObjects sets are optional.");
		AddHelpText(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationName, "The main name for the entity responsible for the creation, maintenance, preservation of this digital item");
		AddHelpText(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationCode, "A familiar abbreviation of entity name.");
		AddHelpText(MXFColumn.AS_07_Core_DMS_NatureOfOrganization, "The nature of an organization (e.g., limited company, government department, etc.)");
		AddHelpText(MXFColumn.AS_07_Core_DMS_WorkingTitle, "Free text: Best known or working title of the production or production component");
		AddHelpText(MXFColumn.AS_07_Core_DMS_SecondaryTitle, "Free text Secondary title of the production or production component");
		AddHelpText(MXFColumn.AS_07_Core_DMS_PictureFormat, "The signal standard (frame resolution and aspect ratio) of the encoded file. Human readable, not controlled vocabulary.");
		AddHelpText(MXFColumn.AS_07_Core_DMS_IntendedAFD, "String value indicating the intended display format for the program, per SMPTE 2016-1-2007 table 1 a3 a2 a1 a0 with optional informative appended text e.g. 1001 Pillarbox, 0100 Letterbox, 1000 FullHD ");
		AddHelpText(MXFColumn.AS_07_Core_DMS_Captions, "Y/N value to indicate if captions are present in the encoded file");
		AddHelpText(MXFColumn.AS_07_Core_DMS_AudioTrackPrimaryLanguage, "The primary language in audio track by codes as defined by RFC5646. Use only when language is known.");
		AddHelpText(MXFColumn.AS_07_Core_DMS_AudioTrackSecondaryLanguage, "The secondary language in audio track by codes as defined by RFC5646. If multiple secondary language are present the RFC tags in white space separated list. Use only when secondary languages are present and language is known");
		AddHelpText(MXFColumn.AS_07_Core_DMS_AudioTrackLayout, "Appropriate values in AS-07 Appendix E");
		AddHelpText(MXFColumn.AS_07_Core_DMS_AudioTrackLayoutComment, "Free text comment to augment AS_07_Core_DMS_AudioTrackLayout. This is for track tagging information and is not to be used for descriptive essays. Robust descriptive data can be held in Supplemental Metadata in GSPs.");
		AddHelpText(MXFColumn.AS_07_Core_DMS_Devices, "Strong references to all AS_07_Core_DMS_Device objects used in this file");

		// TD/BD
		AddHelpText(MXFColumn.AS_07_TD_DMS_PrimaryRFC5646LanguageCode, "");
		AddHelpText(MXFColumn.AS_07_BD_DMS_PrimaryRFC5646LanguageCode, "");
		AddHelpText(MXFColumn.AS_07_TD_DMS_TextBasedObject, "");
		AddHelpText(MXFColumn.AS_07_BD_DMS_TextBasedObject, "");

		//OBJECT
		AddHelpText(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, "");
		AddHelpText(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, "");
		AddHelpText(MXFColumn.AS_07_Object_MIMEMediaType, "");
		AddHelpText(MXFColumn.AS_07_Object_TextMIMEMediaType, "");
		AddHelpText(MXFColumn.AS_07_Object_DataDescription, "");
		AddHelpText(MXFColumn.AS_07_Object_TextDataDescription, "");
		AddHelpText(MXFColumn.AS_07_Object_Note, "");
		AddHelpText(MXFColumn.AS_07_Object_GenericStreamID, "");
		AddHelpText(MXFColumn.AS_07_Object_Identifiers, "");
	}

	public String getHelpText(ColumnDef column) {
		return helpTextSet.get(column);
	}

}
