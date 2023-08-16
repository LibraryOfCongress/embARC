package com.portalmedia.embarc.parser.mxf;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Project;

import uk.gov.nationalarchives.droid.core.BinarySignatureIdentifier;
import uk.gov.nationalarchives.droid.core.SignatureParseException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.resource.FileSystemIdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;

public class MXFFileFormats {
	private static MXFFileFormats instance = null;
	BinarySignatureIdentifier droid;
	//private static String droidSignatureFile = "DROID_SignatureFile_V95.xml";
	private static String droidSignatureFileResources = "resources/DROID_SignatureFile_V95.xml";
	private List<String> fileFormats = new ArrayList<String>();

	public static MXFFileFormats getInstance() throws Exception {
		if (instance == null) {
			instance = new MXFFileFormats();
		}

		return instance;
	}
	
	private MXFFileFormats() throws Exception {
		initDroid();
	}
	
	public List<String> getFileFormats(){
		return fileFormats;
	}

	private void initDroid() throws Exception{        
        Path tempPath = Files.createTempFile("droidSignatureFile", ".xml");

		try (InputStream in = Project.class.getClassLoader().getResourceAsStream(droidSignatureFileResources)) {
			Files.copy(in, tempPath, StandardCopyOption.REPLACE_EXISTING);
			in.close();
		}

		droid = new BinarySignatureIdentifier();

        try {
        	droid.setSignatureFile(tempPath.toString());
            droid.init();
            
        } catch (SignatureParseException x) {
			System.out.println("Signature parse error");
        	throw new Exception("Invalid signature file");
        }

	}

	public  List<String> getFormatMatches(String filename) throws Exception {        
        
        final Path file = Paths.get(filename);
        
        URI resourceUri = file.toUri();
  
        RequestMetaData metaData = new RequestMetaData(
                Files.size(file), Files.getLastModifiedTime(file).toMillis(), filename);
        RequestIdentifier identifier = new RequestIdentifier(resourceUri);
        identifier.setParentId(1L);
        
        IdentificationRequest<Path> request = new FileSystemIdentificationRequest(metaData, identifier);
        request.open(file);

        IdentificationResultCollection resultsCollection = droid.matchBinarySignatures(request);
        List<IdentificationResult> results = resultsCollection.getResults();
        List<String> formats = new ArrayList<String>();
        for(IdentificationResult result : results) {
        	System.out.println(result.getPuid());
        	formats.add(result.getPuid());
        }
        request.close();
        return formats;
	}

}
