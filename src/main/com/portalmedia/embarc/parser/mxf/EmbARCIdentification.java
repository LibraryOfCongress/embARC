package com.portalmedia.embarc.parser.mxf;

import java.security.SecureRandom;


import tv.amwa.maj.enumeration.ProductReleaseType;
import tv.amwa.maj.model.Identification;
import tv.amwa.maj.model.impl.IdentificationImpl;
import tv.amwa.maj.record.AUID;
import tv.amwa.maj.record.ProductVersion;
import tv.amwa.maj.record.TimeStamp;
import tv.amwa.maj.record.impl.AUIDImpl;
import tv.amwa.maj.record.impl.ProductVersionImpl;
import tv.amwa.maj.record.impl.TimeStampImpl;

public class EmbARCIdentification {
	private static EmbARCIdentification instance = null; 
	  
    // variable of type String 
    public IdentificationImpl identification; 
    private String applicationName = "embARC";
    private String supplierName = "Federal Agencies Digital Guidelines Initiative";
    private short majorVersion = 1; // TODO: Lookup actual version -- or move this to an external file
    private short minorVersion = 1;
    private short tertiaryVersion = 1;
    private short patchVersion = 0;
    //private AUID embarcProductId = null;
    private ProductReleaseType releaseType = ProductReleaseType.Released;
    
    private EmbARCIdentification() { 
    	identification = new IdentificationImpl();
    	ProductVersion version = new ProductVersionImpl(majorVersion, minorVersion, tertiaryVersion, patchVersion, releaseType);
    	
        identification.setApplicationVersion(version);
        SecureRandom rand = new SecureRandom();
        int data1 = rand.nextInt();
        short data2 = (short)rand.nextInt(32767);
        short data3 = (short)rand.nextInt(32767);;
        byte [] data4 = new byte[8];
        rand.nextBytes(data4);
        
        AUID auid = new AUIDImpl(data1, data2, data3, data4);
        
        identification.setLinkedGenerationID(auid);
        identification.setApplicationName(applicationName);
        identification.setApplicationPlatform(System.getProperty("os.name"));
        identification.setApplicationSupplierName(supplierName);
        identification.setApplicationVersion(version);
        String versionString = majorVersion + "." + minorVersion + "." + tertiaryVersion + "." + patchVersion;
        identification.setApplicationVersionString(versionString);
        identification.setToolkitVersionString(versionString);
        TimeStamp time = new TimeStampImpl();
        identification.setFileModificationDate(time);
        identification.setGenerationID(auid);
        identification.setApplicationProductID(auid); //TODO: Supply a consistent value for embARC
        
        
    }

    public Identification getIdentification() {
    	return identification;
    }

    public static EmbARCIdentification getInstance() { 
        if (instance == null) 
        	instance = new EmbARCIdentification(); 
  
        return instance; 
    } 
}
