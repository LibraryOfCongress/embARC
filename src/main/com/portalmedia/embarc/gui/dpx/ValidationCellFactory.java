package com.portalmedia.embarc.gui.dpx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.portalmedia.embarc.gui.ValidationWarningIcons;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.gui.model.DPXMetadataColumnViewModel;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Sets and validates TableColumn cell data
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ValidationCellFactory extends TableCell<DPXFileInformationViewModel, String> {
	
	private TableColumn<DPXFileInformationViewModel, String> columnData;
	
	public ValidationCellFactory(TableColumn<DPXFileInformationViewModel, String> column) {
		columnData = column;
	}
	
	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		setStyle(null);
		if (empty || item == null) {
			setGraphic(null);
			setText(null);
		} else {
			final DPXMetadataColumnViewModel mdvm = (DPXMetadataColumnViewModel) columnData.getUserData();
			if (mdvm != null) {
				final HashSet<ValidationRuleSetEnum> brokenRules = new HashSet<>();

				@SuppressWarnings("unchecked")
				final TableRow<DPXFileInformationViewModel> row = getTableRow();
				if (row != null) {
					final DPXFileInformationViewModel fivm = row.getItem();
					if (fivm != null) {
						final HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleViolations = fivm
								.getInvalidRuleSets(mdvm.getColumn());
						if (ruleViolations != null) {
							for (final ValidationRuleSetEnum rule : ControllerMediatorDPX
									.getInstance().getCurrentRuleSets()) {
								if (ruleViolations.containsKey(rule)) {
									brokenRules.add(rule);
								}
							}
						}
					}
				}
				if (brokenRules.size() > 0) {
					final ValidationWarningIcons icons = new ValidationWarningIcons();
					icons.setInvalidRuleSets(brokenRules, mdvm.getColumn());

					final VBox iconsBox = new VBox(icons);
					iconsBox.setAlignment(Pos.CENTER_RIGHT);

					final Label valueLabel = new Label();
					valueLabel.setText(item);

					final VBox valueLabelBox = new VBox(valueLabel);
					valueLabelBox.setAlignment(Pos.CENTER_LEFT);
					valueLabelBox.setFillWidth(true);

					final HBox box = new HBox(valueLabelBox, iconsBox);
					HBox.setHgrow(iconsBox, Priority.SOMETIMES);
					HBox.setHgrow(valueLabelBox, Priority.ALWAYS);

					setText(null);
					setGraphic(box);
				} else {
					setGraphic(null);
					setText(item);
				}
			} else {
				setText(item);
				setGraphic(null);
			}
		}
	}
	
}
