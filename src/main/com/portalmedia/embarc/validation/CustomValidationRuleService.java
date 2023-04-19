package com.portalmedia.embarc.validation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;

public class CustomValidationRuleService {
	static String[] headers = null;

	public static void readRuleSet(String input, String output, TreeMap<String, DPXFileInformation> dpxFileList) {
		JSONObject object = null;
		try (FileReader fr = new FileReader(input)){
			JSONTokener tokener = new JSONTokener(fr);
			object = new JSONObject(tokener);
		} catch (FileNotFoundException e) {
			System.out.println("File not found: Rule set");
		} catch (IOException e1) {
			System.out.println("IO Exception");
		}

		if (object == null) return;

		Object jsonList = object.get("Rules");
		JSONArray jsonArray = (JSONArray)jsonList;
    	CustomValidationRuleSet validation = new CustomValidationRuleSet();

		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject rule = (JSONObject)jsonArray.get(i);
				CustomValidationRuleDefinition ruleDefinition = new CustomValidationRuleDefinition();
				ruleDefinition.setColumn(rule.getString("Column"));
				ruleDefinition.setOperator(rule.getString("Operator"));
				ruleDefinition.setValue(rule.getString("Value"));
				validation.AddRule(getCustomValidationRule(ruleDefinition));
			}
		}

		// Print validation results summary
		System.out.println("-- CONFORMANCE SUMMARY --\n");

		int fileCount = 0;
		List<String> failedFileNames = new ArrayList<String>();
		if (output.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter
		            .ofPattern("yyyy-MM-dd")
		            .withLocale(Locale.getDefault())
		            .withZone(ZoneId.systemDefault());
	        Instant now = Instant.now();
	        String formatted = formatter.format(now);
			output = FilenameUtils.getFullPath(input) + "embarc_cli_conformance_report_" + formatted + ".csv";
		}
		try {
			try(ICsvMapWriter csvWriter = new CsvMapWriter(new FileWriter(output), CsvPreference.STANDARD_PREFERENCE)){
				csvWriter.writeHeader(getHeader());
	
				for (DPXFileInformation fileInfo : dpxFileList.values()) {
					fileCount += 1;
					List<CustomValidationRuleResult> validationResults = validation.Validate(fileInfo);
		        	fileInfo.setValidationResults(validationResults);
		        	final Map<String, Object> result = new HashMap<String, Object>();
					for (CustomValidationRuleResult ruleResult : fileInfo.getValidationResults()) {
			        	if (!ruleResult.isPass()) {
			        		if (!failedFileNames.contains(fileInfo.getName())) {
			        			failedFileNames.add(fileInfo.getName());
			        		}
			        		result.put("Result", "Fail");
			        	} else {
			        		result.put("Result", "Pass");
			        	}
		        		result.put("Filename", fileInfo.getName());
		        		result.put("Field", ruleResult.getColumn());
		        		result.put("Operator", ruleResult.getOperator().name());
		        		result.put("Expected Value", ruleResult.getExpectedValue());
		        		result.put("Actual Value", ruleResult.getActualValue());
		        		csvWriter.write(result, getHeader());
					}
				}
	
				csvWriter.close();
			}
			String numFilesLine = "Number of Files Tested: " + fileCount;
			String numFailsLine = "Number of Failed Files: " + failedFileNames.size();

			System.out.println(numFilesLine);
			System.out.println(numFailsLine);
			System.out.println("\n-- Failed Files --\n");

			for (String file : failedFileNames) {
				System.out.println(file);
			}

			Path outputPath = Paths.get(output);
			System.out.println("\nConformance report location: " + outputPath.toAbsolutePath());
		} catch (IOException e) {
			System.out.println("IO Exception in conformance report");
		}
	}

	private static String[] getHeader() {
		if (headers == null) setHeader();
    	return headers;
	}

	private static void setHeader() {
		List<String> headerList = new ArrayList<String>();
		headerList.add("Filename");
		headerList.add("Field");
		headerList.add("Operator");
		headerList.add("Expected Value");
		headerList.add("Actual Value");
		headerList.add("Result");
		headers = headerList.toArray(new String[0]);
	}

	private static ICustomValidationRule getCustomValidationRule(CustomValidationRuleDefinition definition) {
		ICustomValidationRule rule = null;
		DPXColumn dpxColumn = getDPXColumnFromString(definition.getColumn());
		String value = definition.getValue();

		switch(definition.getOperator()) {
			case "MIN":
				rule = new IntMinValue(dpxColumn, Integer.parseInt(value));
				break;
			case "MAX":
				rule = new IntMaxValue(dpxColumn, Integer.parseInt(value));
				break;
			case "EQUALS":
				rule = new Equals(dpxColumn, value);
				break;
			case "CONTAINS":
				rule = new StringContains(dpxColumn, value);
				break;
			case "NOT_EQUALS":
				rule = new NotEquals(dpxColumn, value);
				break;
			case "STARTS_WITH":
				rule = new StringStartsWith(dpxColumn, value);
				break;
			case "ENDS_WITH":
				rule = new StringEndsWith(dpxColumn, value);
				break;
			case "NOT_EMPTY":
				rule = new ValueNotEmpty(dpxColumn, value);
				break;
		}
		return rule;
	}

	private static DPXColumn getDPXColumnFromString(String name) {
		DPXColumn col = null;

		switch(name) {
			case "MAGIC_NUMBER":
				col = DPXColumn.MAGIC_NUMBER;
				break;
			case "OFFSET_TO_IMAGE_DATA":
				col = DPXColumn.OFFSET_TO_IMAGE_DATA;
				break;
			case "VERSION_NUMBER_OF_HEADER_FORMAT":
				col = DPXColumn.VERSION_NUMBER_OF_HEADER_FORMAT;
				break;
			case "TOTAL_IMAGE_FILE_SIZE":
				col = DPXColumn.TOTAL_IMAGE_FILE_SIZE;
				break;
			case "DITTO_KEY":
				col = DPXColumn.DITTO_KEY;
				break;
			case "GENERIC_SECTION_HEADER_LENGTH":
				col = DPXColumn.GENERIC_SECTION_HEADER_LENGTH;
				break;
			case "INDUSTRY_SPECIFIC_HEADER_LENGTH":
				col = DPXColumn.INDUSTRY_SPECIFIC_HEADER_LENGTH;
				break;
			case "USER_DEFINED_HEADER_LENGTH":
				col = DPXColumn.USER_DEFINED_HEADER_LENGTH;
				break;
			case "IMAGE_FILE_NAME":
				col = DPXColumn.IMAGE_FILE_NAME;
				break;
			case "CREATION_DATETIME":
				col = DPXColumn.CREATION_DATETIME;
				break;
			case "CREATOR":
				col = DPXColumn.CREATOR;
				break;
			case "PROJECT_NAME":
				col = DPXColumn.PROJECT_NAME;
				break;
			case "COPYRIGHT_STATEMENT":
				col = DPXColumn.COPYRIGHT_STATEMENT;
				break;
			case "ENCRYPTION_KEY":
				col = DPXColumn.ENCRYPTION_KEY;
				break;
			case "RESERVED_FILE_INFORMATION_HEADER":
				col = DPXColumn.RESERVED_FILE_INFORMATION_HEADER;
				break;
			case "IMAGE_ORIENTATION":
				col = DPXColumn.IMAGE_ORIENTATION;
				break;
			case "NUMBER_OF_IMAGE_ELEMENTS":
				col = DPXColumn.NUMBER_OF_IMAGE_ELEMENTS;
				break;
			case "PIXELS_PER_LINE":
				col = DPXColumn.PIXELS_PER_LINE;
				break;
			case "LINES_PER_IMAGE_ELEMENT":
				col = DPXColumn.LINES_PER_IMAGE_ELEMENT;
				break;
			case "DATA_SIGN_1":
				col = DPXColumn.DATA_SIGN_1;
				break;
			case "REFERENCE_LOW_DATA_CODE_1":
				col = DPXColumn.REFERENCE_LOW_DATA_CODE_1;
				break;
			case "REFERENCE_LOW_QUANTITY_1":
				col = DPXColumn.REFERENCE_LOW_QUANTITY_1;
				break;
			case "REFERENCE_HIGH_DATA_CODE_1":
				col = DPXColumn.REFERENCE_HIGH_DATA_CODE_1;
				break;
			case "REFERENCE_HIGH_QUANTITY_1":
				col = DPXColumn.REFERENCE_HIGH_QUANTITY_1;
				break;
			case "DESCRIPTOR_1":
				col = DPXColumn.DESCRIPTOR_1;
				break;
			case "TRANSFER_CHARACTERISTIC_1":
				col = DPXColumn.TRANSFER_CHARACTERISTIC_1;
				break;
			case "COLORIMETRIC_SPECIFICATION_1":
				col = DPXColumn.COLORIMETRIC_SPECIFICATION_1;
				break;
			case "BIT_DEPTH_1":
				col = DPXColumn.BIT_DEPTH_1;
				break;
			case "PACKING_1":
				col = DPXColumn.PACKING_1;
				break;
			case "ENCODING_1":
				col = DPXColumn.ENCODING_1;
				break;
			case "OFFSET_TO_DATA_1":
				col = DPXColumn.OFFSET_TO_DATA_1;
				break;
			case "END_OF_LINE_PADDING_1":
				col = DPXColumn.END_OF_LINE_PADDING_1;
				break;
			case "END_OF_IMAGE_PADDING_1":
				col = DPXColumn.END_OF_IMAGE_PADDING_1;
				break;
			case "DESCRIPTION_OF_IMAGE_ELEMENT_1":
				col = DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_1;
				break;
			case "DATA_SIGN_2":
				col = DPXColumn.DATA_SIGN_2;
				break;
			case "REFERENCE_LOW_DATA_CODE_2":
				col = DPXColumn.REFERENCE_LOW_DATA_CODE_2;
				break;
			case "REFERENCE_LOW_QUANTITY_2":
				col = DPXColumn.REFERENCE_LOW_QUANTITY_2;
				break;
			case "REFERENCE_HIGH_DATA_CODE_2":
				col = DPXColumn.REFERENCE_HIGH_DATA_CODE_2;
				break;
			case "REFERENCE_HIGH_QUANTITY_2":
				col = DPXColumn.REFERENCE_HIGH_QUANTITY_2;
				break;
			case "DESCRIPTOR_2":
				col = DPXColumn.DESCRIPTOR_2;
				break;
			case "TRANSFER_CHARACTERISTIC_2":
				col = DPXColumn.TRANSFER_CHARACTERISTIC_2;
				break;
			case "COLORIMETRIC_SPECIFICATION_2":
				col = DPXColumn.COLORIMETRIC_SPECIFICATION_2;
				break;
			case "BIT_DEPTH_2":
				col = DPXColumn.BIT_DEPTH_2;
				break;
			case "PACKING_2":
				col = DPXColumn.PACKING_2;
				break;
			case "ENCODING_2":
				col = DPXColumn.ENCODING_2;
				break;
			case "OFFSET_TO_DATA_2":
				col = DPXColumn.OFFSET_TO_DATA_2;
				break;
			case "END_OF_LINE_PADDING_2":
				col = DPXColumn.END_OF_LINE_PADDING_2;
				break;
			case "END_OF_IMAGE_PADDING_2":
				col = DPXColumn.END_OF_IMAGE_PADDING_2;
				break;
			case "DESCRIPTION_OF_IMAGE_ELEMENT_2":
				col = DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_2;
				break;
			case "DATA_SIGN_3":
				col = DPXColumn.DATA_SIGN_3;
				break;
			case "REFERENCE_LOW_DATA_CODE_3":
				col = DPXColumn.REFERENCE_LOW_DATA_CODE_3;
				break;
			case "REFERENCE_LOW_QUANTITY_3":
				col = DPXColumn.REFERENCE_LOW_QUANTITY_3;
				break;
			case "REFERENCE_HIGH_DATA_CODE_3":
				col = DPXColumn.REFERENCE_HIGH_DATA_CODE_3;
				break;
			case "REFERENCE_HIGH_QUANTITY_3":
				col = DPXColumn.REFERENCE_HIGH_QUANTITY_3;
				break;
			case "DESCRIPTOR_3":
				col = DPXColumn.DESCRIPTOR_3;
				break;
			case "TRANSFER_CHARACTERISTIC_3":
				col = DPXColumn.TRANSFER_CHARACTERISTIC_3;
				break;
			case "COLORIMETRIC_SPECIFICATION_3":
				col = DPXColumn.COLORIMETRIC_SPECIFICATION_3;
				break;
			case "BIT_DEPTH_3":
				col = DPXColumn.BIT_DEPTH_3;
				break;
			case "PACKING_3":
				col = DPXColumn.PACKING_3;
				break;
			case "ENCODING_3":
				col = DPXColumn.ENCODING_3;
				break;
			case "OFFSET_TO_DATA_3":
				col = DPXColumn.OFFSET_TO_DATA_3;
				break;
			case "END_OF_LINE_PADDING_3":
				col = DPXColumn.END_OF_LINE_PADDING_3;
				break;
			case "END_OF_IMAGE_PADDING_3":
				col = DPXColumn.END_OF_IMAGE_PADDING_3;
				break;
			case "DESCRIPTION_OF_IMAGE_ELEMENT_3":
				col = DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_3;
				break;
			case "DATA_SIGN_4":
				col = DPXColumn.DATA_SIGN_4;
				break;
			case "REFERENCE_LOW_DATA_CODE_4":
				col = DPXColumn.REFERENCE_LOW_DATA_CODE_4;
				break;
			case "REFERENCE_LOW_QUANTITY_4":
				col = DPXColumn.REFERENCE_LOW_QUANTITY_4;
				break;
			case "REFERENCE_HIGH_DATA_CODE_4":
				col = DPXColumn.REFERENCE_HIGH_DATA_CODE_4;
				break;
			case "REFERENCE_HIGH_QUANTITY_4":
				col = DPXColumn.REFERENCE_HIGH_QUANTITY_4;
				break;
			case "DESCRIPTOR_4":
				col = DPXColumn.DESCRIPTOR_4;
				break;
			case "TRANSFER_CHARACTERISTIC_4":
				col = DPXColumn.TRANSFER_CHARACTERISTIC_4;
				break;
			case "COLORIMETRIC_SPECIFICATION_4":
				col = DPXColumn.COLORIMETRIC_SPECIFICATION_4;
				break;
			case "BIT_DEPTH_4":
				col = DPXColumn.BIT_DEPTH_4;
				break;
			case "PACKING_4":
				col = DPXColumn.PACKING_4;
				break;
			case "ENCODING_4":
				col = DPXColumn.ENCODING_4;
				break;
			case "OFFSET_TO_DATA_4":
				col = DPXColumn.OFFSET_TO_DATA_4;
				break;
			case "END_OF_LINE_PADDING_4":
				col = DPXColumn.END_OF_LINE_PADDING_4;
				break;
			case "END_OF_IMAGE_PADDING_4":
				col = DPXColumn.END_OF_IMAGE_PADDING_4;
				break;
			case "DESCRIPTION_OF_IMAGE_ELEMENT_4":
				col = DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_4;
				break;
			case "DATA_SIGN_5":
				col = DPXColumn.DATA_SIGN_5;
				break;
			case "REFERENCE_LOW_DATA_CODE_5":
				col = DPXColumn.REFERENCE_LOW_DATA_CODE_5;
				break;
			case "REFERENCE_LOW_QUANTITY_5":
				col = DPXColumn.REFERENCE_LOW_QUANTITY_5;
				break;
			case "REFERENCE_HIGH_DATA_CODE_5":
				col = DPXColumn.REFERENCE_HIGH_DATA_CODE_5;
				break;
			case "REFERENCE_HIGH_QUANTITY_5":
				col = DPXColumn.REFERENCE_HIGH_QUANTITY_5;
				break;
			case "DESCRIPTOR_5":
				col = DPXColumn.DESCRIPTOR_5;
				break;
			case "TRANSFER_CHARACTERISTIC_5":
				col = DPXColumn.TRANSFER_CHARACTERISTIC_5;
				break;
			case "COLORIMETRIC_SPECIFICATION_5":
				col = DPXColumn.COLORIMETRIC_SPECIFICATION_5;
				break;
			case "BIT_DEPTH_5":
				col = DPXColumn.BIT_DEPTH_5;
				break;
			case "PACKING_5":
				col = DPXColumn.PACKING_5;
				break;
			case "ENCODING_5":
				col = DPXColumn.ENCODING_5;
				break;
			case "OFFSET_TO_DATA_5":
				col = DPXColumn.OFFSET_TO_DATA_5;
				break;
			case "END_OF_LINE_PADDING_5":
				col = DPXColumn.END_OF_LINE_PADDING_5;
				break;
			case "END_OF_IMAGE_PADDING_5":
				col = DPXColumn.END_OF_IMAGE_PADDING_5;
				break;
			case "DESCRIPTION_OF_IMAGE_ELEMENT_5":
				col = DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_5;
				break;
			case "DATA_SIGN_6":
				col = DPXColumn.DATA_SIGN_6;
				break;
			case "REFERENCE_LOW_DATA_CODE_6":
				col = DPXColumn.REFERENCE_LOW_DATA_CODE_6;
				break;
			case "REFERENCE_LOW_QUANTITY_6":
				col = DPXColumn.REFERENCE_LOW_QUANTITY_6;
				break;
			case "REFERENCE_HIGH_DATA_CODE_6":
				col = DPXColumn.REFERENCE_HIGH_DATA_CODE_6;
				break;
			case "REFERENCE_HIGH_QUANTITY_6":
				col = DPXColumn.REFERENCE_HIGH_QUANTITY_6;
				break;
			case "DESCRIPTOR_6":
				col = DPXColumn.DESCRIPTOR_6;
				break;
			case "TRANSFER_CHARACTERISTIC_6":
				col = DPXColumn.TRANSFER_CHARACTERISTIC_6;
				break;
			case "COLORIMETRIC_SPECIFICATION_6":
				col = DPXColumn.COLORIMETRIC_SPECIFICATION_6;
				break;
			case "BIT_DEPTH_6":
				col = DPXColumn.BIT_DEPTH_6;
				break;
			case "PACKING_6":
				col = DPXColumn.PACKING_6;
				break;
			case "ENCODING_6":
				col = DPXColumn.ENCODING_6;
				break;
			case "OFFSET_TO_DATA_6":
				col = DPXColumn.OFFSET_TO_DATA_6;
				break;
			case "END_OF_LINE_PADDING_6":
				col = DPXColumn.END_OF_LINE_PADDING_6;
				break;
			case "END_OF_IMAGE_PADDING_6":
				col = DPXColumn.END_OF_IMAGE_PADDING_6;
				break;
			case "DESCRIPTION_OF_IMAGE_ELEMENT_6":
				col = DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_6;
				break;
			case "DATA_SIGN_7":
				col = DPXColumn.DATA_SIGN_7;
				break;
			case "REFERENCE_LOW_DATA_CODE_7":
				col = DPXColumn.REFERENCE_LOW_DATA_CODE_7;
				break;
			case "REFERENCE_LOW_QUANTITY_7":
				col = DPXColumn.REFERENCE_LOW_QUANTITY_7;
				break;
			case "REFERENCE_HIGH_DATA_CODE_7":
				col = DPXColumn.REFERENCE_HIGH_DATA_CODE_7;
				break;
			case "REFERENCE_HIGH_QUANTITY_7":
				col = DPXColumn.REFERENCE_HIGH_QUANTITY_7;
				break;
			case "DESCRIPTOR_7":
				col = DPXColumn.DESCRIPTOR_7;
				break;
			case "TRANSFER_CHARACTERISTIC_7":
				col = DPXColumn.TRANSFER_CHARACTERISTIC_7;
				break;
			case "COLORIMETRIC_SPECIFICATION_7":
				col = DPXColumn.COLORIMETRIC_SPECIFICATION_7;
				break;
			case "BIT_DEPTH_7":
				col = DPXColumn.BIT_DEPTH_7;
				break;
			case "PACKING_7":
				col = DPXColumn.PACKING_7;
				break;
			case "ENCODING_7":
				col = DPXColumn.ENCODING_7;
				break;
			case "OFFSET_TO_DATA_7":
				col = DPXColumn.OFFSET_TO_DATA_7;
				break;
			case "END_OF_LINE_PADDING_7":
				col = DPXColumn.END_OF_LINE_PADDING_7;
				break;
			case "END_OF_IMAGE_PADDING_7":
				col = DPXColumn.END_OF_IMAGE_PADDING_7;
				break;
			case "DESCRIPTION_OF_IMAGE_ELEMENT_7":
				col = DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_7;
				break;
			case "DATA_SIGN_8":
				col = DPXColumn.DATA_SIGN_8;
				break;
			case "REFERENCE_LOW_DATA_CODE_8":
				col = DPXColumn.REFERENCE_LOW_DATA_CODE_8;
				break;
			case "REFERENCE_LOW_QUANTITY_8":
				col = DPXColumn.REFERENCE_LOW_QUANTITY_8;
				break;
			case "REFERENCE_HIGH_DATA_CODE_8":
				col = DPXColumn.REFERENCE_HIGH_DATA_CODE_8;
				break;
			case "REFERENCE_HIGH_QUANTITY_8":
				col = DPXColumn.REFERENCE_HIGH_QUANTITY_8;
				break;
			case "DESCRIPTOR_8":
				col = DPXColumn.DESCRIPTOR_8;
				break;
			case "TRANSFER_CHARACTERISTIC_8":
				col = DPXColumn.TRANSFER_CHARACTERISTIC_8;
				break;
			case "COLORIMETRIC_SPECIFICATION_8":
				col = DPXColumn.COLORIMETRIC_SPECIFICATION_8;
				break;
			case "BIT_DEPTH_8":
				col = DPXColumn.BIT_DEPTH_8;
				break;
			case "PACKING_8":
				col = DPXColumn.PACKING_8;
				break;
			case "ENCODING_8":
				col = DPXColumn.ENCODING_8;
				break;
			case "OFFSET_TO_DATA_8":
				col = DPXColumn.OFFSET_TO_DATA_8;
				break;
			case "END_OF_LINE_PADDING_8":
				col = DPXColumn.END_OF_LINE_PADDING_8;
				break;
			case "END_OF_IMAGE_PADDING_8":
				col = DPXColumn.END_OF_IMAGE_PADDING_8;
				break;
			case "DESCRIPTION_OF_IMAGE_ELEMENT_8":
				col = DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_8;
				break;
			case "RESERVED_IMAGE_INFORMATION_HEADER":
				col = DPXColumn.RESERVED_IMAGE_INFORMATION_HEADER;
				break;
			case "X_OFFSET":
				col = DPXColumn.X_OFFSET;
				break;
			case "Y_OFFSET":
				col = DPXColumn.Y_OFFSET;
				break;
			case "Y_CENTER":
				col = DPXColumn.Y_CENTER;
				break;
			case "X_ORIGINAL_SIZE":
				col = DPXColumn.X_ORIGINAL_SIZE;
				break;
			case "Y_ORIGINAL_SIZE":
				col = DPXColumn.Y_ORIGINAL_SIZE;
				break;
			case "SOURCE_IMAGE_FILENAME":
				col = DPXColumn.SOURCE_IMAGE_FILENAME;
				break;
			case "SOURCE_IMAGE_DATETIME":
				col = DPXColumn.SOURCE_IMAGE_DATETIME;
				break;
			case "INPUT_DEVICE_NAME":
				col = DPXColumn.INPUT_DEVICE_NAME;
				break;
			case "INPUT_DEVICE_SERIAL_NUMBER":
				col = DPXColumn.INPUT_DEVICE_SERIAL_NUMBER;
				break;
			case "BORDER_VALIDITY":
				col = DPXColumn.BORDER_VALIDITY;
				break;
			case "PIXEL_ASPECT_RATIO":
				col = DPXColumn.PIXEL_ASPECT_RATIO;
				break;
			case "X_SCANNED_SIZE":
				col = DPXColumn.X_SCANNED_SIZE;
				break;
			case "Y_SCANNED_SIZE":
				col = DPXColumn.Y_SCANNED_SIZE;
				break;
			case "RESERVED_IMAGE_SOURCE_INFORMATION_HEADER":
				col = DPXColumn.RESERVED_IMAGE_SOURCE_INFORMATION_HEADER;
				break;
			case "FILM_MFG_ID_CODE":
				col = DPXColumn.FILM_MFG_ID_CODE;
				break;
			case "FILM_TYPE":
				col = DPXColumn.FILM_TYPE;
				break;
			case "OFFSET_IN_PERFS":
				col = DPXColumn.OFFSET_IN_PERFS;
				break;
			case "PREFIX":
				col = DPXColumn.PREFIX;
				break;
			case "COUNT":
				col = DPXColumn.COUNT;
				break;
			case "FORMAT":
				col = DPXColumn.FORMAT;
				break;
			case "FRAME_POSITION_IN_SEQUENCE":
				col = DPXColumn.FRAME_POSITION_IN_SEQUENCE;
				break;
			case "SEQUENCE_LENGTH":
				col = DPXColumn.SEQUENCE_LENGTH;
				break;
			case "HELD_COUNT":
				col = DPXColumn.HELD_COUNT;
				break;
			case "FRAME_RATE_OF_ORIGINAL":
				col = DPXColumn.FRAME_RATE_OF_ORIGINAL;
				break;
			case "SHUTTER_ANGLE_OF_CAMERA":
				col = DPXColumn.SHUTTER_ANGLE_OF_CAMERA;
				break;
			case "FRAME_IDENTIFICATION":
				col = DPXColumn.FRAME_IDENTIFICATION;
				break;
			case "SLATE_IDENTIFICATION":
				col = DPXColumn.SLATE_IDENTIFICATION;
				break;
			case "RESERVED_MOTION_PICTURE_FILM_INFORMATION_HEADER":
				col = DPXColumn.RESERVED_MOTION_PICTURE_FILM_INFORMATION_HEADER;
				break;
			case "SMTPE_TIMECODE":
				col = DPXColumn.SMTPE_TIMECODE;
				break;
			case "SMTPE_USERBITS":
				col = DPXColumn.SMTPE_USERBITS;
				break;
			case "INTERLACE":
				col = DPXColumn.INTERLACE;
				break;
			case "FIELD_NUMBER":
				col = DPXColumn.FIELD_NUMBER;
				break;
			case "VIDEO_SIGNAL_STANDARD":
				col = DPXColumn.VIDEO_SIGNAL_STANDARD;
				break;
			case "ZERO":
				col = DPXColumn.ZERO;
				break;
			case "HORIZONTAL_SAMPLING_RATE":
				col = DPXColumn.HORIZONTAL_SAMPLING_RATE;
				break;
			case "VERTICAL_SAMPLING_RATE":
				col = DPXColumn.VERTICAL_SAMPLING_RATE;
				break;
			case "TEMPORAL_SAMPLING_RATE":
				col = DPXColumn.TEMPORAL_SAMPLING_RATE;
				break;
			case "TIME_OFFSET":
				col = DPXColumn.TIME_OFFSET;
				break;
			case "GAMMA":
				col = DPXColumn.GAMMA;
				break;
			case "BLACK_LEVEL":
				col = DPXColumn.BLACK_LEVEL;
				break;
			case "BLACK_GAIN":
				col = DPXColumn.BLACK_GAIN;
				break;
			case "BREAKPOINT":
				col = DPXColumn.BREAKPOINT;
				break;
			case "REFERENCE_WHITE_LEVEL":
				col = DPXColumn.REFERENCE_WHITE_LEVEL;
				break;
			case "INTEGRATION_TIME":
				col = DPXColumn.INTEGRATION_TIME;
				break;
			case "RESERVED_TELEVISION_INFORMATION_HEADER":
				col = DPXColumn.RESERVED_TELEVISION_INFORMATION_HEADER;
				break;
			case "USER_IDENTIFICATION":
				col = DPXColumn.USER_IDENTIFICATION;
				break;
			case "USER_DEFINED_DATA":
				col = DPXColumn.USER_DEFINED_DATA;
				break;
			default:
				break;
		}
		return col;
	}

}
