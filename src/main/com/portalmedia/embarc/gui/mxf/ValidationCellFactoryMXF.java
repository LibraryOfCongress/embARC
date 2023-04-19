package com.portalmedia.embarc.gui.mxf;

import org.apache.commons.lang.StringUtils;

import com.portalmedia.embarc.gui.ValidationWarningIcons;
import com.portalmedia.embarc.gui.model.MXFFileInformationViewModel;
import com.portalmedia.embarc.gui.model.MXFMetadataColumnViewModel;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Sets and validates TableColumn cell data for MXF
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2023-02-23
 */
public class ValidationCellFactoryMXF extends TableCell<MXFFileInformationViewModel, String> {
	
	private TableColumn<MXFFileInformationViewModel, String> columnData;
	private MXFMetadataColumnViewModel mcvmData;
	
	public ValidationCellFactoryMXF(TableColumn<MXFFileInformationViewModel, String> column, MXFMetadataColumnViewModel mcvm) {
		columnData = column;
		mcvmData = mcvm;
	}
	
	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		setStyle(null);
		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			if (mcvmData != null) {
				@SuppressWarnings("unchecked")
				final TableRow<MXFFileInformationViewModel> row = getTableRow();
				if (row != null) {
					final MXFFileInformationViewModel fivm = row.getItem();
					String cellData = columnData.getCellData(fivm);
					if (mcvmData.getMXFColumn().isRequired() && StringUtils.isBlank(cellData)) {
						final ValidationWarningIcons icons = new ValidationWarningIcons();
						final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_OCTAGON);

						icon.setStyleClass("fadgi-sr-warning");
						icons.getChildren().add(icon);
						AnchorPane.setTopAnchor(icon, 2.00);

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
						return;
					}
				}
			}
			setText(item);
			setGraphic(null);
		}
	}

}
