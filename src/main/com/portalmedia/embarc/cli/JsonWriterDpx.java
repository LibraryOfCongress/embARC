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

import org.json.JSONException;
import org.json.JSONObject;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.MetadataColumn;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;
import com.portalmedia.embarc.parser.dpx.DPXMetadata;

/**
 * Creates DPX JSON
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-01-20
 */
public class JsonWriterDpx {

    public static void writeJsonDPXFiles(String outputPath, TreeMap<String, DPXFileInformation> dpxFileList) throws JSONException, IOException {
		JSONObject j = new JSONObject();
        List<JSONObject> jsonObs = new ArrayList<JSONObject>();

    	for (DPXFileInformation dpxFileInfo : dpxFileList.values()) {
    		JSONObject fileJson = new JSONObject();
            HashMap<String, String> fileValues = createJsonFileInfo(dpxFileInfo);
            fileJson.put("General", fileValues);
            
            DPXMetadata data = dpxFileInfo.getFileData();
        	LinkedHashMap<ColumnDef, MetadataColumn> metadata = data.getMetadataHashMap();

        	String currentSection = null;

        	HashMap<String, String> values = new HashMap<String, String>();
        	for (Map.Entry<ColumnDef, MetadataColumn> entry : metadata.entrySet()) {
        		ColumnDef key = entry.getKey();
        		MetadataColumn value = entry.getValue();

        		if (currentSection != key.getSectionDisplayName()) {
                    if (values.size() > 0) {
            			fileJson.put(currentSection, values);
                    }
        			currentSection = key.getSectionDisplayName();
        			values = new HashMap<String, String>();
        		}

        		values.put(key.getDisplayName(), value.getStandardizedValue());
        	}

            fileJson.put(currentSection, values);
            jsonObs.add(fileJson);
    	}

    	j.put("Files", jsonObs);
        FileWriter writer = new FileWriter(outputPath);
        j.write(writer);
        writer.close();
    }

    private static HashMap<String, String> createJsonFileInfo(DPXFileInformation dpxFileInfo) {
    	HashMap<String, String> values = new HashMap<String, String>();
    	File file = new File(dpxFileInfo.getPath());
    	values.put("FileName", dpxFileInfo.getName());
    	values.put("FilePath", dpxFileInfo.getPath());
    	values.put("FileSize", Long.toString(file.length()));

    	return values;
    }

}
