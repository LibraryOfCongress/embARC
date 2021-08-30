package com.portalmedia.embarc.parser.dpx;

import java.util.HashMap;

import com.portalmedia.embarc.parser.ColumnDef;

/**
 * Singleton containing help text for all columns. Used in error reporting and
 * tooltips on tableview
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXColumnHelpText {

	private static DPXColumnHelpText instance = null;

	public static DPXColumnHelpText getInstance() {
		if (instance == null) {
			instance = new DPXColumnHelpText();
		}
		return instance;
	}

	private HashMap<DPXColumn, String> helpTextSet;

	private DPXColumnHelpText() {
		BuildHelpTextSet();
	}

	private void AddHelpText(DPXColumn column, String helpText) {
		helpTextSet.put(column, helpText);
	}

	private void BuildHelpTextSet() {
		helpTextSet = new HashMap<>();

		// File Information Header
		AddHelpText(DPXColumn.MAGIC_NUMBER,
				"Field Size: 4 bytes.\n\nType: ASCII.\n\nDefinition: Indicates the start of the image file and is used to determine byte order. The file format allows machines to create files in either of the two most common byte orders, whichever is easier for that machine. Byte-order translation is only required for machines reading files that were created on a machine with reverse byte order. Programs creating DPX files should write the magic number with the ASCII value of \"SDPX\" (0x53445058 hex). Programs reading DPX files should use the first four bytes to determine the byte order of the file. The first four bytes will be S, D, P, X if the byte order is most significant byte first, or X, P, D, S if the byte order is least significant byte first.");
		AddHelpText(DPXColumn.OFFSET_TO_IMAGE_DATA,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Declaration of the byte offset to the beginning of the image data counting up from 0 bytes at the start of the file.");
		AddHelpText(DPXColumn.VERSION_NUMBER_OF_HEADER_FORMAT,
				"Field Size: 8 bytes. \n\n\n\nType: ASCII. \n\nDefinition: Declaration of the version number of the DPX format. There are currently two versions of DPX: Version 1 (V1.0) defined by ST 268:1998 and Version 2 (V2.0) defined by ST 268:2003 and Amd 1.");
		AddHelpText(DPXColumn.TOTAL_IMAGE_FILE_SIZE,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Indicates the size of the entire file, i.e. containing both header and image data.");
		AddHelpText(DPXColumn.DITTO_KEY,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Indicates that all fields are the same as the previous frame in the sequence except for fields related to the frame number (48, 50, 58, 61). Also, the offsets to the image data (21.12) will change if run-length encoding is used. The ditto key is a read-time shortcut only, and the other fields in the header must still be filled in when the file is created.");
		AddHelpText(DPXColumn.GENERIC_SECTION_HEADER_LENGTH,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Indicates the length of the Generic Section Header in bytes.");
		AddHelpText(DPXColumn.INDUSTRY_SPECIFIC_HEADER_LENGTH,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Indicates the length of the Industry Specific Header in bytes.");
		AddHelpText(DPXColumn.USER_DEFINED_HEADER_LENGTH,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Indicates the length of the User-defined Header in bytes.");
		AddHelpText(DPXColumn.IMAGE_FILE_NAME,
				"Field Size: 100 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed the filename of the DPX file. FADGI Usage: This element should match the filename of the file. The filename must differentiate one DPX frame from another within the same DPX sequence at the file level. Filenames should be unique within the same DPX sequence and ideally within the collection and holding institution.");
		AddHelpText(DPXColumn.CREATION_DATETIME,
				"Field Size: 24 bytes. \n\nType: ASCII. \n\nDefinition: Date this file was created. FADGI Usage: FADGI advises that data and timed data follow ISO 8601 formatting rather than the non-standard formatting that is defined in ST 268 2003. Following the standard ISO 8601 formatting will allow the data to be integrated into other systems with fewer migration and validation issues. Specifically, FADGI recommends the use of ISO 8601 data component separators: colons [:] are used to separate the time elements \"hour\" and \"minute\", and \"minute\" and \"second\"; and hyphens [-] are used to separate the time elements \"year\" and \"month\", \"year\" and \"week\", \"year\" and \"day'', \"month\" and \"day\", and \"week\" and \"day''. In addition, FADGI recommends that the creation time is recorded as Coordinated Universal Time (UTC) as defined in ISO 8601:2004(E) section 4.2.4. For full expressions of data with both date and time information, FADGI recommends the character [T] be used as time designator to indicate the start of the representation of the time of day component.");
		AddHelpText(DPXColumn.CREATOR,
				"Field Size: 100 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed the name of the creator of the DPX file. FADGI Usage: This element contains the institution or entity responsible for the creation, maintenance, and preservation of this digital item. Entity designations should be as specific as possible including a twocharacter county code to avoid the potential for conflict in the responsible organization’s name. Should follow this convention: [Country code]comma space[Entity name].");
		AddHelpText(DPXColumn.PROJECT_NAME,
				"Field Size: 200 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed the name of the project associated with the DPX file. FADGI Usage: This element is recommended as a container for identifiers for the work represented by the DPX sequence at hand and/or as pointers to additional, non-embedded (externally maintained) metadata. Members of the Working Group have repeatedly encountered the need to provide multiple identifiers for a given item. Should follow these conventions. If labeled: Identifier [comma space] type [comma space] comment [semicolon-space if more than one identifier]. If no labeling: Identifier.");
		AddHelpText(DPXColumn.COPYRIGHT_STATEMENT,
				"Field Size: 200 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed rights information about the content of the DPX file. FADGI Usage: Information about copyright and other restrictions (donor, privacy, etc.). Usage by federal agencies will often refer to the documentation of restrictions provided by other, non-embedded metadata. If there are multiple copyrights or other restriction statements, separate them by a semicolon followed by a space.");
		AddHelpText(DPXColumn.ENCRYPTION_KEY,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Indicates that the image data is encrypted to prevent unauthorized use. The default is FFFFFFFF for no encryption. Any other value indicates that the image data is encrypted and this value can be used as the encryption key. Note that the header data is not encrypted.");
		AddHelpText(DPXColumn.RESERVED_FILE_INFORMATION_HEADER,
				"Field Size: 104 bytes. \n\nType: n/a. \n\nDefinition: Reserved by SMPTE for future use.");

		// Image Information Header
		AddHelpText(DPXColumn.IMAGE_ORIENTATION,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: Indicates the orientation of the image data required for display. The possible orientations are listed in Table 2 of the SMPTE 268 standard. The standard orientation for core set images (code 0) is left to right (line direction) and top to bottom (frame direction).");
		AddHelpText(DPXColumn.NUMBER_OF_IMAGE_ELEMENTS,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: Each file represents a single image with up to eight (8) image elements. Image elements are defined as a single component (e.g. luma) or multiple components (e.g. red, green, and blue). This field declares how many image elements are present in the file.");
		AddHelpText(DPXColumn.PIXELS_PER_LINE,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Indicates the active number of pixels per line.");
		AddHelpText(DPXColumn.LINES_PER_IMAGE_ELEMENT,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Indicates the active number of lines for each image element.");

		AddHelpText(DPXColumn.DATA_SIGN_1,
				"Field Size: 4 bytes. Type u32int. \n\nDefinition: Identification of the image’s data type. Signed data can hold both positive and negative values while unsigned types can hold large positive values but cannot hold negative values. DPX Core set images are unsigned by default as specified in ST 268.");
		AddHelpText(DPXColumn.REFERENCE_LOW_DATA_CODE_1,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the minimum expected code value for image data. For printing density, the default value is 0. For ITU-R 601-5 luma, the default value is 16.");
		AddHelpText(DPXColumn.REFERENCE_LOW_QUANTITY_1,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference low data code value. For printing density, the default is a density of 0.00. For ITU-R 601-5, the luma default is 0 mV.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_DATA_CODE_1,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the maximum expected code value for image data. For 10-bit printing density, the default code value is 1023. For ITU-R 601-5 luma, the default value is 235.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_QUANTITY_1,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference high data code value. For printing density, the default is a density of 2.048. For ITU-R 601-5 luma, the default is 700 mV.");
		AddHelpText(DPXColumn.DESCRIPTOR_1,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the components that make up an image element and their pixel packing order. The valid components are listed in table 1 of ST 268.");
		AddHelpText(DPXColumn.TRANSFER_CHARACTERISTIC_1,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the amplitude transfer function used to transform the data from a linear original. The inverse of the transfer function is needed to recreate a linear image element (see Table 5A of ST 268).");
		AddHelpText(DPXColumn.COLORIMETRIC_SPECIFICATION_1,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the appropriate color reference primaries (for additive color systems like television) or color responses (for printing density) (see Table 5B of ST 268).");
		AddHelpText(DPXColumn.BIT_DEPTH_1,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the number of bits for each component in the image element. All components must have the same bit depth. Valid bit depths are 1-, 8-, 10-, 12-, and 16-bit integer, and 32- and 64-bit IEEE floating point (see Table 3A of ST 268).");
		AddHelpText(DPXColumn.PACKING_1,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines the data packing mode. The valid options are listed in Table 3B in ST 268.");
		AddHelpText(DPXColumn.ENCODING_1,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines whether or not the element is run-length encoded. The valid options are listed in Table 3C in ST 268.");
		AddHelpText(DPXColumn.OFFSET_TO_DATA_1,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Offest to data for image element n, defines the offset in bytes to the image data for element n from the beginning of the file.");
		AddHelpText(DPXColumn.END_OF_LINE_PADDING_1,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each line. The default is 0 (no padding).");
		AddHelpText(DPXColumn.END_OF_IMAGE_PADDING_1,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each image element. The default is 0 (no padding).");
		AddHelpText(DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_1,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed a short description differentiating this image element from any other image elements (if present) in the file.");

		AddHelpText(DPXColumn.DATA_SIGN_2,
				"Field Size: 4 bytes. Type u32int. \n\nDefinition: Identification of the image’s data type. Signed data can hold both positive and negative values while unsigned types can hold large positive values but cannot hold negative values. DPX Core set images are unsigned by default as specified in ST 268.");
		AddHelpText(DPXColumn.REFERENCE_LOW_DATA_CODE_2,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the minimum expected code value for image data. For printing density, the default value is 0. For ITU-R 601-5 luma, the default value is 16.");
		AddHelpText(DPXColumn.REFERENCE_LOW_QUANTITY_2,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference low data code value. For printing density, the default is a density of 0.00. For ITU-R 601-5, the luma default is 0 mV.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_DATA_CODE_2,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the maximum expected code value for image data. For 10-bit printing density, the default code value is 1023. For ITU-R 601-5 luma, the default value is 235.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_QUANTITY_2,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference high data code value. For printing density, the default is a density of 2.048. For ITU-R 601-5 luma, the default is 700 mV.");
		AddHelpText(DPXColumn.DESCRIPTOR_2,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the components that make up an image element and their pixel packing order. The valid components are listed in table 1 of ST 268.");
		AddHelpText(DPXColumn.TRANSFER_CHARACTERISTIC_2,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the amplitude transfer function used to transform the data from a linear original. The inverse of the transfer function is needed to recreate a linear image element (see Table 5A of ST 268).");
		AddHelpText(DPXColumn.COLORIMETRIC_SPECIFICATION_2,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the appropriate color reference primaries (for additive color systems like television) or color responses (for printing density) (see Table 5B of ST 268).");
		AddHelpText(DPXColumn.BIT_DEPTH_2,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the number of bits for each component in the image element. All components must have the same bit depth. Valid bit depths are 1-, 8-, 10-, 12-, and 16-bit integer, and 32- and 64-bit IEEE floating point (see Table 3A of ST 268).");
		AddHelpText(DPXColumn.PACKING_2,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines the data packing mode. The valid options are listed in Table 3B in ST 268.");
		AddHelpText(DPXColumn.ENCODING_2,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines whether or not the element is run-length encoded. The valid options are listed in Table 3C in ST 268.");
		AddHelpText(DPXColumn.OFFSET_TO_DATA_2,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Offest to data for image element n, defines the offset in bytes to the image data for element n from the beginning of the file.");
		AddHelpText(DPXColumn.END_OF_LINE_PADDING_2,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each line. The default is 0 (no padding).");
		AddHelpText(DPXColumn.END_OF_IMAGE_PADDING_2,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each image element. The default is 0 (no padding).");
		AddHelpText(DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_2,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed a short description differentiating this image element from any other image elements (if present) in the file.");

		AddHelpText(DPXColumn.DATA_SIGN_3,
				"Field Size: 4 bytes. Type u32int. \n\nDefinition: Identification of the image’s data type. Signed data can hold both positive and negative values while unsigned types can hold large positive values but cannot hold negative values. DPX Core set images are unsigned by default as specified in ST 268.");
		AddHelpText(DPXColumn.REFERENCE_LOW_DATA_CODE_3,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the minimum expected code value for image data. For printing density, the default value is 0. For ITU-R 601-5 luma, the default value is 16.");
		AddHelpText(DPXColumn.REFERENCE_LOW_QUANTITY_3,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference low data code value. For printing density, the default is a density of 0.00. For ITU-R 601-5, the luma default is 0 mV.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_DATA_CODE_3,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the maximum expected code value for image data. For 10-bit printing density, the default code value is 1023. For ITU-R 601-5 luma, the default value is 235.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_QUANTITY_3,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference high data code value. For printing density, the default is a density of 2.048. For ITU-R 601-5 luma, the default is 700 mV.");
		AddHelpText(DPXColumn.DESCRIPTOR_3,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the components that make up an image element and their pixel packing order. The valid components are listed in table 1 of ST 268.");
		AddHelpText(DPXColumn.TRANSFER_CHARACTERISTIC_3,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the amplitude transfer function used to transform the data from a linear original. The inverse of the transfer function is needed to recreate a linear image element (see Table 5A of ST 268).");
		AddHelpText(DPXColumn.COLORIMETRIC_SPECIFICATION_3,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the appropriate color reference primaries (for additive color systems like television) or color responses (for printing density) (see Table 5B of ST 268).");
		AddHelpText(DPXColumn.BIT_DEPTH_3,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the number of bits for each component in the image element. All components must have the same bit depth. Valid bit depths are 1-, 8-, 10-, 12-, and 16-bit integer, and 32- and 64-bit IEEE floating point (see Table 3A of ST 268).");
		AddHelpText(DPXColumn.PACKING_3,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines the data packing mode. The valid options are listed in Table 3B in ST 268.");
		AddHelpText(DPXColumn.ENCODING_3,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines whether or not the element is run-length encoded. The valid options are listed in Table 3C in ST 268.");
		AddHelpText(DPXColumn.OFFSET_TO_DATA_3,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Offest to data for image element n, defines the offset in bytes to the image data for element n from the beginning of the file.");
		AddHelpText(DPXColumn.END_OF_LINE_PADDING_3,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each line. The default is 0 (no padding).");
		AddHelpText(DPXColumn.END_OF_IMAGE_PADDING_3,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each image element. The default is 0 (no padding).");
		AddHelpText(DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_3,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed a short description differentiating this image element from any other image elements (if present) in the file.");

		AddHelpText(DPXColumn.DATA_SIGN_4,
				"Field Size: 4 bytes. Type u32int. \n\nDefinition: Identification of the image’s data type. Signed data can hold both positive and negative values while unsigned types can hold large positive values but cannot hold negative values. DPX Core set images are unsigned by default as specified in ST 268.");
		AddHelpText(DPXColumn.REFERENCE_LOW_DATA_CODE_4,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the minimum expected code value for image data. For printing density, the default value is 0. For ITU-R 601-5 luma, the default value is 16.");
		AddHelpText(DPXColumn.REFERENCE_LOW_QUANTITY_4,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference low data code value. For printing density, the default is a density of 0.00. For ITU-R 601-5, the luma default is 0 mV.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_DATA_CODE_4,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the maximum expected code value for image data. For 10-bit printing density, the default code value is 1023. For ITU-R 601-5 luma, the default value is 235.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_QUANTITY_4,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference high data code value. For printing density, the default is a density of 2.048. For ITU-R 601-5 luma, the default is 700 mV.");
		AddHelpText(DPXColumn.DESCRIPTOR_4,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the components that make up an image element and their pixel packing order. The valid components are listed in table 1 of ST 268.");
		AddHelpText(DPXColumn.TRANSFER_CHARACTERISTIC_4,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the amplitude transfer function used to transform the data from a linear original. The inverse of the transfer function is needed to recreate a linear image element (see Table 5A of ST 268).");
		AddHelpText(DPXColumn.COLORIMETRIC_SPECIFICATION_4,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the appropriate color reference primaries (for additive color systems like television) or color responses (for printing density) (see Table 5B of ST 268).");
		AddHelpText(DPXColumn.BIT_DEPTH_4,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the number of bits for each component in the image element. All components must have the same bit depth. Valid bit depths are 1-, 8-, 10-, 12-, and 16-bit integer, and 32- and 64-bit IEEE floating point (see Table 3A of ST 268).");
		AddHelpText(DPXColumn.PACKING_4,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines the data packing mode. The valid options are listed in Table 3B in ST 268.");
		AddHelpText(DPXColumn.ENCODING_4,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines whether or not the element is run-length encoded. The valid options are listed in Table 3C in ST 268.");
		AddHelpText(DPXColumn.OFFSET_TO_DATA_4,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Offest to data for image element n, defines the offset in bytes to the image data for element n from the beginning of the file.");
		AddHelpText(DPXColumn.END_OF_LINE_PADDING_4,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each line. The default is 0 (no padding).");
		AddHelpText(DPXColumn.END_OF_IMAGE_PADDING_4,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each image element. The default is 0 (no padding).");
		AddHelpText(DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_4,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed a short description differentiating this image element from any other image elements (if present) in the file.");

		AddHelpText(DPXColumn.DATA_SIGN_5,
				"Field Size: 4 bytes. Type u32int. \n\nDefinition: Identification of the image’s data type. Signed data can hold both positive and negative values while unsigned types can hold large positive values but cannot hold negative values. DPX Core set images are unsigned by default as specified in ST 268.");
		AddHelpText(DPXColumn.REFERENCE_LOW_DATA_CODE_5,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the minimum expected code value for image data. For printing density, the default value is 0. For ITU-R 601-5 luma, the default value is 16.");
		AddHelpText(DPXColumn.REFERENCE_LOW_QUANTITY_5,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference low data code value. For printing density, the default is a density of 0.00. For ITU-R 601-5, the luma default is 0 mV.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_DATA_CODE_5,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the maximum expected code value for image data. For 10-bit printing density, the default code value is 1023. For ITU-R 601-5 luma, the default value is 235.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_QUANTITY_5,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference high data code value. For printing density, the default is a density of 2.048. For ITU-R 601-5 luma, the default is 700 mV.");
		AddHelpText(DPXColumn.DESCRIPTOR_5,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the components that make up an image element and their pixel packing order. The valid components are listed in table 1 of ST 268.");
		AddHelpText(DPXColumn.TRANSFER_CHARACTERISTIC_5,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the amplitude transfer function used to transform the data from a linear original. The inverse of the transfer function is needed to recreate a linear image element (see Table 5A of ST 268).");
		AddHelpText(DPXColumn.COLORIMETRIC_SPECIFICATION_5,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the appropriate color reference primaries (for additive color systems like television) or color responses (for printing density) (see Table 5B of ST 268).");
		AddHelpText(DPXColumn.BIT_DEPTH_5,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the number of bits for each component in the image element. All components must have the same bit depth. Valid bit depths are 1-, 8-, 10-, 12-, and 16-bit integer, and 32- and 64-bit IEEE floating point (see Table 3A of ST 268).");
		AddHelpText(DPXColumn.PACKING_5,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines the data packing mode. The valid options are listed in Table 3B in ST 268.");
		AddHelpText(DPXColumn.ENCODING_5,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines whether or not the element is run-length encoded. The valid options are listed in Table 3C in ST 268.");
		AddHelpText(DPXColumn.OFFSET_TO_DATA_5,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Offest to data for image element n, defines the offset in bytes to the image data for element n from the beginning of the file.");
		AddHelpText(DPXColumn.END_OF_LINE_PADDING_5,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each line. The default is 0 (no padding).");
		AddHelpText(DPXColumn.END_OF_IMAGE_PADDING_5,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each image element. The default is 0 (no padding).");
		AddHelpText(DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_5,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed a short description differentiating this image element from any other image elements (if present) in the file.");

		AddHelpText(DPXColumn.DATA_SIGN_6,
				"Field Size: 4 bytes. Type u32int. \n\nDefinition: Identification of the image’s data type. Signed data can hold both positive and negative values while unsigned types can hold large positive values but cannot hold negative values. DPX Core set images are unsigned by default as specified in ST 268.");
		AddHelpText(DPXColumn.REFERENCE_LOW_DATA_CODE_6,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the minimum expected code value for image data. For printing density, the default value is 0. For ITU-R 601-5 luma, the default value is 16.");
		AddHelpText(DPXColumn.REFERENCE_LOW_QUANTITY_6,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference low data code value. For printing density, the default is a density of 0.00. For ITU-R 601-5, the luma default is 0 mV.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_DATA_CODE_6,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the maximum expected code value for image data. For 10-bit printing density, the default code value is 1023. For ITU-R 601-5 luma, the default value is 235.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_QUANTITY_6,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference high data code value. For printing density, the default is a density of 2.048. For ITU-R 601-5 luma, the default is 700 mV.");
		AddHelpText(DPXColumn.DESCRIPTOR_6,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the components that make up an image element and their pixel packing order. The valid components are listed in table 1 of ST 268.");
		AddHelpText(DPXColumn.TRANSFER_CHARACTERISTIC_6,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the amplitude transfer function used to transform the data from a linear original. The inverse of the transfer function is needed to recreate a linear image element (see Table 5A of ST 268).");
		AddHelpText(DPXColumn.COLORIMETRIC_SPECIFICATION_6,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the appropriate color reference primaries (for additive color systems like television) or color responses (for printing density) (see Table 5B of ST 268).");
		AddHelpText(DPXColumn.BIT_DEPTH_6,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the number of bits for each component in the image element. All components must have the same bit depth. Valid bit depths are 1-, 8-, 10-, 12-, and 16-bit integer, and 32- and 64-bit IEEE floating point (see Table 3A of ST 268).");
		AddHelpText(DPXColumn.PACKING_6,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines the data packing mode. The valid options are listed in Table 3B in ST 268.");
		AddHelpText(DPXColumn.ENCODING_6,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines whether or not the element is run-length encoded. The valid options are listed in Table 3C in ST 268.");
		AddHelpText(DPXColumn.OFFSET_TO_DATA_6,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Offest to data for image element n, defines the offset in bytes to the image data for element n from the beginning of the file.");
		AddHelpText(DPXColumn.END_OF_LINE_PADDING_6,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each line. The default is 0 (no padding).");
		AddHelpText(DPXColumn.END_OF_IMAGE_PADDING_6,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each image element. The default is 0 (no padding).");
		AddHelpText(DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_6,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed a short description differentiating this image element from any other image elements (if present) in the file.");

		AddHelpText(DPXColumn.DATA_SIGN_7,
				"Field Size: 4 bytes. Type u32int. \n\nDefinition: Identification of the image’s data type. Signed data can hold both positive and negative values while unsigned types can hold large positive values but cannot hold negative values. DPX Core set images are unsigned by default as specified in ST 268.");
		AddHelpText(DPXColumn.REFERENCE_LOW_DATA_CODE_7,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the minimum expected code value for image data. For printing density, the default value is 0. For ITU-R 601-5 luma, the default value is 16.");
		AddHelpText(DPXColumn.REFERENCE_LOW_QUANTITY_7,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference low data code value. For printing density, the default is a density of 0.00. For ITU-R 601-5, the luma default is 0 mV.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_DATA_CODE_7,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the maximum expected code value for image data. For 10-bit printing density, the default code value is 1023. For ITU-R 601-5 luma, the default value is 235.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_QUANTITY_7,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference high data code value. For printing density, the default is a density of 2.048. For ITU-R 601-5 luma, the default is 700 mV.");
		AddHelpText(DPXColumn.DESCRIPTOR_7,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the components that make up an image element and their pixel packing order. The valid components are listed in table 1 of ST 268.");
		AddHelpText(DPXColumn.TRANSFER_CHARACTERISTIC_7,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the amplitude transfer function used to transform the data from a linear original. The inverse of the transfer function is needed to recreate a linear image element (see Table 5A of ST 268).");
		AddHelpText(DPXColumn.COLORIMETRIC_SPECIFICATION_7,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the appropriate color reference primaries (for additive color systems like television) or color responses (for printing density) (see Table 5B of ST 268).");
		AddHelpText(DPXColumn.BIT_DEPTH_7,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the number of bits for each component in the image element. All components must have the same bit depth. Valid bit depths are 1-, 8-, 10-, 12-, and 16-bit integer, and 32- and 64-bit IEEE floating point (see Table 3A of ST 268).");
		AddHelpText(DPXColumn.PACKING_7,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines the data packing mode. The valid options are listed in Table 3B in ST 268.");
		AddHelpText(DPXColumn.ENCODING_7,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines whether or not the element is run-length encoded. The valid options are listed in Table 3C in ST 268.");
		AddHelpText(DPXColumn.OFFSET_TO_DATA_7,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Offest to data for image element n, defines the offset in bytes to the image data for element n from the beginning of the file.");
		AddHelpText(DPXColumn.END_OF_LINE_PADDING_7,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each line. The default is 0 (no padding).");
		AddHelpText(DPXColumn.END_OF_IMAGE_PADDING_7,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each image element. The default is 0 (no padding).");
		AddHelpText(DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_7,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed a short description differentiating this image element from any other image elements (if present) in the file.");

		AddHelpText(DPXColumn.DATA_SIGN_8,
				"Field Size: 4 bytes. Type u32int. \n\nDefinition: Identification of the image’s data type. Signed data can hold both positive and negative values while unsigned types can hold large positive values but cannot hold negative values. DPX Core set images are unsigned by default as specified in ST 268.");
		AddHelpText(DPXColumn.REFERENCE_LOW_DATA_CODE_8,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the minimum expected code value for image data. For printing density, the default value is 0. For ITU-R 601-5 luma, the default value is 16.");
		AddHelpText(DPXColumn.REFERENCE_LOW_QUANTITY_8,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference low data code value. For printing density, the default is a density of 0.00. For ITU-R 601-5, the luma default is 0 mV.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_DATA_CODE_3,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the maximum expected code value for image data. For 10-bit printing density, the default code value is 1023. For ITU-R 601-5 luma, the default value is 235.");
		AddHelpText(DPXColumn.REFERENCE_HIGH_QUANTITY_8,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the corresponding signal level or measured value to the reference high data code value. For printing density, the default is a density of 2.048. For ITU-R 601-5 luma, the default is 700 mV.");
		AddHelpText(DPXColumn.DESCRIPTOR_8,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the components that make up an image element and their pixel packing order. The valid components are listed in table 1 of ST 268.");
		AddHelpText(DPXColumn.TRANSFER_CHARACTERISTIC_8,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the amplitude transfer function used to transform the data from a linear original. The inverse of the transfer function is needed to recreate a linear image element (see Table 5A of ST 268).");
		AddHelpText(DPXColumn.COLORIMETRIC_SPECIFICATION_8,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the appropriate color reference primaries (for additive color systems like television) or color responses (for printing density) (see Table 5B of ST 268).");
		AddHelpText(DPXColumn.BIT_DEPTH_8,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the number of bits for each component in the image element. All components must have the same bit depth. Valid bit depths are 1-, 8-, 10-, 12-, and 16-bit integer, and 32- and 64-bit IEEE floating point (see Table 3A of ST 268).");
		AddHelpText(DPXColumn.PACKING_8,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines the data packing mode. The valid options are listed in Table 3B in ST 268.");
		AddHelpText(DPXColumn.ENCODING_8,
				"Field Size: 2 bytes. \n\nType: u16int. \n\nDefinition: For image element n, defines whether or not the element is run-length encoded. The valid options are listed in Table 3C in ST 268.");
		AddHelpText(DPXColumn.OFFSET_TO_DATA_8,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Offest to data for image element n, defines the offset in bytes to the image data for element n from the beginning of the file.");
		AddHelpText(DPXColumn.END_OF_LINE_PADDING_8,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each line. The default is 0 (no padding).");
		AddHelpText(DPXColumn.END_OF_IMAGE_PADDING_8,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies the number of padded bytes at the end of each image element. The default is 0 (no padding).");
		AddHelpText(DPXColumn.DESCRIPTION_OF_IMAGE_ELEMENT_8,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: A location to embed a short description differentiating this image element from any other image elements (if present) in the file.");

		AddHelpText(DPXColumn.RESERVED_IMAGE_INFORMATION_HEADER,
				"Field Size: 52 bytes. \n\nType: n/a. \n\nDefinition: Reserved by SMPTE for future use.");

		// Image Source Information Header
		AddHelpText(DPXColumn.X_OFFSET,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the line offset (in pixels) from the first pixel in the original image. The default is 0. This is useful if an image is cropped and the user wishes to specify its location with respect to the original contiguous image.");
		AddHelpText(DPXColumn.Y_OFFSET,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the frame offset (in lines) from the first line in the original contiguous image. The default is 0.");
		AddHelpText(DPXColumn.X_CENTER,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the X image center in pixel units.");
		AddHelpText(DPXColumn.Y_CENTER,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the Y image center in line units.");
		AddHelpText(DPXColumn.X_ORIGINAL_SIZE,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the number of pixels per line in the original image.");
		AddHelpText(DPXColumn.Y_ORIGINAL_SIZE,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the number of lines per image in the original image.");
		AddHelpText(DPXColumn.SOURCE_IMAGE_FILENAME,
				"Field Size: 100 bytes. \n\nType: ASCII. \n\nDefinition: Defines the source image from which this image was extracted or processed.");
		AddHelpText(DPXColumn.SOURCE_IMAGE_DATETIME,
				"Field Size: 24 bytes. \n\nType: ASCII. \n\nDefinition: Defines the creation time of the source image from which the image was extracted or processed. FADGI Usage: FADGI advises that data and timed data follow ISO 8601 formatting rather than the non-standard formatting that is defined in ST 268 2003. Following the standard ISO 8601 formatting will allow the data to be integrated into other systems with fewer migration and validation issues. Specifically, FADGI recommends the use of ISO 8601 data component separators: colons [:] are used to separate the time elements \"hour\" and \"minute\", and \"minute\" and \"second\"; and hyphens [-] are used to separate the time elements \"year\" and \"month\", \"year\" and \"week\", \"year\" and \"day'', \"month\" and \"day\", and \"week\" and \"day''. In addition, FADGI recommends that the creation time is recorded as Coordinated Universal Time (UTC) as defined in ISO 8601:2004(E) section 4.2.4. For full expressions of data with both date and time information, FADGI recommends the character [T] be used as time designator to indicate the start of the representation of the time of day component.");
		AddHelpText(DPXColumn.INPUT_DEVICE_NAME,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: Manufacturer name and model name of film scanner that digitally scanned the motion picture film and produced the DPX file.");
		AddHelpText(DPXColumn.INPUT_DEVICE_SERIAL_NUMBER,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: The serial number of film scanner named in Input Device Name that digitally scanned the motion picture film and produced the DPX file.");
		AddHelpText(DPXColumn.BORDER_VALIDITY,
				"Field Size: 8 bytes (an array of 4 2-byte values). \n\nType: u16int [4]. \n\nDefinition: Defines the region of an image that is eroded due to edge-sensitive filtering operations. The X-left, X-right, Y-top, and Y-bottom value defines the width of the eroded border. The default is 0,0,0,0 in pixel units (no erosion).");
		AddHelpText(DPXColumn.PIXEL_ASPECT_RATIO,
				"Field Size: 8 bytes (an array of 2 4-byte values). \n\nType: u32int [2]. \n\nDefinition: Specified as the ratio of a horizontal integer and a vertical integer. For example, a SMPTE ST 274 signal has a pixel aspect ratio of 1:1, which is 1920 active pixels and 1080 active lines in a 16:9 frame.");
		AddHelpText(DPXColumn.X_SCANNED_SIZE,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the horizontal size of the original scanned optical image in millimeters.");
		AddHelpText(DPXColumn.Y_SCANNED_SIZE,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the vertical size of the original scanned optical image in millimeters.");
		AddHelpText(DPXColumn.RESERVED_IMAGE_SOURCE_INFORMATION_HEADER,
				"Field Size: 20 bytes. \n\nType: n/a. \n\nDefinition: Reserved by SMPTE for future use.");

		// Motion Picture Header
		AddHelpText(DPXColumn.FILM_MFG_ID_CODE,
				"Field Size: 2 bytes. \n\nType: ASCII. \n\nDefinition: Film mfg. ID code (2 digits from film edge code). The 2 digit code to identify the manufacturer of the digitized film.");
		AddHelpText(DPXColumn.FILM_TYPE,
				"Field Size: 2 bytes. \n\nType: ASCII. \n\nDefinition: Encodes data from machine readable portion of film edge code, according to SMPTE ST 254. The 2 digit code to identify the type of film that was digitized. Although the data represented by the code varies by manufacturer, the code may define information about the gauge, emulsion, and film base and more.");
		AddHelpText(DPXColumn.OFFSET_IN_PERFS,
				"Field Size: 2 bytes. \n\nType: ASCII. \n\nDefinition: Offset in Perfs (2 digits from film edge code).");
		AddHelpText(DPXColumn.PREFIX,
				"Field Size: 6 bytes. \n\nType: ASCII. \n\nDefinition: Prefix (6 digits from edge code).");
		AddHelpText(DPXColumn.COUNT,
				"Field Size: 4 bytes. \n\nType: ASCII. \n\nDefinition: Count (4 digits from edge code).");
		AddHelpText(DPXColumn.FORMAT,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: Format, e.g., Academy.");
		AddHelpText(DPXColumn.FRAME_POSITION_IN_SEQUENCE,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Offset in Perfs (2 digits from film edge code).");
		AddHelpText(DPXColumn.SEQUENCE_LENGTH,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Defines the total number of frames in the image sequence.");
		AddHelpText(DPXColumn.HELD_COUNT,
				"Field Size: 4 bytes. \n\nType: u32int. \n\nDefinition: Specifies how many sequential frames for which to hold the current frame. In animation, it is often desirable to hold identical frames.");
		AddHelpText(DPXColumn.FRAME_RATE_OF_ORIGINAL,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Frame rate of the original motion picture object (frames/s).");
		AddHelpText(DPXColumn.SHUTTER_ANGLE_OF_CAMERA,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the shutter angle in degrees of the motion-picture camera. This specifies the temporal sampling aperture.");
		AddHelpText(DPXColumn.FRAME_IDENTIFICATION,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: A user-defined field that labels select frames as key frames or wedge frames, etc.");
		AddHelpText(DPXColumn.SLATE_IDENTIFICATION,
				"Field Size: 100 bytes. \n\nType: ASCII. \n\nDefinition: A user-defined field for recording production information from the camera slates.");
		AddHelpText(DPXColumn.RESERVED_MOTION_PICTURE_FILM_INFORMATION_HEADER,
				"Field Size: 56 bytes. \n\nType: n/a. \n\nDefinition: Reserved by SMPTE for future use.");

		// Television Header
		AddHelpText(DPXColumn.SMTPE_TIMECODE,
				"Field Size: 4 bytes. \n\nType: SMPTE timecode. \n\nDefinition: The characters are encoded into the 32-bit word according to Table 6 of ST 268.");
		AddHelpText(DPXColumn.SMTPE_USERBITS,
				"Field Size: 4 bytes. \n\nType: SMPTE timecode. \n\nDefinition: These are encoded according to Table 6 of ST 268.");
		AddHelpText(DPXColumn.INTERLACE,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Indicates whether the source image was interlaced or not.");
		AddHelpText(DPXColumn.FIELD_NUMBER,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Indicates the field number of the first field in the file, may be 1 or 2 for component video, 1 to 4 for NTSC or component video decoded from NTSC, or 1 to 12 for PAL or component video decoded from PAL. Color frame sequence information is useful when decoding and subsequently re-encoding component video. The field number is set to 0 where field designation is inappropriate.");
		AddHelpText(DPXColumn.VIDEO_SIGNAL_STANDARD,
				"Field Size: 1 byte. \n\nType: u8int. \n\nDefinition: Defines the video source. Video signal standards are listed in Table 4 of ST 268.");
		AddHelpText(DPXColumn.ZERO,
				"Field Size: 1 byte. \n\nType: n/a. \n\nDefinition: Always zero, used for byte alignment within the header.");
		AddHelpText(DPXColumn.HORIZONTAL_SAMPLING_RATE,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: The clock rate at which samples were acquired. This is an inverse function of the total number of samples per scan line, rather than the active number of pixels per line indicated in field 19. Thus, for SMPTE ST 274 at 24.00 Hz frame rate, for example, it would be 74.25 MHz.");
		AddHelpText(DPXColumn.VERTICAL_SAMPLING_RATE,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: The rate at which the scanning of the whole extent of the image is repeated, even if each such scan is incomplete, i.e. is interlaced. Thus, for example, although 625/50 scanning has a true frame rate of 25 Hz, its vertical sampling rate would be considered to be 50 Hz.");
		AddHelpText(DPXColumn.TEMPORAL_SAMPLING_RATE,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Temporal sampling rate or frame rate (Hz).");
		AddHelpText(DPXColumn.TIME_OFFSET,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the edge of the digital image with respect to sync and the sampling phase which is necessary to reconstruct a composite image. The sync reference is the reference edge of horizontal sync.");
		AddHelpText(DPXColumn.GAMMA,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the power law exponent that represents the gamma correction applied to a video image. In the expression Y = X 1/gamma , the default gamma for NTSC is 2.2.");
		AddHelpText(DPXColumn.BLACK_LEVEL,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the digital code value representing reference black (camera lens capped, RGB signal set to 0 mV). For ITU-R 601-5, the default black level code value is 16.");
		AddHelpText(DPXColumn.BLACK_GAIN,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the linear gain applied to signals below the breakpoint (this is 4.5 for SMPTE ST 274).");
		AddHelpText(DPXColumn.BREAKPOINT,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the signal level above which the gamma law is applied (this is 0.018 of full scale for SMPTE ST 274).");
		AddHelpText(DPXColumn.REFERENCE_WHITE_LEVEL,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the digital code value representing reference white (90% reflectance white card, RGB signal set to 700 mV). For ITU-R 601-5, the default reference white level code value is 235.");
		AddHelpText(DPXColumn.INTEGRATION_TIME,
				"Field Size: 4 bytes. \n\nType: real32int (IEEE Floating Point). \n\nDefinition: Defines the temporal sampling aperture of the television camera; most useful for CCD cameras.");
		AddHelpText(DPXColumn.RESERVED_TELEVISION_INFORMATION_HEADER,
				"Field Size: 1 byte. \n\nType: n/a. \n\nDefinition: Always zero, used for byte alignment within the header.");

		// User Defined Data
		AddHelpText(DPXColumn.USER_IDENTIFICATION,
				"Field Size: 32 bytes. \n\nType: ASCII. \n\nDefinition: Label to identify the FADGI Digitization Process History structured data block in User Defined Data. FADGI Usage: \"FADGI process history.\"");
		AddHelpText(DPXColumn.USER_DEFINED_DATA,
				"Field Size: 1,000,000 bytes (max). \n\nType: any. \n\nDefinition: User defined data - Postage stamp, processing logs, etc. (length is variable with maximum length of 1 Mbyte). This FADGI-defined element is designed to summarize data on the digitizing process including signal chain specifics and other elements. Based on the ‘Coding History’ element defined by EBU R98-1999: Format for the <CodingHistory> field in Broadcast Wave Format files for the constrained space within the Broadcast Wave ‘bext’ chunk, this field employs a defined string variable for each parameter of the digitization process. Data in this field defined as a collection of strings, each presented on\n"
						+ "a separate line, containing a history of the coding processes applied to the file. The first line documents the source film reel, the second line contains data on the capture process, the third line of data records information on the storage of the file. A new line is added when the coding history related to the file is changed. \n"
						+ "\n"
						+ "Each variable within a string is separated by a comma-space and each line should end with a carriage return and line feed. Each variable is optional, to be used when needed.\n\n"
						+ "\n" + "O=format (reversal, print, positive, negative, DPXv1, DPXv2, etc.)\n"
						+ "G=gauge (super8mm, 8mm,16mm, 35mm, etc.)\n" + "C=color (color, BW)\n"
						+ "S=sound (silent, composite optical, composite mag, separate optical reel, separate mag reel, etc.)\n"
						+ "D=summary of condition issues, especially if condition impacts visual quality of digitized image\n"
						+ "F=frames per second\n" + "A=aspect ratio\n" + "L=timing, grading (one-light, scene)\n"
						+ "W=bit depth (12-bit, 10-bit, 8-bit, etc.)\n" + "R=resolution (2K, 4K, 8K, etc.)\n"
						+ "M=color model (RGB Log, etc.)\n"
						+ "N=name of vendor or operator who scanned film (if applicable)\n"
						+ "T=free ASCII text string; contains no commas but semicolons may be used.");
	}

	public String getHelpText(ColumnDef column) {
		return helpTextSet.get(column);
	}

}
