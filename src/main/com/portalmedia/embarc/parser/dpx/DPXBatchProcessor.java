package com.portalmedia.embarc.parser.dpx;

import java.util.LinkedList;
import java.util.List;

import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;

import javafx.collections.ObservableList;

/**
 * Process dpx batches
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-01-20
 */
public class DPXBatchProcessor {
    
    public static List<DPXSequenceError> getSequenceErrorList(ObservableList<DPXFileInformationViewModel> dpxFileList, boolean allErrors) {
    	List<DPXSequenceError> result = new LinkedList<DPXSequenceError>();
		boolean firstLap = true;

    	boolean fileNameSeqValid = true;
    	boolean imageFileNameSeqValid = true;
    	boolean sourceImageFileNameSeqValid = true;
    	boolean framePositionInSeqValid = true;

    	String prevFileNameSeq = "";
    	String prevImageFileNameSeq = "";
    	String prevSourceImageFileNameSeq = "";
    	String prevFramePosition = "";

		for (DPXFileInformationViewModel fileInfo : dpxFileList) {
			String dpxName = fileInfo.getProp("name");

    		if (fileNameSeqValid || allErrors) {
        		String currFileNameSeq = getSequenceNumFromFileName(dpxName);
        		if (!isSequential(prevFileNameSeq, currFileNameSeq, firstLap)) {
        			fileNameSeqValid = false;
        			result.add(
    					new DPXSequenceError(
							dpxName,
							"Filename",
							"Break in Sequence",
							currFileNameSeq + " is not in sequence with " + prevFileNameSeq
						)
    				);
        		}
        		if (!isUnique(prevFileNameSeq, currFileNameSeq, firstLap)) {
        			fileNameSeqValid = false;
        			result.add(
    					new DPXSequenceError(
    						dpxName,
    						"Filename",
    						"Duplicates in Sequence",
    						currFileNameSeq + " is duplicated"
    					)
    				);
        		}
        		prevFileNameSeq = currFileNameSeq;
    		}

			String imageFileName = fileInfo.getProp(DPXColumn.IMAGE_FILE_NAME);
    		if (imageFileNameSeqValid || allErrors) {
        		String currImageFileNameSeq = getSequenceNumFromFileName(imageFileName);
        		if (!isSequential(prevImageFileNameSeq, currImageFileNameSeq, firstLap)) {
        			imageFileNameSeqValid = false;
        			result.add(
        				new DPXSequenceError(
        					dpxName,
        					DPXColumn.IMAGE_FILE_NAME.getDisplayName(),
        					"Break in Sequence",
        					currImageFileNameSeq + " is not in sequence with " + prevImageFileNameSeq
        				)
        			);
        		}
        		if (!isUnique(prevImageFileNameSeq, currImageFileNameSeq, firstLap)) {
        			imageFileNameSeqValid = false;
        			result.add(
        				new DPXSequenceError(
        					dpxName,
        					DPXColumn.IMAGE_FILE_NAME.getDisplayName(),
        					"Duplicates in Sequence",
        					currImageFileNameSeq + " is duplicated"
        				)
        			);
        		}
        		prevImageFileNameSeq = currImageFileNameSeq;
    		}

			String sourceImageFileName = fileInfo.getProp(DPXColumn.SOURCE_IMAGE_FILENAME);
    		if (sourceImageFileNameSeqValid || allErrors) {
        		String currSourceImageFileNameSeq = getSequenceNumFromFileName(sourceImageFileName);
        		if (!isSequential(prevSourceImageFileNameSeq, currSourceImageFileNameSeq, firstLap)) {
        			sourceImageFileNameSeqValid = false;
        			result.add(
    					new DPXSequenceError(
							dpxName,
							DPXColumn.SOURCE_IMAGE_FILENAME.getDisplayName(),
							"Break in Sequence",
							currSourceImageFileNameSeq + " is not in sequence with " + prevSourceImageFileNameSeq
    					)
    				);
        		}
        		if (!isUnique(prevSourceImageFileNameSeq, currSourceImageFileNameSeq, firstLap)) {
        			sourceImageFileNameSeqValid = false;
        			result.add(
    					new DPXSequenceError(
							dpxName,
							DPXColumn.SOURCE_IMAGE_FILENAME.getDisplayName(),
							"Duplicates in Sequence",
							currSourceImageFileNameSeq + " is duplicated"
						)
    				);
        		}
        		prevSourceImageFileNameSeq = currSourceImageFileNameSeq;
    		}

			String framePositionInSequence = fileInfo.getProp(DPXColumn.FRAME_POSITION_IN_SEQUENCE);
    		if (framePositionInSeqValid || allErrors) {
        		String currFramePosition = framePositionInSequence;
        		if (!isSequential(prevFramePosition, currFramePosition, firstLap)) {
        			framePositionInSeqValid = false;
        			result.add(
        				new DPXSequenceError(
        					dpxName,
							DPXColumn.FRAME_POSITION_IN_SEQUENCE.getDisplayName(),
        					"Break in Sequence",
        					currFramePosition + " is not in sequence with " + prevFramePosition
        				)
        			);
        		}
        		if (!isUnique(prevFramePosition, currFramePosition, firstLap)) {
        			framePositionInSeqValid = false;
        			result.add(
        				new DPXSequenceError(
        					dpxName,
							DPXColumn.FRAME_POSITION_IN_SEQUENCE.getDisplayName(),
        					"Duplicates in Sequence",
        					currFramePosition + " is duplicated"
        				)
        			);
        		}
        		prevFramePosition = currFramePosition;
    		}

    		if (firstLap) firstLap = false;
    	}

    	return result;
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

}
