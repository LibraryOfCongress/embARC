package com.portalmedia.embarc.parser.dpx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.BinaryFileReader;
import com.portalmedia.embarc.parser.ByteOrderEnum;
import com.portalmedia.embarc.parser.MetadataColumn;
import com.portalmedia.embarc.parser.ValueToBytesHelper;

/**
 * Service to read from and write to a DPX files
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXService {
	public static boolean writeFile(DPXFileInformationViewModel file, String inputPath, String outputPath)
			throws Exception {
		// Original value of offset to image data - needed for checksum
		final String imageDataStartString = file.getProp(DPXColumn.OFFSET_TO_IMAGE_DATA);
		final int imageDataStart = Integer.parseInt(imageDataStartString);

		// Create a temp file.
		final String tempPath = outputPath + ".tmp";
		try (FileOutputStream fos = new FileOutputStream(tempPath)) {
			// Determine byte ordering
			final String magicNumber = file.getProp(DPXColumn.MAGIC_NUMBER);
			final ByteOrder byteOrder = magicNumber.equals("SDPX") ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;

			// Get the length of the user defined data
			final String userDefinedDataLengthStr = file.getProp(DPXColumn.USER_DEFINED_HEADER_LENGTH);
			Integer userDefinedHeaderLength = userDefinedDataLengthStr.isEmpty() ? null : Integer.parseInt(userDefinedDataLengthStr);
			
			// Get the length of the industry specific header
			final String industrySpecificHeaderLengthStr = file.getProp(DPXColumn.INDUSTRY_SPECIFIC_HEADER_LENGTH);
			final int industrySpecificHeaderLength = industrySpecificHeaderLengthStr.isEmpty() ? 0 : Integer.parseInt(industrySpecificHeaderLengthStr);
			
			// Get the number of image elements
			final String numberOfImageElementsStr = file.getProp(DPXColumn.NUMBER_OF_IMAGE_ELEMENTS);
			final int numberOfImageElements = numberOfImageElementsStr.isEmpty() ? 0 : Integer.parseInt(numberOfImageElementsStr);

			int offsetToImageData = file.getOffsetToImageData();
			int totalChange = offsetToImageData - Integer.parseInt(file.getProp(DPXColumn.OFFSET_TO_IMAGE_DATA));

			// Iterate through each column
			for (final DPXColumn column : DPXColumn.values()) {
				String value = "";
				int length = column.getLength();

				switch(column) {
					case USER_DEFINED_HEADER_LENGTH:
						if (userDefinedHeaderLength != null) value = String.valueOf(userDefinedHeaderLength);
						break;
					case USER_IDENTIFICATION:
						if (userDefinedHeaderLength == null || userDefinedHeaderLength == 0) {
							continue;
						} else value = file.getProp(column);
						break;
					case USER_DEFINED_DATA:
						if (userDefinedHeaderLength == null || userDefinedHeaderLength == 0) {
							continue;
						} else {
							length = userDefinedHeaderLength - DPXColumn.USER_IDENTIFICATION.getLength();
							if (length < 0) length = 0;
							value = file.getProp(column);
						}
						break;
					case OFFSET_TO_IMAGE_DATA:
						value = String.valueOf(offsetToImageData);
						break;
					case INDUSTRY_SPECIFIC_HEADER_LENGTH:
						if (industrySpecificHeaderLength == 0) value = String.valueOf(384);
						else value = industrySpecificHeaderLengthStr;
						break;
					case TOTAL_IMAGE_FILE_SIZE:
						{int val = Integer.parseInt(file.getProp(DPXColumn.TOTAL_IMAGE_FILE_SIZE));
						value = String.valueOf(val + totalChange);}
						break;
					case OFFSET_TO_DATA_1:
						if (numberOfImageElements > 0) {
							int val = Integer.parseInt(file.getProp(DPXColumn.OFFSET_TO_DATA_1));
							value = String.valueOf(val + totalChange);
						}
						break;
					case OFFSET_TO_DATA_2:
						if (numberOfImageElements > 1) {
							int val = Integer.parseInt(file.getProp(DPXColumn.OFFSET_TO_DATA_2));
							value = String.valueOf(val + totalChange);
						}
						break;
					case OFFSET_TO_DATA_3:
						if (numberOfImageElements > 2) {
							int val = Integer.parseInt(file.getProp(DPXColumn.OFFSET_TO_DATA_3));
							value = String.valueOf(val + totalChange);
						}
						break;
					case OFFSET_TO_DATA_4:
						if (numberOfImageElements > 3) {
							int val = Integer.parseInt(file.getProp(DPXColumn.OFFSET_TO_DATA_4));
							value = String.valueOf(val + totalChange);
						}
						break;
					case OFFSET_TO_DATA_5:
						if (numberOfImageElements > 4) {
							int val = Integer.parseInt(file.getProp(DPXColumn.OFFSET_TO_DATA_5));
							value = String.valueOf(val + totalChange);
						}
						break;
					case OFFSET_TO_DATA_6:
						if (numberOfImageElements > 5) {
							int val = Integer.parseInt(file.getProp(DPXColumn.OFFSET_TO_DATA_6));
							value = String.valueOf(val + totalChange);
						}
						break;
					case OFFSET_TO_DATA_7:
						if (numberOfImageElements > 6) {
							int val = Integer.parseInt(file.getProp(DPXColumn.OFFSET_TO_DATA_7));
							value = String.valueOf(val + totalChange);
						}
						break;
					case OFFSET_TO_DATA_8:
						if (numberOfImageElements > 7) {
							int val = Integer.parseInt(file.getProp(DPXColumn.OFFSET_TO_DATA_8));
							value = String.valueOf(val + totalChange);
						}
						break;
					default:
						value = file.getProp(column);
						break;
				}

				final byte[] convertedValue = ValueToBytesHelper.ToTypedValue(column, value, length, byteOrder);
				fos.write(convertedValue);
			}

			// Read the image data from the original file
			try(InputStream is = new FileInputStream(inputPath)){
				final byte[] buffer = new byte[1024];
				int length;
				is.skip(imageDataStart);

				// Write the image data to the temp file
				while ((length = is.read(buffer)) > 0) {
					fos.write(buffer, 0, length);
				}
				is.close();
				fos.close();

				// Copy the temp file to the final destination
				final Path tempFile = new File(tempPath).toPath();
				try {
					Files.copy(tempFile, new File(outputPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (final Exception ex) {
					System.out.println("Error copying files");
				}
				try {
					// cleanup temp file
					Files.delete(tempFile);
					return true;
				} catch (final Exception ex) {
					System.out.println("Error deleting temp file");
				}
			}
			

		} catch (final Exception ex) {
			// Cleanup on error
			final Path tempFile = new File(tempPath).toPath();
			if (Files.exists(tempFile)) {
				Files.delete(tempFile);
			}
			System.out.println("Error writing file - inner");
			
			return false;
		}
		return false;
	}

	String filePath;

	public DPXService(String filePath) throws FileNotFoundException {
		this.filePath = filePath;
	}

	public DPXMetadata readFile() throws Exception {
		try(final BinaryFileReader dpxFile = new BinaryFileReader(filePath)){

			// Default byte order
			ByteOrderEnum byteOrder = ByteOrderEnum.BIG;
	
			// Create a new object to hold the metadata
			final DPXMetadata dpxData = new DPXMetadata();
	
			// Create an empty byte array for unread values;
			byte[] emptyBA = new byte[0];
	
			// Iterate through each column in the File Information Header
			for (final DPXColumn c : DPXSection.FILE_INFORMATION_HEADER.getColumns()) {
				// Read the section of bytes
				final byte[] bytes = dpxFile.readBytes(c.getLength());
	
				// Store this data in a metadata column
				MetadataColumn mdColumn = DPXMetadataColumnFactory.create(c, bytes, byteOrder);
	
				// If the magic number is "SDPX", we know the rest of the file is big endian
				if (c == DPXColumn.MAGIC_NUMBER) {
					if (mdColumn.toString().equals("SDPX")) {
					} else if (mdColumn.toString().equals("XPDS")) {
						byteOrder = ByteOrderEnum.LITTLE;
						mdColumn = DPXMetadataColumnFactory.create(DPXColumn.MAGIC_NUMBER, bytes, byteOrder);
					} else {
						throw new IllegalArgumentException("Invalid magic number");
					}
				}
	
				// Store the new column
				dpxData.setValue(mdColumn);
			}
	
			int industrySpecificHeaderLength = dpxData.getColumn(DPXColumn.INDUSTRY_SPECIFIC_HEADER_LENGTH).getInt();
	
			// Iterate through each column in the Image Information Header
			for (final DPXColumn c : DPXSection.IMAGE_INFORMATION_HEADER.getColumns()) {
				final MetadataColumn mdColumn = DPXMetadataColumnFactory.create(c, dpxFile.readBytes(c.getLength()),
						byteOrder);
				dpxData.setValue(mdColumn);
			}
	
			// Iterate through each column in the Image Source Information Header
			for (final DPXColumn c : DPXSection.IMAGE_SOURCE_INFORMATION_HEADER.getColumns()) {
				final MetadataColumn mdColumn = DPXMetadataColumnFactory.create(c, dpxFile.readBytes(c.getLength()),
						byteOrder);
				dpxData.setValue(mdColumn);
			}
	
			// Do not read industry specific data if the industry specific header length is 0
			if (industrySpecificHeaderLength > 0) {
				// Iterate through each column in the Motion Picture Film Information Header
				for (final DPXColumn c : DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER.getColumns()) {
					final MetadataColumn mdColumn = DPXMetadataColumnFactory.create(c, dpxFile.readBytes(c.getLength()),
							byteOrder);
					dpxData.setValue(mdColumn);
				}
	
				// Iterate through each column in the Television Information Header
				for (final DPXColumn c : DPXSection.TELEVISION_INFORMATION_HEADER.getColumns()) {
					final MetadataColumn mdColumn = DPXMetadataColumnFactory.create(c, dpxFile.readBytes(c.getLength()),
							byteOrder);
					dpxData.setValue(mdColumn);
				}
			} else {
				// set all fields in motion pic and tv sections to empty values
				for (final DPXColumn c : DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER.getColumns()) {
					final MetadataColumn mdColumn = DPXMetadataColumnFactory.create(c, emptyBA, byteOrder);
					dpxData.setValue(mdColumn);
				}
	
				for (final DPXColumn c : DPXSection.TELEVISION_INFORMATION_HEADER.getColumns()) {
					final MetadataColumn mdColumn = DPXMetadataColumnFactory.create(c, emptyBA, byteOrder);
					dpxData.setValue(mdColumn);
				}
			}
	
			// Get the length of the User Identification Field
			final int userIdLength = DPXColumn.USER_IDENTIFICATION.getLength();
	
			// User defined data length is the total specified in the User Defined Header
			// Length column minus the length of the user identification length
			final int lengthOfUserData = Math
					.max(dpxData.getColumn(DPXColumn.USER_DEFINED_HEADER_LENGTH).getInt() - userIdLength, 0);
			
			if (lengthOfUserData > 0) {
				// Read the user identification data
				final MetadataColumn user = DPXMetadataColumnFactory.create(DPXColumn.USER_IDENTIFICATION,
						dpxFile.readBytes(userIdLength), byteOrder);
				dpxData.setValue(user);
	
				// Read the user defined data.
				final MetadataColumn userDefinedData = DPXMetadataColumnFactory.create(DPXColumn.USER_DEFINED_DATA,
						dpxFile.readBytes(lengthOfUserData), byteOrder);
				dpxData.setValue(userDefinedData);
			} else {
				// set user identification and user defined data to empty values
				final MetadataColumn user = DPXMetadataColumnFactory.create(DPXColumn.USER_IDENTIFICATION, emptyBA, byteOrder);
				dpxData.setValue(user);
	
				final MetadataColumn userDefinedData = DPXMetadataColumnFactory.create(DPXColumn.USER_DEFINED_DATA, emptyBA, byteOrder);
				dpxData.setValue(userDefinedData);
			}
			return dpxData;
		}
	}

}
