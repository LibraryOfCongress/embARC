package com.portalmedia.embarc.gui.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.SectionDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.collections.ObservableList;

/**
 * Collects and reports violations based on the tab sections of the tableview.
 * Stores a list of sections that have a violation
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class TabSummary {
	private static TabSummary tabSummary;
	private HashSet<SectionDef> sectionViolations;

	public static void append(DPXFileInformationViewModel fivm, HashSet<ValidationRuleSetEnum> ruleSets) {
		for (final ColumnDef column : DPXColumn.values()) {
			if (tabSummary.hasSectionViolation(column.getSection())) {
				continue;
			}

			final HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleViolations = fivm.getRuleViolations(column);
			if (ruleViolations != null && !ruleViolations.isEmpty()) {
				for (final ValidationRuleSetEnum rule : ruleSets) {
					if (ruleViolations.containsKey(rule)) {
						tabSummary.addSectionViolation(column.getSection());
					}
				}
			}
		}
	}

	// Static methods for creating the summary
	public static TabSummary create(ObservableList<DPXFileInformationViewModel> files,
			HashSet<ValidationRuleSetEnum> ruleSets) {
		final TabSummary ts = new TabSummary();

		if (files == null || files.size() == 0) {
			return ts;
		}

		for (final DPXFileInformationViewModel fivm : files) {
			for (final ColumnDef column : DPXColumn.values()) {
				if (ts.hasSectionViolation(column.getSection())) {
					continue;
				}

				final HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleViolations = fivm
						.getRuleViolations(column);
				if (ruleViolations != null && !ruleViolations.isEmpty()) {
					for (final ValidationRuleSetEnum rule : ruleSets) {
						if (ruleViolations.containsKey(rule)) {
							ts.addSectionViolation(column.getSection());
						}
					}
				}
			}
		}
		return ts;
	}

	public static TabSummary getTabSummary() {
		return tabSummary;
	}

	public static void start() {
		tabSummary = new TabSummary();
	}

	public TabSummary() {
		sectionViolations = new HashSet<>();
	}

	private void addSectionViolation(SectionDef section) {
		sectionViolations.add(section);
	}

	public boolean hasSectionViolation(SectionDef section) {
		return sectionViolations.contains(section);
	}

}
