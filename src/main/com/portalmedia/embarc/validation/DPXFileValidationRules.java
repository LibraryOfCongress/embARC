package com.portalmedia.embarc.validation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;

/**
 * Create a singleton containing file validation rules implementing
 * IFileValidationRule. Returns the entire list or just the rules for a specific
 * column.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXFileValidationRules {

	private static DPXFileValidationRules instance = null;

	public static DPXFileValidationRules getInstance() {
		if (instance == null) {
			instance = new DPXFileValidationRules();
		}

		return instance;
	}

	private List<IFileValidationRule> rules;

	private DPXFileValidationRules() {
		BuildRuleSet();
	}

	private void BuildRuleSet() {
		rules = new LinkedList<>();
		// rules.add(new DPXFileGenericSectionHeaderLengthRule());
		rules.add(new DPXFileNameMatchesRule());

		rules.add(new DPXImageBitDepthRule(DPXColumn.BIT_DEPTH_1, 1));
		rules.add(new DPXImageBitDepthRule(DPXColumn.BIT_DEPTH_2, 2));
		rules.add(new DPXImageBitDepthRule(DPXColumn.BIT_DEPTH_3, 3));
		rules.add(new DPXImageBitDepthRule(DPXColumn.BIT_DEPTH_4, 4));
		rules.add(new DPXImageBitDepthRule(DPXColumn.BIT_DEPTH_5, 5));
		rules.add(new DPXImageBitDepthRule(DPXColumn.BIT_DEPTH_6, 6));
		rules.add(new DPXImageBitDepthRule(DPXColumn.BIT_DEPTH_7, 7));
		rules.add(new DPXImageBitDepthRule(DPXColumn.BIT_DEPTH_8, 8));

		rules.add(new DPXImageColorimetricSpecificationRule(DPXColumn.COLORIMETRIC_SPECIFICATION_1, 1));
		rules.add(new DPXImageColorimetricSpecificationRule(DPXColumn.COLORIMETRIC_SPECIFICATION_2, 2));
		rules.add(new DPXImageColorimetricSpecificationRule(DPXColumn.COLORIMETRIC_SPECIFICATION_3, 3));
		rules.add(new DPXImageColorimetricSpecificationRule(DPXColumn.COLORIMETRIC_SPECIFICATION_4, 4));
		rules.add(new DPXImageColorimetricSpecificationRule(DPXColumn.COLORIMETRIC_SPECIFICATION_5, 5));
		rules.add(new DPXImageColorimetricSpecificationRule(DPXColumn.COLORIMETRIC_SPECIFICATION_6, 6));
		rules.add(new DPXImageColorimetricSpecificationRule(DPXColumn.COLORIMETRIC_SPECIFICATION_7, 7));
		rules.add(new DPXImageColorimetricSpecificationRule(DPXColumn.COLORIMETRIC_SPECIFICATION_8, 8));

		rules.add(new DPXImageDataSignRule(DPXColumn.DATA_SIGN_1, 1));
		rules.add(new DPXImageDataSignRule(DPXColumn.DATA_SIGN_2, 2));
		rules.add(new DPXImageDataSignRule(DPXColumn.DATA_SIGN_3, 3));
		rules.add(new DPXImageDataSignRule(DPXColumn.DATA_SIGN_4, 4));
		rules.add(new DPXImageDataSignRule(DPXColumn.DATA_SIGN_5, 5));
		rules.add(new DPXImageDataSignRule(DPXColumn.DATA_SIGN_6, 6));
		rules.add(new DPXImageDataSignRule(DPXColumn.DATA_SIGN_7, 7));
		rules.add(new DPXImageDataSignRule(DPXColumn.DATA_SIGN_8, 8));

		rules.add(new DPXImageDescriptorRule(DPXColumn.DESCRIPTOR_1, 1));
		rules.add(new DPXImageDescriptorRule(DPXColumn.DESCRIPTOR_2, 2));
		rules.add(new DPXImageDescriptorRule(DPXColumn.DESCRIPTOR_3, 3));
		rules.add(new DPXImageDescriptorRule(DPXColumn.DESCRIPTOR_4, 4));
		rules.add(new DPXImageDescriptorRule(DPXColumn.DESCRIPTOR_5, 5));
		rules.add(new DPXImageDescriptorRule(DPXColumn.DESCRIPTOR_6, 6));
		rules.add(new DPXImageDescriptorRule(DPXColumn.DESCRIPTOR_7, 7));
		rules.add(new DPXImageDescriptorRule(DPXColumn.DESCRIPTOR_8, 8));

		rules.add(new DPXImageEncodingRule(DPXColumn.ENCODING_1, 1));
		rules.add(new DPXImageEncodingRule(DPXColumn.ENCODING_2, 2));
		rules.add(new DPXImageEncodingRule(DPXColumn.ENCODING_3, 3));
		rules.add(new DPXImageEncodingRule(DPXColumn.ENCODING_4, 4));
		rules.add(new DPXImageEncodingRule(DPXColumn.ENCODING_5, 5));
		rules.add(new DPXImageEncodingRule(DPXColumn.ENCODING_6, 6));
		rules.add(new DPXImageEncodingRule(DPXColumn.ENCODING_7, 7));
		rules.add(new DPXImageEncodingRule(DPXColumn.ENCODING_8, 8));

		rules.add(new DPXImageOffsetToImageDataRule(DPXColumn.OFFSET_TO_DATA_1, 1));
		rules.add(new DPXImageOffsetToImageDataRule(DPXColumn.OFFSET_TO_DATA_2, 2));
		rules.add(new DPXImageOffsetToImageDataRule(DPXColumn.OFFSET_TO_DATA_3, 3));
		rules.add(new DPXImageOffsetToImageDataRule(DPXColumn.OFFSET_TO_DATA_4, 4));
		rules.add(new DPXImageOffsetToImageDataRule(DPXColumn.OFFSET_TO_DATA_5, 5));
		rules.add(new DPXImageOffsetToImageDataRule(DPXColumn.OFFSET_TO_DATA_6, 6));
		rules.add(new DPXImageOffsetToImageDataRule(DPXColumn.OFFSET_TO_DATA_7, 7));
		rules.add(new DPXImageOffsetToImageDataRule(DPXColumn.OFFSET_TO_DATA_8, 8));

		rules.add(new DPXImagePackingRule(DPXColumn.PACKING_1, 1));
		rules.add(new DPXImagePackingRule(DPXColumn.PACKING_2, 2));
		rules.add(new DPXImagePackingRule(DPXColumn.PACKING_3, 3));
		rules.add(new DPXImagePackingRule(DPXColumn.PACKING_4, 4));
		rules.add(new DPXImagePackingRule(DPXColumn.PACKING_5, 5));
		rules.add(new DPXImagePackingRule(DPXColumn.PACKING_6, 6));
		rules.add(new DPXImagePackingRule(DPXColumn.PACKING_7, 7));
		rules.add(new DPXImagePackingRule(DPXColumn.PACKING_8, 8));

		rules.add(new DPXImageTransferCharacteristicRule(DPXColumn.TRANSFER_CHARACTERISTIC_1, 1));
		rules.add(new DPXImageTransferCharacteristicRule(DPXColumn.TRANSFER_CHARACTERISTIC_2, 2));
		rules.add(new DPXImageTransferCharacteristicRule(DPXColumn.TRANSFER_CHARACTERISTIC_3, 3));
		rules.add(new DPXImageTransferCharacteristicRule(DPXColumn.TRANSFER_CHARACTERISTIC_4, 4));
		rules.add(new DPXImageTransferCharacteristicRule(DPXColumn.TRANSFER_CHARACTERISTIC_5, 5));
		rules.add(new DPXImageTransferCharacteristicRule(DPXColumn.TRANSFER_CHARACTERISTIC_6, 6));
		rules.add(new DPXImageTransferCharacteristicRule(DPXColumn.TRANSFER_CHARACTERISTIC_7, 7));
		rules.add(new DPXImageTransferCharacteristicRule(DPXColumn.TRANSFER_CHARACTERISTIC_8, 8));
	}

	public HashSet<IFileValidationRule> getColumnRules(ColumnDef column) {
		final HashSet<IFileValidationRule> toReturn = new HashSet<>();
		for (final IFileValidationRule rule : rules) {
			if (rule.getTargetColumn() == column) {
				toReturn.add(rule);
			}
		}
		return toReturn;
	}

	public List<IFileValidationRule> getRules() {
		return rules;
	}
}
