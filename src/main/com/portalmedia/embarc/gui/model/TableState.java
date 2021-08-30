package com.portalmedia.embarc.gui.model;

/**
 * Keeps track of TableView state
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class TableState {

	private static boolean selectAll = false;
	private static boolean deselectAll = false;
	private static boolean updateSummary = false;
	private static boolean updateEditor = false;
	private static boolean deleteFiles = false;
	private static boolean updatingSummary = false;
	private static boolean updateTabs = false;
	private static boolean currentlySelectAll = false;
	private static boolean currentlyFilteredByErrors = false;
	
	public static boolean isSelectAll() {
		return selectAll;
	}
	public static void setSelectAll(boolean selectAll) {
		TableState.selectAll = selectAll;
	}
	public static boolean getUpdateSummary() {
		return updateSummary;
	}
	public static void setUpdateSummary(boolean updateSummary) {
		TableState.updateSummary = updateSummary;
		if(updateSummary) updateEditor = true;
	}
	public static boolean getUpdateEditor() {
		return updateEditor;
	}
	public static void setUpdateEditor(boolean updateEditor) {
		TableState.updateEditor = updateEditor;
	}
	public static boolean getDeselectAll() {
		return deselectAll;
	}
	public static void setDeselectAll(boolean deselectAll) {
		TableState.deselectAll = deselectAll;
	}
	public static boolean getDeleteFiles() {
		return deleteFiles;
	}
	public static void setDeleteFiles(boolean deleteFiles) {
		updateEditor = true;
		TableState.deleteFiles = deleteFiles;
	}
	public static boolean isUpdatingSummary() {
		return updatingSummary;
	}
	public static void setUpdatingSummary(boolean updatingSummary) {
		TableState.updatingSummary = updatingSummary;
	}
	public static boolean isUpdateTabs() {
		return updateTabs;
	}
	public static void setUpdateTabs(boolean updateTabs) {
		TableState.updateTabs = updateTabs;
	}
	public static boolean isCurrentlyFilteredByErrors() {
		return currentlyFilteredByErrors;
	}
	public static void setCurrentlyFilteredByErrors(boolean currentlyFilteredByErrors) {
		TableState.currentlyFilteredByErrors = currentlyFilteredByErrors;
	}
	public static boolean isCurrentlySelectAll() {
		return currentlySelectAll;
	}
	public static void setCurrentlySelectAll(boolean currentlySelectAll) {
		TableState.currentlySelectAll = currentlySelectAll;
	}
	
}
