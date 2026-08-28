package com.portalmedia.embarc.parser.mxf;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared escaping/splitting logic for IdentifierSetHelper and DeviceSetHelper, which both encode a
 * list of records as fields joined by a run of 4 commas, records joined by a run of 4 slashes. A
 * field value containing a run of 4 (or more) of the delimiter character used to be indistinguishable
 * from the delimiter itself, corrupting the split. Every backslash, comma, and slash in a raw field
 * value is now backslash-escaped before joining, so an unescaped run of the delimiter character can
 * only be a real delimiter -- single, unescaped commas/slashes (i.e. anything shorter than the 4-char
 * run) are left untouched on read for compatibility with files written before this escaping existed.
 */
final class DelimitedListCodec {
	private DelimitedListCodec() {}

	static String escapeField(String value) {
		if (value == null) return "";
		StringBuilder sb = new StringBuilder(value.length());
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '\\' || c == ',' || c == '/') sb.append('\\');
			sb.append(c);
		}
		return sb.toString();
	}

	private static boolean isEscapable(char c) {
		return c == '\\' || c == ',' || c == '/';
	}

	static String unescapeField(String value) {
		StringBuilder sb = new StringBuilder(value.length());
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '\\' && i + 1 < value.length() && isEscapable(value.charAt(i + 1))) {
				sb.append(value.charAt(++i));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Splits on runs of exactly `runLength` (or more) unescaped occurrences of `delimiter`.
	 * Backslash-escaped characters are skipped over (not unescaped here) so this can be applied at
	 * the record level (slash) and then again at the field level (comma) on each resulting piece,
	 * with a single final {@link #unescapeField} pass on each leaf field. A backslash NOT followed
	 * by one of the escapable characters is not an escape sequence (e.g. a raw "C:\path" typed
	 * directly into a CSV cell): leave it as an ordinary character rather than swallowing it, so
	 * pre-existing, un-escaped text with incidental backslashes doesn't silently lose them.
	 */
	static List<String> splitOnDelimiterRun(String value, char delimiter, int runLength) {
		List<String> parts = new ArrayList<String>();
		int start = 0;
		int i = 0;
		int length = value.length();
		while (i < length) {
			char c = value.charAt(i);
			if (c == '\\' && i + 1 < length && isEscapable(value.charAt(i + 1))) {
				i += 2;
				continue;
			}
			if (c == delimiter) {
				int runEnd = i;
				while (runEnd < length && value.charAt(runEnd) == delimiter) runEnd++;
				if (runEnd - i >= runLength) {
					parts.add(value.substring(start, i));
					i += runLength;
					start = i;
					continue;
				}
				i = runEnd;
				continue;
			}
			i++;
		}
		parts.add(value.substring(start));
		return parts;
	}
}
