package com.portalmedia.embarc.parser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Date;

/**
 * Class of convenience methods for turning string values to typed values and
 * then into byte arrays.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class ValueToBytesHelper {

	public static byte[] ToByte(String value, int length, ByteOrder byteOrder) {
		byte i = (byte) 255;
		if (value.length() == 0) {

		} else {
			i = Byte.parseByte(value);
		}
		return ByteBuffer.allocate(length).put(i).array();
	}

	public static byte[] ToDate(String value, int length, ByteOrder byteOrder) {
		return ByteBuffer.allocate(length).put(value.getBytes()).array();
	}

	public static byte[] ToFloat(String value, int length, ByteOrder byteOrder) {
		float i = -32768;
		if (value.length() == 0) {
			final int v = (int) Long.parseLong("ffffffff", 16);
			final ByteBuffer buffer = ByteBuffer.allocate(length);
			buffer.order(byteOrder);
			buffer.putInt(v);
			final byte[] bytes = buffer.array();
			return bytes;
		} else {
			i = Float.parseFloat(value);
		}

		final ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.order(byteOrder);
		buffer.putFloat(i);
		final byte[] bytes = buffer.array();
		return bytes;
	}

	public static byte[] ToInt(String value, int length, ByteOrder byteOrder) {
		Long i;
		if (value.length() == 0) {
			i = Long.parseLong("ffffffff", 16);
		} else {
			i = Long.parseLong(value);
		}

		final ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.order(byteOrder);
		buffer.putInt(((int) (i & 0xffffffffL)));
		final byte[] bytes = buffer.array();
		return bytes;
	}

	public static byte[] ToIntArray(String value, int valueLength, int arrayLength, ByteOrder byteOrder) {
		final int[] intValues = new int[(arrayLength / 4)];
		if (value.length() == 0) {

			final byte[] bytes = new byte[arrayLength];
			final byte b = (byte) 255;
			for (int x = 0; x < valueLength; x++) {
				bytes[x] = b;
			}
			return bytes;
		} else {
			final String[] values = value.replace("[", "").replace("]", "").split(",");
			for (Integer i = 0; i < (arrayLength / 4); i++) {
				if (i >= values.length) {
					final int v = (int) Long.parseLong("ffffffff", 16);
					intValues[i] = v;
				} else if (values[i].length() == 0) {
					final int v = (int) Long.parseLong("ffffffff", 16);
					intValues[i] = v;
				} else {
					intValues[i] = Integer.parseInt(values[i]);
				}
			}
		}

		final ByteBuffer byteBuff = ByteBuffer.allocate(arrayLength);
		byteBuff.order(byteOrder);
		for (final int i : intValues) {
			byteBuff.putInt(i);
		}
		final byte[] bytes = byteBuff.array();
		if (byteOrder == ByteOrder.nativeOrder()) {
			return ByteBuffer.allocate(arrayLength).put(bytes).array();
		} else {
			return ByteBuffer.allocate(arrayLength).order(byteOrder).put(bytes).array();
		}
	}

	public static byte[] ToShort(String value, int length, ByteOrder byteOrder) {

		int i;
		if (value.length() == 0) {
			final int v = (int) Long.parseLong("ffffffff", 16);
			final ByteBuffer buffer = ByteBuffer.allocate(length);
			buffer.order(byteOrder);
			buffer.putShort((short) v);
			final byte[] bytes = buffer.array();
			return bytes;

		} else {
			i = Integer.parseInt(value);
		}

		final ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.order(byteOrder);
		buffer.putShort(((short) (i & 0xffff)));
		final byte[] bytes = buffer.array();
		return bytes;
	}

	public static byte[] ToShortArray(String value, int valueLength, int arrayLength, ByteOrder byteOrder) {
		final short[] shortValues = new short[(arrayLength / 2)];
		if (value.length() == 0) {
			final byte[] bytes = new byte[arrayLength];
			final byte b = (byte) 255;
			for (int x = 0; x < valueLength; x++) {
				bytes[x] = b;
			}
			return bytes;
		} else {
			final String[] values = value.replace("[", "").replace("]", "").split(",");

			for (short i = 0; i < (arrayLength / 2); i++) {
				if (values.length <= i) {
					shortValues[i] = (short) 65535;
				} else if (values[i].length() == 0) {
					shortValues[i] = (short) 65535;
				} else {
					shortValues[i] = Short.parseShort(values[i]);
				}
			}
		}
		final ByteBuffer byteBuff = ByteBuffer.allocate(arrayLength);
		byteBuff.order(byteOrder);
		for (final short i : shortValues) {
			byteBuff.putShort(i);
		}
		final byte[] bytes = byteBuff.array();
		if (byteOrder == ByteOrder.nativeOrder()) {
			return ByteBuffer.allocate(arrayLength).put(bytes).array();
		} else {
			return ByteBuffer.allocate(arrayLength).order(byteOrder).put(bytes).array();
		}
	}

	public static byte[] ToString(String value, int length, ByteOrder byteOrder) {
		final byte[] bytes = Arrays.copyOfRange(value.getBytes(), 0, Math.min(length, value.length()));
		return ByteBuffer.allocate(length).put(bytes).array();
	}

	// Single entry point for all values.
	// TODO: Remove need to pass in DPXColumnEnum into this method
	public static byte[] ToTypedValue(ColumnDef column, String value, int length, ByteOrder byteOrder)
			throws Exception {
		if (column.getType() == int.class) {
			return ToInt(value, length, byteOrder);
		} else if (column.getType() == String.class) {
			return ToString(value, length, byteOrder);
		} else if (column.getType() == short.class) {
			return ToShort(value, length, byteOrder);
		} else if (column.getType() == float.class) {
			return ToFloat(value, length, byteOrder);
		} else if (column.getType() == byte.class) {
			return ToByte(value, length, byteOrder);
		} else if (column.getType() == Date.class) {
			return ToDate(value, length, byteOrder);
		} else if (column.getType() == int[].class) {
			return ToIntArray(value, column.getLength(), length, byteOrder);
		} else if (column.getType() == short[].class) {
			return ToShortArray(value, column.getLength(), length, byteOrder);
		}
		throw new Exception("Invalid data type " + column.getType());
	}

}
