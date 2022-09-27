package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.parser.dpx.DPXColumn;

public class IntMaxValue implements ICustomValidationRule {
	private int expectedValue = 0;
	private DPXColumn column = null;
	private Operator operator = Operator.MAX;
	
	public IntMaxValue(DPXColumn column, int expectedValue) {
		this.expectedValue = expectedValue;
		this.column = column;
	}
	
	@Override
	public boolean isValid(String actualValue) {
		Integer i = new Integer(actualValue);
		return i <= expectedValue;
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
		return Integer.toString(expectedValue);
	}

}
