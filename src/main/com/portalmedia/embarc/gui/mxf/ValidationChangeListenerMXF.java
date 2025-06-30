package com.portalmedia.embarc.gui.mxf;

import org.apache.commons.lang.StringUtils;
import org.controlsfx.control.textfield.CustomTextField;

import com.portalmedia.embarc.gui.ValidationWarningHelper;
import com.portalmedia.embarc.parser.ColumnDef;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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
	HBox validationInfo;

	public ValidationChangeListenerMXF(CustomTextField textField, ColumnDef column, HBox validationInfo) {
		this.textField = textField;
		this.column = column;
		this.validationInfo = validationInfo;
	}

	@Override
	public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
		setMissingRequiredField(newValue);
	}
	
	public void setMissingRequiredField(String value) {
		if (this.column.isRequired() && StringUtils.isBlank(value)) {
			final MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ALERT_OCTAGON);		
			icon.setStyleClass("fadgi-sr-warning");			
			validationInfo.getChildren().add(icon);
			textField.setAccessibleText("Missing Required Value");
		} else {
			validationInfo = new HBox();
		}
	}
}
