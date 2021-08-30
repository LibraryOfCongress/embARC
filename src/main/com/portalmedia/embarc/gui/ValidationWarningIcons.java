package com.portalmedia.embarc.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.validation.DPXColumnValidationRules;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * Displays rule set violation warning icons on invalid data fields
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ValidationWarningIcons extends AnchorPane {
	@FXML
	private HBox warningIcons;

	public ValidationWarningIcons() {
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ValidationWarningIcons.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet) {
		// TODO: if column isn't available when this is called (see
		// centerPaneController) there will be no tooltip
		if (invalidRuleSet == null) {
			return;
		}
		for (final ValidationRuleSetEnum rule : invalidRuleSet) {
			if (rule == ValidationRuleSetEnum.FADGI_O) { // yellow circle
				final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_CIRCLE);
				icon.setStyleClass("fadgi-o-warning");
				icon.setSize("18");
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.FADGI_R) { // orange triangle
				final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
				icon.setStyleClass("fadgi-r-warning");
				icon.setSize("16");
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.FADGI_SR) { // red octagon
				final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_OCTAGON);
				icon.setStyleClass("fadgi-sr-warning");
				icon.setSize("18");
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.SMPTE_C) { // grey square
				final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_BOX);
				icon.setStyleClass("smpte-c-warning");
				icon.setSize("18");
				warningIcons.getChildren().add(icon);
			}
		}
	}

	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet, ColumnDef fieldName) {

		final HashMap<ValidationRuleSetEnum, List<IValidationRule>> validationRuleSet = DPXColumnValidationRules
				.getInstance().getRuleSet(fieldName);

		String ruleText = "No rule set";

		for (final ValidationRuleSetEnum rule : invalidRuleSet) {
			if (rule == ValidationRuleSetEnum.FADGI_O) { // yellow circle
				for (final ValidationRuleSetEnum key : validationRuleSet.keySet()) {
					if (key == rule) {
						for (final IValidationRule vRule : validationRuleSet.get(key)) {
							ruleText = vRule.getRule();
						}
					}
				}
				final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_CIRCLE);
				icon.setStyleClass("fadgi-o-warning");
				final Tooltip tt = new Tooltip(ruleText);
				tt.setStyle("-fx-text-fill: white; -fx-font-size: 12px");
				Tooltip.install(icon, tt);
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.FADGI_R) { // orange triangle
				for (final ValidationRuleSetEnum key : validationRuleSet.keySet()) {
					if (key == rule) {
						for (final IValidationRule vRule : validationRuleSet.get(key)) {
							ruleText = vRule.getRule();
						}
					}
				}
				final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
				icon.setStyleClass("fadgi-r-warning");
				final Tooltip tt = new Tooltip(ruleText);
				tt.setStyle("-fx-text-fill: white;");
				Tooltip.install(icon, tt);
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.FADGI_SR) { // red octagon
				for (final ValidationRuleSetEnum key : validationRuleSet.keySet()) {
					if (key == rule) {
						for (final IValidationRule vRule : validationRuleSet.get(key)) {
							ruleText = vRule.getRule();
						}
					}
				}
				final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_OCTAGON);
				icon.setStyleClass("fadgi-sr-warning");
				final Tooltip tt = new Tooltip(ruleText);
				tt.setStyle("-fx-text-fill: white;");
				Tooltip.install(icon, tt);
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.SMPTE_C) { // grey square
				for (final ValidationRuleSetEnum key : validationRuleSet.keySet()) {
					if (key == rule) {
						for (final IValidationRule vRule : validationRuleSet.get(key)) {
							ruleText = vRule.getRule();
						}
					}
				}
				final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_BOX);
				icon.setStyleClass("smpte-c-warning");
				final Tooltip tt = new Tooltip(ruleText);
				tt.setStyle("-fx-text-fill: white;");
				Tooltip.install(icon, tt);
				warningIcons.getChildren().add(icon);
			}
		}
	}
}
