package com.portalmedia.embarc.parser.mxf;

public enum MXFFileInfo {
	FORMAT("Format", "format"),
	VERSION("Version", "version"),
	PROFILE("Profile", "profile"),
	FILE_SIZE("File Size", "size"),
	VIDEO_TRACK_COUNT("Video Track Count", "videoTrackCount"),
	AUDIO_TRACK_COUNT("Audio Track Count", "audioTrackCount"),
	CAPTION_TRACK_COUNT("Caption Track Count", "captionTrackCount"),
	TIMECODE_TRACK_COUNT("Timecode Track Count", "timecodeTrackCount"),
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
