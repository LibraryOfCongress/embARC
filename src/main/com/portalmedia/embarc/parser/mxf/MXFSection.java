package com.portalmedia.embarc.parser.mxf;

import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.portalmedia.embarc.parser.SectionDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;

public enum MXFSection implements SectionDef {
	FILEINFO("File Information"),
	CORE("Core"),
	BD("BD"),
	TD("TD"),
	OBJECT("Object");
	
	private String displayName;

	@JsonCreator
	private MXFSection(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public LinkedList<MXFColumn> getColumnsMXF() {
		LinkedList<MXFColumn> list = new LinkedList<MXFColumn>();
		for (MXFColumn column : MXFColumn.values()) {
			if (column.getSection() == this) list.add(column);
		}
		return list;
	}

	@Override
	public LinkedList<DPXColumn> getColumns() {
		// TODO Auto-generated method stub
		return null;
	}
}
