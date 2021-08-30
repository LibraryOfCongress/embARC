package DPXTests;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.awt.*;
import java.awt.image.*;


import org.junit.Assert;
import org.junit.Test;

import com.portalmedia.embarc.database.DBService;
import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.FileFormat;
import com.portalmedia.embarc.parser.FileFormatDetection;
import com.portalmedia.embarc.parser.MetadataColumn;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;
import com.portalmedia.embarc.parser.dpx.DPXMetadata;
import com.portalmedia.embarc.parser.dpx.DPXService;
import com.portalmedia.embarc.validation.IsValidIntRule;


public class DPXTests {
	
	//String inputFile = "./src/tests/resources/Oxberry Cinescan/Reb_0000047.dpx";

	String inputFile = "./src/tests/resources/00256.dpx";
	String outputFile = "./src/tests/resources/output/00256_out.tiff";
	@Test
	public void serializeFileInformationViewModel() {

		DPXFileInformation f = DPXFileListHelper.createDPXFileInformation(inputFile);
		
		DPXFileInformationViewModel fivm = new DPXFileInformationViewModel();
		fivm.setProp("name", f.getName());
		fivm.setProp("path", f.getPath());
		fivm.setId(f.getId());

		DBService<DPXFileInformationViewModel> dbService = new DBService<DPXFileInformationViewModel>(DPXFileInformationViewModel.class);
		dbService.dropCollection();
		dbService = new DBService<DPXFileInformationViewModel>(DPXFileInformationViewModel.class);
		dbService.add(fivm);
		
		DPXFileInformationViewModel f2 = dbService.get(1);
		
		dbService.update(f2);
	}
	/*
	 * @Test public void runDPXTest() throws Exception { DPXService service = new
	 * DPXService(inputFile); DPXMetadata d = service.readFile();
	 * 
	 * LinkedHashMap<DPXColumnEnum,MetadataColumn> md = d.getMetadataHashMap();
	 * for(DPXColumnEnum key : md.keySet()) { MetadataColumn column = md.get(key);
	 * System.out.println( column.getName() + ": " + column.toString()); }
	 * 
	 * }
	 * 
	 * @Test public void writeDPXTest() throws Exception { DPXService service = new
	 * DPXService(inputFile); DPXMetadata d = service.readFile();
	 * LinkedHashMap<DPXColumnEnum,MetadataColumn> md = d.getMetadataHashMap();
	 * for(DPXColumnEnum key : md.keySet()) { MetadataColumn column = md.get(key);
	 * System.out.println( column.getName() + ": " + column.toString()); }
	 * 
	 * //service.writeFile(d, inputFile, outputFile);
	 * 
	 * } static BufferedImage toImage(byte[] data, int w, int h) { DataBuffer buffer
	 * = new DataBufferByte(data, data.length); WritableRaster raster =
	 * Raster.createInterleavedRaster(buffer, w, 1556, 2048, 1, new int[]{0}, null);
	 * ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY); ColorModel cm =
	 * new ComponentColorModel(cs, false, true, Transparency.OPAQUE,
	 * DataBuffer.TYPE_BYTE); return new BufferedImage(cm, raster, false, null); }
	 * 
	 * @Test public void readWriteReadDPXTest() throws Exception { DPXService
	 * service = new DPXService(inputFile); DPXMetadata d = service.readFile();
	 * LinkedHashMap<DPXColumnEnum,MetadataColumn> md = d.getMetadataHashMap();
	 * for(DPXColumnEnum key : md.keySet()) { MetadataColumn column = md.get(key);
	 * System.out.println( column.getName() + ": " + column.toString()); }
	 * 
	 * //service.writeFile(d, inputFile, outputFile);
	 * 
	 * DPXService service2 = new DPXService(inputFile); DPXMetadata d2 =
	 * service2.readFile(); LinkedHashMap<DPXColumnEnum,MetadataColumn> md2 =
	 * d2.getMetadataHashMap();
	 * 
	 * 
	 * for(DPXColumnEnum key : md.keySet()) { MetadataColumn column = md.get(key);
	 * MetadataColumn column2 = md2.get(key);
	 * Assert.assertArrayEquals(column.getCurrentValue(),
	 * column2.getCurrentValue()); System.out.println( column.toString() + ": " +
	 * column2.toString()); }
	 * 
	 * }
	 * 
	 * @Test public void testEnumValues() { for(DPXColumnEnum d :
	 * DPXColumnEnum.values()) { System.out.println(d.getDisplayName() + ":" +
	 * d.getSection()); } }
	 * 
	 * @Test public void testFileFormatDetection() { FileFormat format =
	 * FileFormatDetection.getFileFormat(inputFile);
	 * 
	 * Assert.assertEquals(format, FileFormat.DPX); }
	 * 
	 * @Test public void displayImage() {
	 * 
	 * Path tempPath; try { tempPath = Files.createTempFile("ffplay", "");
	 * 
	 * try (InputStream in = getClass().getResourceAsStream("/resources/ffplay")) {
	 * Files.copy(in, tempPath, StandardCopyOption.REPLACE_EXISTING); }
	 * 
	 * PosixFileAttributeView view = Files.getFileAttributeView(tempPath,
	 * PosixFileAttributeView.class); if (view != null) { Set<PosixFilePermission>
	 * perms = view.readAttributes().permissions(); if
	 * (perms.add(PosixFilePermission.OWNER_EXECUTE)) { view.setPermissions(perms);
	 * } } List<String> params = java.util.Arrays.asList(tempPath.toString(),
	 * inputFile); ProcessBuilder b = new ProcessBuilder(params); try { Process p =
	 * b.start();
	 * 
	 * int exitCode = p.waitFor(); } catch (IOException | InterruptedException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * } catch (IOException e1) { // TODO Auto-generated catch block
	 * e1.printStackTrace(); }
	 * 
	 * }
	 * 
	 * @Test public void testSerializingData() throws Exception { DPXService
	 * service2 = new DPXService(inputFile); DPXMetadata object =
	 * service2.readFile(); String filename = "file.ser";
	 * 
	 * // Serialization try { //Saving of object in a file FileOutputStream file =
	 * new FileOutputStream(filename); ObjectOutputStream out = new
	 * ObjectOutputStream(file);
	 * 
	 * // Method for serialization of object out.writeObject(object);
	 * 
	 * out.close(); file.close();
	 * 
	 * System.out.println("Object has been serialized");
	 * 
	 * }
	 * 
	 * catch(IOException ex) { System.out.println("IOException is caught");
	 * System.out.println(ex.toString()); ex.printStackTrace(); }
	 * 
	 * 
	 * DPXMetadata object1 = null;
	 * 
	 * // Deserialization try { // Reading the object from a file FileInputStream
	 * file = new FileInputStream(filename); ObjectInputStream in = new
	 * ObjectInputStream(file);
	 * 
	 * // Method for deserialization of object object1 =
	 * (DPXMetadata)in.readObject();
	 * 
	 * in.close(); file.close();
	 * 
	 * System.out.println("Object has been deserialized "); }
	 * 
	 * catch(IOException ex) { System.out.println("IOException is caught");
	 * System.out.println(ex.getMessage()); }
	 * 
	 * catch(ClassNotFoundException ex) {
	 * System.out.println("ClassNotFoundException is caught"); } }
	 */

	@Test
	public void TestValidInt() {
		Assert.assertTrue(new IsValidIntRule().isValid("0"));
		Assert.assertTrue(new IsValidIntRule().isValid("2147483647"));
		Assert.assertTrue(new IsValidIntRule().isValid("4294967295"));
		Assert.assertFalse(new IsValidIntRule().isValid("-1"));
		Assert.assertFalse(new IsValidIntRule().isValid("4294967296"));
	}
	
}
