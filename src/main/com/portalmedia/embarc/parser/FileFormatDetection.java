package com.portalmedia.embarc.parser;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;

import com.portalmedia.embarc.parser.mxf.MXFFileFormats;

public class FileFormatDetection {
	
	public static FileFormat getFileFormat(String file) {
		if (isDPX(file)) return FileFormat.DPX;
		if (isMXF(file)) return FileFormat.MXF;
		return FileFormat.OTHER;
	}
	
	public static boolean isDPX(String file) {
		try {
			
			try (BinaryFileReader f = new BinaryFileReader(file)){

				String firstFourBytes = f.readAscii(4);
				
				f.skip(4);
				
				String nextSequence = f.readAscii(4);

				if(firstFourBytes.equals("SDPX") && nextSequence.matches("[vV][1-9][.][0-9]"))
					return true;
				else if(firstFourBytes.equals("XPDS") && nextSequence.matches("[vV][1-9][.][0-9]"))
					return true;
			} 
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found checking is dpx");
		} 
		
		return false;
	}
	
	private static List<String> validMxfFiles = new ArrayList<String>() {{add("fmt/200"); add("fmt/783"); add("fmt/790");}};
	
	public static boolean isMXF(String file) {
		List<String> droidFormats = new ArrayList<String>();
		try {
			droidFormats = MXFFileFormats.getInstance().getFormatMatches(file);
		} catch (Exception e) {
			return false;
		}

		for(String format : droidFormats) {
			if(validMxfFiles.contains(format)) {
				return true;
			}
		}
		return false;
	}

	public static String getExtension(ByteBuffer buffer) throws Exception{
		  Tika t = new Tika();
		  
		  String type = t.detect(buffer.array());		
		  
		  MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
		  MimeType mime = allTypes.forName(type);
		  String ext = mime.getExtension(); // .jpg
		  
		  return ext;
		  /*
		  MimeType mimeType = null;
		    try {
		        mimeType = new MimeTypes().forName(type);
		    } catch (MimeTypeException e) {
		    }

		    if (mimeType != null) {
		        String extension = mimeType.getExtension();
		        //do something with the extension
		        return extension;
		    }
		    */
		  
		 //return null; 
	}

	private static List<String> validMxfOperationalPatterns = new ArrayList<String>() {{
		
		//MXF OP1a SingleItem SinglePackage
		add("060e2b34.04010101.0d010201.01010000");
		//MXF OP1a SingleItem SinglePackage UniTrack Stream Internal
		add("060e2b34.04010101.0d010201.01010100");
		//MXF OP1a SingleItem SinglePackage UniTrack Stream External
		add("060e2b34.04010101.0d010201.01010300");
		//MXF OP1a SingleItem SinglePackage UniTrack NonStream Internal
		add("060e2b34.04010101.0d010201.01010500");
		//MXF OP1a SingleItem SinglePackage UniTrack NonStream External
		add("060e2b34.04010101.0d010201.01010700");
		//MXF OP1a SingleItem SinglePackage MultiTrack Stream Internal
		add("060e2b34.04010101.0d010201.01010900");
		//MXF OP1a SingleItem SinglePackage MultiTrack Stream External
		add("060e2b34.04010101.0d010201.01010b00");
		//MXF OP1a SingleItem SinglePackage MultiTrack NonStream Internal
		add("060e2b34.04010101.0d010201.01010d00");
		//MXF OP1a SingleItem SinglePackage MultiTrack NonStream External
		add("060e2b34.04010101.0d010201.01010f00");
		//MXF OP1b SingleItem GangedPackages
		add("060e2b34.04010101.0d010201.01020000");
		//MXF OP1b SingleItem GangedPackages UniTrack Stream Internal
		add("060e2b34.04010101.0d010201.01020100");
		//MXF OP1b SingleItem GangedPackages UniTrack Stream External
		add("060e2b34.04010101.0d010201.01020300");
		//MXF OP1b SingleItem GangedPackages UniTrack NonStream Internal
		add("060e2b34.04010101.0d010201.01020500");
		//MXF OP1b SingleItem GangedPackages UniTrack NonStream External
		add("060e2b34.04010101.0d010201.01020700");
		//MXF OP1b SingleItem GangedPackages MultiTrack Stream Internal
		add("060e2b34.04010101.0d010201.01020900");
		//MXF OP1b SingleItem GangedPackages MultiTrack Stream External
		add("060e2b34.04010101.0d010201.01020b00");
		//MXF OP1b SingleItem GangedPackages MultiTrack NonStream Internal
		add("060e2b34.04010101.0d010201.01020d00");
		//MXF OP1b SingleItem GangedPackages MultiTrack NonStream External
		add("060e2b34.04010101.0d010201.01020f00");
		//MXF OP3c EditItems AlternatePackages
		add("060e2b34.04010101.0d010201.03030000");
		//MXF OP3c EditItems AlternatePackages UniTrack Stream Internal NoProcessing
		add("060e2b34.04010101.0d010201.03030100");
		//MXF OP3c EditItems AlternatePackages UniTrack Stream Internal MayProcess
		add("060e2b34.04010101.0d010201.03030110");
		//MXF OP3c EditItems AlternatePackages UniTrack Stream External NoProcessing
		add("060e2b34.04010101.0d010201.03030300");
		//MXF OP3c EditItems AlternatePackages UniTrack Stream External MayProcess
		add("060e2b34.04010101.0d010201.03030310");
		//MXF OP3c EditItems AlternatePackages UniTrack NonStream Internal NoProcessing
		add("060e2b34.04010101.0d010201.03030500");
		//MXF OP3c EditItems AlternatePackages UniTrack NonStream Internal MayProcess
		add("060e2b34.04010101.0d010201.03030510");
		//MXF OP3c EditItems AlternatePackages UniTrack NonStream External NoProcessing
		add("060e2b34.04010101.0d010201.03030700");
		//MXF OP3c EditItems AlternatePackages UniTrack NonStream External MayProcess
		add("060e2b34.04010101.0d010201.03030710");
		//MXF OP3c EditItems AlternatePackages MultiTrack Stream Internal NoProcessing
		add("060e2b34.04010101.0d010201.03030900");
		//MXF OP3c EditItems AlternatePackages MultiTrack Stream Internal MayProcess
		add("060e2b34.04010101.0d010201.03030910");
		//MXF OP3c EditItems AlternatePackages MultiTrack Stream External NoProcessing
		add("060e2b34.04010101.0d010201.03030b00");
		//MXF OP3c EditItems AlternatePackages MultiTrack Stream External MayProcess
		add("060e2b34.04010101.0d010201.03030b10");
		//MXF OP3c EditItems AlternatePackages MultiTrack NonStream Internal NoProcessing
		add("060e2b34.04010101.0d010201.03030d00");
		//MXF OP3c EditItems AlternatePackages MultiTrack NonStream Internal MayProcess
		add("060e2b34.04010101.0d010201.03030d10");
		//MXF OP3c EditItems AlternatePackages MultiTrack NonStream External NoProcessing
		add("060e2b34.04010101.0d010201.03030f00");
		//MXF OP3c EditItems AlternatePackages MultiTrack NonStream External MayProcess
		add("060e2b34.04010101.0d010201.03030f10");
	}};
	
	public static boolean validMxfOperationalPattern(String operationalPattern) {
		operationalPattern = operationalPattern.replace("urn:smpte:ul:", "");
		return validMxfOperationalPatterns.contains(operationalPattern);
	}
}
