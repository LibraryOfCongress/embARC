package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.parser.dpx.DPXColumn;

public class StringEndsWith implements ICustomValidationRule {
	private String expectedValue = null;
	private DPXColumn column = null;
	private Operator operator = Operator.ENDS_WITH;

	public StringEndsWith(DPXColumn column, String expectedValue) {
		this.expectedValue = expectedValue;
		this.column = column;
	}
	
	@Override
	public boolean isValid(String actualValue) {
		return actualValue.endsWith(expectedValue);
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
