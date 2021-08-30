package com.portalmedia.embarc.parser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Helper method to turn bytes arrays into types and then into strings
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class BytesToStringHelper {
	private static String[] getIntArray(byte[] value, ByteOrder byteOrder) {
		final String[] toReturn = new String[value.length / 4];
		int returnIndex = 0;
		for (int i = 0; i < value.length; i = i + 4) {
			toReturn[returnIndex] = Long.toString(ByteBuffer.wrap(value).order(byteOrder).getInt() & 0xffffffffL);
			returnIndex++;
		}
		return toReturn;
	}

	private static String[] getShortArray(byte[] value, ByteOrder byteOrder) {
		final String[] toReturn = new String[value.length / 2];
		int returnIndex = 0;
		for (int i = 0; i < value.length; i = i + 2) {
			toReturn[returnIndex] = Integer.toString(ByteBuffer.wrap(value).order(byteOrder).getShort() & 0xffff);
			returnIndex++;
		}
		return toReturn;
	}

	private static String toByteArrayString(byte[] value) {
		String toReturn = "[";
		final byte[] valueArray = value;
		for (int i = 0; i < valueArray.length; i++) {
			toReturn += Byte.toString(valueArray[i]);
			if (i != valueArray.length - 1) {
				toReturn += ",";
			}
		}
		toReturn += "]";
		return toReturn;
	}

	public static String toByteString(byte[] value) {
		return Byte.toString(value[0]);
	}

	public static String toFloatString(byte[] value, ByteOrder byteOrder) {
		return Float.toString(ByteBuffer.wrap(value).order(byteOrder).getFloat());
	}

	public static String toIntArrayString(byte[] value, ByteOrder byteOrder) {
		String toReturn = "[";
		final String[] valueArray = getIntArray(value, byteOrder);
		for (int i = 0; i < valueArray.length; i++) {
			toReturn += valueArray[i];
			if (i != valueArray.length - 1) {
				toReturn += ",";
			}
		}
		toReturn += "]";
		return toReturn;
	}

	public static String toIntString(byte[] value, ByteOrder byteOrder) {
		return Long.toString(ByteBuffer.wrap(value).order(byteOrder).getInt() & 0xffffffffL);
	}

	public static String toLongString(byte[] value, ByteOrder byteOrder) {
		return Long.toString(ByteBuffer.wrap(value).order(byteOrder).getLong());
	}

	public static String toShortArrayString(byte[] value, ByteOrder byteOrder) {
		String toReturn = "[";
		final String[] valueArray = getShortArray(value, byteOrder);
		for (int i = 0; i < valueArray.length; i++) {
			toReturn += valueArray[i];
			if (i != valueArray.length - 1) {
				toReturn += ",";
			}
		}
		toReturn += "]";
		return toReturn;
	}

	public static String toShortString(byte[] value, ByteOrder byteOrder) {
		return Integer.toString(ByteBuffer.wrap(value).order(byteOrder).getShort() & 0xffff);
	}

	public static String toString(byte[] value) {
		return new String(value);
	}

	public static String toTypedString(Class<?> type, byte[] value, ByteOrder byteOrder) {
		if (type == byte[].class) {
			return toByteArrayString(value);
		} else if (type == byte.class) {
			return toByteString(value);
		} else if (type == float.class) {
			return toFloatString(value, byteOrder);
		} else if (type == int[].class) {
			return toIntArrayString(value, byteOrder);
		} else if (type == int.class) {
			return toIntString(value, byteOrder);
		} else if (type == long.class) {
			return toLongString(value, byteOrder);
		} else if (type == short[].class) {
			return toShortArrayString(value, byteOrder);
		} else if (type == short.class) {
			return toShortString(value, byteOrder);
		} else if (type == String.class) {
			return toString(value);
		} else {
			return "INVALID TYPE";
		}
	}
}
