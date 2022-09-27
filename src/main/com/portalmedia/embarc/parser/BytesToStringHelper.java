package com.portalmedia.embarc.parser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Helper method to turn bytes arrays into types and then into strings
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class BytesToStringHelper {
	private static String[] getStringIntArray(byte[] value, ByteOrder byteOrder) {
		final String[] toReturn = new String[value.length / 4];
		int returnIndex = 0;
		for (int i = 0; i < value.length; i = i + 4) {
			toReturn[returnIndex] = Long.toString(ByteBuffer.wrap(value).order(byteOrder).getInt() & 0xffffffffL);
			returnIndex++;
		}
		return toReturn;
	}
	private static Long[] getIntArray(byte[] value, ByteOrder byteOrder) {
		final Long[] toReturn = new Long[value.length / 4];
		int returnIndex = 0;
		for (int i = 0; i < value.length; i = i + 4) {
			toReturn[returnIndex] = getLong(value, byteOrder);
			returnIndex++;
		}
		return toReturn;
	}
	
	private static String[] getStringShortArray(byte[] value, ByteOrder byteOrder) {
		final String[] toReturn = new String[value.length / 2];
		int returnIndex = 0;
		for (int i = 0; i < value.length; i = i + 2) {
			toReturn[returnIndex] = Integer.toString(ByteBuffer.wrap(value).order(byteOrder).getShort() & 0xffff);
			returnIndex++;
		}
		return toReturn;
	}

	private static Integer[] getShortArray(byte[] value, ByteOrder byteOrder) {
		final Integer[] toReturn = new Integer[value.length / 2];
		int returnIndex = 0;
		for (int i = 0; i < value.length; i = i + 2) {
			toReturn[returnIndex] = ByteBuffer.wrap(value).order(byteOrder).getShort() & 0xffff1;
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
		return Float.toString(getFloat(value, byteOrder));
	}
	
	private static Float getFloat(byte[] value, ByteOrder byteOrder) {
		return ByteBuffer.wrap(value).order(byteOrder).getFloat();
	}

	public static String toIntArrayString(byte[] value, ByteOrder byteOrder) {
		String toReturn = "[";
		final String[] valueArray = getStringIntArray(value, byteOrder);
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
		return Long.toString(getLong(value, byteOrder));
	}

	public static Long getLong(byte[] value, ByteOrder byteOrder) {
		return ByteBuffer.wrap(value).order(byteOrder).getInt() & 0xffffffffL;
	}

	public static String toLongString(byte[] value, ByteOrder byteOrder) {
		return Long.toString(ByteBuffer.wrap(value).order(byteOrder).getLong());
	}

	public static String toShortArrayString(byte[] value, ByteOrder byteOrder) {
		String toReturn = "[";
		final String[] valueArray = getStringShortArray(value, byteOrder);
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
		return Integer.toString(getInteger(value, byteOrder));
	}


	public static int getInteger(byte[] value, ByteOrder byteOrder) {
		return ByteBuffer.wrap(value).order(byteOrder).getShort() & 0xffff;
	}
	
	public static String toString(byte[] value) {
		return new String(value).replaceAll("\\u0000", "");
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
	
	private static Boolean isValidString(byte[] value, ByteOrder byteOrder) {
		CharsetDecoder d = Charset.forName("US-ASCII").newDecoder();
	    try {
	      CharBuffer r = d.decode(ByteBuffer.wrap(value).order(byteOrder));
	      r.toString();
	    }
	    catch(CharacterCodingException e) {
	      return false;
	    }
	    return true;
	}
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	private static String getHexString(byte[] value, ByteOrder byteOrder) {
		char[] hexChars = new char[value.length * 2];
	    for (int j = 0; j < value.length; j++) {
	        int v = value[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return "HEX: " + new String(hexChars);
	}
	public static Boolean isNull(byte[] value, Class<?> type) {
		byte searchValue = (byte) 0xff;
		if(type.equals(String.class)) {
			searchValue = (byte) 0x00;
		}
	    for(int i = 0; i < value.length; i++) {
	    	if(value[i]!=searchValue) {
	    		return false;
	    	}
	    }
	    return true;
	}
	private static Boolean isValidShort(Integer s) {
		return s >= Short.MIN_VALUE && s <= Short.MAX_VALUE;
	}

	private static Boolean isValidInteger(Long i) {
		return i >= Integer.MIN_VALUE && i <= Integer.MAX_VALUE;
	}
	private static Boolean isValidIntegerArray(Long[] la) {
		for(int i = 0; i < la.length; i++) {
			if(!isValidInteger(la[i])) {
				return false;
			}
		}
		return true;
	}
	private static Boolean isValidShortArray(Integer[] la) {
		for(int i = 0; i < la.length; i++) {
			if(!isValidShort(la[i])) {
				return false;
			}
		}
		return true;
	}
	public static String toStandardizedTypedString(Class<?> type, byte[] value, ByteOrder byteOrder) {

		if(isNull(value, type)) return "NULL";
		
		if (type == byte[].class) {
			return toByteArrayString(value);
		} else if (type == byte.class) {
			return toByteString(value);
		} else if (type == float.class) {
			Float f = getFloat(value, byteOrder);
			return f >=0 ? toFloatString(value, byteOrder) : getHexString(value, byteOrder);
		} else if (type == int[].class) {
			Long[] la = getIntArray(value, byteOrder);
			return isValidIntegerArray(la) ? toIntArrayString(value, byteOrder) : getHexString(value, byteOrder);
		} else if (type == int.class) {
			Long l = getLong(value, byteOrder);
			return isValidInteger(l) ? toIntString(value, byteOrder) : getHexString(value, byteOrder);
		} else if (type == long.class) {
			Long l = getLong(value, byteOrder);
			return l >= 0 ? toLongString(value, byteOrder) : getHexString(value, byteOrder);
		} else if (type == short[].class) {
			Integer[] la = getShortArray(value, byteOrder);
			return isValidShortArray(la) ? toShortArrayString(value, byteOrder) : getHexString(value, byteOrder);
		} else if (type == short.class) {
			Integer s = getInteger(value, byteOrder);
			return isValidShort(s) ? toShortString(value, byteOrder) : getHexString(value, byteOrder);
		} else if (type == String.class) {
			if(isValidString(value, byteOrder)) {
				return toString(value);
			}
			else {
				return getHexString(value, byteOrder);
			}
		} else {
			return "INVALID TYPE";
		}
	}
}
