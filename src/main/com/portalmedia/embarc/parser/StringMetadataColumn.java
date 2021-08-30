package com.portalmedia.embarc.parser;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.portalmedia.embarc.parser.dpx.DPXColumn;

/**
 * Method to store all needed information about a metadata column.
 *
 * TODO: Is this class necessary? Can we get to a step where it is no longer
 * needed.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class StringMetadataColumn implements Serializable, MetadataColumnDef {

	private static final long serialVersionUID = 1L;

	ColumnDef columnDef;

	Class<?> type;
	String name;
	public ByteOrderEnum byteOrder;
	protected String currentValue;

	public StringMetadataColumn() {

	}

	public StringMetadataColumn(ColumnDef columnDef, String value) {
		this.columnDef = columnDef;
		this.name = columnDef.getDisplayName();
		this.type = columnDef.getType();
		this.currentValue = value;
	}

	@Override
	public ColumnDef getColumnDef() {
		return columnDef;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	@Override
	public String toString() {
		return currentValue;
	}

	public void setValue(String value) {
		currentValue = value;
	}

}
