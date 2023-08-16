package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.parser.dpx.DPXColumn;

public class CustomValidationRuleResult {
	private String expectedValue;
	private String actualValue;
	private Operator operator;
	private DPXColumn column;
	private boolean pass;
	public String getExpectedValue() {
		return expectedValue;
	}
	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}
	public String getActualValue() {
		return actualValue;
	}
	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	public DPXColumn getColumn() {
		return column;
	}
	public void setColumn(DPXColumn column) {
		this.column = column;
	}
	public boolean isPass() {
		return pass;
	}
	public void setPass(boolean pass) {
		this.pass = pass;
	}
}
