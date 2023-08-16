package com.portalmedia.embarc.parser.mxf;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.portalmedia.embarc.parser.MetadataColumnDef;

public class MXFMetadata {
	int pictureTrackCount;
	int soundTrackCount;
	int otherTrackCount;
	String format;
	String version;
	String profile;
	long fileSize;
	int bdCount;
	int tdCount;
	MXFFileDescriptorResult fileDescriptors;
	boolean hasAS07CoreDMSFramework;

	public MXFFileDescriptorResult getFileDescriptors() {
		return fileDescriptors;
	}

	public void setFileDescriptors(MXFFileDescriptorResult fileDescriptors) {
		this.fileDescriptors = fileDescriptors;
	}

	public int getTDCount() {
		return tdCount;
	}

	public void setTDCount(int tdCount) {
		this.tdCount = tdCount;
	}

	public int getBDCount() {
		return bdCount;
	}

	public void setBDCount(int bdCount) {
		this.bdCount = bdCount;
	}

	public int getPictureTrackCount() {
		return pictureTrackCount;
	}

	public void setPictureTrackCount(int pictureTrackCount) {
		this.pictureTrackCount = pictureTrackCount;
	}

	public int getOtherTrackCount() {
		return otherTrackCount;
	}

	public void setOtherTrackCount(int otherTrackCount) {
		this.otherTrackCount = otherTrackCount;
	}
	public int getSoundTrackCount() {
		return soundTrackCount;
	}

	public void setSoundTrackCount(int soundTrackCount) {
		this.soundTrackCount = soundTrackCount;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	
	HashMap<MXFColumn, MetadataColumnDef> coreColumns = new LinkedHashMap<MXFColumn, MetadataColumnDef>();

	// InstanceUID
	HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> bdColumns = new HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>>();
	HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> tdColumns = new HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>>();
	
	
	public HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> getBDColumns() {
		return bdColumns;
	}

	public void setBDColumns(HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> bdColumns) {
		this.bdColumns = bdColumns;
	}
		
	public HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> getTDColumns() {
		return tdColumns;
	}

	public void setTDColumns(HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> tdColumns) {
		this.tdColumns = tdColumns;
	}
	
	public HashMap<MXFColumn, MetadataColumnDef> getCoreColumns() {
		return coreColumns;
	}

	public void setCoreColumns(HashMap<MXFColumn, MetadataColumnDef> coreColumns) {
		this.coreColumns = coreColumns;
	}

	public boolean getHasAS07CoreDMSFramework() {
		return hasAS07CoreDMSFramework;
	}

	public void setHasAS07CoreDMSFramework(boolean hasCore) {
		this.hasAS07CoreDMSFramework = hasCore;
	}

}
