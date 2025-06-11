package com.portalmedia.embarc.gui;

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
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;

/**
 * Create rule set violation warning icons to display by invalid data fields
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ValidationWarningHelper {
	public static HBox setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet) {
		HBox warningIcons = new HBox();
		// TODO: if column isn't available when this is called (see
		// centerPaneController) there will be no icon
		if (invalidRuleSet == null) {
			return warningIcons;
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
		return warningIcons;
	}
	
	public static boolean getInvalidRuleSetsAndUpdateErrorIcons(
			HBox warningIcons, 
			Set<ValidationRuleSetEnum> invalidRuleSet,
			ColumnDef fieldName) {
		if(warningIcons == null) {
			return false;
		}
		
		warningIcons.getChildren().clear(); //reset warnings
		boolean hasInvalidRules = false;
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
					
				hasInvalidRules = true;
				final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_CIRCLE);
				icon.setAccessibleText(ruleText);
				icon.setStyleClass("fadgi-o-warning-clickable");
	
				HBox iconHbox = ValidationWarningHelper.CreateValidationIcon("FADGI-O", ruleText);
				iconHbox.getChildren().add(icon);
				warningIcons.getChildren().add(iconHbox);
			} else if (rule == ValidationRuleSetEnum.FADGI_R) { // orange triangle
				for (final ValidationRuleSetEnum key : validationRuleSet.keySet()) {
					if (key == rule) {
						for (final IValidationRule vRule : validationRuleSet.get(key)) {
							ruleText = vRule.getRule();
						}
					}
				}
				
				hasInvalidRules = true;
				final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
				icon.setAccessibleText(ruleText);
				icon.setStyleClass("fadgi-r-warning-clickable");
				
				HBox iconHbox = ValidationWarningHelper.CreateValidationIcon("FADGI-R", ruleText);
				iconHbox.getChildren().add(icon);
				warningIcons.getChildren().add(iconHbox);
			} else if (rule == ValidationRuleSetEnum.FADGI_SR) { // red octagon
				for (final ValidationRuleSetEnum key : validationRuleSet.keySet()) {
					if (key == rule) {
						for (final IValidationRule vRule : validationRuleSet.get(key)) {
							ruleText = vRule.getRule();
						}
					}
				}
				
				hasInvalidRules = true;
				final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_OCTAGON);
				icon.setAccessibleText(ruleText);
				icon.setStyleClass("fadgi-sr-warning-clickable");
				
				HBox iconHbox = ValidationWarningHelper.CreateValidationIcon("FADGI-SR", ruleText);
				iconHbox.getChildren().add(icon);
				warningIcons.getChildren().add(iconHbox);
			} else if (rule == ValidationRuleSetEnum.SMPTE_C) { // grey square
				for (final ValidationRuleSetEnum key : validationRuleSet.keySet()) {
					if (key == rule) {
						for (final IValidationRule vRule : validationRuleSet.get(key)) {
							ruleText = vRule.getRule();
						}
					}
				}
				
				hasInvalidRules = true;
				final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_BOX);
				icon.setAccessibleText(ruleText);
				icon.setStyleClass("smpte-c-warning-clickable");
				HBox iconHbox = ValidationWarningHelper.CreateValidationIcon("SMPTE-C", ruleText);
				iconHbox.getChildren().add(icon);
				warningIcons.getChildren().add(iconHbox);
			}
		}
		return hasInvalidRules;
	}

	
	public static HBox CreateValidationIcon(String titleText, String ruleText) {
		HBox iconHbox = new HBox();
		iconHbox.setFocusTraversable(true);
		iconHbox.getStyleClass().add("hbox"); //focus helper
		iconHbox.setAlignment(Pos.CENTER_RIGHT);
		iconHbox.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.SPACE) {
				ValidationWarningHelper.showValidationErrorAlert(titleText, ruleText);
			}
		});
		iconHbox.setOnMouseClicked(event -> {
			ValidationWarningHelper.showValidationErrorAlert(titleText, ruleText);
		});
		iconHbox.setAccessibleRole(AccessibleRole.BUTTON);
		iconHbox.setAccessibleText(titleText + " Open modal for details.");
		return iconHbox;
	}
	
	public static void showValidationErrorAlert(String ruleLabelText, String errorText) {
		final Alert alert = AccessibleAlertHelper.CreateAccessibleAlert(ruleLabelText, AlertType.NONE, errorText, ButtonType.CLOSE);		
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(Main.getPrimaryStage());
		
		 DialogPane dialogPane = alert.getDialogPane();		 
		 dialogPane.lookupButton(ButtonType.CLOSE).setAccessibleHelp(errorText);
		 
		 dialogPane.getStylesheets().add(ValidationWarningHelper.class.getResource("/com/portalmedia/embarc/gui/application.css").toExternalForm());
		 dialogPane.getStyleClass().add("alertDialog");	 

		alert.showAndWait();
		if (alert.getResult() == ButtonType.CLOSE) {
			alert.close();
		}
	}
}
