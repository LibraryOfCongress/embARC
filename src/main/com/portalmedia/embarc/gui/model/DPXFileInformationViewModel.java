package com.portalmedia.embarc.gui.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.validation.DPXColumnValidationRules;
import com.portalmedia.embarc.validation.DPXFileValidationRules;
import com.portalmedia.embarc.validation.IFileValidationRule;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

/**
 * Wrapper for a file. Includes necessary validation rule violations,
 * properties, and OS specific metadata. This object is store in the database as
 * well as used for display in the table.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXFileInformationViewModel implements Mappable {

	@Id
	private int id;
	private boolean edited = false;
	private LinkedHashMap<String, String> properties;
	private HashMap<ColumnDef, HashMap<ValidationRuleSetEnum, List<IValidationRule>>> invalidRules;
	private HashSet<ValidationRuleSetEnum> invalidRuleSetList;
	HashSet<IFileValidationRule> invalidFileRules;

	private HashMap<ValidationRuleSetEnum, List<ColumnDef>> ruleSetsBroken;

	private boolean userDefinedDataEdited = false;
	private boolean userDefinedDataSet = false;

	private boolean smptecError;
	private boolean fadgioError;
	private boolean fadgirError;
	private boolean fadgisrError;
	private int userDefinedDataOriginalLength = 0;
	private boolean fileShouldBeWritten = true;

	public DPXFileInformationViewModel() {
		this(null, null, null);
		properties = new LinkedHashMap<>();
		invalidRules = new HashMap<>();
		invalidFileRules = new HashSet<>();
		invalidRuleSetList = new HashSet<>();
		ruleSetsBroken = new HashMap<>();
	}

	public DPXFileInformationViewModel(String name, String path, String uuid) {
		super();
	}

	public void autoPopulateName() {
		final String name = getProp("name");
		if (name.length() <= DPXColumn.IMAGE_FILE_NAME.getLength()) {
			setProp(DPXColumn.IMAGE_FILE_NAME, name);
			setEdited(true);
		}
	}

	public HashMap<DPXColumn, HashMap<ValidationRuleSetEnum, List<IValidationRule>>> getFileValidations(
			ColumnDef column) {
		final HashMap<DPXColumn, HashMap<ValidationRuleSetEnum, List<IValidationRule>>> toReturn = new HashMap<>();

		for (final IFileValidationRule v : DPXFileValidationRules.getInstance().getColumnRules(column)) {
			if (!v.isValid(this)) {
				HashMap<ValidationRuleSetEnum, List<IValidationRule>> columnViolations = new HashMap<>();

				if (toReturn.containsKey(v.getTargetColumn())) {
					columnViolations = toReturn.get(v.getTargetColumn());
				}

				List<IValidationRule> ruleSetViolations = new LinkedList<>();

				if (columnViolations.containsKey(v.getRuleSet())) {
					ruleSetViolations = columnViolations.get(v.getRuleSet());
				}

				ruleSetViolations.add(v.getValidationRule());

				columnViolations.put(v.getRuleSet(), ruleSetViolations);

				toReturn.put(v.getTargetColumn(), columnViolations);
			}
		}
		return toReturn;
	}

	public int getId() {
		return id;
	}

	public HashSet<IFileValidationRule> getInvalidFileRules() {
		return invalidFileRules;
	}

	public HashMap<ColumnDef, HashMap<ValidationRuleSetEnum, List<IValidationRule>>> getInvalidRules() {
		return invalidRules;
	}

	public HashSet<ValidationRuleSetEnum> getInvalidRuleSetList() {
		invalidRuleSetList.clear();
		for (final ColumnDef c : invalidRules.keySet()) {
			final HashMap<ValidationRuleSetEnum, List<IValidationRule>> r = invalidRules.get(c);
			for (final ValidationRuleSetEnum rs : r.keySet()) {
				if (!invalidRules.get(c).get(rs).isEmpty()) {
					invalidRuleSetList.add(rs);
				}
			}
		}
		return invalidRuleSetList;
	}

	public HashMap<ColumnDef, HashMap<ValidationRuleSetEnum, List<IValidationRule>>> getInvalidRuleSets() {
		return invalidRules;
	}

	public HashMap<ValidationRuleSetEnum, List<IValidationRule>> getInvalidRuleSets(ColumnDef column) {
		return invalidRules.get(column);
	}

	public int getOffsetToImageData() {
		int totalMetadata = 0;
		for (final ColumnDef column : DPXColumn.values()) {
			if (column != DPXColumn.USER_DEFINED_DATA && column != DPXColumn.USER_IDENTIFICATION) {
				totalMetadata += column.getLength();
			}
		}

		final String userDefinedDataLengthStr = getProp(DPXColumn.USER_DEFINED_HEADER_LENGTH);
		
		if (userDefinedDataLengthStr.isEmpty()) {
			return totalMetadata;
		} else {
			return totalMetadata + Integer.parseInt(userDefinedDataLengthStr);
		}
	}
	
	/*
 	public int getOffsetToImageData() {
		final String userDefinedDataLengthStr = getProp(DPXColumn.USER_DEFINED_HEADER_LENGTH);
		int userDefinedDataLength = userDefinedDataLengthStr.isEmpty() ? 0 : Integer.parseInt(userDefinedDataLengthStr);
		int totalMetadata = 0;
 		for (final DPXColumn column : DPXColumn.values()) {
 			if (column != DPXColumn.USER_DEFINED_DATA && column != DPXColumn.USER_IDENTIFICATION) {
 				totalMetadata += column.getLength();
 			}
 		}
		final int total = totalMetadata + userDefinedDataLength;
		return total;
 	}
 	*/

	public String getProp(ColumnDef column) {
		String columnName = column.getDisplayName() + column.getSectionDisplayName();
		if (column.hasSubsection()) {
			columnName += column.getSubsection().getDisplayName();
		}
		final String c = properties.get(columnName);

		if (c == null) {
			return "";
		}
		return c;
	}

	public String getProp(String columnName) {
		return properties.get(columnName);
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public HashMap<ValidationRuleSetEnum, List<ColumnDef>> getRuleSetsBroken() {
		return ruleSetsBroken;
	}

	public HashMap<ValidationRuleSetEnum, List<IValidationRule>> getRuleViolations(ColumnDef column) {
		return invalidRules.get(column);
	}

	public int getUserDefinedDataOriginalLength() {
		return userDefinedDataOriginalLength;
	}

	public boolean getFileShouldBeWritten() {
		return fileShouldBeWritten;
	}

	public boolean hasError(Set<ValidationRuleSetEnum> ruleSets) {
		for (final ColumnDef c : invalidRules.keySet()) {
			final HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleSetViolations = invalidRules.get(c);
			for (final ValidationRuleSetEnum rule : ruleSets) {
				if (ruleSetViolations != null && ruleSetViolations.containsKey(rule)) {
					final List<IValidationRule> rules = ruleSetViolations.get(rule);
					if (!rules.isEmpty()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isEdited() {
		return edited;
	}

	public boolean isFadgioError() {
		return fadgioError;
	}

	public boolean isFadgirError() {
		return fadgirError;
	}

	public boolean isFadgisrError() {
		return fadgisrError;
	}

	public boolean isSmptecError() {
		return smptecError;
	}

	public boolean isUserDefinedDataEdited() {
		return userDefinedDataEdited;
	}

	public boolean isUserDefinedDataSet() {
		return userDefinedDataSet;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(NitriteMapper mapper, Document document) {
		if (document != null) {
			setId((Integer) document.get("id"));
			setProperties((LinkedHashMap<String, String>) document.get("properties"));
			setInvalidRules((HashMap<ColumnDef, HashMap<ValidationRuleSetEnum, List<IValidationRule>>>) document
					.get("invalidRules"));
			setInvalidRuleSetList((HashSet<ValidationRuleSetEnum>) document.get("invalidRuleSetList"));
			setInvalidFileRules((HashSet<IFileValidationRule>) document.get("invalidFileRules"));
			setUserDefinedDataEdited((boolean) document.get("userDefinedDataEdited"));
			setUserDefinedDataSet((boolean) document.get("userDefinedDataSet"));
			setUserDefinedDataOriginalLength((Integer) document.get("userDefinedDataOriginalLength"));
			setEdited((boolean) document.get("edited"));
			setSmptecError((boolean) document.get("smptecError"));
			setFadgioError((boolean) document.get("fadgioError"));
			setFadgirError((boolean) document.get("fadgirError"));
			setFadgisrError((boolean) document.get("fadgisrError"));
			setRuleSetsBroken((HashMap<ValidationRuleSetEnum, List<ColumnDef>>) document.get("ruleSetsBroken"));
			setFileShouldBeWritten((boolean) document.get("fileShouldBeWritten"));
		}
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
	}

	public void setFadgioError(boolean fadgioError) {
		this.fadgioError = fadgioError;
	}

	public void setFadgirError(boolean fadgirError) {
		this.fadgirError = fadgirError;
	}

	public void setFadgisrError(boolean fadgisrError) {
		this.fadgisrError = fadgisrError;
	}

	public void setId(int id) {
		this.id = id;
		setProp("id", String.valueOf(id));
	}

	public void setInvalidFileRules(HashSet<IFileValidationRule> invalidFileRules) {
		this.invalidFileRules = invalidFileRules;
	}

	public void setInvalidRules(
			HashMap<ColumnDef, HashMap<ValidationRuleSetEnum, List<IValidationRule>>> invalidRules) {
		this.invalidRules = invalidRules;
	}

	public void setInvalidRuleSetList(HashSet<ValidationRuleSetEnum> invalidRuleSetList) {
		this.invalidRuleSetList = invalidRuleSetList;
	}

	public void setProp(ColumnDef key, String value) {
		String columnName = key.getDisplayName() + key.getSectionDisplayName();
		if (key.hasSubsection()) {
			columnName += key.getSubsection().getDisplayName();
		}

		if (value == null) {
			return;
		}

		properties.put(columnName, value);

		if (key == DPXColumn.USER_DEFINED_DATA) {
			int userIdLength = getProp(DPXColumn.USER_IDENTIFICATION).length();
			
			if (value.length() == 0 && userIdLength == 0 && getProp(DPXColumn.USER_DEFINED_HEADER_LENGTH).isEmpty()) {
				updateUserDefinedDataOriginalNull();
			} else if (value.length() == 0 && userIdLength == 0) {
				updateUserDefinedDataOriginalLength(0);
			} else {
				final int totalLength = value.length() + DPXColumn.USER_IDENTIFICATION.getLength();
				updateUserDefinedDataOriginalLength(totalLength);
			}
		} else if (key == DPXColumn.USER_IDENTIFICATION) {
			int userDefinedDataLength = getProp(DPXColumn.USER_DEFINED_DATA).length();
			
			if (value.length() == 0 && userDefinedDataLength == 0 && getProp(DPXColumn.USER_DEFINED_HEADER_LENGTH).isEmpty()) {
				updateUserDefinedDataOriginalNull();
			} else if (value.length() == 0 && userDefinedDataLength == 0) {
				updateUserDefinedDataOriginalLength(0);
			} else {
				final int totalLength = DPXColumn.USER_IDENTIFICATION.getLength() + userDefinedDataLength;
				updateUserDefinedDataOriginalLength(totalLength);
			}
		}

		final HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleSets = DPXColumnValidationRules.getInstance()
				.getRuleSet(key);

		final HashMap<ValidationRuleSetEnum, List<IValidationRule>> invalidRuleSets = new HashMap<>();

		final HashMap<DPXColumn, HashMap<ValidationRuleSetEnum, List<IValidationRule>>> invalidFileValidations = getFileValidations(
				key);

		for (final ValidationRuleSetEnum ruleSet : ruleSets.keySet()) {

			List<ColumnDef> brokenForThisRule = ruleSetsBroken.get(ruleSet);

			if (brokenForThisRule == null) {
				brokenForThisRule = new LinkedList<>();
			}
			brokenForThisRule.remove(key);

			final List<IValidationRule> rules = ruleSets.get(ruleSet);
			final List<IValidationRule> invalidRules = new LinkedList<>();
			if (rules != null) {
				for (final IValidationRule rule : rules) {
					if (!rule.isValid(value)) {
						invalidRules.add(rule);
						brokenForThisRule.add(key);

					}
				}
			}
			final HashMap<ValidationRuleSetEnum, List<IValidationRule>> fileValidations = invalidFileValidations
					.get(key);

			if (fileValidations != null) {
				final List<IValidationRule> fileRules = fileValidations.get(ruleSet);
				for (final IValidationRule fvr : fileRules) {
					if (!fvr.isValid(value)) {
						invalidRules.add(fvr);
						brokenForThisRule.add(key);
					}
				}
			}
			if (!invalidRules.isEmpty()) {
				invalidRuleSets.put(ruleSet, invalidRules);
			}
			ruleSetsBroken.put(ruleSet, brokenForThisRule);
		}
		invalidRules.remove(key);
		if (!invalidRuleSets.isEmpty()) {
			invalidRules.put(key, invalidRuleSets);
		}
		setRuleSetErrors();
	}

	public void setProp(String columnName, String value) {
		properties.put(columnName, value);
	}

	public void setProperties(LinkedHashMap<String, String> properties) {
		this.properties = properties;
	}

	private void setRuleSetErrors() {
		for (final ValidationRuleSetEnum rule : ValidationRuleSetEnum.values()) {
			int count = 0;
			final List<ColumnDef> columns = ruleSetsBroken.get(rule);
			if (columns != null) {
				count = columns.size();
			}
			final boolean hasErrors = (count > 0);
			if (rule == ValidationRuleSetEnum.SMPTE_C) {
				smptecError = hasErrors;
			} else if (rule == ValidationRuleSetEnum.FADGI_O) {
				fadgioError = hasErrors;
			} else if (rule == ValidationRuleSetEnum.FADGI_R) {
				fadgirError = hasErrors;
			} else if (rule == ValidationRuleSetEnum.FADGI_SR) {
				fadgisrError = hasErrors;
			}
		}
	}

	public void setRuleSetsBroken(HashMap<ValidationRuleSetEnum, List<ColumnDef>> ruleSetsBroken) {
		this.ruleSetsBroken = ruleSetsBroken;
	}

	public void setSmptecError(boolean smptecError) {
		this.smptecError = smptecError;
	}

	public void setUserDefinedDataEdited(boolean userDefinedDataEdited) {
		this.userDefinedDataEdited = userDefinedDataEdited;
	}

	public void setUserDefinedDataOriginalLength(Integer length) {
		userDefinedDataOriginalLength = length;
	}

	public void setFileShouldBeWritten(boolean shouldBeWritten) {
		fileShouldBeWritten = shouldBeWritten;
	}

	public void setUserDefinedDataSet(boolean userDefinedDataSet) {
		this.userDefinedDataSet = userDefinedDataSet;
	}

	public void updateUserDefinedDataOriginalLength(Integer totalLength) {
		if (!userDefinedDataSet) {
			final String origLenStr = getProp(DPXColumn.USER_DEFINED_HEADER_LENGTH);
			if(origLenStr.isEmpty()) {
				userDefinedDataOriginalLength = 0;
			}
			else {
				final int origLen = Integer.parseInt(origLenStr);
				userDefinedDataOriginalLength = origLen;
			}
			userDefinedDataSet = true;
		} else {
			userDefinedDataEdited = true;
			final String udhlName = DPXColumn.USER_DEFINED_HEADER_LENGTH.getDisplayName()
					+ DPXColumn.USER_DEFINED_HEADER_LENGTH.getSectionDisplayName();
			properties.put(udhlName, String.valueOf(totalLength));
		}
	}
	
	public void updateUserDefinedDataOriginalNull() {
		userDefinedDataSet = true;
	}

	// Mapper methods for Nitrite DB
	@Override
	public Document write(NitriteMapper mapper) {
		final Document document = new Document();
		document.put("id", getId());
		document.put("properties", getProperties());
		document.put("invalidRules", getInvalidRules());
		document.put("invalidRuleSetList", getInvalidRuleSetList());
		document.put("invalidFileRules", getInvalidFileRules());
		document.put("userDefinedDataEdited", isUserDefinedDataEdited());
		document.put("userDefinedDataSet", isUserDefinedDataSet());
		document.put("userDefinedDataOriginalLength", getUserDefinedDataOriginalLength());
		document.put("edited", isEdited());
		document.put("smptecError", isSmptecError());
		document.put("fadgioError", isFadgioError());
		document.put("fadgirError", isFadgirError());
		document.put("fadgisrError", isFadgisrError());
		document.put("ruleSetsBroken", getRuleSetsBroken());
		document.put("fileShouldBeWritten", getFileShouldBeWritten());
		return document;
	}

}
