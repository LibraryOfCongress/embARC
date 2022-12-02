package com.portalmedia.embarc.gui.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.portalmedia.embarc.gui.mxf.MXFProfileULMap;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.parser.mxf.MXFFileDescriptorResult;

/**
 * Wrapper for a file. Includes necessary validation rule violations,
 * properties, and OS specific metadata. Used for display in the table.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class MXFFileInformationViewModel {

	private int id;
	private boolean edited = false;
	private LinkedHashMap<String, String> properties;
	private HashMap<MXFColumn, MetadataColumnDef> coreData;
	private HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> tdElements;
	private HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> bdElements;
	private MXFFileDescriptorResult fileDescriptors;

	public MXFFileInformationViewModel(String name, String path, String uuid) {
		coreData = new LinkedHashMap<>();
		properties = new LinkedHashMap<>();
		tdElements = new HashMap<>();
		bdElements = new HashMap<>();
		this.setProp("name", name);
		this.setProp("path", path);
		this.setProp("uuid", uuid);
	}

	public void setId(int id) {
		this.id = id;
		setProp("id", String.valueOf(id));
	}

	public int getId() {
		return id;
	}

	public void setProp(MXFColumn key, String value) {
		String columnName = key.getDisplayName() + key.getSectionDisplayName();
		if (value == null) return;
		properties.put(columnName, value);
	}

	public String getProp(MXFColumn column) {
		String columnName = column.getDisplayName() + column.getSectionDisplayName();
		final String c = properties.get(columnName);
		if (c == null) return "";
		return c;
	}

	public void setProp(String columnName, String value) {
		properties.put(columnName, value);
	}

	public String getProp(String columnName) {
		if ("profile".equals(columnName)) {
			return mapProfileToControlledList(properties.get(columnName));
		}

		return properties.get(columnName);
	}

	public void setProperties(LinkedHashMap<String, String> properties) {
		this.properties = properties;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
	}

	public boolean getEdited() {
		return edited;
	}

	public void setTDElement(String key, LinkedHashMap<MXFColumn, MetadataColumnDef> value) {
		tdElements.put(key, value);
	}

	public void setBDElement(String key, LinkedHashMap<MXFColumn, MetadataColumnDef> value) {
		bdElements.put(key, value);
	}

	public LinkedHashMap<MXFColumn, MetadataColumnDef> getTDElement(String key) {
		return tdElements.get(key);
	}

	public LinkedHashMap<MXFColumn, MetadataColumnDef> getBDElement(String key) {
		return bdElements.get(key);
	}
	
	public HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> getAllTDElements() {
		return tdElements;
	}
	
	public HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> getAllBDElements() {
		return bdElements;
	}

	public String getBDElementProperty(int index, MXFColumn col) {
		int entryIndex = 0;
		String key = "";
		for (Map.Entry<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> entry : bdElements.entrySet()) {
			if (entryIndex == index) key = entry.getKey();
			entryIndex++;
		}
		LinkedHashMap<MXFColumn, MetadataColumnDef> map = bdElements.get(key);
		if (map == null) return "";
		MetadataColumnDef currentCol = map.get(col);
		if (currentCol == null) return "";
		else return currentCol.getCurrentValue();
	}

	public String getTDElementProperty(int index, MXFColumn col) {
		int entryIndex = 0;
		String key = "";
		for (Map.Entry<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> entry : tdElements.entrySet()) {
			if (entryIndex == index) key = entry.getKey();
			entryIndex++;
		}
		LinkedHashMap<MXFColumn, MetadataColumnDef> map = tdElements.get(key);
		if (map == null) return "";
		MetadataColumnDef currentCol = map.get(col);
		if (currentCol == null) return "";
		else return currentCol.getCurrentValue();
	}

	public int getTDElementCount() {
		return tdElements.size();
	}

	public int getBDElementCount() {
		return bdElements.size();
	}

	public void setFileDescriptors(MXFFileDescriptorResult fileDescriptorResult) {
		this.fileDescriptors = fileDescriptorResult;
	}

	public MXFFileDescriptorResult getFileDescriptors() {
		return fileDescriptors;
	}

	public HashMap<MXFColumn, MetadataColumnDef> getCoreData() {
		return coreData;
	}

	public void setCoreData(HashMap<MXFColumn, MetadataColumnDef> coreData) {
		this.coreData = coreData;
	}

	private String mapProfileToControlledList(String profileUl) {
		String stripped = profileUl.replace("urn:smpte:ul:", "");
		HashMap<String, String> profileULMap = new MXFProfileULMap().getMap();
		String val = profileULMap.get(stripped);
		if (val == null || "".equals(val)) return profileUl;
		return val += "\n" + "(" + profileUl + ")";
	}

}
