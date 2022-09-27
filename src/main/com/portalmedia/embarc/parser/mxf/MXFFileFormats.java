package com.portalmedia.embarc.parser.mxf;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.Project;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.validation.DPXColumnValidationRules;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.IsRequired;
import com.portalmedia.embarc.validation.IsValidAsciiValidationRule;
import com.portalmedia.embarc.validation.IsValidByteRule;
import com.portalmedia.embarc.validation.IsValidFloatRule;
import com.portalmedia.embarc.validation.IsValidIntRangeRule;
import com.portalmedia.embarc.validation.IsValidIntRule;
import com.portalmedia.embarc.validation.IsValidMatchesTextRule;
import com.portalmedia.embarc.validation.IsValidProjectNameRule;
import com.portalmedia.embarc.validation.IsValidRegexRule;
import com.portalmedia.embarc.validation.IsValidShortRangeRule;
import com.portalmedia.embarc.validation.IsValidShortRule;
import com.portalmedia.embarc.validation.IsValidUDDRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

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

	public static MXFFileFormats getInstance() throws Exception {
		if (instance == null) {
			instance = new MXFFileFormats();
		}

		return instance;
	}

	BinarySignatureIdentifier droid;
	
	
	private static String droidSignatureFile = "DROID_SignatureFile_V95.xml";
	private static String droidSignatureFileResources = "resources/DROID_SignatureFile_V95.xml";
	
	private MXFFileFormats() throws Exception {
		initDroid();
	}
	List<String> fileFormats = new ArrayList<String>();
	
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
			x.printStackTrace();
        	throw new Exception("Invalid signature file");
        }

	}
	public  List<String> getFormatMatches(String filename) throws Exception{        
        
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
