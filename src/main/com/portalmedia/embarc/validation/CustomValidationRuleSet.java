package com.portalmedia.embarc.validation;

import java.util.ArrayList;
import java.util.List;

import com.portalmedia.embarc.parser.MetadataColumn;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;
import com.portalmedia.embarc.parser.dpx.DPXMetadata;

public class CustomValidationRuleSet {
	private List<ICustomValidationRule> rules = new ArrayList<ICustomValidationRule>();

	public void AddRule(ICustomValidationRule rule) {
		rules.add(rule);
	}

	public List<CustomValidationRuleResult> Validate(DPXFileInformation fileInfo) {
		List<CustomValidationRuleResult> results = new ArrayList<CustomValidationRuleResult>();
		DPXMetadata fileData = fileInfo.getFileData();
		for(ICustomValidationRule rule : rules) {
			CustomValidationRuleResult result = new CustomValidationRuleResult();
			MetadataColumn column = fileData.getColumn(rule.getColumn());
			result.setPass(rule.isValid(column.getCurrentValue()));
			result.setActualValue(column.getCurrentValue());
			result.setColumn(rule.getColumn());
			result.setExpectedValue(rule.getExpectedValue());
			result.setOperator(rule.getOperator());
			results.add(result);
		}
		return results;
	}
}
