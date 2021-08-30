package com.portalmedia.embarc.validation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;

/**
 * Creates a singleton containing all of the column specific validation rules.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXColumnValidationRules {

	private static DPXColumnValidationRules instance = null;

	public static DPXColumnValidationRules getInstance() {
		if (instance == null) {
			instance = new DPXColumnValidationRules();
		}

		return instance;
	}

	private HashMap<DPXColumn, HashMap<ValidationRuleSetEnum, List<IValidationRule>>> ruleSet;

	// Basic rule text
	private final String valid32 = "Must be valid unsigned 32bit integer";

	private final String valid16 = "Must be valid unsigned 16bit integer";
	private final String valid8 = "Must be valid unsigned 8bit integer";
	private final String FsIfNull = "should be x.FFFFFFFF if null";
	private final String haveValIfNull = "Should have value; " + FsIfNull;
	private final String validASCII = "Must be valid ASCII characters";
	private final String validReal = "Must be a valid real 32bit integer";
	private final String zerosIfNull = "should be all 0s if null";
	private final String partUndefWarn = "= warning that a non-defined value is in use; ";
	private final String fullUndefWarn = partUndefWarn + " cannot = 255";
	private final String gen = "General \n\n";
	private final String str = "FADGI Strongly Recommended \n\n";
	private final String rec = "FADGI Recommended \n\n";
	private final String opt = "FADGI Optional \n\n";
	private final String smc = "SMPTE-C \n\n";

	private DPXColumnValidationRules() {
		BuildRuleSet();
	}

	private void AddRule(DPXColumn column, ValidationRuleSetEnum ruleType, IValidationRule rule) {
		HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleSetHashMap = new HashMap<>();
		List<IValidationRule> ruleList = new LinkedList<>();
		if (ruleSet.containsKey(column)) {
			ruleSetHashMap = ruleSet.get(column);

			if (ruleSetHashMap.containsKey(ruleType)) {
				ruleList = ruleSetHashMap.get(ruleType);
			}
		}

		ruleList.add(rule);
		ruleSetHashMap.put(ruleType, ruleList);
		ruleSet.put(column, ruleSetHashMap);
	}

	private void AddRule(DPXColumn column, ValidationRuleSetEnum ruleType, IValidationRule rule, String ruleText) {
		HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleSetHashMap = new HashMap<>();
		List<IValidationRule> ruleList = new LinkedList<>();
		if (ruleSet.containsKey(column)) {
			ruleSetHashMap = ruleSet.get(column);

			if (ruleSetHashMap.containsKey(ruleType)) {
				ruleList = ruleSetHashMap.get(ruleType);
			}
		}
		rule.setRule(ruleText);
		ruleList.add(rule);
		ruleSetHashMap.put(ruleType, ruleList);
		ruleSet.put(column, ruleSetHashMap);
	}

	private void BuildRuleSet() {
		ruleSet = new HashMap<>();

		// File Information
		AddRule(DPXColumn.MAGIC_NUMBER, ValidationRuleSetEnum.SMPTE_C,
				new IsValidMatchesTextRule(new String[] { "SDPX", "XPDS" }),
				smc + "Must be ASCII form of either SDPX (0x53445058) or XPDS (0x58504453)");
		AddRule(DPXColumn.OFFSET_TO_IMAGE_DATA, ValidationRuleSetEnum.SMPTE_C, new IsValidIntRule(), smc + valid32);
		AddRule(DPXColumn.VERSION_NUMBER_OF_HEADER_FORMAT, ValidationRuleSetEnum.SMPTE_C,
				new IsValidRegexRule("[vV][1-9][.][0-9]"),
				smc + "Must contain 'V' character; currently can be 'V1.0' or 'V2.0'");
		AddRule(DPXColumn.TOTAL_IMAGE_FILE_SIZE, ValidationRuleSetEnum.SMPTE_C, new IsValidIntRule(),
				smc + valid32 + "; must match number of bytes on disk for this file.");
		AddRule(DPXColumn.DITTO_KEY, ValidationRuleSetEnum.GENERAL, new IsValidIntRangeRule(0, 1),
				gen + haveValIfNull);
		AddRule(DPXColumn.GENERIC_SECTION_HEADER_LENGTH, ValidationRuleSetEnum.GENERAL, new IsValidIntRule(),
				gen + "Should have value that equals the total bytes in the Generic Section Header");
		AddRule(DPXColumn.INDUSTRY_SPECIFIC_HEADER_LENGTH, ValidationRuleSetEnum.GENERAL, new IsValidIntRule(),
				gen + "Should have value that equals the total bytes in the Industry Specific Header");
		AddRule(DPXColumn.USER_DEFINED_HEADER_LENGTH, ValidationRuleSetEnum.FADGI_O, new IsValidIntRule(),
				gen + "Should have value that equals the total bytes in the Industry Specific Header");
		AddRule(DPXColumn.IMAGE_FILE_NAME, ValidationRuleSetEnum.FADGI_SR, new IsValidAsciiValidationRule(),
				str + "Must match the filename of the file as stored in the file system.");
		AddRule(DPXColumn.CREATION_DATETIME, ValidationRuleSetEnum.FADGI_SR, new IsValidRegexRule(
				"^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([Z]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$"),
				str + "Must conform to the following format: YYYY-MM-DDTHH:MM:SSZ, or any smaller version, e.g., YYYY, YYYY-MM, or YYYY-MM-DD.");
		AddRule(DPXColumn.CREATOR, ValidationRuleSetEnum.FADGI_SR, new IsValidRegexRule("[A-Za-z]*[,][ ].*"),
				str + "Must conform to the following format: [Country code]comma space[Entity name]");

		// TODO: 02/14/19, new validation rule on Project Name
		AddRule(DPXColumn.PROJECT_NAME, ValidationRuleSetEnum.FADGI_SR, new IsRequired());
		AddRule(DPXColumn.PROJECT_NAME, ValidationRuleSetEnum.FADGI_SR, new IsValidAsciiValidationRule());
		AddRule(DPXColumn.PROJECT_NAME, ValidationRuleSetEnum.FADGI_SR, new IsValidProjectNameRule(), str
				+ "Must have value. If labeled: Identifier [comma space] type [comma space] comment [semicolon-space if more than one identifier]. If no labeling: Identifier");
		AddRule(DPXColumn.COPYRIGHT_STATEMENT, ValidationRuleSetEnum.FADGI_R, new IsRequired());
		AddRule(DPXColumn.COPYRIGHT_STATEMENT, ValidationRuleSetEnum.FADGI_R, new IsValidAsciiValidationRule(), rec
				+ "Must have value; if there are multiple copyrights or other restriction statements, separate them by a semicolon followed by a space.");
		AddRule(DPXColumn.ENCRYPTION_KEY, ValidationRuleSetEnum.GENERAL, new IsValidIntRule(), gen + haveValIfNull);

		// Image Information Header
		AddRule(DPXColumn.IMAGE_ORIENTATION, ValidationRuleSetEnum.SMPTE_C, new IsValidShortRule());
		AddRule(DPXColumn.IMAGE_ORIENTATION, ValidationRuleSetEnum.SMPTE_C,
				new IsValidShortRangeRule((short) 0, (short) 7),
				smc + valid16 + "; can be values of 0-7; values 8-254 " + fullUndefWarn);
		AddRule(DPXColumn.NUMBER_OF_IMAGE_ELEMENTS, ValidationRuleSetEnum.SMPTE_C, new IsValidShortRule());
		AddRule(DPXColumn.NUMBER_OF_IMAGE_ELEMENTS, ValidationRuleSetEnum.SMPTE_C,
				new IsValidShortRangeRule((short) 0, (short) 7),
				smc + valid16 + "; can be values of 0-7; values 8-254 " + fullUndefWarn
						+ " there must be as many 'Image Element Structures' in the file as this number designates");
		AddRule(DPXColumn.PIXELS_PER_LINE, ValidationRuleSetEnum.SMPTE_C, new IsValidIntRule(),
				smc + valid32 + "; cannot = 255");
		AddRule(DPXColumn.LINES_PER_IMAGE_ELEMENT, ValidationRuleSetEnum.SMPTE_C, new IsValidIntRule(),
				smc + valid32 + "; cannot = 255");

		// Image Source Information Header
		AddRule(DPXColumn.X_OFFSET, ValidationRuleSetEnum.GENERAL, new IsValidIntRule(),
				gen + valid32 + "; " + FsIfNull);
		AddRule(DPXColumn.Y_OFFSET, ValidationRuleSetEnum.GENERAL, new IsValidIntRule(),
				gen + valid32 + "; " + FsIfNull);
		AddRule(DPXColumn.X_CENTER, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + valid32 + "; " + FsIfNull);
		AddRule(DPXColumn.Y_CENTER, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + valid32 + "; " + FsIfNull);
		AddRule(DPXColumn.X_ORIGINAL_SIZE, ValidationRuleSetEnum.GENERAL, new IsValidIntRule(),
				gen + valid32 + "; " + FsIfNull);
		AddRule(DPXColumn.Y_ORIGINAL_SIZE, ValidationRuleSetEnum.GENERAL, new IsValidIntRule(),
				gen + valid32 + "; " + FsIfNull);
		AddRule(DPXColumn.SOURCE_IMAGE_FILENAME, ValidationRuleSetEnum.GENERAL, new IsValidAsciiValidationRule(),
				gen + "Should be all 0s if null");
		AddRule(DPXColumn.SOURCE_IMAGE_DATETIME, ValidationRuleSetEnum.FADGI_R, new IsValidRegexRule(
				"^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([Z]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$"),
				rec + "Must conform to the following format: YYYY-MM-DDTHH:MM:SSZ, or any smaller version, e.g., YYYY, YYYY-MM, or YYYY-MM-DD.");
		AddRule(DPXColumn.INPUT_DEVICE_NAME, ValidationRuleSetEnum.FADGI_R, new IsRequired());
		AddRule(DPXColumn.INPUT_DEVICE_NAME, ValidationRuleSetEnum.FADGI_R, new IsValidAsciiValidationRule(),
				rec + validASCII + "; must have value");
		AddRule(DPXColumn.INPUT_DEVICE_SERIAL_NUMBER, ValidationRuleSetEnum.FADGI_R, new IsRequired());
		AddRule(DPXColumn.INPUT_DEVICE_SERIAL_NUMBER, ValidationRuleSetEnum.FADGI_R,
				new IsValidAsciiValidationRule(), rec + validASCII + "; must have value");

		// TODO:: Valid int array for
		// DPXColumnEnum.BORDER_VALIDITY;
		// DPXColumnEnum.PIXEL_ASPECT_RATIO;

		AddRule(DPXColumn.X_SCANNED_SIZE, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + valid32 + "; " + FsIfNull);
		AddRule(DPXColumn.Y_SCANNED_SIZE, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + valid32 + "; " + FsIfNull);

		// Motion Picture Header
		AddRule(DPXColumn.FILM_MFG_ID_CODE, ValidationRuleSetEnum.FADGI_O, new IsRequired());
		AddRule(DPXColumn.FILM_MFG_ID_CODE, ValidationRuleSetEnum.FADGI_O, new IsValidAsciiValidationRule(),
				opt + validASCII + "; must have value");
		AddRule(DPXColumn.FILM_TYPE, ValidationRuleSetEnum.FADGI_O, new IsRequired());
		AddRule(DPXColumn.FILM_TYPE, ValidationRuleSetEnum.FADGI_O, new IsValidAsciiValidationRule(),
				opt + validASCII + "; must have value");
		AddRule(DPXColumn.OFFSET_IN_PERFS, ValidationRuleSetEnum.GENERAL, new IsValidAsciiValidationRule(),
				gen + zerosIfNull);
		AddRule(DPXColumn.PREFIX, ValidationRuleSetEnum.GENERAL, new IsValidAsciiValidationRule(),
				gen + zerosIfNull);
		AddRule(DPXColumn.COUNT, ValidationRuleSetEnum.GENERAL, new IsValidAsciiValidationRule(),
				gen + zerosIfNull);
		AddRule(DPXColumn.FORMAT, ValidationRuleSetEnum.GENERAL, new IsValidAsciiValidationRule(),
				gen + zerosIfNull);
		AddRule(DPXColumn.FRAME_POSITION_IN_SEQUENCE, ValidationRuleSetEnum.FADGI_R, new IsValidIntRule(),
				rec + valid32);
		AddRule(DPXColumn.SEQUENCE_LENGTH, ValidationRuleSetEnum.FADGI_R, new IsValidIntRule(), rec + valid32);
		AddRule(DPXColumn.HELD_COUNT, ValidationRuleSetEnum.GENERAL, new IsValidIntRule(),
				gen + valid32 + "; " + FsIfNull);
		AddRule(DPXColumn.FRAME_RATE_OF_ORIGINAL, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + "Must be a valid real 32bit integer; " + FsIfNull);
		AddRule(DPXColumn.SHUTTER_ANGLE_OF_CAMERA, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + "Must be a valid real 32bit integer; " + FsIfNull);
		AddRule(DPXColumn.FRAME_IDENTIFICATION, ValidationRuleSetEnum.GENERAL, new IsValidAsciiValidationRule(),
				gen + zerosIfNull);
		AddRule(DPXColumn.SLATE_IDENTIFICATION, ValidationRuleSetEnum.GENERAL, new IsValidAsciiValidationRule(),
				gen + zerosIfNull);

		// Television header
		AddRule(DPXColumn.SMTPE_TIMECODE, ValidationRuleSetEnum.GENERAL, new IsValidIntRule(), gen + FsIfNull);
		AddRule(DPXColumn.SMTPE_USERBITS, ValidationRuleSetEnum.GENERAL, new IsValidIntRule(), gen + FsIfNull);
		AddRule(DPXColumn.INTERLACE, ValidationRuleSetEnum.GENERAL, new IsValidIntRule(),
				gen + valid8 + "; should be FF if null");
		AddRule(DPXColumn.FIELD_NUMBER, ValidationRuleSetEnum.GENERAL, new IsValidByteRule(),
				gen + valid8 + "; should be FF if null");
		AddRule(DPXColumn.VIDEO_SIGNAL_STANDARD, ValidationRuleSetEnum.GENERAL, new IsValidByteRule(),
				gen + valid8 + "; should be FF if null");

		// TODO: Zero
		AddRule(DPXColumn.HORIZONTAL_SAMPLING_RATE, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + validReal + "; " + FsIfNull);
		AddRule(DPXColumn.VERTICAL_SAMPLING_RATE, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + validReal + "; " + FsIfNull);
		AddRule(DPXColumn.TEMPORAL_SAMPLING_RATE, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + validReal + "; " + FsIfNull);
		AddRule(DPXColumn.TIME_OFFSET, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + validReal + "; " + FsIfNull);
		AddRule(DPXColumn.GAMMA, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + validReal + "; " + FsIfNull);
		AddRule(DPXColumn.BLACK_LEVEL, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + validReal + "; " + FsIfNull);
		AddRule(DPXColumn.BREAKPOINT, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + validReal + "; " + FsIfNull);
		AddRule(DPXColumn.REFERENCE_WHITE_LEVEL, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + validReal + "; " + FsIfNull);
		AddRule(DPXColumn.INTEGRATION_TIME, ValidationRuleSetEnum.GENERAL, new IsValidFloatRule(),
				gen + validReal + "; " + FsIfNull);

		// User defined data
		AddRule(DPXColumn.USER_IDENTIFICATION, ValidationRuleSetEnum.FADGI_O, new IsValidAsciiValidationRule());
		AddRule(DPXColumn.USER_IDENTIFICATION, ValidationRuleSetEnum.FADGI_O,
				new IsValidRegexRule("(?i)\\bFADGI process history\\b"), opt + "Must read: 'FADGI process history'");
		AddRule(DPXColumn.USER_DEFINED_DATA, ValidationRuleSetEnum.FADGI_O, new IsValidUDDRule(),
				"Based on the ‘Coding History’ element defined by EBU R98-1999: \nFormat for the <CodingHistory> field in Broadcast Wave Format files for the constrained space within the Broadcast Wave ‘bext’ chunk, \nthis field employs a defined string variable for each parameter of the digitization process.\n\nEach new line with text should start with 'O='");
	}

	public HashMap<ValidationRuleSetEnum, List<IValidationRule>> getRules(ColumnDef column) {
		final HashMap<ValidationRuleSetEnum, List<IValidationRule>> rules = new HashMap<>();

		if (ruleSet.containsKey(column)) {
			final HashMap<ValidationRuleSetEnum, List<IValidationRule>> rulesList = ruleSet.get(column);
			for (final ValidationRuleSetEnum ruleType : ValidationRuleSetEnum.values()) {
				if (rulesList.containsKey(ruleType)) {
					rules.put(ruleType, rulesList.get(ruleType));
				}
			}
		}

		return rules;
	}

	public HashMap<ValidationRuleSetEnum, List<IValidationRule>> getRuleSet(ColumnDef column) {
		if (ruleSet.containsKey(column)) {
			final HashMap<ValidationRuleSetEnum, List<IValidationRule>> rulesList = ruleSet.get(column);
			return rulesList;
		}

		return new HashMap<>();
	}

}
