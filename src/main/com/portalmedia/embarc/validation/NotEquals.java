package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.parser.dpx.DPXColumn;

public class NotEquals implements ICustomValidationRule {
	private String expectedValue = null;
	private DPXColumn column = null;
	private Operator operator = Operator.NOT_EQUALS;
	
	public NotEquals(DPXColumn column, String expectedValue) {
		this.expectedValue = expectedValue;
		this.column = column;
	}
	
	@Override
	public boolean isValid(String actualValue) {
		if (column.getType() == int.class) {
			Integer i = new Integer(actualValue);
			Integer expectedInt = new Integer(expectedValue);
			return i != expectedInt;
		}

		return !actualValue.equals(expectedValue);
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
