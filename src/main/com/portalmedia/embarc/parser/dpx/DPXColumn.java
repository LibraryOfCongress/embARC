package com.portalmedia.embarc.parser.dpx;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.DisplayType;
import com.portalmedia.embarc.parser.ImageElementDef;

/**
 * All DPX columns, display name, section, type, whether it is editable, and it's
 * display type.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public enum DPXColumn implements Serializable, ColumnDef {

	// File information header columns
	MAGIC_NUMBER("Magic Number", DPXSection.FILE_INFORMATION_HEADER, 4, String.class, false, DisplayType.ASCII),
	OFFSET_TO_IMAGE_DATA("Offset to Image Data", DPXSection.FILE_INFORMATION_HEADER, 4, int.class, false,
			DisplayType.INTEGER),
	VERSION_NUMBER_OF_HEADER_FORMAT("Version Number of Header Format", DPXSection.FILE_INFORMATION_HEADER, 8,
			String.class, false, DisplayType.ASCII),
	TOTAL_IMAGE_FILE_SIZE("Total Image File Size", DPXSection.FILE_INFORMATION_HEADER, 4, int.class, false,
			DisplayType.INTEGER),
	DITTO_KEY("Ditto Key", DPXSection.FILE_INFORMATION_HEADER, 4, int.class, false, DisplayType.INTEGER),
	GENERIC_SECTION_HEADER_LENGTH("Generic Section Header Length", DPXSection.FILE_INFORMATION_HEADER, 4, int.class,
			false, DisplayType.INTEGER),
	INDUSTRY_SPECIFIC_HEADER_LENGTH("Industry Specific Header Length", DPXSection.FILE_INFORMATION_HEADER, 4,
			int.class, false, DisplayType.INTEGER),
	USER_DEFINED_HEADER_LENGTH("User Defined Header Length", DPXSection.FILE_INFORMATION_HEADER, 4, int.class,
			false, DisplayType.INTEGER),
	IMAGE_FILE_NAME("Image Filename", DPXSection.FILE_INFORMATION_HEADER, 100, String.class, true,
			DisplayType.ASCII),
	CREATION_DATETIME("Creation Datetime", DPXSection.FILE_INFORMATION_HEADER, 24, String.class,
			"yyyy:MM:dd:HH:mm:ss", true, DisplayType.DATEPICKER),
	CREATOR("Creator", DPXSection.FILE_INFORMATION_HEADER, 100, String.class, true, DisplayType.ASCII),
	PROJECT_NAME("Project Name", DPXSection.FILE_INFORMATION_HEADER, 200, String.class, true, DisplayType.ASCII),
	COPYRIGHT_STATEMENT("Copyright Statement", DPXSection.FILE_INFORMATION_HEADER, 200, String.class, true,
			DisplayType.ASCII),
	ENCRYPTION_KEY("Encryption Key", DPXSection.FILE_INFORMATION_HEADER, 4, int.class, false, DisplayType.INTEGER),
	RESERVED_FILE_INFORMATION_HEADER("Reserved", DPXSection.FILE_INFORMATION_HEADER, 104, String.class, false,
			DisplayType.ASCII),

	// Image Information Header
	IMAGE_ORIENTATION("Image Orientation", DPXSection.IMAGE_INFORMATION_HEADER, 2, short.class, false,
			DisplayType.INTEGER),
	NUMBER_OF_IMAGE_ELEMENTS("Number of Image Elements", DPXSection.IMAGE_INFORMATION_HEADER, 2, short.class, false,
			DisplayType.INTEGER),
	PIXELS_PER_LINE("Pixels Per Line", DPXSection.IMAGE_INFORMATION_HEADER, 4, int.class, false,
			DisplayType.INTEGER),
	LINES_PER_IMAGE_ELEMENT("Lines per Image Element", DPXSection.IMAGE_INFORMATION_HEADER, 4, int.class, false,
			DisplayType.INTEGER),
	// Data Structure for Image Element 1
	DATA_SIGN_1("Data Sign", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_1, 4, int.class,
			false, DisplayType.INTEGER),
	REFERENCE_LOW_DATA_CODE_1("Reference Low Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_1, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_LOW_QUANTITY_1("Reference Low Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_1, 4, float.class, false, DisplayType.FLOAT),
	REFERENCE_HIGH_DATA_CODE_1("Reference High Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_1, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_HIGH_QUANTITY_1("Reference High Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_1, 4, float.class, false, DisplayType.FLOAT),
	DESCRIPTOR_1("Descriptor", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_1, 1, byte.class,
			false, DisplayType.INTEGER),
	TRANSFER_CHARACTERISTIC_1("Transfer Characteristic", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_1, 1, byte.class, false, DisplayType.INTEGER),
	COLORIMETRIC_SPECIFICATION_1("Colorimetric Specification", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_1, 1, byte.class, false, DisplayType.INTEGER),
	BIT_DEPTH_1("Bit Depth", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_1, 1, byte.class,
			false, DisplayType.INTEGER),
	PACKING_1("Packing", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_1, 2, short.class,
			false, DisplayType.INTEGER),
	ENCODING_1("Encoding", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_1, 2, short.class,
			false, DisplayType.INTEGER),
	OFFSET_TO_DATA_1("Offset to Data", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_1, 4,
			int.class, false, DisplayType.INTEGER),
	END_OF_LINE_PADDING_1("End of Line Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_1, 4, int.class, false, DisplayType.INTEGER),
	END_OF_IMAGE_PADDING_1("End of Image Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_1, 4, int.class, false, DisplayType.INTEGER),
	DESCRIPTION_OF_IMAGE_ELEMENT_1("Description of Image Element", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_1, 32, String.class, true, DisplayType.ASCII),
	// Data Structure for Image Element 2
	DATA_SIGN_2("Data Sign", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_2, 4, int.class,
			false, DisplayType.INTEGER),
	REFERENCE_LOW_DATA_CODE_2("Reference Low Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_2, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_LOW_QUANTITY_2("Reference Low Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_2, 4, float.class, false, DisplayType.FLOAT),
	REFERENCE_HIGH_DATA_CODE_2("Reference High Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_2, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_HIGH_QUANTITY_2("Reference High Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_2, 4, float.class, false, DisplayType.FLOAT),
	DESCRIPTOR_2("Descriptor", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_2, 1, byte.class,
			false, DisplayType.INTEGER),
	TRANSFER_CHARACTERISTIC_2("Transfer Characteristic", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_2, 1, byte.class, false, DisplayType.INTEGER),
	COLORIMETRIC_SPECIFICATION_2("Colorimetric Specification", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_2, 1, byte.class, false, DisplayType.INTEGER),
	BIT_DEPTH_2("Bit Depth", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_2, 1, byte.class,
			false, DisplayType.INTEGER),
	PACKING_2("Packing", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_2, 2, short.class,
			false, DisplayType.INTEGER),
	ENCODING_2("Encoding", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_2, 2, short.class,
			false, DisplayType.INTEGER),
	OFFSET_TO_DATA_2("Offset to Data", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_2, 4,
			int.class, false, DisplayType.INTEGER),
	END_OF_LINE_PADDING_2("End of Line Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_2, 4, int.class, false, DisplayType.INTEGER),
	END_OF_IMAGE_PADDING_2("End of Image Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_2, 4, int.class, false, DisplayType.INTEGER),
	DESCRIPTION_OF_IMAGE_ELEMENT_2("Description of Image Element", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_2, 32, String.class, true, DisplayType.ASCII),
	// Data Structure for Image Element 3
	DATA_SIGN_3("Data Sign", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_3, 4, int.class,
			false, DisplayType.INTEGER),
	REFERENCE_LOW_DATA_CODE_3("Reference Low Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_3, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_LOW_QUANTITY_3("Reference Low Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_3, 4, float.class, false, DisplayType.FLOAT),
	REFERENCE_HIGH_DATA_CODE_3("Reference High Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_3, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_HIGH_QUANTITY_3("Reference High Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_3, 4, float.class, false, DisplayType.FLOAT),
	DESCRIPTOR_3("Descriptor", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_3, 1, byte.class,
			false, DisplayType.INTEGER),
	TRANSFER_CHARACTERISTIC_3("Transfer Characteristic", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_3, 1, byte.class, false, DisplayType.INTEGER),
	COLORIMETRIC_SPECIFICATION_3("Colorimetric Specification", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_3, 1, byte.class, false, DisplayType.INTEGER),
	BIT_DEPTH_3("Bit Depth", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_3, 1, byte.class,
			false, DisplayType.INTEGER),
	PACKING_3("Packing", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_3, 2, short.class,
			false, DisplayType.INTEGER),
	ENCODING_3("Encoding", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_3, 2, short.class,
			false, DisplayType.INTEGER),
	OFFSET_TO_DATA_3("Offset to Data", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_3, 4,
			int.class, false, DisplayType.INTEGER),
	END_OF_LINE_PADDING_3("End of Line Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_3, 4, int.class, false, DisplayType.INTEGER),
	END_OF_IMAGE_PADDING_3("End of Image Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_3, 4, int.class, false, DisplayType.INTEGER),
	DESCRIPTION_OF_IMAGE_ELEMENT_3("Description of Image Element", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_3, 32, String.class, true, DisplayType.ASCII),
	// Data Structure for Image Element 4
	DATA_SIGN_4("Data Sign", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_4, 4, int.class,
			false, DisplayType.INTEGER),
	REFERENCE_LOW_DATA_CODE_4("Reference Low Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_4, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_LOW_QUANTITY_4("Reference Low Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_4, 4, float.class, false, DisplayType.FLOAT),
	REFERENCE_HIGH_DATA_CODE_4("Reference High Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_4, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_HIGH_QUANTITY_4("Reference High Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_4, 4, float.class, false, DisplayType.FLOAT),
	DESCRIPTOR_4("Descriptor", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_4, 1, byte.class,
			false, DisplayType.INTEGER),
	TRANSFER_CHARACTERISTIC_4("Transfer Characteristic", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_4, 1, byte.class, false, DisplayType.INTEGER),
	COLORIMETRIC_SPECIFICATION_4("Colorimetric Specification", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_4, 1, byte.class, false, DisplayType.INTEGER),
	BIT_DEPTH_4("Bit Depth", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_4, 1, byte.class,
			false, DisplayType.INTEGER),
	PACKING_4("Packing", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_4, 2, short.class,
			false, DisplayType.INTEGER),
	ENCODING_4("Encoding", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_4, 2, short.class,
			false, DisplayType.INTEGER),
	OFFSET_TO_DATA_4("Offset to Data", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_4, 4,
			int.class, false, DisplayType.INTEGER),
	END_OF_LINE_PADDING_4("End of Line Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_4, 4, int.class, false, DisplayType.INTEGER),
	END_OF_IMAGE_PADDING_4("End of Image Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_4, 4, int.class, false, DisplayType.INTEGER),
	DESCRIPTION_OF_IMAGE_ELEMENT_4("Description of Image Element", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_4, 32, String.class, true, DisplayType.ASCII),
	// Data Structure for Image Element 5
	DATA_SIGN_5("Data Sign", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_5, 4, int.class,
			false, DisplayType.INTEGER),
	REFERENCE_LOW_DATA_CODE_5("Reference Low Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_5, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_LOW_QUANTITY_5("Reference Low Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_5, 4, float.class, false, DisplayType.FLOAT),
	REFERENCE_HIGH_DATA_CODE_5("Reference High Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_5, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_HIGH_QUANTITY_5("Reference High Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_5, 4, float.class, false, DisplayType.FLOAT),
	DESCRIPTOR_5("Descriptor", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_5, 1, byte.class,
			false, DisplayType.INTEGER),
	TRANSFER_CHARACTERISTIC_5("Transfer Characteristic", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_5, 1, byte.class, false, DisplayType.INTEGER),
	COLORIMETRIC_SPECIFICATION_5("Colorimetric Specification", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_5, 1, byte.class, false, DisplayType.INTEGER),
	BIT_DEPTH_5("Bit Depth", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_5, 1, byte.class,
			false, DisplayType.INTEGER),
	PACKING_5("Packing", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_5, 2, short.class,
			false, DisplayType.INTEGER),
	ENCODING_5("Encoding", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_5, 2, short.class,
			false, DisplayType.INTEGER),
	OFFSET_TO_DATA_5("Offset to Data", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_5, 4,
			int.class, false, DisplayType.INTEGER),
	END_OF_LINE_PADDING_5("End of Line Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_5, 4, int.class, false, DisplayType.INTEGER),
	END_OF_IMAGE_PADDING_5("End of Image Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_5, 4, int.class, false, DisplayType.INTEGER),
	DESCRIPTION_OF_IMAGE_ELEMENT_5("Description of Image Element", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_5, 32, String.class, true, DisplayType.ASCII),
	// Data Structure for Image Element 6
	DATA_SIGN_6("Data Sign", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_6, 4, int.class,
			false, DisplayType.INTEGER),
	REFERENCE_LOW_DATA_CODE_6("Reference Low Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_6, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_LOW_QUANTITY_6("Reference Low Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_6, 4, float.class, false, DisplayType.FLOAT),
	REFERENCE_HIGH_DATA_CODE_6("Reference High Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_6, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_HIGH_QUANTITY_6("Reference High Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_6, 4, float.class, false, DisplayType.FLOAT),
	DESCRIPTOR_6("Descriptor", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_6, 1, byte.class,
			false, DisplayType.INTEGER),
	TRANSFER_CHARACTERISTIC_6("Transfer Characteristic", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_6, 1, byte.class, false, DisplayType.INTEGER),
	COLORIMETRIC_SPECIFICATION_6("Colorimetric Specification", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_6, 1, byte.class, false, DisplayType.INTEGER),
	BIT_DEPTH_6("Bit Depth", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_6, 1, byte.class,
			false, DisplayType.INTEGER),
	PACKING_6("Packing", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_6, 2, short.class,
			false, DisplayType.INTEGER),
	ENCODING_6("Encoding", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_6, 2, short.class,
			false, DisplayType.INTEGER),
	OFFSET_TO_DATA_6("Offset to Data", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_6, 4,
			int.class, false, DisplayType.INTEGER),
	END_OF_LINE_PADDING_6("End of Line Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_6, 4, int.class, false, DisplayType.INTEGER),
	END_OF_IMAGE_PADDING_6("End of Image Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_6, 4, int.class, false, DisplayType.INTEGER),
	DESCRIPTION_OF_IMAGE_ELEMENT_6("Description of Image Element", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_6, 32, String.class, true, DisplayType.ASCII),
	// Data Structure for Image Element 7
	DATA_SIGN_7("Data Sign", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_7, 4, int.class,
			false, DisplayType.INTEGER),
	REFERENCE_LOW_DATA_CODE_7("Reference Low Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_7, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_LOW_QUANTITY_7("Reference Low Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_7, 4, float.class, false, DisplayType.FLOAT),
	REFERENCE_HIGH_DATA_CODE_7("Reference High Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_7, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_HIGH_QUANTITY_7("Reference High Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_7, 4, float.class, false, DisplayType.FLOAT),
	DESCRIPTOR_7("Descriptor", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_7, 1, byte.class,
			false, DisplayType.INTEGER),
	TRANSFER_CHARACTERISTIC_7("Transfer Characteristic", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_7, 1, byte.class, false, DisplayType.INTEGER),
	COLORIMETRIC_SPECIFICATION_7("Colorimetric Specification", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_7, 1, byte.class, false, DisplayType.INTEGER),
	BIT_DEPTH_7("Bit Depth", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_7, 1, byte.class,
			false, DisplayType.INTEGER),
	PACKING_7("Packing", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_7, 2, short.class,
			false, DisplayType.INTEGER),
	ENCODING_7("Encoding", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_7, 2, short.class,
			false, DisplayType.INTEGER),
	OFFSET_TO_DATA_7("Offset to Data", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_7, 4,
			int.class, false, DisplayType.INTEGER),
	END_OF_LINE_PADDING_7("End of Line Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_7, 4, int.class, false, DisplayType.INTEGER),
	END_OF_IMAGE_PADDING_7("End of Image Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_7, 4, int.class, false, DisplayType.INTEGER),
	DESCRIPTION_OF_IMAGE_ELEMENT_7("Description of Image Element", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_7, 32, String.class, true, DisplayType.ASCII),
	// Data Structure for Image Element 8
	DATA_SIGN_8("Data Sign", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_8, 4, int.class,
			false, DisplayType.INTEGER),
	REFERENCE_LOW_DATA_CODE_8("Reference Low Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_8, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_LOW_QUANTITY_8("Reference Low Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_8, 4, float.class, false, DisplayType.FLOAT),
	REFERENCE_HIGH_DATA_CODE_8("Reference High Data Code Value", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_8, 4, int.class, false, DisplayType.INTEGER),
	REFERENCE_HIGH_QUANTITY_8("Reference High Quantity Represented", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_8, 4, float.class, false, DisplayType.FLOAT),
	DESCRIPTOR_8("Descriptor", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_8, 1, byte.class,
			false, DisplayType.INTEGER),
	TRANSFER_CHARACTERISTIC_8("Transfer Characteristic", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_8, 1, byte.class, false, DisplayType.INTEGER),
	COLORIMETRIC_SPECIFICATION_8("Colorimetric Specification", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_8, 1, byte.class, false, DisplayType.INTEGER),
	BIT_DEPTH_8("Bit Depth", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_8, 1, byte.class,
			false, DisplayType.INTEGER),
	PACKING_8("Packing", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_8, 2, short.class,
			false, DisplayType.INTEGER),
	ENCODING_8("Encoding", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_8, 2, short.class,
			false, DisplayType.INTEGER),
	OFFSET_TO_DATA_8("Offset to Data", DPXSection.IMAGE_INFORMATION_HEADER, DPXImageElement.IMAGE_ELEMENT_8, 4,
			int.class, false, DisplayType.INTEGER),
	END_OF_LINE_PADDING_8("End of Line Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_8, 4, int.class, false, DisplayType.INTEGER),
	END_OF_IMAGE_PADDING_8("End of Image Padding", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_8, 4, int.class, false, DisplayType.INTEGER),
	DESCRIPTION_OF_IMAGE_ELEMENT_8("Description of Image Element", DPXSection.IMAGE_INFORMATION_HEADER,
			DPXImageElement.IMAGE_ELEMENT_8, 32, String.class, true, DisplayType.ASCII),

	RESERVED_IMAGE_INFORMATION_HEADER("Reserved", DPXSection.IMAGE_INFORMATION_HEADER, 52, String.class, false,
			DisplayType.ASCII),

	// Image Source Information Header
	X_OFFSET("X Offset", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 4, int.class, false, DisplayType.INTEGER),
	Y_OFFSET("Y Offset", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 4, int.class, false, DisplayType.INTEGER),
	X_CENTER("X Center", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 4, float.class, false, DisplayType.FLOAT),
	Y_CENTER("Y Center", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 4, float.class, false, DisplayType.FLOAT),
	X_ORIGINAL_SIZE("X Original Size", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 4, int.class, false,
			DisplayType.INTEGER),
	Y_ORIGINAL_SIZE("Y Original Size", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 4, int.class, false,
			DisplayType.INTEGER),
	SOURCE_IMAGE_FILENAME("Source Image Filename", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 100, String.class,
			true, DisplayType.ASCII),
	SOURCE_IMAGE_DATETIME("Source Image Date/Time", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 24, String.class,
			"yyyy:MM:dd:HH:mm:ss", true, DisplayType.DATEPICKER),
	INPUT_DEVICE_NAME("Input Device Name", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 32, String.class, true,
			DisplayType.ASCII),
	INPUT_DEVICE_SERIAL_NUMBER("Input Device Serial Number", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 32,
			String.class, true, DisplayType.ASCII),
	BORDER_VALIDITY("Border Validity", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 8, short[].class, false,
			DisplayType.BORDER_VALIDITY),
	PIXEL_ASPECT_RATIO("Pixel Aspect Ratio", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 8, int[].class, false,
			DisplayType.PIXEL_ASPECT_RATIO),
	X_SCANNED_SIZE("X Scanned Size", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 4, float.class, false,
			DisplayType.FLOAT),
	Y_SCANNED_SIZE("Y Scanned Size", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 4, float.class, false,
			DisplayType.FLOAT),
	RESERVED_IMAGE_SOURCE_INFORMATION_HEADER("Reserved", DPXSection.IMAGE_SOURCE_INFORMATION_HEADER, 20,
			String.class, false, DisplayType.ASCII),

	// Motion picture header
	FILM_MFG_ID_CODE("Film mfg. ID code", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 2, String.class, true,
			DisplayType.ASCII),
	FILM_TYPE("Film Type", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 2, String.class, true,
			DisplayType.ASCII),
	OFFSET_IN_PERFS("Offset in Perfs", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 2, String.class, true,
			DisplayType.ASCII),
	PREFIX("Prefix", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 6, String.class, true, DisplayType.ASCII),
	COUNT("Count", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 4, String.class, true, DisplayType.ASCII),
	FORMAT("Format", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 32, String.class, true, DisplayType.ASCII),
	FRAME_POSITION_IN_SEQUENCE("Frame Position in Sequence", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 4,
			int.class, true, DisplayType.INTEGER),
	SEQUENCE_LENGTH("Sequence Length", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 4, int.class, true,
			DisplayType.INTEGER),
	HELD_COUNT("Held Count", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 4, int.class, true,
			DisplayType.INTEGER),
	FRAME_RATE_OF_ORIGINAL("Frame Rate of Original", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 4,
			float.class, true, DisplayType.FLOAT),
	SHUTTER_ANGLE_OF_CAMERA("Shutter Angle of Camera in Degrees", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER,
			4, float.class, true, DisplayType.FLOAT),
	FRAME_IDENTIFICATION("Frame Identification", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 32,
			String.class, true, DisplayType.ASCII),
	SLATE_IDENTIFICATION("Slate Information", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER, 100, String.class,
			true, DisplayType.ASCII),
	RESERVED_MOTION_PICTURE_FILM_INFORMATION_HEADER("Reserved", DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER,
			56, String.class, false, DisplayType.ASCII),

	// Television
	SMTPE_TIMECODE("SMPTE Timecode", DPXSection.TELEVISION_INFORMATION_HEADER, 4, int.class, false,
			DisplayType.FLOAT),
	SMTPE_USERBITS("SMPTE Userbits", DPXSection.TELEVISION_INFORMATION_HEADER, 4, int.class, false,
			DisplayType.FLOAT),
	INTERLACE("Interlace", DPXSection.TELEVISION_INFORMATION_HEADER, 1, byte.class, false, DisplayType.FLOAT),
	FIELD_NUMBER("Field Number", DPXSection.TELEVISION_INFORMATION_HEADER, 1, byte.class, false, DisplayType.FLOAT),
	VIDEO_SIGNAL_STANDARD("Video Signal Standard", DPXSection.TELEVISION_INFORMATION_HEADER, 1, byte.class, false,
			DisplayType.FLOAT),
	ZERO("Zero", DPXSection.TELEVISION_INFORMATION_HEADER, 1, byte.class, false, DisplayType.FLOAT),
	HORIZONTAL_SAMPLING_RATE("Horizontal Sampling Rate", DPXSection.TELEVISION_INFORMATION_HEADER, 4, float.class,
			false, DisplayType.FLOAT),
	VERTICAL_SAMPLING_RATE("Vertical Sampling Rate", DPXSection.TELEVISION_INFORMATION_HEADER, 4, float.class,
			false, DisplayType.FLOAT),
	TEMPORAL_SAMPLING_RATE("Temporal Sampling Rate or Frame Rate", DPXSection.TELEVISION_INFORMATION_HEADER, 4,
			float.class, false, DisplayType.FLOAT),
	TIME_OFFSET("Time Offset From Sync to First Pixel", DPXSection.TELEVISION_INFORMATION_HEADER, 4, float.class,
			false, DisplayType.FLOAT),
	GAMMA("Gamma", DPXSection.TELEVISION_INFORMATION_HEADER, 4, float.class, false, DisplayType.FLOAT),
	BLACK_LEVEL("Black Level Code Value", DPXSection.TELEVISION_INFORMATION_HEADER, 4, float.class, false,
			DisplayType.FLOAT),
	BLACK_GAIN("Black Gain", DPXSection.TELEVISION_INFORMATION_HEADER, 4, float.class, false, DisplayType.FLOAT),
	BREAKPOINT("Breakpoint", DPXSection.TELEVISION_INFORMATION_HEADER, 4, float.class, false, DisplayType.FLOAT),
	REFERENCE_WHITE_LEVEL("Reference White Level Code Value", DPXSection.TELEVISION_INFORMATION_HEADER, 4,
			float.class, false, DisplayType.FLOAT),
	INTEGRATION_TIME("Integration Time", DPXSection.TELEVISION_INFORMATION_HEADER, 4, float.class, false,
			DisplayType.FLOAT),
	RESERVED_TELEVISION_INFORMATION_HEADER("Reserved", DPXSection.TELEVISION_INFORMATION_HEADER, 76, String.class,
			false, DisplayType.ASCII),

	// User defined
	USER_IDENTIFICATION("User Identification", DPXSection.USER_DEFINED_DATA, 32, String.class, true,
			DisplayType.ASCII),
	USER_DEFINED_DATA("User Defined Data", DPXSection.USER_DEFINED_DATA, 10000, String.class, true,
			DisplayType.ASCII);

	private String displayName;
	private DPXSection section;
	private ImageElementDef subsection;
	private int length;
	private Class<?> type;
	private String dateFormat;
	private boolean editable;
	private DisplayType displayType;

	@JsonCreator
	private DPXColumn(String displayName, DPXSection section, ImageElementDef subsection, int length,
			Class<?> type, boolean editable, DisplayType displayType) {
		this.displayName = displayName;
		this.section = section;
		this.subsection = subsection;
		this.length = length;
		this.type = type;
		this.editable = editable;
		this.displayType = displayType;
	}

	@JsonCreator
	private DPXColumn(String displayName, DPXSection section, int length, Class<?> type, boolean editable,
			DisplayType displayType) {
		this.displayName = displayName;
		this.section = section;
		this.length = length;
		this.type = type;
		this.editable = editable;
		this.subsection = DPXImageElement.NONE;
		this.displayType = displayType;
	}

	@JsonCreator
	private DPXColumn(String displayName, DPXSection section, int length, Class<?> type, String dateFormat,
			boolean editable, DisplayType displayType) {
		this.displayName = displayName;
		this.section = section;
		this.length = length;
		this.type = type;
		this.dateFormat = dateFormat;
		this.editable = editable;
		this.subsection = DPXImageElement.NONE;
		this.displayType = displayType;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public String getDisplayName() {
		return displayName;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}

	public boolean getEditable() {
		return editable;
	}

	public String getFormat() {
		return dateFormat;
	}

	public int getLength() {
		return length;
	}

	public DPXSection getSection() {
		return section;
	}

	public String getSectionDisplayName() {
		return section.getDisplayName();
	}

	public ImageElementDef getSubsection() {
		return subsection;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean hasSubsection() {
		return subsection != DPXImageElement.NONE;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	@Override
	public int getSortOrder() {
		return 0;
	}

	@Override
	public boolean isRequired() {
		// TODO Auto-generated method stub
		return false;
	}
}
