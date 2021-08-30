package com.portalmedia.embarc.gui.mxf;

import java.util.HashMap;

import com.portalmedia.embarc.gui.model.MXFFileInformationViewModel;
import com.portalmedia.embarc.gui.model.MXFSelectedFilesSummary;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.SectionDef;
import com.portalmedia.embarc.parser.mxf.MXFColumn;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;

/**
 * Facilitates communication between UI components controllers
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ControllerMediatorMXF {

	private static class ControllerMediatorHolder {
		private static final ControllerMediatorMXF INSTANCE = new ControllerMediatorMXF();
	}

	public static ControllerMediatorMXF getInstance() {
		return ControllerMediatorHolder.INSTANCE;
	}

	private TabAreaMXFController tabAreaMXFController;

	private EditAreaMXFController editAreaMXFController;
	
	private SessionSummaryController sessionSummaryController;

	private CoreMXFController editMXFController;

	private WriteMXFController writeMXFController;

	private DescriptorMXFController descriptorMXFController;

	private TDBDController tdbdController;

	private FileInfoController fileInfoController;

	private final BooleanProperty isEditing = new SimpleBooleanProperty();

	private ControllerMediatorMXF() {}

	public void registerTabAreaController(TabAreaMXFController controller) {
		tabAreaMXFController = controller;
	}

	public void registerEditAreaController(EditAreaMXFController controller) {
		editAreaMXFController = controller;
	}

	public void registerSessionSummaryController(SessionSummaryController controller) {
		sessionSummaryController = controller;
	}

	public void registerEditMXFCoreForm(CoreMXFController controller) {
		editMXFController = controller;
	}
	
	public void registerWriteView(WriteMXFController controller) {
		writeMXFController = controller;
	}

	public void registerGeneralMXF(DescriptorMXFController controller) {
		descriptorMXFController = controller;
	}

	public void registerTDBDView(TDBDController controller) {
		tdbdController = controller;
	}

	public void registerFileInfoView(FileInfoController controller) {
		fileInfoController = controller;
	}

	public void setCore() {
		editAreaMXFController.setCoreControl();
	}

	public void setDescriptor() {
		editAreaMXFController.setDescriptorControl();
	}

	public void setWriter() {
		editAreaMXFController.setWriteControl();
	}

	public void setTDBD(SectionDef section) {
		editAreaMXFController.setTDBDControl(section);
	}

	public void setFileInfo() {
		editAreaMXFController.setFileInfoControl();
	}

	public void resetEditArea() {
		editAreaMXFController.resetEditArea();
	}

	public void setFileList() {
		tabAreaMXFController.setFiles();
		sessionSummaryController.setFiles();
	}

	public ObservableList<MXFFileInformationViewModel> getSelectedFileList() {
		return tabAreaMXFController.getSelectedFiles();
	}

	public void setSelectedFileList(MXFSelectedFilesSummary list) {
		sessionSummaryController.setSelectedFileList(list);
	}

	public BooleanProperty isEditingProperty() {
		return isEditing;
	}

	public void deleteSelectedFiles() {
		tabAreaMXFController.deleteSelectedFiles();
	}

	public void selectAllFiles() {
		tabAreaMXFController.selectAllFiles();
	}

	public void deselectAllFiles() {
		tabAreaMXFController.deselectAllFiles();
	}
	
	public MXFSelectedFilesSummary getSelectedFilesSummary() {
		return tabAreaMXFController.getSelectedFilesSummary();
	}

	public void updateChangedValues(HashMap<MXFColumn, MetadataColumnDef> changedValues) {
		tabAreaMXFController.updateChangedValues(changedValues);
	}
	
}
