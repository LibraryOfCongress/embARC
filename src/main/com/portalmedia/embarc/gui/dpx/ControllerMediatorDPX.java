package com.portalmedia.embarc.gui.dpx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.gui.model.SelectedFilesSummary;
import com.portalmedia.embarc.parser.SectionDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.system.UserPreferencesService;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 * Facilitates communication between UI components controllers
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ControllerMediatorDPX implements IMediateControllers {
	private MainViewController mainViewController;
	private CenterPaneController centerPaneController;
	private MetadataEditorController metadataEditorController;
	private WorkingSummaryController workingSummaryController;
	private EditorForm editorForm;
	private WriteFilesView writeFilesView;
	private HashSet<ValidationRuleSetEnum> validationRulesSelected;
	private final BooleanProperty isEditing = new SimpleBooleanProperty();

	private static class ControllerMediatorHolder {
		private static final ControllerMediatorDPX INSTANCE = new ControllerMediatorDPX();
	}

	public static ControllerMediatorDPX getInstance() {
		return ControllerMediatorHolder.INSTANCE;
	}

	private ControllerMediatorDPX() {
		initUserPreferences();
	}

	public void createImageChecksumReport() {
		mainViewController.createImageChecksumReport();
	}

	public void createReport() {
		mainViewController.createValidationReport();
	}

	@Override
	public void deleteSelectedFiles() {
		centerPaneController.deleteSelectedFiles();
	}

	@Override
	public void deselectAllFiles() {
		centerPaneController.deselectAllFiles();
	}

	@Override
	public int getCurrentFileCount() {
		return centerPaneController.getTableSize();
	}	

	public HashSet<ValidationRuleSetEnum> getCurrentRuleSets() {
		initUserPreferences();
		return validationRulesSelected;
	}

	public int getEditedFieldsCount() {
		return editorForm.getEditedFieldsCount();
	}

	public ObservableList<DPXFileInformationViewModel> getSelectedFileList() {
		return centerPaneController.getSelectedFiles();
	}

	public SelectedFilesSummary getSelectedFilesSummary() {
		return centerPaneController.getSelectedFilesSummary();
	}

	public TableView<DPXFileInformationViewModel> getTable() {
		return centerPaneController.getTable();
	}

	private void initUserPreferences() {
		final UserPreferencesService userPreferences = new UserPreferencesService();
		if (validationRulesSelected == null) {
			validationRulesSelected = new HashSet<>();
			final List<ValidationRuleSetEnum> selectedRules = userPreferences.getRuleSets();
			for (final ValidationRuleSetEnum rule : selectedRules) {
				validationRulesSelected.add(rule);
			}
		}
	}

	public BooleanProperty isEditingProperty() {
		return isEditing;
	}

	public void notifyColumnsSet() {
	}

	@Override
	public void refetchFileList() {
		setFileList();
		centerPaneController.setSelectedRuleSets(getCurrentRuleSets());
		centerPaneController.setTabWarnings();
		centerPaneController.refreshEditor(true);
	}

	@Override
	public void registerCenterPaneController(CenterPaneController controller) {
		centerPaneController = controller;
	}

	@Override
	public void registerEditorForm(EditorForm controller) {
		editorForm = controller;
	}

	@Override
	public void registerMainViewController(MainViewController controller) {
		mainViewController = controller;
	}

	@Override
	public void registerMetadataEditorController(MetadataEditorController controller) {
		metadataEditorController = controller;
	}

	@Override
	public void registerWorkingSummaryController(WorkingSummaryController controller) {
		workingSummaryController = controller;
	}

	@Override
	public void registerWriteFilesView(WriteFilesView controller) {
		writeFilesView = controller;
	}

	public void resetEditor(SectionDef section) {
		metadataEditorController.resetEditControl(section);
	}

	public void resetValidationRuleIndicators() {
		writeFilesView.resetValidationRuleIndicators();
	}

	@Override
	public void selectAllFiles() {
		centerPaneController.selectAllFiles();
	}

	public void setEditedFieldsCount(int count) {
		if (editorForm != null) editorForm.setEditedFieldsCount(count);
	}

	public void setEditor(SectionDef section) {
		metadataEditorController.setEditControl(section);
	}

	@Override
	public void setFileList() {
		centerPaneController.setFiles();
		workingSummaryController.setFiles();
	}

	@Override
	public void setFiles() {
		workingSummaryController.setFiles();
	}

	public void setGeneralEditor() {
		metadataEditorController.setGeneralControl();
	}

	@Override
	public void setSelectedFileList(ObservableList<DPXFileInformationViewModel> list) {
		workingSummaryController.setSelectedFileList(list);
	}

	public void showColumnVisibilityDialogue() {
		centerPaneController.showColumnVisibilityDialogue();
	}

	public void refreshDataTemplateList() {
		System.out.println("TODO: Refresh data template list");
		Main.setMenuBarDPX(true);
	}

	public int toggleErrorsOnlyFilter(Boolean isChecked) {
		if (isChecked) {
			final int numWithViolations = centerPaneController.FilterByViolations();
			return numWithViolations;
		} else {
			centerPaneController.ClearFilterByViolations();
			return 0;
		}
	}

	public void toggleRuleSet(ValidationRuleSetEnum ruleSet) {
		initUserPreferences();
		final UserPreferencesService userPreferences = new UserPreferencesService();
		if (validationRulesSelected.contains(ruleSet)) {
			validationRulesSelected.remove(ruleSet);
			userPreferences.removeRuleSet(ruleSet);
		} else {
			validationRulesSelected.add(ruleSet);
			userPreferences.addRuleSet(ruleSet);
		}
		centerPaneController.setSelectedRuleSets(validationRulesSelected);
		centerPaneController.refreshValidation();
		metadataEditorController.refreshValidation();
		workingSummaryController.resetErrorCount();
	}

	public void updateChangedValues(HashMap<DPXColumn, String> changedValues) {
		centerPaneController.updateChangedValues(changedValues);
	}

	public void updateChangedValuesAllFiles(HashMap<DPXColumn, String> changedValues) {
		centerPaneController.updateChangedValuesAllFiles(changedValues);
	}

}
