package com.portalmedia.embarc.parser.dpx;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.portalmedia.embarc.parser.ImageElementDef;

/**
 * Type containing each of the image elements within the image element header
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public enum DPXImageElement implements Serializable, ImageElementDef {
	NONE(""), 
	IMAGE_ELEMENT_1("Image Element 1"), 
	IMAGE_ELEMENT_2("Image Element 2"),
	IMAGE_ELEMENT_3("Image Element 3"), 
	IMAGE_ELEMENT_4("Image Element 4"), 
	IMAGE_ELEMENT_5("Image Element 5"),
	IMAGE_ELEMENT_6("Image Element 6"), 
	IMAGE_ELEMENT_7("Image Element 7"), 
	IMAGE_ELEMENT_8("Image Element 8");

	private String displayName;

	@JsonCreator
	private DPXImageElement(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

}
