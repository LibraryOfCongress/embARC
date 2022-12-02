package DPXTests;

import org.junit.Assert;
import org.junit.Test;

import com.portalmedia.embarc.database.DBService;
import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;
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
	@Test
	public void TestValidInt() {
		Assert.assertTrue(new IsValidIntRule().isValid("0"));
		Assert.assertTrue(new IsValidIntRule().isValid("2147483647"));
		Assert.assertTrue(new IsValidIntRule().isValid("4294967295"));
		Assert.assertFalse(new IsValidIntRule().isValid("-1"));
		Assert.assertFalse(new IsValidIntRule().isValid("4294967296"));
	}
	
}
