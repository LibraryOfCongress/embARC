package com.portalmedia.embarc.gui.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author PortalMedia
 * @since 2020-07-22
 **/

@XmlRootElement(name="AS07TimecodeLabelSubdescriptor")
public class AS07TimecodeLabelSubdescriptor {

	private String DateTimeSymbol;
	private String DateTimeEssenceTrackID;

	public String getDateTimeSymbol(){
		return DateTimeSymbol;
	}

	@XmlElement(name="DateTimeSymbol")
	public void setDateTimeSymbol(String DateTimeSymbol){
		this.DateTimeSymbol = DateTimeSymbol;
	}

	public String getDateTimeSymbolDisplayName() {
		return "DateTime Symbol";
	}

	public String getDateTimeSymbolHelpText() {
		return "Symbol that specifies the timecode, values listed in RP224";
	}

	public String getDateTimeEssenceTrackID(){
		return DateTimeEssenceTrackID;
	}

	@XmlElement(name="DateTimeEssenceTrackID")
	public void setDateTimeEssenceTrackID(String DateTimeEssenceTrackID){
		this.DateTimeEssenceTrackID = DateTimeEssenceTrackID;
	}

	public String getDateTimeEssenceTrackIDDisplayName() {
		return "DateTime Essence Track ID";
	}

	public String getDateTimeEssenceTrackIDHelpText() {
		return "Link to (i.e. value of) the Track ID of the audio track where the timecode data is stored in a Top Level Source Package. If this optional property is absent, this implies that the timecode data contained in the Timecode Track is Master Timecode and there is no timecode data on essence tracks. If provided, values shall conform to SMPTE ST 377-1:2011 (table B.15): for Master Timecode, the value shall be 1 (one), and for Historical Source Timecode, even if there are multiple instances, the value shall be 0 (zero).";
	}
}
