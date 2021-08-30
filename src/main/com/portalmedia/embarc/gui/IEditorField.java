package com.portalmedia.embarc.gui;

import java.util.Set;

import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

/**
 * Interface for editable metadata fields
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public interface IEditorField {

	void clearValidation();

	DPXColumn getColumn();

	String getValue();

	void resetValueChanged();

	void setColumn(DPXColumn column);

	void setEditable(boolean editable);

	void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet);

	void setLabel(String text);

	void setLabel(String text, String helpText);

	void setPopoutIcon();

	void setValue(String value);

	void setVisible(boolean b);

	boolean valueChanged();

	MXFColumn getMXFColumn();
}