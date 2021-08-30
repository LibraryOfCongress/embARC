package com.portalmedia.embarc.gui;

import java.util.Set;

import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.scene.layout.AnchorPane;

/**
 * Small text input area for editable ASCII fields
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class LabelField extends AnchorPane implements IEditorField {
	
	boolean valueHasChanged = false;
	MXFColumn mxfColumn;
	String value;
	
	@Override
	public void clearValidation() {
		// TODO Auto-generated method stub
	}
	@Override
	public DPXColumn getColumn() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return value;
	}
	@Override
	public void resetValueChanged() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setColumn(DPXColumn column) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setLabel(String text) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setLabel(String text, String helpText) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setPopoutIcon() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setValue(String val) {
		// TODO Auto-generated method stub
		value = val;
	}
	@Override
	public boolean valueChanged() {
		// TODO Auto-generated method stub
		return valueHasChanged;
	}
	@Override
	public MXFColumn getMXFColumn() {
		// TODO Auto-generated method stub
		return mxfColumn;
	}

	public void setValueChanged() {
		valueHasChanged = true;
	}

	public void setMXFColumn(MXFColumn col) {
		this.mxfColumn = col;
	}
}
