package com.portalmedia.embarc.cli;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.MetadataColumn;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;

/**
 * Process dpx batches
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-01-20
 */
public class BatchProcessorDpx {
    
    public static void processBatch(TreeMap<String, DPXFileInformation> dpxTreeMap) {
    	String fileNameSeqResult = "";
    	String imageFileNameSeqResult = "";
    	String sourceImageFileNameSeqResult = "";
    	String framePositionInSeqResult = "";

    	String fileNameSeqDuplicateResult = "";
    	String imageFileNameSeqDuplicateResult = "";
    	String sourceImageFileNameSeqDuplicateResult = "";
    	String framePositionInSeqDuplicateResult = "";

    	String prevFileNameSeq = "";
    	String prevImageFileNameSeq = "";
    	String prevSourceImageFileNameSeq = "";
    	String prevFramePosition = "";
		boolean firstLap = true;

    	for (DPXFileInformation dpx : dpxTreeMap.values()) {
    		LinkedHashMap<ColumnDef, MetadataColumn> metadata = dpx.getFileData().getMetadataHashMap();

    		if (fileNameSeqResult.isEmpty()) {
        		String currFileNameSeq = getSequenceNumFromFileName(dpx.getName());
        		if (!isSequential(prevFileNameSeq, currFileNameSeq, firstLap)) {
        			fileNameSeqResult = "Break In Sequence";
        		}
        		if (!isUnique(prevFileNameSeq, currFileNameSeq, firstLap)) {
        			fileNameSeqDuplicateResult = "Duplicates In Sequence";
        		}
        		prevFileNameSeq = currFileNameSeq;
    		}

    		if (imageFileNameSeqResult.isEmpty()) {
        		String currImageFileNameSeq = getSequenceNumFromFileName(metadata.get(DPXColumn.IMAGE_FILE_NAME).getStandardizedValue());
        		if (!isSequential(prevImageFileNameSeq, currImageFileNameSeq, firstLap)) {
        			imageFileNameSeqResult = "Break In Sequence";
        		}
        		if (!isUnique(prevImageFileNameSeq, currImageFileNameSeq, firstLap)) {
        			imageFileNameSeqDuplicateResult = "Duplicates In Sequence";
        		}
        		prevImageFileNameSeq = currImageFileNameSeq;
    		}

    		if (sourceImageFileNameSeqResult.isEmpty()) {
        		String currSourceImageFileNameSeq = getSequenceNumFromFileName(metadata.get(DPXColumn.SOURCE_IMAGE_FILENAME).getStandardizedValue());
        		if (!isSequential(prevSourceImageFileNameSeq, currSourceImageFileNameSeq, firstLap)) {
        			sourceImageFileNameSeqResult = "Break In Sequence";
        		}
        		if (!isUnique(prevSourceImageFileNameSeq, currSourceImageFileNameSeq, firstLap)) {
        			sourceImageFileNameSeqDuplicateResult = "Duplicates In Sequence";
        		}
        		prevSourceImageFileNameSeq = currSourceImageFileNameSeq;
    		}

    		if (framePositionInSeqResult.isEmpty()) {
        		String currFramePosition = metadata.get(DPXColumn.FRAME_POSITION_IN_SEQUENCE).getStandardizedValue();
        		if (!isSequential(prevFramePosition, currFramePosition, firstLap)) {
        			framePositionInSeqResult = "Break In Sequence";
        		}
        		if (!isUnique(prevFramePosition, currFramePosition, firstLap)) {
        			framePositionInSeqDuplicateResult = "Duplicates In Sequence";
        		}
        		prevFramePosition = currFramePosition;
    		}

    		if (firstLap) firstLap = false;
    	}

    	if (fileNameSeqResult.isEmpty()) fileNameSeqResult = "No Break In Sequence";
    	System.out.println("Filename Sequence Analysis: " + fileNameSeqResult + "\n");
    	
    	if (fileNameSeqDuplicateResult.isEmpty()) fileNameSeqDuplicateResult = "No Duplicates In Sequence";
    	System.out.println("Filename Sequence Duplication Analysis: " + fileNameSeqDuplicateResult + "\n");

    	if (imageFileNameSeqResult.isEmpty()) imageFileNameSeqResult = "No Break In Sequence";
    	System.out.println("Image Filename Metadata Sequence Analysis: " + imageFileNameSeqResult + "\n");

    	if (imageFileNameSeqDuplicateResult.isEmpty()) imageFileNameSeqDuplicateResult = "No Duplicates In Sequence";
    	System.out.println("Image Filename Metadata Sequence Duplication Analysis: " + imageFileNameSeqDuplicateResult + "\n");
    	
    	if (sourceImageFileNameSeqResult.isEmpty()) sourceImageFileNameSeqResult = "No Break In Sequence";
    	System.out.println("Source Image Filename Metadata Sequence Analysis: " + sourceImageFileNameSeqResult + "\n");
    	
    	if (sourceImageFileNameSeqDuplicateResult.isEmpty()) sourceImageFileNameSeqDuplicateResult = "No Duplicates In Sequence";
    	System.out.println("Source Image Filename Metadata Sequence Duplication Analysis: " + sourceImageFileNameSeqDuplicateResult + "\n");
    	
    	if (framePositionInSeqResult.isEmpty()) framePositionInSeqResult = "No Break In Sequence";
    	System.out.println("Frame Position In Sequence Metadata Analysis: " + framePositionInSeqResult + "\n");
    	
    	if (framePositionInSeqDuplicateResult.isEmpty()) framePositionInSeqDuplicateResult = "No Duplicates In Sequence";
    	System.out.println("Frame Position In Sequence Metadata Duplication Analysis: " + framePositionInSeqDuplicateResult + "\n");

    	batchFileSizeAnalysis(dpxTreeMap);
    	batchMetadataAnalysis(dpxTreeMap);
    }

    private static String getSequenceNumFromFileName(String fileName) {
    	String f = fileName.split(".dpx")[0];
    	String seq = "";

    	for (int i = f.length() -1; i >= 0; i--) {
    		Character c = f.charAt(i);
    		if (!Character.isDigit(c)) break;
    		seq += c;
    	}

    	return new StringBuilder(seq).reverse().toString();
    }

    private static boolean isSequential(String a, String b, boolean firstLap) {
    	if (firstLap) return true;
    	if (a.isEmpty() || b.isEmpty()) return false; // either are empty => not sequential
    	if ((Integer.parseInt(a) + 1) == Integer.parseInt(b)) return true;
    	return false;
    }

    private static boolean isUnique(String a, String b, boolean firstLap) {
    	if (firstLap) return true;
    	if (a.isEmpty() || b.isEmpty()) return true;
    	if (Integer.parseInt(a) == Integer.parseInt(b)) return false;
    	else return true;
    }

    private static void batchFileSizeAnalysis(TreeMap<String, DPXFileInformation> dpxTreeMap) {
    	HashMap<String, String> errorFileList = new HashMap<String, String>();

    	for (DPXFileInformation dpx : dpxTreeMap.values()) {
    		LinkedHashMap<ColumnDef, MetadataColumn> metadata = dpx.getFileData().getMetadataHashMap();
    		String totalImageFileSizeValue = metadata.get(DPXColumn.TOTAL_IMAGE_FILE_SIZE).getStandardizedValue();
    		Long totalImageFileSize = Long.parseLong(totalImageFileSizeValue);
    		File file = new File(dpx.getPath());

    		if (file.length() < totalImageFileSize) {
    			errorFileList.put(dpx.getName(), "Truncated");
    		} else if (file.length() > totalImageFileSize) {
    			errorFileList.put(dpx.getName(), "Inflated");
    		}
    	}

    	if (errorFileList.size() > 0) {
    		System.out.println("File Size Analysis: " + errorFileList.size() + " issues detected");
    		for (Map.Entry<String, String> set : errorFileList.entrySet()) {
    			System.out.format("%-2s%-4s%-2s%-10s", "", set.getKey(), ": ", set.getValue());
    			System.out.println("");
    		}
    	} else {
    		System.out.println("File Size Analysis: No Issues Detected");
    	}
    }

    private static void batchMetadataAnalysis(TreeMap<String, DPXFileInformation> dpxTreeMap) {
    	System.out.println("\n- Sequence Metadata Analysis -");
    	LinkedHashMap<ColumnDef, String> metadataSummary = new LinkedHashMap<ColumnDef, String>();

    	boolean firstLap = true;

    	// Iterate through all files, check for differing values
    	for (DPXFileInformation dpx : dpxTreeMap.values()) {
    		LinkedHashMap<ColumnDef, MetadataColumn> currMetadata = dpx.getFileData().getMetadataHashMap();

    		for (Map.Entry<ColumnDef, MetadataColumn> entry : currMetadata.entrySet()) {
    			ColumnDef key = entry.getKey();
    			String value = entry.getValue().getStandardizedValue();
    			String summaryValue = metadataSummary.get(key);

    			if (firstLap) {
    				metadataSummary.put(key, value);
    				continue;
    			}
    			
    			if (!summaryValue.equals(value)) {
    				metadataSummary.put(key, "{multiple values}");
    			}
    		}

    		if (firstLap) firstLap = false;
    	}


    	// After iteration is complete, print final metadata summary to console
    	String section = "";
    	String subsection = "";

    	for (Map.Entry<ColumnDef, String> entry : metadataSummary.entrySet()) {
    		ColumnDef key = entry.getKey();
    		String value = entry.getValue();

    		if (!section.equals(key.getSectionDisplayName())) {
    			section = key.getSectionDisplayName();
    			System.out.println("\n" + section);
    			System.out.println("--------------------------------------------------------------------------------");
    		}

    		if ("Image Information".equals(section)) {
    			String sub = key.getSubsection().getDisplayName();
    			if (!sub.equals(subsection) && !"".equals(sub)) {
    				subsection = sub;
    				System.out.println("  --- " + subsection + " ---");
    			}
    		}

			System.out.format("%-2s%-40s%-1s", "", key.getDisplayName(), value);
			System.out.println("");
		}

    	System.out.println("");
    }

}
