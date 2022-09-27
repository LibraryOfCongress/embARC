package MXFTests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//import java.io.File; // if you use File
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.portalmedia.embarc.parser.FileFormatDetection;
import com.portalmedia.embarc.parser.FileInformation;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.mxf.EmbARCIdentification;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.parser.mxf.MXFFileDescriptorResult;
import com.portalmedia.embarc.parser.mxf.MXFMetadata;
import com.portalmedia.embarc.parser.mxf.MXFService;
import com.portalmedia.embarc.parser.mxf.MXFServiceImpl;
import com.portalmedia.embarc.parser.mxf.ManifestParser;
import com.portalmedia.embarc.parser.mxf.ManifestParserImpl;
import com.portalmedia.embarc.parser.mxf.ManifestType;

import tv.amwa.maj.io.mxf.LocalTagEntry;
import tv.amwa.maj.io.mxf.MXFFile;
import tv.amwa.maj.io.mxf.PrimerPack;
import tv.amwa.maj.model.AS07CoreDMSFramework;
import tv.amwa.maj.model.AS07GSPDMSObject;
import tv.amwa.maj.model.Identification;
import tv.amwa.maj.model.Preface;
import tv.amwa.maj.model.impl.AS07CoreDMSDeviceObjectsImpl;
import tv.amwa.maj.model.impl.AS07DMSIdentifierSetImpl;
import tv.amwa.maj.model.impl.AS07GspBdDMSFrameworkImpl;
import tv.amwa.maj.model.impl.AS07GspTdDMSFrameworkImpl;
import tv.amwa.maj.record.AUID;
import tv.amwa.maj.record.impl.AUIDImpl;

public class MXFReadWriteTests {

	String filePath ="";
	MXFService service = null;
	
	@Before
	public void Init() throws FileNotFoundException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File srcFile = new File(classLoader.getResource("as07_sample1-gf-unc-3.1.mxf").getFile());
        
        filePath = srcFile.getAbsolutePath();
        
        service = new MXFServiceImpl(filePath); 
	}
	@Test
	public void CanReadMxfFile() {
		MXFFile mxf = service.getFile();
		
		Assert.assertNotNull(mxf);
	}
	@Test
	public void CanValidateManifest() throws ParserConfigurationException, SAXException, IOException {

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		File schemaFile =new File(classLoader.getResource("RDD48-Manifest-20180827.xsd").getFile());
		
		File f = new File(classLoader.getResource("RDD48_LakeJulianP1060231_50i_576_MKV_FFV1_422_8_ag_20220517.mxf_1003_DATA_DOWNLOAD.xml").getFile());
		Source xmlFile = new StreamSource(f);
		

	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    Document doc = db.parse(f);
	    
	    Element root = doc.getDocumentElement();
	    
	    System.out.println(root.getNodeName());
		
		SchemaFactory schemaFactory = SchemaFactory
		    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
		  Schema schema = schemaFactory.newSchema(schemaFile);
		  Validator validator = schema.newValidator();
		  validator.validate(xmlFile);
		  System.out.println(xmlFile.getSystemId() + " is valid");
		} catch (SAXException e) {
		  System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
		} catch (IOException e) {}
	}
	
	@Test
	public void CanValidateMxfFileManifest() throws ParserConfigurationException, SAXException, IOException {
		ByteBuffer bb = service.GetGenericStream(2);

		ManifestParser parser = new ManifestParserImpl();
		
		ManifestType type = parser.isManifest(bb);
		
		Assert.assertTrue(type==ManifestType.INVALID_MANIFEST);
	}
	@Test
	public void CanDownloadTD() throws FileNotFoundException {
		ByteBuffer bb = service.GetGenericStream(2);
		Assert.assertNotNull(bb);
		
		File file = new File("stream_test.xml");
		
		FileChannel channel = new FileOutputStream(file, false).getChannel();
		 
        // Flips this buffer.  The limit is set to the current position and then
        // the position is set to zero.  If the mark is defined then it is discarded.
        //bb.flip();

        // Writes a sequence of bytes to this channel from the given buffer.
        try {
			channel.write(bb);
	        // close the channel
	        channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void CanDownloadBD() throws FileNotFoundException {
		ByteBuffer bb = service.GetGenericStream(1);
		Assert.assertNotNull(bb);
		
		File file = new File("stream_test.tiff");
		
		FileChannel channel = new FileOutputStream(file, false).getChannel();
		 
        // Flips this buffer.  The limit is set to the current position and then
        // the position is set to zero.  If the mark is defined then it is discarded.
        //bb.flip();

        // Writes a sequence of bytes to this channel from the given buffer.
        try {
			channel.write(bb);
	        // close the channel
	        channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Test
	public void CanGetExtension() throws Exception {
		ByteBuffer bb = service.GetGenericStream(2);
		Assert.assertNotNull(bb);
				 
		String ext = FileFormatDetection.getExtension(bb);
		
		Assert.assertNotNull(ext);
		
	}
	@Test
	public void CanWriteAs07Changes() throws IOException {
		String shimName = "test shim name";
		String responsibleOrganizationName = "test org name";
		String responsibleOrganizationCode = "test org code";
		String pictureFormat = "test picture format";
		String captions = "test captions";
		AUID audioTrackLayout = new AUIDImpl();// TODO
		String intendedAFD = "test AFD";
		List<AS07DMSIdentifierSetImpl> identifiers = new ArrayList<AS07DMSIdentifierSetImpl>(); // TODO
		List<AS07CoreDMSDeviceObjectsImpl> devices = Collections.synchronizedList(new ArrayList<AS07CoreDMSDeviceObjectsImpl>()); // TODO
		String audioTrackLayoutComment = "test audio track layout comment";
		String audioTrackSecondaryLanguage = "test audio track secondary language";
		String audioTrackPrimaryLanguage = "test audio track primary languag";
		String natureOfOrganization = "test nature of org";
		String secondaryTitle = "test secondary title";
		String workingTitle = "test working title";
		String ident1Comment = "Identifier Comment 1";
		String ident1Role = "Role 1";
		String ident1Type = "Type 1";
		String ident1Value = "Value 1";
		
		String outputFile = "output_mxf_test.mxf";
		service.readFile();
		Assert.assertTrue(service.hasAS07CoreDMSFramework());
		
		AS07CoreDMSFramework dms = service.getAS07CoreDMSFramework();

		Assert.assertNotNull(dms);
		
		dms.setShimName(shimName);
		dms.setResponsibleOrganizationName(responsibleOrganizationName);
		dms.setResponsibleOrganizationCode(responsibleOrganizationCode);
		dms.setPictureFormat(pictureFormat);
		dms.setCaptions(captions);
		dms.setIntendedAFD(intendedAFD);
		dms.setAudioTrackLayoutComment(audioTrackLayoutComment);
		dms.setAudioTrackSecondaryLanguage(audioTrackSecondaryLanguage);
		dms.setAudioTrackPrimaryLanguage(audioTrackPrimaryLanguage);
		dms.setNatureOfOrganization(natureOfOrganization);
		dms.setSecondaryTitle(secondaryTitle);
		dms.setWorkingTitle(workingTitle);
		
		AS07DMSIdentifierSetImpl ident1 = new AS07DMSIdentifierSetImpl();
		ident1.setIdentifierComment("Identifier Comment 1");
		ident1.setIdentifierRole("Role 1");
		ident1.setIdentifierType("Type 1");
		ident1.setIdentifierValue("Value 1");
		dms.addIdentifiers(ident1);
		int identifierCount = dms.getIdentifiersSize();
				

		service.writeFile(outputFile, dms);
		
		File file = new File(outputFile);

		Assert.assertTrue(file.exists());		
		Assert.assertTrue(file.length()>0);
				
		
		MXFService service2 = new MXFServiceImpl(outputFile);
		service2.readFile();
		Assert.assertTrue(service2.hasAS07CoreDMSFramework());
		

		AS07CoreDMSFramework dms2 = service2.getAS07CoreDMSFramework();

		Assert.assertEquals(dms2.getShimName(),shimName);
		Assert.assertEquals(dms2.getResponsibleOrganizationName(),responsibleOrganizationName);
		Assert.assertEquals(dms2.getResponsibleOrganizationCode(),responsibleOrganizationCode);
		Assert.assertEquals(dms2.getPictureFormat(),pictureFormat);
		Assert.assertEquals(dms2.getCaptions(),captions);
		Assert.assertEquals(dms2.getIntendedAFD(),intendedAFD);
		Assert.assertEquals(dms2.getAudioTrackLayoutComment(),audioTrackLayoutComment);
		Assert.assertEquals(dms2.getAudioTrackSecondaryLanguage(),audioTrackSecondaryLanguage);
		Assert.assertEquals(dms2.getAudioTrackPrimaryLanguage(),audioTrackPrimaryLanguage);
		Assert.assertEquals(dms2.getNatureOfOrganization(),natureOfOrganization);
		Assert.assertEquals(dms2.getSecondaryTitle(),secondaryTitle);
		Assert.assertEquals(dms2.getWorkingTitle(),workingTitle);
		Assert.assertEquals(dms2.getIdentifiersSize(), identifierCount);
		AS07DMSIdentifierSetImpl ident2 = dms2.getIdentifiers().get(identifierCount - 1);
		Assert.assertTrue(ident2.getIdentifierValue().equals(ident1Value));
		Assert.assertTrue(ident2.getIdentifierComment().equals(ident1Comment));
		Assert.assertTrue(ident2.getIdentifierRole().equals(ident1Role));
		Assert.assertTrue(ident2.getIdentifierType().equals(ident1Type));
			
		
		Preface preface = service2.getPreface();
		
		EmbARCIdentification identificationSingleton = EmbARCIdentification.getInstance();
		
		boolean foundIdent = false;
		for(Identification ident : preface.getIdentifications()) {
			if(ident.getGenerationID().equals(identificationSingleton.getIdentification().getGenerationID())) {
				foundIdent = true;
				break;
			}
		}
		Assert.assertTrue(foundIdent);
		Assert.assertTrue(dms2.getLinkedGenerationID().equals(identificationSingleton.getIdentification().getGenerationID()));
				
	}
	@Test
	public void CanWriteMxfFile() throws IOException {
		String outputFile = "output_mxf_test.mxf";
		service.readFile();
		Assert.assertTrue(service.hasAS07CoreDMSFramework());
		service.writeFile(outputFile, (AS07CoreDMSFramework) null);
		File file = new File(outputFile);

		Assert.assertTrue(file.exists());		
		Assert.assertTrue(file.length()>0);
		
		MXFService service2 = new MXFServiceImpl(outputFile);
		service2.readFile();
		//service2.readFile();
		Assert.assertTrue(service2.hasAS07CoreDMSFramework());
		
		//Assert.assertTrue(file.delete());

		MXFService service4 = new MXFServiceImpl(filePath);
		MXFService service3 = new MXFServiceImpl(outputFile);
		
		MXFFile mxfFile2 = service3.getFile();
		MXFFile mxfFile = service4.getFile();
		
		PrimerPack pp1 = mxfFile.getHeaderPartition().readHeaderMetadata().getPrimerPack();
		PrimerPack pp2 = mxfFile2.getHeaderPartition().readHeaderMetadata().getPrimerPack();
		
		Set<LocalTagEntry> tags = pp1.getLocalTagEntryBatch();
		Set<LocalTagEntry> tags2 = pp2.getLocalTagEntryBatch();
		Set<LocalTagEntry> missingTags = new HashSet<LocalTagEntry>();
		for(LocalTagEntry tag2 : tags2) {
			boolean found = false;
			for(LocalTagEntry tag1 : tags) {
				if(tag1.getLocalTag()==tag2.getLocalTag()) {
					found = true;
					break;
				}
			}
			if(found == false) {
				missingTags.add(tag2);
			}
		}
		for(LocalTagEntry missing : missingTags) {
			System.out.println(missing.toString());
		}
		
	}
	@Test
	public void GetTrackDescriptors() {

		service.readFile();
		MXFFileDescriptorResult descriptors = service.getDescriptors();
		Assert.assertTrue(descriptors.getCDCIDescriptor().size()>0);
		Assert.assertTrue(descriptors.getWavePCMDescriptors().size()>0);
	}
	@Test
	public void GetsAs07Core() throws FileNotFoundException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File srcFile = new File(classLoader.getResource("as07_sample2-jpeg2000-1.1.mxf").getFile());
        
        filePath = srcFile.getAbsolutePath();
        service = new MXFServiceImpl(filePath);
		
        
        AS07CoreDMSFramework returned = service.getAS07CoreDMSFramework();
       
        
        Assert.assertFalse(returned==null);
	}

	@Test
	public void GetsAs07TD() throws FileNotFoundException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File srcFile = new File(classLoader.getResource("as07_sample2-jpeg2000-1.1.mxf").getFile());
        
        filePath = srcFile.getAbsolutePath();
        service = new MXFServiceImpl(filePath);
		
        
        List<AS07GspTdDMSFrameworkImpl> returned = service.getAS07GspTdDMSFramework();
       
        
        Assert.assertFalse(returned.isEmpty());
        
        List<AS07GSPDMSObject> returnedObjects = service.getAS07GSPDMSObjects();

        Assert.assertFalse(returnedObjects.isEmpty());
	}

	@Test
	public void GetsAs07BDObjects() throws FileNotFoundException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File srcFile = new File(classLoader.getResource("as07_sample2-jpeg2000-1.1.mxf").getFile());
        
        filePath = srcFile.getAbsolutePath();
        service = new MXFServiceImpl(filePath);
		
        
        List<AS07GspBdDMSFrameworkImpl> returned = service.getAS07GspBdDMSFramework();
       
        
        Assert.assertFalse(returned.isEmpty());
        
        List<AS07GSPDMSObject> returnedObjects = service.getAS07GSPDMSObjects();

        Assert.assertFalse(returnedObjects.isEmpty());
	}
	
	@Test
	public void GetMXFMetadata() throws FileNotFoundException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File srcFile = new File(classLoader.getResource("as07_sample2-jpeg2000-1.1.mxf").getFile());
        
        filePath = srcFile.getAbsolutePath();
        service = new MXFServiceImpl(filePath);
		
        
        FileInformation<MXFMetadata> returned = service.getMetadata();
        
        returned.getFileData().getCoreColumns().forEach((k,v) -> {
        	
        	System.out.println("Current KEY: " + k.getDisplayName() + "Current Value !!!! ->  " + v.getCurrentValue());
        });
        
        
        Assert.assertFalse(returned==null);
        MXFMetadata metadata = returned.getFileData();
       

        Assert.assertNotNull(metadata.getFileDescriptors());
        Assert.assertTrue(metadata.getFileDescriptors().getCDCIDescriptor().size()>0);
        Assert.assertTrue(metadata.getFileDescriptors().getWavePCMDescriptors().size()>0);
        
        Assert.assertFalse(metadata.getCoreColumns().isEmpty());
        HashMap<MXFColumn, MetadataColumnDef> coreCols = metadata.getCoreColumns();
        System.out.println("SoundCount" + metadata.getSoundTrackCount());
        System.out.println("PictureCount" + metadata.getPictureTrackCount());
        System.out.println("OtherCount" + metadata.getOtherTrackCount());
        for(MXFColumn col : coreCols.keySet())
        {
        	System.out.println(col.getDisplayName() + ":" + coreCols.get(col).toString());
        }
        Assert.assertFalse(metadata.getTDColumns().isEmpty());
        HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> tdCols = metadata.getTDColumns();
        for(String key : tdCols.keySet())
        {
        	for(MXFColumn col : tdCols.get(key).keySet()) {
            	System.out.println(col.getDisplayName() + ":" + tdCols.get(key).get(col).toString());
        	}
        }
        Assert.assertFalse(metadata.getBDColumns().isEmpty());
        
        HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> bdCols = metadata.getBDColumns();
        for(String key : bdCols.keySet())
        {
        	for(MXFColumn col : bdCols.get(key).keySet()) {
            	System.out.println(col.getDisplayName() + ":" + bdCols.get(key).get(col).toString());
        	}
        }
	}

	@Test
	public void GetIdentification() {
		EmbARCIdentification identificationSingleton = EmbARCIdentification.getInstance();
		
		System.out.println(identificationSingleton.identification.toString());

		EmbARCIdentification identificationSingleton2 = EmbARCIdentification.getInstance();
		System.out.println(identificationSingleton2.identification.toString());
	}
	
	
}
