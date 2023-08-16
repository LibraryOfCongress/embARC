package com.portalmedia.embarc.gui.helper;

import java.io.File;

/**
 * Cleans input path string to avoid path manipulation and other security issues
 * Code borrowed from StackExchange #103884
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2021-08-27
 */
public class CleanInputPathHelper {
	public static String cleanString(String aString) {
		if (aString == null) return null;
		String cleanString = "";
		for (int i = 0; i < aString.length(); ++i) {
			cleanString += cleanChar(aString.charAt(i));
		}
		return cleanString;
	}

	private static char cleanChar(char aChar) {
		// 0 - 9
		for (int i = 48; i < 58; ++i) {
			if (aChar == i) return (char) i;
		}
		
		// 'A' - 'Z'
		for (int i = 65; i < 91; ++i) {
			if (aChar == i) return (char) i;
		}
		
		// 'a' - 'z'
		for (int i = 97; i < 123; ++i) {
			if (aChar == i) return (char) i;
		}
		// handle file separator
		char fileSeparator = File.separatorChar;
		if (aChar == fileSeparator) return fileSeparator;
		// other valid characters
		switch (aChar) {
			case '/':
			return '/';
			case '.':
			return '.';
			case '-':
			return '-';
			case '_':
			return '_';
			case ' ':
			return ' ';
			case ':':
			return ':';
		}
		return '%';
	}
}

