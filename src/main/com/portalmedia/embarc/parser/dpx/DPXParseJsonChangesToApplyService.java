package com.portalmedia.embarc.parser.dpx;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.portalmedia.embarc.validation.CustomValidationRuleService;

/**
 * Validates JSON input for DPX processing
 *
 * @author PortalMedia
 * @since 2023-08-30
 **/
public class DPXParseJsonChangesToApplyService {
	/*
	 * Expected input is a JSON object with pairs of field: value. Ex:
	 * 
	 * {
	 * 		"PROJECT_NAME": "Example Project Name",
	 * 		"COPYRIGHT_STATEMENT": "Example copyright statement"
	 * }
	 */
	public static DPXParseJsonChangesToApplyResult getApplyChangesJsonResult(String input) {
		JSONObject object = null;
		try (FileReader fr = new FileReader(input)) {
			JSONTokener tokener = new JSONTokener(fr);
			object = new JSONObject(tokener);
		} catch (FileNotFoundException e) {
			System.out.println("File not found: input JSON");
		} catch (IOException e1) {
			System.out.println("IO Exception");
		}

		if (object == null) {
			return null;
		}
		
		DPXParseJsonChangesToApplyResult result = new DPXParseJsonChangesToApplyResult();

		Iterator<String> keys = object.keys();
		HashMap<DPXColumn, String> validPairs = new HashMap<DPXColumn, String>();
		HashMap<String, String> invalidPairs = new HashMap<String, String>();

		while (keys.hasNext()) {
		    String key = keys.next();
		    String value = object.getString(key);
		    DPXColumn col = CustomValidationRuleService.getDPXColumnFromString(key);

		    if (col == null) {
		    	invalidPairs.put(key, "not a valid DPX field");
		    	continue;
		    }

		    if (!col.getEditable()) {
		    	invalidPairs.put(key, "not an editable DPX field");
		    	continue;
		    }

		    String valueToApplyError = getValueToApplyError(col, value);

		    if (valueToApplyError != "") {
		    	invalidPairs.put(key, valueToApplyError);
		    	continue;
		    }

		    validPairs.put(col, value);
		}

		result.setInvalidPairs(invalidPairs);
		result.setValidPairs(validPairs);

		return result;
	}
	
	private static String getValueToApplyError(DPXColumn col, String value) {
		if (value == null) {
			return "value is null";
		}
		if (value.length() > col.getLength()) {
			return "value length exceeds limit";
		}
		return "";
	}
	
}
