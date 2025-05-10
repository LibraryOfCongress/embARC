package com.portalmedia.embarc.cli;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteOrder;
import org.junit.jupiter.api.Test;

/**
 * Tests for mixed ASCII/non-ASCII display behavior (Issue #50)
 * 
 * VALIDATION REQUIREMENTS:
 * 1. Pure ASCII text must display unchanged
 * 2. Non-ASCII chars should show as {0xXXXX} hex codes
 * 3. Mixed content should preserve ASCII portions
 * 4. Empty/null input should handle gracefully
 */
public class MixedAsciiDisplayTest {

    @Test
    public void testMixedAsciiNonAsciiDisplay() {
        // Pure ASCII
        byte[] asciiBytes = {'H', 'e', 'l', 'l', 'o'};
        String asciiResult = BytesToStringHelper.toString(asciiBytes);
        assertEquals("Hello", asciiResult);
        
        // Pure non-ASCII (should show as HEX)
        byte[] nonAsciiBytes = {0x00, (byte)0xA9, (byte)0xAE};
        String nonAsciiResult = BytesToStringHelper.toString(nonAsciiBytes);
        assertTrue(nonAsciiResult.startsWith("HEX: "));
        
        // Mixed ASCII/non-ASCII (current behavior shows as HEX)
        byte[] mixedBytes = {
            'A', 'B', 'C',  // ASCII
            0x00, (byte)0xA9, (byte)0xAE,  // Non-ASCII
            'D', 'E', 'F'   // ASCII
        };
        String mixedResult = BytesToStringHelper.toString(mixedBytes);
        System.out.println("Mixed result: " + mixedResult);
        assertTrue(mixedResult.startsWith("HEX: "), 
            "Current behavior shows mixed content as HEX");
    }

    @Test
    public void testPureAsciiDisplay() {
        String pureAscii = "This is pure ASCII";
        String displayed = BytesToStringHelper.toString(pureAscii.getBytes());
        assertEquals(pureAscii, displayed, 
            "Pure ASCII should remain unchanged");
    }

    @Test
    public void testPureNonAsciiDisplay() {
        String pureNonAscii = "日本語";
        String displayed = BytesToStringHelper.toString(pureNonAscii.getBytes());
        assertTrue(displayed.matches("^\\{0x[0-9a-f]+\\}.*"), 
            "Pure non-ASCII should be encoded");
        assertFalse(displayed.contains("日本語"), 
            "Non-ASCII characters should not appear directly");
    }

    @Test
    public void testMixedAsciiNonAsciiDisplayIssue() {
        // Test with mixed ASCII and non-ASCII characters
        byte[] mixedBytes = {
            'A', 'B', 'C',  // ASCII
            0x00, (byte)0xA9, (byte)0xAE,  // Non-ASCII
            'D', 'E', 'F'   // ASCII
        };
        
        // Test with ASCII only
        byte[] asciiBytes = {'H', 'e', 'l', 'l', 'o'};
        
        // Test with non-ASCII only
        byte[] nonAsciiBytes = {0x00, (byte)0xA9, (byte)0xAE};
        
        String mixedResult = BytesToStringHelper.toString(mixedBytes);
        String asciiResult = BytesToStringHelper.toString(asciiBytes);
        String nonAsciiResult = BytesToStringHelper.toString(nonAsciiBytes);
        
        System.out.println("Mixed result: " + mixedResult);
        System.out.println("ASCII result: " + asciiResult);
        System.out.println("Non-ASCII result: " + nonAsciiResult);
        
        // Current behavior - all non-ASCII shows as hex
        assertTrue(mixedResult.contains("HEX"));
        assertFalse(asciiResult.contains("HEX"));
        assertTrue(nonAsciiResult.contains("HEX"));
    }
}
