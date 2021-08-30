package com.portalmedia.embarc.parser.dpx;

import com.portalmedia.embarc.parser.ByteOrderEnum;
import com.portalmedia.embarc.parser.MetadataColumn;

public class DPXMetadataColumnFactory {
	
	public static MetadataColumn create(DPXColumn column, byte[] values, ByteOrderEnum byteOrder) throws Exception
	{
		MetadataColumn md = new MetadataColumn(column, byteOrder);	
		md.setValue(values);
		return md;
	}
}
