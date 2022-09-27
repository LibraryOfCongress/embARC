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
public class MetadataColumn implements Serializable, MetadataColumnDef {

	private static final long serialVersionUID = 1L;

	ColumnDef columnDef;

	Class<?> type;
	String name;
	public ByteOrderEnum byteOrder;
	protected byte[] currentValue;

	public MetadataColumn() {

	}

	public MetadataColumn(ColumnDef columnDef, String value) {
		this.columnDef = columnDef;
		this.name = columnDef.getDisplayName();
		this.type = columnDef.getType();
	}
	
	public MetadataColumn(ColumnDef columnDef, ByteOrderEnum byteOrder) {
		this.columnDef = columnDef;
		this.name = columnDef.getDisplayName();
		this.type = columnDef.getType();
		setByteOrder(byteOrder);
	}

	public ByteOrder getByteOrder() {
		if (byteOrder == ByteOrderEnum.BIG) {
			return ByteOrder.BIG_ENDIAN;
		}
		return ByteOrder.LITTLE_ENDIAN;
	}

	public ColumnDef getColumnDef() {
		return columnDef;
	}

	public int getInt() {
		return ByteBuffer.wrap(currentValue).order(getByteOrder()).getInt();
	}

	public boolean isNull() {
		for (final byte b : currentValue) {
			if (b != -1) {
				return false;
			}
		}
		return true;
	}

	private void setByteOrder(ByteOrderEnum bo) {
		byteOrder = bo;
	}

	public void setValue(byte[] newValue) {
		currentValue = newValue;
	}

	@Override
	public String toString() {
		if (isNull()) {
			return "";
		}
		return BytesToStringHelper.toTypedString(type, currentValue, getByteOrder());
	}

	@Override
	public String getCurrentValue() {
		return BytesToStringHelper.toTypedString(type, currentValue, getByteOrder());
	}
	
	public String getStandardizedValue() {
		return BytesToStringHelper.toStandardizedTypedString(type, currentValue, getByteOrder());
	}
}
