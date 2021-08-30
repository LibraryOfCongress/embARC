package com.portalmedia.embarc.parser.mxf;

import com.portalmedia.embarc.parser.FileInformation;

import tv.amwa.maj.io.mxf.MXFFile;

public interface MXFWriterService {
	boolean writeFile(FileInformation<MXFMetadata> metadata, String outputFile);
}
