package com.portalmedia.embarc.gui.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.StringMetadataColumn;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.parser.mxf.MXFFileDescriptorResult;
import com.portalmedia.embarc.parser.mxf.MXFFileInfo;
import com.portalmedia.embarc.parser.mxf.MXFSection;

/**
 * Creates and stores a summary of mxf files in current session.
 * Includes column display values.
 *
 * @author PortalMedia
 * @since 2020-07-14
 **/
public class MXFSelectedFilesSummary {
	private static MXFSelectedFilesSummary summary;
	int fileCount;
	String fileName;
	String filePath;
	HashMap<MXFColumn, String> columnDisplayValues;
	HashMap<MXFFileInfo, String> fileInfoDisplayValues;
	HashSet<MXFColumn> possibleColumns = new HashSet<>();
	HashSet<MXFFileInfo> possibleFileInfoProps = new HashSet<>();
	HashSet<String> possibleTextColumns = new HashSet<>();
	MXFFileDescriptorResult fileDescriptors;
	private HashMap<MXFColumn, MetadataColumnDef> coreData;
	HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> tdElements;
	HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> bdElements;
	boolean filesAreMissingAS07CoreDMSFramework;

	public MXFSelectedFilesSummary() {
		columnDisplayValues = new HashMap<>();
		fileInfoDisplayValues = new HashMap<>();
		fileCount = 0;
		possibleTextColumns.add("fileName");
		possibleTextColumns.add("filePath");
		for (final MXFColumn c : MXFColumn.values()) {
			possibleColumns.add(c);
		}
		for (final MXFFileInfo f : MXFFileInfo.values()) {
			possibleFileInfoProps.add(f);
		}
	}

	public String getDisplayValues(ColumnDef column) {
		return columnDisplayValues.get(column);
	}

	public void setDisplayValue(MXFColumn column, String value) {
		if (value == null) value = "";
		if (columnDisplayValues.containsKey(column) && !columnDisplayValues.get(column).equals(value)) {
			columnDisplayValues.put(column, "{multiple values}");
			possibleColumns.remove(column);
		} else {
			columnDisplayValues.put(column, value);
		}
	}
	
	public String getFileInfoDisplayValues(MXFFileInfo fileInfoProp) {
		return fileInfoDisplayValues.get(fileInfoProp);
	}

	public void setFileInfoDisplayValue(MXFFileInfo fileInfoProp, String value) {
		if (value == null) value = "";
		if (fileInfoDisplayValues.containsKey(fileInfoProp) && !fileInfoDisplayValues.get(fileInfoProp).equals(value)) {
			fileInfoDisplayValues.put(fileInfoProp, "{multiple values}");
			possibleFileInfoProps.remove(fileInfoProp);
		} else {
			fileInfoDisplayValues.put(fileInfoProp, value);
		}
	}

	public MXFFileDescriptorResult getFileDescriptors() {
		return fileDescriptors;
	}

	public void setFileDescriptors(MXFFileDescriptorResult fileDescriptorResult) {
		if (getFileCount() == 1) fileDescriptors = fileDescriptorResult;
		else fileDescriptors = null;
	}

	public HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> getAllTDElements() {
		return tdElements;
	}
	
	public LinkedHashMap<MXFColumn, MetadataColumnDef> getTDElement(String key) {
		return tdElements.get(key);
	}

	public void setTDElements(HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> tdElementsResult) {
		if (getFileCount() == 1) tdElements = tdElementsResult;
		else tdElements = null;
	}

	public HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> getAllBDElements() {
		return bdElements;
	}

	public LinkedHashMap<MXFColumn, MetadataColumnDef> getBDElement(String key) {
		return bdElements.get(key);
	}

	public void setBDElements(HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> bdElementsResult) {
		if (getFileCount() == 1) bdElements = bdElementsResult;
		else bdElements = null;
	}

	public int getFileCount() {
		return fileCount;
	}

	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		if (this.fileName == null || this.fileName.isEmpty() || this.fileName.equals(fileName)) {
			this.fileName = fileName;
		} else {
			this.fileName = "{multiple values}";
			possibleTextColumns.remove("fileName");
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		if (this.filePath == null || this.filePath.isEmpty() || this.filePath.equals(filePath)) {
			this.filePath = filePath;
		} else {
			this.filePath = "{multiple values}";
			possibleTextColumns.remove("filePath");
		}
	}

	public HashMap<MXFColumn, MetadataColumnDef> getCoreData() {
		return coreData;
	}

	public void setCoreData(HashMap<MXFColumn, MetadataColumnDef> newCoreData) {
		if (getFileCount() == 1) {
			coreData = newCoreData;
			return;
		}

		HashMap<MXFColumn, MetadataColumnDef> currentData = getCoreData();

		if (currentData == null || currentData.isEmpty()) {
			coreData = newCoreData;
			return;
		}

		HashMap<MXFColumn, MetadataColumnDef> combinedData = new LinkedHashMap<MXFColumn, MetadataColumnDef>();

		for (Map.Entry<MXFColumn, MetadataColumnDef> entry : currentData.entrySet()) {
		    if (newCoreData == null) continue;
		    
		    MXFColumn key = entry.getKey();
		    MetadataColumnDef value = entry.getValue();

		    if (newCoreData.get(key) == null) continue;
		    
		    String newValue = newCoreData.get(key).getCurrentValue();

		    if (key == MXFColumn.AS_07_Core_DMS_Devices || key == MXFColumn.AS_07_Core_DMS_Identifiers) {
		    	System.out.println("skipping devices/identifiers");
		    	combinedData.put(key, new StringMetadataColumn(key, newValue));
		    	continue;
		    }
		    
		    if (newValue == null || value.getCurrentValue() == null) continue;

		    if (!newValue.equals(value.getCurrentValue())) {
		    	combinedData.put(key, new StringMetadataColumn(key, "{multiple values}"));
		    } else {
		    	combinedData.put(key, new StringMetadataColumn(key, value.getCurrentValue()));
		    }
		}

		coreData = combinedData;
	}

	public boolean getFilesAreMissingAS07CoreDMSFramework() {
		return filesAreMissingAS07CoreDMSFramework;
	}

	public void setFilesAreMissingAS07CoreDMSFramework(boolean filesAreMissingAS07CoreDMSFramework) {
		this.filesAreMissingAS07CoreDMSFramework = filesAreMissingAS07CoreDMSFramework;
	}

	public static MXFSelectedFilesSummary create(List<MXFFileInformationViewModel> files) {
		summary = new MXFSelectedFilesSummary();
		summary.setFileCount(files.size());

		if (files == null || files.size() == 0) return summary;
		boolean missingAS07CoreDMSFramework = false;

		for (final MXFFileInformationViewModel vm : files) {
			summary.setCoreData(vm.getCoreData());
			summary.setFileDescriptors(vm.getFileDescriptors());
			summary.setTDElements(vm.getAllTDElements());
			summary.setBDElements(vm.getAllBDElements());
			summary.setFileName(vm.getProp("name"));
			summary.setFilePath(vm.getProp("path"));
			if (vm.getProp("hasAS07CoreDMSFramework").equals("false")) {
				missingAS07CoreDMSFramework = true;
			}

			@SuppressWarnings("unchecked")
			final HashSet<MXFColumn> columnsLeft = (HashSet<MXFColumn>) summary.getPossibleColumnsLeft().clone();
			for (final MXFColumn column : columnsLeft) {
				if (column.getSection() == MXFSection.CORE) continue;
				String value = vm.getProp(column.getDisplayName());
				if (value == null) value = "";
				summary.setDisplayValue(column, value);
			}
			@SuppressWarnings("unchecked")
			final HashSet<MXFFileInfo> fileInfoPropsLeft = (HashSet<MXFFileInfo>) summary.getPossibleFileInfoPropsLeft().clone();
			for (final MXFFileInfo fileInfo : fileInfoPropsLeft) {
				String value = vm.getProp(fileInfo.getIdentifier());
				if (value == null) value = "";
				summary.setFileInfoDisplayValue(fileInfo, value);
			}

			if (!summary.hasPossibleColumnsLeft()) break;
		}

		summary.setFilesAreMissingAS07CoreDMSFramework(missingAS07CoreDMSFramework);
		System.currentTimeMillis();
		return summary;
	}

	public static MXFSelectedFilesSummary append(MXFFileInformationViewModel vm) {
		summary.setFileCount(summary.getFileCount() + 1);
		summary.setFileName(vm.getProp("name"));
		summary.setFilePath(vm.getProp("path"));
		@SuppressWarnings("unchecked")
		final HashSet<MXFColumn> columnsLeft = (HashSet<MXFColumn>) summary.getPossibleColumnsLeft().clone();
		for (final MXFColumn column : columnsLeft) {
			String value = vm.getProp(column);
			if (value == null) value = "";
			summary.setDisplayValue(column, value);
		}
		@SuppressWarnings("unchecked")
		final HashSet<MXFFileInfo> fileInfoPropsLeft = (HashSet<MXFFileInfo>) summary.getPossibleFileInfoPropsLeft().clone();
		for (final MXFFileInfo fileInfo : fileInfoPropsLeft) {
			String value = vm.getProp(fileInfo.getDisplayName());
			if (value == null) value = "";
			summary.setFileInfoDisplayValue(fileInfo, value);
		}
		return summary;
	}

	public HashSet<MXFColumn> getPossibleColumnsLeft() {
		return possibleColumns;
	}

	public boolean hasPossibleColumnsLeft() {
		return possibleColumns.size() > 0 || possibleTextColumns.size() > 0;
	}
	
	public HashSet<MXFFileInfo> getPossibleFileInfoPropsLeft() {
		return possibleFileInfoProps;
	}

	public boolean hasPossibleFileInfoPropsLeft() {
		return possibleFileInfoProps.size() > 0 || possibleTextColumns.size() > 0;
	}

}
