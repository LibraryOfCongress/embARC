package standalone;

import com.portalmedia.embarc.parser.BytesToStringHelper;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MixedAsciiTest {
    
    @Test
    public void testMixedAsciiNonAsciiDisplay() {
        // Test with mixed ASCII and non-ASCII characters
        byte[] mixedBytes = {
            'A', 'B', 'C',  // ASCII
            0x00, (byte)0xA9, (byte)0xAE,  // Non-ASCII
            'D', 'E', 'F'   // ASCII
        };
        
        String result = BytesToStringHelper.toString(mixedBytes);
        System.out.println("Mixed result: " + result);
        
        // Current behavior - all non-ASCII shows as hex
        assertTrue(result.contains("HEX"));
    }
}
