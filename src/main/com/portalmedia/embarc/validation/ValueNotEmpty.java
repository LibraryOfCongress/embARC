package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.parser.dpx.DPXColumn;

public class ValueNotEmpty implements ICustomValidationRule {
	private String expectedValue = null;
	private DPXColumn column = null;
	private Operator operator = Operator.NOT_EMPTY;

	public ValueNotEmpty(DPXColumn column, String expectedValue) {
		this.expectedValue = expectedValue;
		this.column = column;
	}
	
	@Override
	public boolean isValid(String actualValue) {
		if (actualValue == null) return false;
		if (actualValue.isEmpty()) return false;
		// TODO: any more checks? Zeros?
		return true;
	}
	
	@Override
	public DPXColumn getColumn() {
		return column;
	}
	
	@Override
	public Operator getOperator() {
		return operator;
	}
	
	@Override
	public String getExpectedValue() {
		return expectedValue;
	}

}
