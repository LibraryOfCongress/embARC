package MXFTests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;


import org.junit.Assert;
import org.junit.Test;

import com.portalmedia.embarc.parser.FileFormatDetection;
import com.portalmedia.embarc.parser.mxf.MXFService;
import com.portalmedia.embarc.parser.mxf.MXFServiceImpl;

import tv.amwa.maj.io.mxf.HeaderMetadata;
import tv.amwa.maj.io.mxf.HeaderPartitionPack;
import tv.amwa.maj.io.mxf.MXFFactory;
import tv.amwa.maj.io.mxf.MXFFile;
import tv.amwa.maj.io.mxf.RandomIndexPack;
import tv.amwa.maj.model.ContentStorage;
import tv.amwa.maj.model.MaterialPackage;
import tv.amwa.maj.model.Track;


public class MXFFormatDetectionTests {
	@Test
	public void TestContainsAS07DMSIdentifierSet() throws FileNotFoundException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File srcFile = new File(classLoader.getResource("as07_sample1-gf-unc-3.1.mxf").getFile());
        
        Assert.assertNotNull(srcFile);
        
        MXFService mxfService = new MXFServiceImpl(srcFile.getAbsolutePath());
        
        boolean isMxf = mxfService.hasAS07DMSIdentifierSet();
        
        Assert.assertTrue(isMxf);
		
	}
	@Test
	public void TestContainsAS07CoreDMSFramework() throws FileNotFoundException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File srcFile = new File(classLoader.getResource("as07_sample1-gf-unc-3.1.mxf").getFile());
        
        Assert.assertNotNull(srcFile);
        
        MXFService mxfService = new MXFServiceImpl(srcFile.getAbsolutePath());
        
        boolean isMxf = mxfService.hasAS07CoreDMSFramework();
        
        Assert.assertTrue(isMxf);
		
	}
	@Test
	public void TestNotContainsAS07CoreDMSFramework() throws FileNotFoundException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File srcFile = new File(classLoader.getResource("NTSC_SD_DV25_colorbar.mxf").getFile());
        
        Assert.assertNotNull(srcFile);
        
        MXFService mxfService = new MXFServiceImpl(srcFile.getAbsolutePath());
        
        boolean isMxf = mxfService.hasAS07CoreDMSFramework();
        
        Assert.assertFalse(isMxf);
		
	}
	@Test
	public void TestNotMxfFile() {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File srcFile = new File(classLoader.getResource("00255.dpx").getFile());
        
        Assert.assertNotNull(srcFile);
        
        boolean isMxf = FileFormatDetection.isMXF(srcFile.getAbsolutePath());
        
        Assert.assertFalse(isMxf);
		
	}

	@Test
	public void TestNotAcceptableMxfFile() {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File srcFile = new File(classLoader.getResource("00255.dpx").getFile());
        
        Assert.assertNotNull(srcFile);
        
        boolean isMxf = FileFormatDetection.isMXF(srcFile.getAbsolutePath());
        
        Assert.assertFalse(isMxf);
		
	}

	@Test
	public void TestValidMxf200() {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File srcFile = new File(classLoader.getResource("freeMXF-mxf1a.mxf").getFile());
        
        Assert.assertNotNull(srcFile);
        
        boolean isMxf = FileFormatDetection.isMXF(srcFile.getAbsolutePath());
        
        Assert.assertTrue(isMxf);
	}
	@Test
	public void TestValidMxf783() {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File srcFile = new File(classLoader.getResource("fmt-783-signature-id-792.mxf").getFile());
        
        Assert.assertNotNull(srcFile);
        
        boolean isMxf = FileFormatDetection.isMXF(srcFile.getAbsolutePath());
        
        Assert.assertTrue(isMxf);
	}
	@Test
	public void TestValidMxf900() {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File srcFile = new File(classLoader.getResource("fmt-790-signature-id-799.mxf").getFile());
        
        Assert.assertNotNull(srcFile);
        
        boolean isMxf = FileFormatDetection.isMXF(srcFile.getAbsolutePath());
        
        Assert.assertTrue(isMxf);
	}
	
	@Test
	public void TestValidOperationalPattern() {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File srcFile = new File(classLoader.getResource("as07_sample1-gf-unc-3.1.mxf").getFile());

		MXFFile mxfFile = MXFFactory.readPartitions(srcFile.getAbsolutePath());
		
		
		System.out.println(mxfFile.toString());
		
		Assert.assertNotNull(mxfFile);

		RandomIndexPack rip = mxfFile.getRandomIndexPack();
		Assert.assertNotNull(rip);
		
		HeaderPartitionPack hpp = mxfFile.getHeaderPartition().getPartitionPack();

		HeaderMetadata fromTheHeader = mxfFile.getHeaderPartition().readHeaderMetadata();
		ContentStorage cs = fromTheHeader.getPreface().getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packages = cs.getPackages();
		
		for(tv.amwa.maj.model.Package p : packages) {
			if(p instanceof MaterialPackage) {
				System.out.println("FOUND IT");
				List<? extends Track> tracks = p.getPackageTracks();
				
				for(@SuppressWarnings("unused") Track t : tracks) {
					//System.out.println(t.toString());
					
				}
			}
		}
		

		System.out.println(hpp.getOperationalPattern());
		
		//System.out.println(hpp.getOperationalPattern().toString());
		boolean valid = FileFormatDetection.validMxfOperationalPattern(hpp.getOperationalPattern().toString());
		
		Assert.assertTrue(valid);
	}

}
