package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.parser.dpx.DPXColumn;

public class StringContains implements ICustomValidationRule {
	private String expectedValue = null;
	private DPXColumn column = null;
	private Operator operator = Operator.CONTAINS;

	public StringContains(DPXColumn column, String expectedValue) {
		this.expectedValue = expectedValue;
		this.column = column;
	}
	
	@Override
	public boolean isValid(String actualValue) {
		return actualValue.contains(expectedValue);
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
