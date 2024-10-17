package com.portalmedia.embarc.system;

import java.util.HashMap;
import java.util.List;

import com.portalmedia.embarc.database.DBService;
import com.portalmedia.embarc.parser.dpx.DPXDataTemplate;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

/**
 * Handles communication between the UI layer and database for storing user
 * preferences
 *
 * @author PortalMedia
 * @version 0.1.4+
 * @since 2019-07-24
 */
public class UserPreferencesService {

	DBService<UserPreferences> dbService;
	UserPreferences userPreferences;

	public UserPreferencesService() {
		dbService = new DBService<UserPreferences>(UserPreferences.class);
		userPreferences = getUserPreferences();
	}

	// Get all stored DPXDataTemplates
	public HashMap<String, DPXDataTemplate> getDPXDataTemplates() {
		return userPreferences.getDPXDataTemplates();
	}

	// Store a DPX meta-data template
	public void addDpxDataTemplate(DPXDataTemplate template) {
		userPreferences.addOrUpdateDPXDataTemplate(template);
		dbService.update(userPreferences);
	}

	// Add a hidden DPX column
	public void addHiddenDPXColumn(String columnName) {
		userPreferences.addHiddenDPXColumn(columnName);
		dbService.update(userPreferences);
	}

	// Add a validation rule set
	public void addRuleSet(ValidationRuleSetEnum ruleSet) {
		userPreferences.addRuleSet(ruleSet);
		dbService.update(userPreferences);
	}

	// Get the list of hidden DPX columns
	public List<String> getHiddenDPXColumns() {
		return userPreferences.getHiddenDPXColumns();
	}

	// Get the image checksum report directory path
	public String getImageChecksumReportPath() {
		return userPreferences.getImageChecksumReportPath();
	}

	// Get the image checksum validation report directory path (generated when writing files)
	public String getImageChecksumValidationReportPath() {
		return userPreferences.getImageChecksumValidationReportPath();
	}

	// Get the list of selected rule sets
	public List<ValidationRuleSetEnum> getRuleSets() {
		return userPreferences.getRuleSets();
	}

	// Get user preference for saving image checksums when writing files
	public boolean getSaveImageChecksums() {
		return userPreferences.isSaveImageChecksums();
	}

	// Get the directory path for writing files when not overwriting files
	public String getSaveToPath() {
		return userPreferences.getSaveToPath();
	}

	// Get user preference for saving to a separated directory
	public boolean getSaveToSeparateDirectory() {
		return userPreferences.getSaveToSeparateDirectory();
	}

	// Get user preferences from the database. If it doesn't exists, create a new
	// one.
	private UserPreferences getUserPreferences() {
		// ID of user preferences is hard coded to 1 at this point. At some point in the
		// future, different types of users and/or different preferences could be
		// assigned
		UserPreferences userPreferences = dbService.get(1);
		if (userPreferences == null) {
			userPreferences = new UserPreferences();
			dbService.add(userPreferences);
		}
		return userPreferences;
	}

	// Get the validation rule violation report directory path
	public String getValidationReportPath() {
		return userPreferences.getValidationReportPath();
	}

	// Get user preference for writing only edited files in write files dialog
	public boolean getWriteOnlyEditedFiles() {
		return userPreferences.isWriteOnlyEditedFiles();
	}

	// Remove a DPX meta data template
	public void removeDpxDataTemplate(String templateName) {
		userPreferences.removeDPXDataTemplate(templateName);
		dbService.update(userPreferences);
	}

	// Remove a dpx column from the list of hidden columns
	public void removeHiddenDPXColumn(String columnName) {
		userPreferences.removeHiddenDPXColumn(columnName);
		dbService.update(userPreferences);
	}

	// Remove a rule set from the list of selected rule sets
	public void removeRuleSet(ValidationRuleSetEnum ruleSet) {
		userPreferences.removeRuleSet(ruleSet);
		dbService.update(userPreferences);
	}

	// Sets the directory path for the image checksum report
	public void setImageChecksumReportPath(String imageChecksumReportPath) {
		userPreferences.setImageChecksumReportPath(imageChecksumReportPath);
		dbService.update(userPreferences);
	}

	// Set preference for saving files to a separate directory
	public void setImageChecksumValidationReportPath(String value) {
		userPreferences.setImageChecksumValidationReportPath(value);
		dbService.update(userPreferences);
	}

	// Sets a selected validation rule set
	public void setRuleSets(List<ValidationRuleSetEnum> ruleSets) {
		userPreferences.setRuleSets(ruleSets);
		dbService.update(userPreferences);
	}

	// Set preference for saving image checksums when writing files
	public void setSaveImageChecksums(boolean value) {
		userPreferences.setSaveImageChecksums(value);
		dbService.update(userPreferences);
	}

	// Set preference for output path for saving files to a separate directory
	public void setSaveToPath(String value) {
		userPreferences.setSaveToPath(value);
		dbService.update(userPreferences);
	}

	// Set preference for saving files to a separate directory
	public void setSaveToSeparateDirectory(boolean value) {
		userPreferences.setSaveToSeparateDirectory(value);
		dbService.update(userPreferences);
	}

	// Set the directory path for the validation rule report path
	public void setValidationReportPath(String path) {
		userPreferences.setValidationReportPath(path);
		dbService.update(userPreferences);
	}

	// Set preference for only writing over edited files
	public void setWriteOnlyEditedFiles(boolean value) {
		userPreferences.setWriteOnlyEditedFiles(value);
		dbService.update(userPreferences);
	}
}
