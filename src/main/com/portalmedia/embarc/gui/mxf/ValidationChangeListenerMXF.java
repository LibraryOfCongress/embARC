package com.portalmedia.embarc.gui.mxf;

import org.apache.commons.lang.StringUtils;
import org.controlsfx.control.textfield.CustomTextField;

import com.portalmedia.embarc.gui.ValidationWarningIcons;
import com.portalmedia.embarc.parser.ColumnDef;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.AnchorPane;

/**
 * Sets, determines, and reports missing required fields
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2023-02-13
 */
public class ValidationChangeListenerMXF implements ChangeListener<String> {

	private CustomTextField textField;
	ColumnDef column;

	public ValidationChangeListenerMXF(CustomTextField textField, ColumnDef column) {
		this.textField = textField;
		this.column = column;
	}

	@Override
	public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
		setMissingRequiredField(newValue);
	}
	
	public void setMissingRequiredField(String value) {
		final ValidationWarningIcons icons = new ValidationWarningIcons();
		if (this.column.isRequired() && StringUtils.isBlank(value)) {
			final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_OCTAGON);
			icon.setStyleClass("fadgi-sr-warning");
			icons.getChildren().add(icon);
			AnchorPane.setTopAnchor(icon, 5.00);
			if (textField != null) {
				textField.setRight(icons);
			}
		} else {
			textField.setRight(icons);
		}
	}
}
