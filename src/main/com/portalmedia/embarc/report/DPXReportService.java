package com.portalmedia.embarc.report;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.helper.RowNumberManager;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.scene.control.TableView;

/**
 * Produces the Validation and Image Hash report
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXReportService {
	public static void WriteImageHashCsv(List<HashReportValue> values, String csvFile) throws IOException {

		try(final FileWriter writer = new FileWriter(csvFile)){
			// Write the header
			CSVUtils.writeLine(writer, Arrays.asList("FileName", "Original CRC-32C", "New CRC-32C", "Match"));
	
			// For each before/after hash, write a line
			for (final HashReportValue hrv : values) {
				String match = "FALSE";
				String orgHash = hrv.getOriginalHash();
				String newHash = hrv.getNewHash();
				if (orgHash.equals(newHash)) match = "TRUE";
				CSVUtils.writeLine(writer, Arrays.asList(hrv.getFileName(), orgHash, newHash, match));
			}
			writer.flush();
			writer.close();
		}
	}

	public static void WriteImageHashCsv(TableView<DPXFileInformationViewModel> table, String csvFile) throws IOException {
		try(final FileWriter writer = new FileWriter(csvFile)){
			// Create the header
			CSVUtils.writeLine(writer, Arrays.asList("RowNum", "FileName", "CRC-32C"));
	
			int lastRowNum = 1;
			// Iterate through each file in the table
			for (final DPXFileInformationViewModel fivm : table.getItems()) {
				if (fivm == null) {
					continue;
				}
	
				// Get the row number of the item in the table. Row number can change based on
				// sorting
				int rowNum = RowNumberManager.getRowNum(fivm.getId());
	
				// If we didn't find one, use the last row number we saw.
				if (rowNum == 0) {
					lastRowNum++;
					rowNum = lastRowNum;
				} else {
					lastRowNum = rowNum;
				}
	
				// Get the name and the path of the file
				final String name = fivm.getProp("name");
				final String path = fivm.getProp("path");
	
				// Get the image offset
				final int start = Integer.parseInt(fivm.getProp(DPXColumn.OFFSET_TO_IMAGE_DATA));
	
				// Get the image data as bytes.
				final byte[] imageData = DPXFileListHelper.getBytesFromFile(path, start);
	
				// Get the CRC32 of the image
				final String hash = DPXFileListHelper.getCrc32Hash(imageData);
	
				// Write the record
				CSVUtils.writeLine(writer, Arrays.asList(String.valueOf(rowNum), name, hash));
			}
			writer.flush();
			writer.close();
		}
	}

	public static void WriteValidationCsv(TableView<DPXFileInformationViewModel> table, String csvFile)
			throws IOException {

		try(final FileWriter writer = new FileWriter(csvFile)){
			// Create the header
			CSVUtils.writeLine(writer,
					Arrays.asList("RowNum", "FileName", "Section", "Subsection", "Column", "RuleSet", "Error"));
	
			int lastRowNum = 1;
			// Iterate through the table of files
			for (final DPXFileInformationViewModel fivm : table.getItems()) {
				// Get the row number of the item
				int rowNum = RowNumberManager.getRowNum(fivm.getId());
	
				// If we didn't get one, use the last row number we saw and go from there
				if (rowNum == 0) {
					lastRowNum++;
					rowNum = lastRowNum;
				} else {
					lastRowNum = rowNum;
				}
	
				// Get the name of the file
				final String name = fivm.getProp("name");
	
				// Get the columns with invalid rules
				final HashMap<ColumnDef, HashMap<ValidationRuleSetEnum, List<IValidationRule>>> invalidRules = fivm
						.getInvalidRuleSets();
	
				// If we have any, iterate through each column, ruleset, and rule...writing each
				// to the CSV
				if (invalidRules.size() > 0) {
					for (final ColumnDef column : invalidRules.keySet()) {
						for (final ValidationRuleSetEnum ruleSet : invalidRules.get(column).keySet()) {
							for (final IValidationRule rule : invalidRules.get(column).get(ruleSet)) {
	
								CSVUtils.writeLine(writer,
										Arrays.asList(String.valueOf(rowNum), name, column.getSection().getDisplayName(),
												column.hasSubsection() ? column.getSubsection().getDisplayName() : "",
												column.getDisplayName(), ruleSet.toString(), rule.getRule()),
										',', '"');
							}
						}
					}
				}
			}
			writer.flush();
			writer.close();
		}
	}
}
