package com.portalmedia.embarc.parser.dpx;

import java.io.Serializable;

import org.dizitart.no2.objects.Id;

import com.portalmedia.embarc.parser.ColumnDef;

/**
 * Wrapper containing disk data for the DPX file and also it's metadata
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXFileInformation implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name, path;
	private boolean edited;
	@Id
	private int id;
	private DPXMetadata fileData;

	public DPXFileInformation() {
		super();
	}

	public DPXFileInformation(String name, String path, int id) {
		super();
		this.name = name;
		this.path = path;
		this.id = id;
	}

	public boolean getEdited() {
		return edited;
	}

	public DPXMetadata getFileData() {
		return fileData;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getOffsetToImageData() {
		final String userDefinedData = getValue(DPXColumn.USER_DEFINED_DATA);
		int totalMetadata = 0;
		final int userDefintedDataLength = userDefinedData.length();
		for (final ColumnDef column : DPXColumn.values()) {
			if (column != DPXColumn.USER_DEFINED_DATA) {
				totalMetadata += column.getLength();
			}
		}
		final int total = totalMetadata + userDefintedDataLength;
		return total;
	}

	public String getPath() {
		return this.path;
	}

	public String getValue(ColumnDef column) {
		return fileData.getColumn(column).toString();
	}

	public void setFileData(DPXMetadata data) {
		this.fileData = data;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void updateData(ColumnDef column, String value) {
		edited = true;
		fileData.setValue(column, value);
	}
}
