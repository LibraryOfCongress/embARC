package com.portalmedia.embarc.parser.mxf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.DisplayType;
import com.portalmedia.embarc.parser.ImageElementDef;
import com.portalmedia.embarc.parser.SectionDef;

public enum MXFColumn implements ColumnDef {
	AS_07_Core_DMS_ShimName("Shim Name", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, true),
	AS_07_Core_DMS_Identifiers("Identifiers", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, true),
	AS_07_Core_DMS_ResponsibleOrganizationName("Responsible Organization Name", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, true),
	AS_07_Core_DMS_ResponsibleOrganizationCode("Responsible Organization Code", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, false),
	AS_07_Core_DMS_NatureOfOrganization("Nature of Organization", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, false),
	AS_07_Core_DMS_WorkingTitle("Working Title", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, false),
	AS_07_Core_DMS_SecondaryTitle("Secondary Title", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, false),
	AS_07_Core_DMS_PictureFormat("Picture Format", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, true),
	AS_07_Core_DMS_IntendedAFD("Intended AFD", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, true),
	AS_07_Core_DMS_Captions("Captions", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, true),
	AS_07_Core_DMS_AudioTrackPrimaryLanguage("Audio Primary Language", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, false),
	AS_07_Core_DMS_AudioTrackSecondaryLanguage("Audio Secondary Language", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, false),
	AS_07_Core_DMS_AudioTrackLayout("Audio Track Layout", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, true),
	AS_07_Core_DMS_AudioTrackLayoutComment("Audio Track Layout Comment", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, false),
	AS_07_Core_DMS_Devices("Devices", MXFSection.CORE, String.class, true, DisplayType.ASCII, false, false),

	AS_07_TD_DMS_PrimaryRFC5646LanguageCode("Primary Language Code", MXFSection.TD, String.class, false, DisplayType.ASCII),
	AS_07_BD_DMS_PrimaryRFC5646LanguageCode("Primary Language Code", MXFSection.BD, String.class, false, DisplayType.ASCII),
	
	AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier("TextBasedMetadataPayloadSchemeIdentifier", MXFSection.OBJECT, String.class, false, DisplayType.ASCII),
	AS_07_Object_RFC5646TextLanguageCode("RFC5646 Text Language Code", MXFSection.OBJECT, String.class, false, DisplayType.ASCII, 1),
	AS_07_Object_MIMEMediaType("RDD 48 MIME Media Type", MXFSection.OBJECT, String.class, false, DisplayType.ASCII, 2),
	AS_07_Object_TextMIMEMediaType("GSP MIME Media Type", MXFSection.OBJECT, String.class, false, DisplayType.ASCII, 3),
	AS_07_Object_DataDescription("RDD 48 DMS Data Description", MXFSection.OBJECT, String.class, false, DisplayType.ASCII, 4),
	AS_07_Object_TextDataDescription("GSP Data Description", MXFSection.OBJECT, String.class, false, DisplayType.ASCII, 5),
	AS_07_Object_Note("RDD 48 DMS Note", MXFSection.OBJECT, String.class, false, DisplayType.ASCII, 6),
	AS_07_Object_GenericStreamID("Generic Stream ID", MXFSection.OBJECT, String.class, false, DisplayType.ASCII, 7),
	AS_07_Manifest("Manifest", MXFSection.TD, String.class, false, DisplayType.ASCII, 8),
	AS_07_Manifest_Valid("Manifest Valid", MXFSection.TD, String.class, false, DisplayType.ASCII, 9),
	AS_07_Object_Identifiers("Identifiers", MXFSection.OBJECT, String.class, false, DisplayType.ASCII, 10),
	
	AS_07_TD_DMS_TextBasedObject("Text Based Object", MXFSection.TD, String.class, false, DisplayType.ASCII, true, false),
	AS_07_BD_DMS_TextBasedObject("Binary Based Object", MXFSection.BD, String.class, false, DisplayType.ASCII, true, false);

	private String displayName;
	private MXFSection section;
	private int length;
	private Class<?> type;
	private String dateFormat;
	private boolean editable;
	private DisplayType displayType;
	private boolean hasSubsection;
	private int sortOrder;
	private boolean required;

	@JsonCreator
	private MXFColumn(String displayName, MXFSection section, Class<?> type, boolean editable, DisplayType displayType) {
		this.displayName = displayName;
		this.section = section;
		this.type = type;
		this.editable = editable;
		this.displayType = displayType;
	}

	@JsonCreator
	private MXFColumn(String displayName, MXFSection section, Class<?> type, boolean editable, DisplayType displayType, boolean hasSubsection, boolean required) {
		this.displayName = displayName;
		this.section = section;
		this.type = type;
		this.editable = editable;
		this.displayType = displayType;
		this.hasSubsection = hasSubsection;
		this.required = required;
	}

	@JsonCreator
	private MXFColumn(String displayName, MXFSection section, Class<?> type, boolean editable, DisplayType displayType, int sortOrder) {
		this.displayName = displayName;
		this.section = section;
		this.type = type;
		this.editable = editable;
		this.displayType = displayType;
		this.sortOrder = sortOrder;
	}

	@Override
	public String getDateFormat() {
		return dateFormat;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public DisplayType getDisplayType() {
		return displayType;
	}

	@Override
	public boolean getEditable() {
		return editable;
	}

	@Override
	public String getFormat() {
		// TODO Auto-generated method stub
		return dateFormat;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public SectionDef getSection() {
		// TODO Auto-generated method stub
		return section;
	}

	@Override
	public String getSectionDisplayName() {
		return section.getDisplayName();
	}

	@Override
	public ImageElementDef getSubsection() {
		return null;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public boolean hasSubsection() {
		return hasSubsection;
	}

	@Override
	public int getSortOrder() {
		return sortOrder;
	}

	@Override
	public boolean isRequired() {
		return required;
	}
}
