package com.portalmedia.embarc.gui.model;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.mxf.MXFColumn;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Wrapper for a MXFColumn and necessary methods for getting and display
 * information in table.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class MXFMetadataColumnViewModel {

	private final IntegerProperty length;
	private final StringProperty sectionDisplayName;
	private ColumnDef column;

	public MXFMetadataColumnViewModel() {
		this.length = new SimpleIntegerProperty();
		this.sectionDisplayName = new SimpleStringProperty();
	}

	public ColumnDef getColumn() {
		return column;
	}

	public final String getDisplayName() {
		return column.getDisplayName();
	}

	public final Integer getLength() {
		return this.lengthProperty().get();
	}

	public final String getSectionDisplayName() {
		return column.getSectionDisplayName();
	}

	public boolean isEditable() {
		return column.getEditable();
	}

	public final IntegerProperty lengthProperty() {
		return this.length;
	}

	public final StringProperty sectionDisplayNameProperty() {
		return this.sectionDisplayName;
	}

	public void setColumn(ColumnDef column) {
		this.column = column;
	}

	public final void setLength(final Integer length) {
		this.lengthProperty().set(length);
	}

	public final MXFColumn getMXFColumn() {
		return (MXFColumn)column;
	}

}
