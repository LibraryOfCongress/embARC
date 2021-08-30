package com.portalmedia.embarc.gui.helper;

import java.util.HashMap;

/**
 * Keeps track of row numbers per file shown in the table view. Used to track
 * which file is associated with row in reporting.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class RowNumberManager {
	// ID to rownumber
	private static HashMap<Integer, Integer> rowNumbers = new HashMap<>();

	public static void addRow(int id, int rowNumber) {
		rowNumbers.put(id, rowNumber);
	}

	public static int getRowNum(int id) {
		if (!rowNumbers.containsKey(id)) {
			return 0;
		}
		return rowNumbers.get(id);
	}

	public static boolean hasRowNum(int id) {
		return rowNumbers.containsKey(id);
	}

	public static void removeRow(int id, int rowNumber) {
		rowNumbers.remove(id);
	}
}
