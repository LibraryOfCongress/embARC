package com.portalmedia.embarc.parser;

import java.util.LinkedList;

import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.mxf.MXFColumn;

public interface SectionDef {

	String getDisplayName();

	LinkedList<DPXColumn> getColumns();

	LinkedList<MXFColumn> getColumnsMXF();
}