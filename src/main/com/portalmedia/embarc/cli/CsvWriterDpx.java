package com.portalmedia.embarc.cli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.MetadataColumn;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;
import com.portalmedia.embarc.parser.dpx.DPXMetadata;

/**
 * Creates DPX CSV
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-01-20
 */
public class CsvWriterDpx {
	private static String[] headers = null;

	public static void writeCsvDPXFiles(String outputPath, TreeMap<String, DPXFileInformation> dpxFileList) throws IOException {
		try(ICsvMapWriter csvWriter = new CsvMapWriter(new FileWriter(outputPath), CsvPreference.STANDARD_PREFERENCE)){
	    	csvWriter.writeHeader(getHeaderColumns());
	    	
	    	for (DPXFileInformation fileInfo : dpxFileList.values()) {
	        	csvDPXMetadata(fileInfo, csvWriter);
	    	}
	
	    	csvWriter.close();
		}
    }

    private static void setHeaderColumns() {
    	List<String> headerList = new ArrayList<String>();
    	headerList.add("FileName");
    	headerList.add("FilePath");
    	headerList.add("FileSize");
    	for (DPXColumn c : DPXColumn.values()) {
    		String printName = c.getDisplayName();
    		String currentSection = "";
    		if (!currentSection.equals(c.getSectionDisplayName())) {
    			currentSection = c.getSectionDisplayName();
    		}

    		if ("Image Information".equals(currentSection)) {
    			String subsection = c.getSubsection().getDisplayName();
    			if (subsection != "") {
    				printName = subsection += " - " + printName;
    			}
    		}

    		headerList.add(printName);
    	}
    	headers = headerList.toArray(new String[0]);
    }

    private static String[] getHeaderColumns() {
    	if (headers == null) setHeaderColumns();
    	return headers;
    }

    private static void csvDPXMetadata(DPXFileInformation dpxFileInfo, ICsvMapWriter csvWriter) throws IOException {
    	System.out.println("\n" + dpxFileInfo.getName());
    	
    	DPXMetadata data = dpxFileInfo.getFileData();
    	LinkedHashMap<ColumnDef, MetadataColumn> metadata = data.getMetadataHashMap();
    	
    	final Map<String, Object> dpxItem = new HashMap<String, Object>();

    	File file = new File(dpxFileInfo.getPath());
    	dpxItem.put("FileName", dpxFileInfo.getName());
    	dpxItem.put("FilePath", dpxFileInfo.getPath());
    	dpxItem.put("FileSize", file.length());
    	for (Map.Entry<ColumnDef, MetadataColumn> entry : metadata.entrySet()) {
    		ColumnDef key = entry.getKey();
    		MetadataColumn value = entry.getValue();
    		dpxItem.put(key.getDisplayName(), value.getStandardizedValue());
    	}

    	csvWriter.write(dpxItem, getHeaderColumns());
    }

}
