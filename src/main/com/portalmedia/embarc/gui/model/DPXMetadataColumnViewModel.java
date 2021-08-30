package com.portalmedia.embarc.gui.model;

import java.util.HashMap;
import java.util.List;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Wrapper for a DPXColumn and necessary methods for getting and display
 * information in table.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXMetadataColumnViewModel {

	private final IntegerProperty length;
	private final StringProperty sectionDisplayName;
	private Boolean hasSubsection;
	private ColumnDef column;
	private HashMap<ValidationRuleSetEnum, List<IValidationRule>> validationRules;

	public DPXMetadataColumnViewModel() {
		this.length = new SimpleIntegerProperty();
		this.sectionDisplayName = new SimpleStringProperty();
		this.setHasSubsection(false);
	}

	public ColumnDef getColumn() {
		return column;
	}

	public final String getDisplayName() {
		return column.getDisplayName();
	}

	public Boolean getHasSubsection() {
		return hasSubsection;
	}

	public final Integer getLength() {
		return this.lengthProperty().get();
	}

	public final String getSectionDisplayName() {
		return column.getSectionDisplayName();
	}

	public String getSubsectionName() {
		return column.getSubsection().getDisplayName();
	}

	public boolean isEditable() {
		return column.getEditable();
	}

	public boolean isValid(ValidationRuleSetEnum ruleType, String value) {
		final List<IValidationRule> rules = validationRules.get(ruleType);
		if (rules != null) {
			for (final IValidationRule rule : rules) {
				if (!rule.isValid(value)) {
					return false;
				}
			}
		}
		return true;
	}

	public final IntegerProperty lengthProperty() {
		return this.length;
	}

	public final StringProperty sectionDisplayNameProperty() {
		return this.sectionDisplayName;
	}

	public void setColumn(ColumnDef column) {
		this.column = column;
	}

	public void setHasSubsection(Boolean hasSubsection) {
		this.hasSubsection = hasSubsection;
	}

	public final void setLength(final Integer length) {
		this.lengthProperty().set(length);
	}

	public void setValidationRules(HashMap<ValidationRuleSetEnum, List<IValidationRule>> validationRules) {
		this.validationRules = validationRules;
	}

}
