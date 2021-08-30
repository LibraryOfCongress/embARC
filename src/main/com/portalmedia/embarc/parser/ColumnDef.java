package com.portalmedia.embarc.parser;

public interface ColumnDef {

	String getDateFormat();

	String getDisplayName();

	DisplayType getDisplayType();

	boolean getEditable();

	String getFormat();

	int getLength();

	SectionDef getSection();

	String getSectionDisplayName();

	ImageElementDef getSubsection();

	Class<?> getType();

	boolean hasSubsection();

	String toString();

	int getSortOrder();

}