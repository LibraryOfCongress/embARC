package com.portalmedia.embarc.gui.model;

import java.util.HashSet;

import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

/**
 * Keeps list of IDs associated with each type of violation to keep track of the
 * total number of violations by type.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DatabaseSummary {
	private static HashSet<Integer> files = new HashSet<>();
	private static HashSet<Integer> smptecViolations = new HashSet<>();
	private static HashSet<Integer> fadgioViolations = new HashSet<>();
	private static HashSet<Integer> fadgirViolations = new HashSet<>();
	private static HashSet<Integer> fadgisrViolations = new HashSet<>();

	public static void addFile(DPXFileInformationViewModel fileInformation) {
		final HashSet<ValidationRuleSetEnum> violations = fileInformation.getInvalidRuleSetList();
		final int id = fileInformation.getId();
		files.add(id);

		for (final ValidationRuleSetEnum rule : violations) {
			if (rule == ValidationRuleSetEnum.SMPTE_C) {
				smptecViolations.add(id);
			} else if (rule == ValidationRuleSetEnum.FADGI_O) {
				fadgioViolations.add(id);
			} else if (rule == ValidationRuleSetEnum.FADGI_R) {
				fadgirViolations.add(id);
			} else if (rule == ValidationRuleSetEnum.FADGI_SR) {
				fadgisrViolations.add(id);
			}
		}
	}

	public static void deleteAll() {
		files.clear();
		smptecViolations.clear();
		fadgioViolations.clear();
		fadgirViolations.clear();
		fadgisrViolations.clear();
	}

	public static int getErrorCount(ValidationRuleSetEnum ruleSet) {
		if (ruleSet == ValidationRuleSetEnum.SMPTE_C) {
			return smptecViolations.size();
		} else if (ruleSet == ValidationRuleSetEnum.FADGI_O) {
			return fadgioViolations.size();
		} else if (ruleSet == ValidationRuleSetEnum.FADGI_R) {
			return fadgirViolations.size();
		} else if (ruleSet == ValidationRuleSetEnum.FADGI_SR) {
			return fadgisrViolations.size();
		}
		return 0;
	}

	public static int getFileCount() {
		return files.size();
	}

	public static void removeFile(DPXFileInformationViewModel fileInformation) {
		final int id = fileInformation.getId();
		files.remove(id);
		smptecViolations.remove(id);
		fadgioViolations.remove(id);
		fadgirViolations.remove(id);
		fadgisrViolations.remove(id);
	}

	public static void updateFile(DPXFileInformationViewModel fileInformation) {
		removeFile(fileInformation);
		addFile(fileInformation);
	}

}
