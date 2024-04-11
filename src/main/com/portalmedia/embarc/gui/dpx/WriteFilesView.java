package com.portalmedia.embarc.gui.dpx;

import java.io.IOException;
import java.util.HashSet;

import com.portalmedia.embarc.gui.model.DatabaseSummary;
import com.portalmedia.embarc.parser.SectionDef;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Editor pane view when no files are selected. Displays rule set check boxes
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class WriteFilesView extends AnchorPane {
	@FXML
	private AnchorPane writeFilesSummaryPane;
	private CheckBox smptcCB;
	private CheckBox fsrCB;
	private CheckBox frCB;
	private CheckBox foCB;

	public WriteFilesView() {
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WriteFilesView.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
		ControllerMediatorDPX.getInstance().registerWriteFilesView(this);
		smptcCB = new CheckBox();
		fsrCB = new CheckBox();
		frCB = new CheckBox();
		foCB = new CheckBox();
		resetValidationRuleIndicators();
	}

	public void resetValidationRuleIndicators() {
		final HashSet<ValidationRuleSetEnum> selectedRules = ControllerMediatorDPX.getInstance().getCurrentRuleSets();

		if (selectedRules.contains(ValidationRuleSetEnum.SMPTE_C)) {
			smptcCB.setSelected(true);
		} else {
			smptcCB.setSelected(false);
		}

		if (selectedRules.contains(ValidationRuleSetEnum.FADGI_SR)) {
			fsrCB.setSelected(true);
		} else {
			fsrCB.setSelected(false);
		}

		if (selectedRules.contains(ValidationRuleSetEnum.FADGI_R)) {
			frCB.setSelected(true);
		} else {
			frCB.setSelected(false);
		}

		if (selectedRules.contains(ValidationRuleSetEnum.FADGI_O)) {
			foCB.setSelected(true);
		} else {
			foCB.setSelected(false);
		}

	}

	public void setMessage(SectionDef section) {
		
		final VBox container = new VBox();
		AnchorPane.setTopAnchor(container, 0.00);
		AnchorPane.setRightAnchor(container, 0.00);
		AnchorPane.setBottomAnchor(container, 0.00);
		AnchorPane.setLeftAnchor(container, 0.00);
		writeFilesSummaryPane.getChildren().add(container);
		
		final VBox headerBox = new VBox();
		headerBox.getStyleClass().add("editor-header-box");
		headerBox.setSpacing(10);
		AnchorPane.setTopAnchor(headerBox, 0.00);
		AnchorPane.setRightAnchor(headerBox, 0.00);
		AnchorPane.setBottomAnchor(headerBox, 0.00);
		AnchorPane.setLeftAnchor(headerBox, 0.00);
		final Label l1 = new Label("File Summary");
		l1.setFocusTraversable(true);
		l1.getStyleClass().add("editor-header");
		headerBox.getChildren().add(l1);
		final Label l2 = new Label("Select one or more files to view and edit data");
		l2.setFocusTraversable(true);
		l2.getStyleClass().add("editor-header-selected-files-label");
		headerBox.getChildren().add(l2);

		final HBox gridBox = new HBox();
		gridBox.setPadding(new Insets(10, 0, 0, 10));
		final GridPane grid = new GridPane();
		gridBox.getChildren().add(grid);
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(15);
		grid.setVgap(15);

		container.getChildren().addAll(headerBox, gridBox);

		final Label totalFilesLabel = new Label("Total Files:");
		grid.add(totalFilesLabel, 0, 0);

		final Label totalFilesText = new Label(String.valueOf(DatabaseSummary.getFileCount()));
		grid.add(totalFilesText, 1, 0);
		totalFilesLabel.setAccessibleText(totalFilesText.getText() + " total files");
		totalFilesLabel.setFocusTraversable(true);

		smptcCB.setId("SMPTE_C");
		final Label smptcLabel = new Label("Files with SMPTE-C Violations"); // grey square
		smptcLabel.setLabelFor(smptcCB);
		final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_BOX);
		icon.setStyleClass("smpte-c-warning");
		icon.setSize("18");
		smptcLabel.setGraphic(icon);
		HBox smptcHBox = new HBox();
		smptcHBox.setSpacing(10);
		smptcHBox.getChildren().add(smptcCB);
		smptcHBox.getChildren().add(smptcLabel);
		grid.add(smptcHBox, 0, 1);
		int smptcErrors = DatabaseSummary.getErrorCount(ValidationRuleSetEnum.SMPTE_C);
		grid.add(new Label(String.valueOf(smptcErrors)), 1, 1);
		smptcLabel.setAccessibleText(smptcErrors + " files with SMPTE-C Violations.");
		smptcCB.setOnAction(event -> handleCheckboxEvent(event));

		fsrCB.setId("FADGI_SR");
		final Label fsrLabel = new Label("Files with FADGI-SR violations"); // red octagon
		fsrLabel.setLabelFor(fsrCB);
		final MaterialDesignIconView icon1 = new MaterialDesignIconView(MaterialDesignIcon.ALERT_OCTAGON);
		icon1.setStyleClass("fadgi-sr-warning");
		icon1.setSize("18");
		fsrLabel.setGraphic(icon1);
		HBox fsrHBox = new HBox();
		fsrHBox.setSpacing(10);
		fsrHBox.getChildren().add(fsrCB);
		fsrHBox.getChildren().add(fsrLabel);
		grid.add(fsrHBox, 0, 2);
		int fsrErrors = DatabaseSummary.getErrorCount(ValidationRuleSetEnum.FADGI_SR);
		grid.add(new Label(String.valueOf(fsrErrors)), 1, 2);
		fsrLabel.setAccessibleText(fsrErrors + " files with FADGI-SR violations.");
		fsrCB.setOnAction(event -> handleCheckboxEvent(event));

		frCB.setId("FADGI_R");
		final Label frLabel = new Label("Files with FADGI-R Violations"); // orange triangle
		frLabel.setLabelFor(frCB);
		final FontAwesomeIconView icon2 = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
		icon2.setStyleClass("fadgi-r-warning");
		icon2.setSize("16");
		frLabel.setGraphic(icon2);
		HBox frHBox = new HBox();
		frHBox.setSpacing(10);
		frHBox.getChildren().add(frCB);
		frHBox.getChildren().add(frLabel);
		grid.add(frHBox, 0, 3);
		int frErrors = DatabaseSummary.getErrorCount(ValidationRuleSetEnum.FADGI_R);
		grid.add(new Label(String.valueOf(frErrors)), 1, 3);
		frLabel.setAccessibleText(frErrors + " files with FADGI-R violations.");
		frCB.setOnAction(event -> handleCheckboxEvent(event));

		foCB.setId("FADGI_O");
		final Label foLabel = new Label("Files with FADGI-O Violations"); // yellow circle
		foLabel.setLabelFor(foCB);
		final MaterialDesignIconView icon3 = new MaterialDesignIconView(MaterialDesignIcon.ALERT_CIRCLE);
		icon3.setStyleClass("fadgi-o-warning");
		icon3.setSize("18");
		foLabel.setGraphic(icon3);
		HBox foHBox = new HBox();
		foHBox.setSpacing(10);
		foHBox.getChildren().add(foCB);
		foHBox.getChildren().add(foLabel);
		grid.add(foHBox, 0, 4);
		int foErrors = DatabaseSummary.getErrorCount(ValidationRuleSetEnum.FADGI_O);
		grid.add(new Label(String.valueOf(foErrors)), 1, 4);
		foLabel.setAccessibleText(foErrors + " files with FADGI-O violations.");
		foCB.setOnAction(event -> handleCheckboxEvent(event));
	}

	private void handleCheckboxEvent(ActionEvent event) {
		if (event.getSource() instanceof CheckBox) {
			final CheckBox cb = (CheckBox) event.getSource();
			final String id = cb.getId();
			handleCheckboxEventId(id);
		}
	}

	private void handleCheckboxEventId(String id) {
		if ("SMPTE_C".equals(id)) {
			ControllerMediatorDPX.getInstance().toggleRuleSet(ValidationRuleSetEnum.SMPTE_C);
		} else if ("FADGI_SR".equals(id)) {
			ControllerMediatorDPX.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_SR);
		} else if ("FADGI_R".equals(id)) {
			ControllerMediatorDPX.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_R);
		} else if ("FADGI_O".equals(id)) {
			ControllerMediatorDPX.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_O);
		}
	}
}
