package com.portalmedia.embarc.parser.mxf;

import java.util.LinkedList;
import java.util.List;

import com.portalmedia.embarc.gui.model.MXFMetadataColumnViewModel;
import com.portalmedia.embarc.parser.ColumnDef;

/**
 * Singleton to manage a list of MXF column objects needed for the TableView
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class MXFMetadataColumnViewModelList {

	private static MXFMetadataColumnViewModelList instance = null;

	public static MXFMetadataColumnViewModelList getInstance() {
		if (instance == null) {
			instance = new MXFMetadataColumnViewModelList();
		}

		return instance;
	}

	private List<MXFMetadataColumnViewModel> columns;

	private MXFMetadataColumnViewModelList() {
		buildMXFColumns();
	}

	private void buildMXFColumns() {
		columns = new LinkedList<>();
		for (final ColumnDef c : MXFColumn.values()) {
			final MXFMetadataColumnViewModel mcvm = new MXFMetadataColumnViewModel();
			mcvm.setLength(c.getLength());
			mcvm.setColumn(c);
			columns.add(mcvm);
		}
	}

	public List<MXFMetadataColumnViewModel> getColumns() {
		return columns;
	}

}
