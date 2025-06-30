package com.portalmedia.embarc.gui.dpx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.controlsfx.control.textfield.CustomTextField;

import com.portalmedia.embarc.gui.ValidationWarningHelper;
import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.validation.DPXColumnValidationRules;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * Sets, determines, and reports metadata rule violations
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ValidationChangeListener implements ChangeListener<String> {

	private CustomTextField textField;
	private TextArea textArea;
	private HBox validationInfo;
	ColumnDef column;

	public ValidationChangeListener(CustomTextField textField, ColumnDef column, HBox validationInfo) {
		this.textField = textField;
		this.column = column;
		this.validationInfo = validationInfo;
	}

	public ValidationChangeListener(TextArea textArea, ColumnDef column, HBox validationInfo) {
		this.column = column;
		this.textArea = textArea;
		this.validationInfo = validationInfo;
	}

	@Override
	public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
		if (newValue == null) {
			return;
		}

		final HashMap<ValidationRuleSetEnum, List<IValidationRule>> validationRules = DPXColumnValidationRules
				.getInstance().getRules(column);
		final HashSet<ValidationRuleSetEnum> activeRuleSets = ControllerMediatorDPX.getInstance().getCurrentRuleSets();
		if (validationRules.size() > 0) {
			final Set<ValidationRuleSetEnum> violatedRules = new HashSet<>();
			for (final ValidationRuleSetEnum ruleSet : validationRules.keySet()) {
				if (!activeRuleSets.contains(ruleSet)) {
					continue;
				}
				for (final IValidationRule rule : validationRules.get(ruleSet)) {
					if (!rule.isValid(newValue)) {
						violatedRules.add(ruleSet);
						break;
					}
				}
			}
			
			if(this.textArea != null) {
				setInvalidRuleSets(violatedRules, textArea);
			}
			
			if(this.textField != null) {
				setInvalidRuleSets(violatedRules, textField);
			}
		}
	}
	
	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet, CustomTextField textField) {
		final boolean hasErrors = ValidationWarningHelper.getInvalidRuleSetsAndUpdateErrorIcons(
				validationInfo, 
				invalidRuleSet, 
				column);
		String validationWarning = hasErrors ? "; Contains errors" : "";
		textField.setAccessibleText(textField.getText() + validationWarning);
	}

	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet, TextArea textArea) {
		final boolean hasErrors = ValidationWarningHelper.getInvalidRuleSetsAndUpdateErrorIcons(
				validationInfo, 
				invalidRuleSet, 
				column);
		String validationWarning = hasErrors ? "; Contains errors" : "";
		textArea.setAccessibleText(textArea.getText() + validationWarning);
	}
}
