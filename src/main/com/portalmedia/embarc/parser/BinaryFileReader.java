package com.portalmedia.embarc.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Wrapper for input stream, which helps with parsing specific formats, etc.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class BinaryFileReader {
	private InputStream inputStream;
	public long fileSizeBytes;
	public long bytesRead;
	public ByteOrder byteOrder;
	
	public BinaryFileReader(String inputFilePath) throws FileNotFoundException{
		Initialize(inputFilePath);
		byteOrder = ByteOrder.BIG_ENDIAN;
	}

	public BinaryFileReader(String inputFilePath, ByteOrder inputByteOrder) throws FileNotFoundException{
		Initialize(inputFilePath);
		byteOrder = inputByteOrder;
	}
	
	public void SetByteOrder(ByteOrder inputByteOrder) {
		byteOrder = inputByteOrder;
	}
	
	private void Initialize(String inputFilePath) throws FileNotFoundException
	{
		inputStream = new FileInputStream(inputFilePath);
		fileSizeBytes = new File(inputFilePath).length();
		bytesRead = 0;
	}
	
	public void skip(int position)
	{
		try {
			inputStream.skip(position);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void close() {
		try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean hasMoreData() {
		return bytesRead < fileSizeBytes;
	}
	
	public boolean canRead(int length) {
		return bytesRead + length < fileSizeBytes;
	}
	
	public String readAscii(int length) {
		byte[] bytes;
		try {
			bytes = readBytes(length);
			
			// Wrap bytes in buffer to set byte order
			ByteBuffer bb = ByteBuffer.wrap(bytes);
			bb.order(byteOrder);
			
			// Write files to new byte array using new byte ordering
			// TODO: Is there a better way to do this?
			byte[] destBytes = new byte[length];
			bb.get(destBytes);
			
			return new String(destBytes, "UTF-8");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
		
	public short readInt16() {
		byte[] bytes;
		try {
			bytes = readBytes(2);
			return ByteBuffer.wrap(bytes).order(byteOrder).getShort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public byte readInt8() {
		byte[] bytes;
		try {
			bytes = readBytes(1);
			return bytes[0];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public int readInt32() {
		byte[] bytes;
		try {
			int length = 4;
			bytes = readBytes(length);
			return ByteBuffer.wrap(bytes).order(byteOrder).getInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public float readFloat32() {
		byte[] bytes;
		try {
			int length = 4;
			bytes = readBytes(length);
			return ByteBuffer.wrap(bytes).order(byteOrder).getFloat();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public long readInt64() {
		byte[] bytes;
		try {
			int length = 8;
			bytes = readBytes(length);
			return ByteBuffer.wrap(bytes).order(byteOrder).getLong();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public Date readDate(String format, int length) {
		String dateString = readAscii(length);
		
		if(dateString.trim().length()==0) return null;
		
		DateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
		
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}
	
	public byte[] readBytes(int length) throws IOException
	{
		byte [] bytes = new byte[length];
		for(int bp =0; bp < length; bp++)
		{
			bytes[bp] = (byte) inputStream.read();
			bytesRead++;
		}
		return bytes;
	}
	
	public long getCurrentPosition()
	{
		return bytesRead;
	}
}
