package com.portalmedia.embarc.gui;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;

import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

/**
 * Date Picker component for date fields
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class DatePickerField extends AnchorPane implements IEditorField {
	@FXML
	private DatePicker datePicker;
	@FXML
	private Label editorTextFieldLabel;

	private DPXColumn column;
	private LocalDate originalValue;
	private MXFColumn mxfColumn;

	public DatePickerField() {
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DatePickerField.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#clearValidation()
	 */
	@Override
	public void clearValidation() {
		// TODO: EditorTextField.setRight(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#getColumn()
	 */
	@Override
	public DPXColumn getColumn() {
		return column;
	}

	@Override
	public String getValue() {
		return datePicker.getValue().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#valueChanged()
	 */
	@Override
	public void resetValueChanged() {
		originalValue = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.portalmedia.embarc.gui.IEditorField#setColumn(DPXColumn)
	 */
	@Override
	public void setColumn(DPXColumn column) {

		this.column = column;
		final StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(column.getDateFormat());
			SimpleDateFormat format = new SimpleDateFormat(column.getDateFormat());

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					final Date d = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
					return format.format(d);
				} else {
					return "";
				}
			}
		};
		datePicker.setConverter(converter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#setEditable(boolean)
	 */
	@Override
	public void setEditable(boolean editable) {
		datePicker.setEditable(editable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.portalmedia.embarc.gui.IEditorField#setInvalidRuleSets(java.util.Set)
	 */
	@Override
	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet) {
		final ValidationWarningIcons icons = new ValidationWarningIcons();
		icons.setInvalidRuleSets(invalidRuleSet);
		AnchorPane.setBottomAnchor(icons, 0.0);
		AnchorPane.setTopAnchor(icons, 0.0);
		// TODO: EditorTextField.setRight(icons);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String text) {
		editorTextFieldLabel.setText(text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String labelText, String helpText) {
		final Tooltip tt = new Tooltip(labelText + "\n\n" + helpText);
		tt.setStyle("-fx-text-fill: white; -fx-font-size: 12px");
		tt.setPrefWidth(500);
		tt.setWrapText(true);
		tt.setAutoHide(false);
		editorTextFieldLabel.setText(labelText);
		editorTextFieldLabel.setTooltip(tt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#setPopoutIcon()
	 */
	@Override
	public void setPopoutIcon() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		if (value.isEmpty() || "{multiple values}".equals(value)) {
			return;
		}
		Date input;
		try {
			input = new SimpleDateFormat(column.getDateFormat()).parse(value);
			final Instant instant = input.toInstant();
			final ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
			final LocalDate l = zdt.toLocalDate();
			if (originalValue == null) {
				originalValue = l;
			}
			datePicker.setValue(l);
		} catch (final ParseException e) {
			System.out.println("Parse Error");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.portalmedia.embarc.gui.IEditorField#valueChanged()
	 */
	@Override
	public boolean valueChanged() {
		if (originalValue == null) {
			return false;
		}
		if (originalValue.equals(datePicker.getValue())) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.portalmedia.embarc.gui.IEditorField#setMXFColumn(MXFColumn)
	 */
	public void setMXFColumn(MXFColumn col) {
		this.mxfColumn = col;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#getMXFColumn()
	 */
	public MXFColumn getMXFColumn() {
		return mxfColumn;
	}
}
