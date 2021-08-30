package DPXTests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class Launcher {

	public static void main(String[] args) {
		
		Result result = JUnitCore.runClasses(DPXTests.class);
		
	    for (Failure failure : result.getFailures()) {
	      System.out.println(failure.toString());
	    }
		
		
	}
	
}
