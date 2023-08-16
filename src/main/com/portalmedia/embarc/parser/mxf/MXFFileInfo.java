package com.portalmedia.embarc.parser.mxf;

public enum MXFFileInfo {
	FORMAT("Format", "format"),
	VERSION("Version", "version"),
	PROFILE("Profile", "profile"),
	FILE_SIZE("File Size", "size"),
	PICTURE_TRACK_COUNT("Picture Track Count", "pictureTrackCount"),
	SOUND_TRACK_COUNT("Sound Track Count", "soundTrackCount"),
	OTHER_TRACK_COUNT("Other Track Count", "otherTrackCount"),
	TD_COUNT("TD Count", "tdTrackCount"),
	BD_COUNT("BD Count", "bdTrackCount");
	
	private String displayName;
	private String identifier;

	private MXFFileInfo(String displayName, String identifier) {
		this.displayName = displayName;
		this.identifier = identifier;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getIdentifier() {
		return identifier;
	}
}
