package com.portalmedia.embarc.parser.mxf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import com.portalmedia.embarc.parser.FileInformation;
import com.portalmedia.embarc.parser.MetadataColumnDef;

import tv.amwa.maj.io.mxf.MXFFile;
import tv.amwa.maj.model.AS07CoreDMSFramework;
import tv.amwa.maj.model.AS07GSPDMSObject;
import tv.amwa.maj.model.Preface;
import tv.amwa.maj.model.impl.AS07GspBdDMSFrameworkImpl;
import tv.amwa.maj.model.impl.AS07GspTdDMSFrameworkImpl;

public interface MXFService {

	void readFile();

	MXFFile getFile();

	MXFFileWriteResult writeFile(String outputFilePath, AS07CoreDMSFramework udpatedCore) throws IOException;

	boolean hasAS07CoreDMSFramework();

	boolean hasAS07DMSIdentifierSet();
	AS07CoreDMSFramework getAS07CoreDMSFramework();
	void setAS07CoreDMSFramework(AS07CoreDMSFramework dms);
	FileInformation<MXFMetadata> getMetadata();
	List<AS07GspTdDMSFrameworkImpl> getAS07GspTdDMSFramework();
	List<AS07GspBdDMSFrameworkImpl> getAS07GspBdDMSFramework();
	List<AS07GSPDMSObject> getAS07GSPDMSObjects();
	MXFFileDescriptorResult getDescriptors();
	Preface getPreface();
	MXFFileWriteResult writeFile(String outputFilePath, HashMap<MXFColumn, MetadataColumnDef> coreColumns) throws IOException;

	ByteBuffer GetGenericStream(int streamId);

	boolean DownloadGenericStream(int streamId, String outputFile);

}