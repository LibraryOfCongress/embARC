package com.portalmedia.embarc.report;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.portalmedia.embarc.parser.dpx.DPXSequenceError;

/**
 * Writes DPX sequence gap analysis report to specified CSV file
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-01-20
 */
public class DPXSequenceAnalysisReportCSVWriter {
	private static String[] headers = null;

	public static void writeSequenceAnalysisReportCSV(String outputPath, List<DPXSequenceError> sequenceErrors) throws IOException {
		try(FileWriter fileWriter = new FileWriter(outputPath)){
			try(ICsvMapWriter csvWriter = new CsvMapWriter(fileWriter, CsvPreference.STANDARD_PREFERENCE)) {
				csvWriter.writeHeader(getHeaderColumns());
				for (DPXSequenceError seqError : sequenceErrors) {
					writeSequenceErrorCsv(seqError, csvWriter);
				}
			}
		}
    }

    private static void setHeaderColumns() {
    	List<String> headerList = new ArrayList<String>();
		headerList.add("Filename");
		headerList.add("Column");
		headerList.add("Error");
		headerList.add("Error Description");
    	headers = headerList.toArray(new String[0]);
    }

    private static String[] getHeaderColumns() {
    	if (headers == null) setHeaderColumns();
    	return headers;
    }

    private static void writeSequenceErrorCsv(DPXSequenceError sequenceError, ICsvMapWriter csvWriter) throws IOException {
    	final Map<String, Object> seqErrorItem = new HashMap<String, Object>();
		seqErrorItem.put("Filename", sequenceError.getFilename());
		seqErrorItem.put("Column", sequenceError.getColumn());
		seqErrorItem.put("Error", sequenceError.getError());
		seqErrorItem.put("Error Description", sequenceError.getErrorDescription());
    	csvWriter.write(seqErrorItem, getHeaderColumns());
    }

}
