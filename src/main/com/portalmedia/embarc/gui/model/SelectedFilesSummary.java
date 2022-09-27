package com.portalmedia.embarc.gui.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

/**
 * Creates and stores a summary of files in the database. Includes a list of
 * violations, violated rules sets, and column display values.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class SelectedFilesSummary {
	private static SelectedFilesSummary summary;

	HashMap<DPXColumn, String> columnDisplayValues;
	HashMap<DPXColumn, HashMap<ValidationRuleSetEnum, List<IValidationRule>>> ruleViolations;
	HashSet<DPXColumn> possibleColumns = new HashSet<>();
	HashSet<String> possibleTextColumns = new HashSet<>();
	int fileCount;
	HashSet<ValidationRuleSetEnum> validRuleSets;
	String fileName;
	String filePath;

	public SelectedFilesSummary() {
		columnDisplayValues = new HashMap<>();
		validRuleSets = new HashSet<>();
		ruleViolations = new HashMap<>();
		fileCount = 0;
		possibleTextColumns.add("fileName");
		possibleTextColumns.add("filePath");
		for (final DPXColumn c : DPXColumn.values()) {
			possibleColumns.add(c);
		}
	}

	public static SelectedFilesSummary append(DPXFileInformationViewModel fivm) {

		summary.setFileCount(summary.getFileCount() + 1);

		summary.setFileName(fivm.getProp("name"));
		summary.setFilePath(fivm.getProp("path"));
		@SuppressWarnings("unchecked")
		final HashSet<DPXColumn> columnsLeft = (HashSet<DPXColumn>) summary.getPossibleColumnsLeft().clone();
		for (final DPXColumn column : columnsLeft) {
			String value = fivm.getProp(column);
			if (value == null) {
				value = "";
			}
			summary.setDisplayValue(column, value);
			final HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleViolations = fivm.getRuleViolations(column);
			if (ruleViolations == null) {
				continue;
			}
			for (final ValidationRuleSetEnum ruleSet : ruleViolations.keySet()) {
				summary.addRuleViolation(column, ruleSet, ruleViolations.get(ruleSet));
			}
		}
		return summary;
	}

	public static SelectedFilesSummary create(List<DPXFileInformationViewModel> files) {

		summary = new SelectedFilesSummary();

		summary.setFileCount(files.size());

		if (files == null || files.size() == 0) {
			return summary;
		}

		// final ExecutorService executor = Executors.newFixedThreadPool(5); // it's
		// just an arbitrary number
		// final List<Future<?>> futures = new ArrayList<>();

		for (final DPXFileInformationViewModel fivm : files) {
			// Future<?> future = executor.submit(() -> {
			summary.setFileName(fivm.getProp("name"));
			summary.setFilePath(fivm.getProp("path"));
			@SuppressWarnings("unchecked")
			final HashSet<DPXColumn> columnsLeft = (HashSet<DPXColumn>) summary.getPossibleColumnsLeft().clone();
			for (final DPXColumn column : columnsLeft) {
				String value = fivm.getProp(column);
				if (value == null) {
					value = "";
				}
				summary.setDisplayValue(column, value);

				final HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleViolations = fivm
						.getRuleViolations(column);
				if (ruleViolations == null) {
					continue;
				}
				for (final ValidationRuleSetEnum ruleSet : ruleViolations.keySet()) {
					summary.addRuleViolation(column, ruleSet, ruleViolations.get(ruleSet));
				}

			}
			if (!summary.hasPossibleColumnsLeft()) {
				break; // executor.shutdown();
				// });
				// futures.add(future);
			}
		}
		// try {
		// for (Future<?> future : futures) {
		// future.get(); // do anything you need, e.g. isDone(), ...
		// }
		// } catch (InterruptedException | ExecutionException e) {
		// e.printStackTrace();
		// }

		System.currentTimeMillis();
		return summary;
	}

	public void addRuleViolation(DPXColumn column, ValidationRuleSetEnum ruleSet, List<IValidationRule> rules) {
		if (rules == null || rules.size() == 0) {
			return;
		}
		HashMap<ValidationRuleSetEnum, List<IValidationRule>> violations;
		if (!ruleViolations.containsKey(column)) {
			violations = new HashMap<>();
		} else {
			violations = ruleViolations.get(column);
		}

		violations.put(ruleSet, rules);
		ruleViolations.put(column, violations);
	}

	public String getDisplayValues(ColumnDef column) {
		return columnDisplayValues.get(column);
	}

	public int getFileCount() {
		return fileCount;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public HashSet<DPXColumn> getPossibleColumnsLeft() {
		return possibleColumns;
	}

	public Set<ValidationRuleSetEnum> getRuleSetViolations(ColumnDef column) {
		final HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleSetViolations = ruleViolations.get(column);

		final HashSet<ValidationRuleSetEnum> toReturn = new HashSet<>();

		if (ruleSetViolations == null) {
			return new HashSet<>();
		}

		for (final ValidationRuleSetEnum selectedRule : validRuleSets) {
			if (ruleSetViolations.containsKey(selectedRule)) {
				toReturn.add(selectedRule);
			}
		}

		return toReturn;
	}

	public boolean hasPossibleColumnsLeft() {
		return possibleColumns.size() > 0 || possibleTextColumns.size() > 0;
	}

	public void setDisplayValue(DPXColumn column, String value) {
		if (value == null) {
			value = "";
		}
		if (columnDisplayValues.containsKey(column) && !columnDisplayValues.get(column).equals(value)) {
			columnDisplayValues.put(column, "{multiple values}");
			possibleColumns.remove(column);
		} else {
			columnDisplayValues.put(column, value);
		}
	}

	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public void setFileName(String fileName) {
		if (this.fileName == null || this.fileName.isEmpty() || this.fileName.equals(fileName)) {
			this.fileName = fileName;
		} else {
			this.fileName = "{multiple values}";
			possibleTextColumns.remove("fileName");
		}
	}

	public void setFilePath(String filePath) {
		if (this.filePath == null || this.filePath.isEmpty() || this.filePath.equals(filePath)) {
			this.filePath = filePath;
		} else {
			this.filePath = "{multiple values}";
			possibleTextColumns.remove("filePath");
		}
	}

	public void setValidRuleSets(HashSet<ValidationRuleSetEnum> selectedRuleSets) {
		validRuleSets = selectedRuleSets;
	}
}
