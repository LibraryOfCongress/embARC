package com.portalmedia.embarc.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dizitart.no2.objects.Id;

import com.portalmedia.embarc.parser.dpx.DPXDataTemplate;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

/**
 * Stores user preferences through out the application
 *
 * @author PortalMedia
 * @version 0.1.4+
 * @since 2019-07-24
 */
public class UserPreferences {
	@Id
	int id;
	List<ValidationRuleSetEnum> ruleSets;
	List<String> hiddenDPXColumns;
	boolean saveImageChecksums;
	String imageChecksumReportPath;
	String imageChecksumValidationReportPath;
	String saveToPath;
	String validationReportPath;
	boolean saveToSeparateDirectory;
	boolean writeOnlyEditedFiles;
	HashMap<String, DPXDataTemplate> dpxDataTemplates;

	public UserPreferences() {
		ruleSets = new ArrayList<>();
		hiddenDPXColumns = new ArrayList<>();
		dpxDataTemplates = new HashMap<>();
		ruleSets.add(ValidationRuleSetEnum.SMPTE_C);
		id = 1;
		saveImageChecksums = true;
		writeOnlyEditedFiles = true;
	}

	public void addHiddenDPXColumn(String column) {
		if (!hiddenDPXColumns.contains(column)) {
			hiddenDPXColumns.add(column);
		}
	}
	
	public HashMap<String, DPXDataTemplate> getDPXDataTemplates() {
		return dpxDataTemplates;
	}

	public void addOrUpdateDPXDataTemplate(DPXDataTemplate template) {
		dpxDataTemplates.put(template.getName(), template);
	}

	public void addRuleSet(ValidationRuleSetEnum ruleSet) {
		if (!ruleSets.contains(ruleSet)) {
			ruleSets.add(ruleSet);
		}
	}

	public List<String> getHiddenDPXColumns() {
		return hiddenDPXColumns;
	}

	public String getImageChecksumReportPath() {
		return imageChecksumReportPath;
	}

	public String getImageChecksumValidationReportPath() {
		return imageChecksumValidationReportPath;
	}

	public List<ValidationRuleSetEnum> getRuleSets() {
		return ruleSets;
	}

	public String getSaveToPath() {
		return saveToPath;
	}

	public boolean getSaveToSeparateDirectory() {
		return saveToSeparateDirectory;
	}

	public String getValidationReportPath() {
		return validationReportPath;
	}

	public boolean isRuleSetSelected(ValidationRuleSetEnum rule) {
		return ruleSets.contains(rule);
	}

	public boolean isSaveImageChecksums() {
		return saveImageChecksums;
	}

	public boolean isWriteOnlyEditedFiles() {
		return writeOnlyEditedFiles;
	}

	public void removeDPXDataTemplate(String templateName) {
		dpxDataTemplates.remove(templateName);
	}

	public void removeHiddenDPXColumn(String column) {
		hiddenDPXColumns.remove(column);
	}

	public void removeRuleSet(ValidationRuleSetEnum ruleSet) {
		ruleSets.remove(ruleSet);
	}

	public void setImageChecksumReportPath(String imageChecksumReportPath) {
		this.imageChecksumReportPath = imageChecksumReportPath;
	}

	public void setImageChecksumValidationReportPath(String imageChecksumValidationReportPath) {
		this.imageChecksumValidationReportPath = imageChecksumValidationReportPath;
	}

	public void setRuleSets(List<ValidationRuleSetEnum> ruleSets) {
		this.ruleSets = ruleSets;
	}

	public void setSaveImageChecksums(boolean saveImageChecksums) {
		this.saveImageChecksums = saveImageChecksums;
	}

	public void setSaveToPath(String saveToPath) {
		this.saveToPath = saveToPath;
	}

	public void setSaveToSeparateDirectory(boolean saveToSeparateDirectory) {
		this.saveToSeparateDirectory = saveToSeparateDirectory;
	}

	public void setValidationReportPath(String path) {
		validationReportPath = path;
	}

	public void setWriteOnlyEditedFiles(boolean writeOnlyEditedFiles) {
		this.writeOnlyEditedFiles = writeOnlyEditedFiles;
	}

}
