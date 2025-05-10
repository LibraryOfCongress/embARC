package standalone;

import com.portalmedia.embarc.parser.BytesToStringHelper;

public class MixedAsciiDemo {
    public static void main(String[] args) {
        // Test with mixed ASCII and non-ASCII characters
        byte[] mixedBytes = {
            'A', 'B', 'C',  // ASCII
            0x00, (byte)0xA9, (byte)0xAE,  // Non-ASCII
            'D', 'E', 'F'   // ASCII
        };
        
        String result = BytesToStringHelper.toString(mixedBytes);
        System.out.println("Mixed ASCII/non-ASCII output: " + result);
        
        // Test with ASCII only
        byte[] asciiBytes = {'H', 'e', 'l', 'l', 'o'};
        System.out.println("ASCII only output: " + BytesToStringHelper.toString(asciiBytes));
        
        // Test with non-ASCII only
        byte[] nonAsciiBytes = {0x00, (byte)0xA9, (byte)0xAE};
        System.out.println("Non-ASCII only output: " + BytesToStringHelper.toString(nonAsciiBytes));
    }
}
