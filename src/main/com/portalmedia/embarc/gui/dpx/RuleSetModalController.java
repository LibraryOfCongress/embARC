package com.portalmedia.embarc.gui.dpx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;

/**
 * Controls rule set modal
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-03-11
 */
public class RuleSetModalController {

	private static class ControllerHolder {
		private static final RuleSetModalController INSTANCE = new RuleSetModalController();
	}

	public static RuleSetModalController getInstance() {
		return ControllerHolder.INSTANCE;
	}

	private RuleSetModalController() {}
	
	// Present an alert to the user, allowing them to select which type of
	// validation rule sets they would like to be used
	public void showRuleSetsDialog() {
		final Alert ruleSetsDialog = new Alert(AlertType.INFORMATION, "", ButtonType.OK);
		ruleSetsDialog.initModality(Modality.APPLICATION_MODAL);
		ruleSetsDialog.initOwner(Main.getPrimaryStage());
		ruleSetsDialog.setResizable(true);
		ruleSetsDialog.setGraphic(null);
		ruleSetsDialog.setTitle("Rule Sets");
		ruleSetsDialog.setHeaderText(null);
		ruleSetsDialog.setContentText("Toggle rule sets");
		
		final List<CheckBox> checkBoxes = createRuleSetCheckboxes();
		if (checkBoxes.size() > 0) {
			final GridPane grid = new GridPane();
			grid.setHgap(20);
			grid.setVgap(20);
			grid.setPadding(new Insets(20, 50, 20, 20));
			int rowIndex = 0;
			for (final CheckBox cb : checkBoxes) {
				final Text sectionHead = new Text(cb.getId());
				sectionHead.setStyle("fx-font-weight: bold");
				grid.add(sectionHead, 0, rowIndex);
				grid.add(cb, 1, rowIndex);
				rowIndex++;
			}
			ruleSetsDialog.getDialogPane().setContent(grid);
		} else {
			ruleSetsDialog.setContentText("No files in workspace.");
		}
		ruleSetsDialog.initOwner(Main.getPrimaryStage());
		ruleSetsDialog.showAndWait();
		if (ruleSetsDialog.getResult() == ButtonType.OK) {
			ruleSetsDialog.close();
		}
	}
	
	private List<CheckBox> createRuleSetCheckboxes() {
		final List<CheckBox> cbList = new ArrayList<>();
		final HashSet<ValidationRuleSetEnum> currentRules = ControllerMediatorDPX.getInstance().getCurrentRuleSets();
		final String alertTextStyles = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black; -fx-fill: black;"; 
		
		// SMPTE-C - grey square
		final CheckBox smpteCheckbox = new CheckBox("SMPTE-C");
		smpteCheckbox.setStyle(alertTextStyles);
		final FontIcon icon2 = new FontIcon(MaterialDesign.MDI_ALERT_BOX);
		icon2.getStyleClass().add("smpte-c-warning");
		icon2.setIconSize(18);
		smpteCheckbox.setGraphic(icon2);
		if (currentRules.contains(ValidationRuleSetEnum.SMPTE_C)) {
			smpteCheckbox.setSelected(true);
		} else {
			smpteCheckbox.setSelected(false);
		}
		smpteCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean ov, Boolean nv) {
				ControllerMediatorDPX.getInstance().toggleRuleSet(ValidationRuleSetEnum.SMPTE_C);
				ControllerMediatorDPX.getInstance().resetValidationRuleIndicators();
			}
		});
		cbList.add(smpteCheckbox);

		// FADGI-SR - red octagon
		final CheckBox fadgiSRCheckbox = new CheckBox("FADGI-SR");
		fadgiSRCheckbox.setStyle(alertTextStyles);
		final FontIcon icon3 = new FontIcon(MaterialDesign.MDI_ALERT_OCTAGON);
		icon3.getStyleClass().add("fadgi-sr-warning");
		icon3.setIconSize(18);
		fadgiSRCheckbox.setGraphic(icon3);
		if (currentRules.contains(ValidationRuleSetEnum.FADGI_SR)) {
			fadgiSRCheckbox.setSelected(true);
		} else {
			fadgiSRCheckbox.setSelected(false);
		}
		fadgiSRCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean ov, Boolean nv) {
				ControllerMediatorDPX.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_SR);
				ControllerMediatorDPX.getInstance().resetValidationRuleIndicators();
			}
		});
		cbList.add(fadgiSRCheckbox);

		// FADGI-R - orange triangle
		final CheckBox fadgiRCheckbox = new CheckBox("FADGI-R");
		fadgiRCheckbox.setStyle(alertTextStyles);
		final FontIcon icon4 = new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
		icon4.getStyleClass().add("fadgi-r-warning");
		icon4.setIconSize(16);
		fadgiRCheckbox.setGraphic(icon4);
		if (currentRules.contains(ValidationRuleSetEnum.FADGI_R)) {
			fadgiRCheckbox.setSelected(true);
		} else {
			fadgiRCheckbox.setSelected(false);
		}
		fadgiRCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean ov, Boolean nv) {
				ControllerMediatorDPX.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_R);
				ControllerMediatorDPX.getInstance().resetValidationRuleIndicators();
			}
		});
		cbList.add(fadgiRCheckbox);

		// FADGI-O - yellow circle
		final CheckBox fadgiOCheckbox = new CheckBox("FADGI-O");
		fadgiOCheckbox.setStyle(alertTextStyles);
		final FontIcon icon5 = new FontIcon(MaterialDesign.MDI_ALERT_CIRCLE);
		icon5.getStyleClass().add("fadgi-o-warning");
		icon5.setIconSize(18);
		fadgiOCheckbox.setGraphic(icon5);
		if (currentRules.contains(ValidationRuleSetEnum.FADGI_O)) {
			fadgiOCheckbox.setSelected(true);
		} else {
			fadgiOCheckbox.setSelected(false);
		}
		fadgiOCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean ov, Boolean nv) {
				ControllerMediatorDPX.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_O);
				ControllerMediatorDPX.getInstance().resetValidationRuleIndicators();
			}
		});
		cbList.add(fadgiOCheckbox);

		return cbList;
	}

}
