package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.parser.dpx.DPXColumn;

public interface ICustomValidationRule {

	boolean isValid(String actualValue);
	DPXColumn getColumn();
	String getExpectedValue();
	Operator getOperator();

}