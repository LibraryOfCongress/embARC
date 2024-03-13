package com.portalmedia.embarc.parser.dpx;

import java.util.HashMap;

public class DPXParseJsonChangesToApplyResult {
	private HashMap<DPXColumn, String> valid;
	private HashMap<String, String> invalid;

	public HashMap<DPXColumn, String> getValidPairs() {
		return valid;
	}

	public void setValidPairs(HashMap<DPXColumn, String> validPairs) {
		this.valid = validPairs;
	}

	public HashMap<String, String> getInvalidPairs() {
		return invalid;
	}

	public void setInvalidPairs(HashMap<String, String> invalidPairs) {
		this.invalid = invalidPairs;
	}

}
