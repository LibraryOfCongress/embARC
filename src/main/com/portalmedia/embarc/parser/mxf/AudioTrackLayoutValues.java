package com.portalmedia.embarc.parser.mxf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class AudioTrackLayoutValues {

	HashMap<String, String> values = new HashMap<String, String>();

	public AudioTrackLayoutValues() {
		values.put("060e2b34.04010101.0d0e0101.07020401", "AudioLayoutSilence");
		values.put("060e2b34.04010101.0d0e0101.07020402", "AudioLayoutUnknown");
		values.put("060e2b34.04010101.0d0e0101.07020403", "AudioLayout1TrackUndef");
		values.put("060e2b34.04010101.0d0e0101.07020404", "AudioLayout2TrackUndef");
		values.put("060e2b34.04010101.0d0e0101.07020405", "AudioLayout3TrackUndef");
		values.put("060e2b34.04010101.0d0e0101.07020406", "AudioLayout4TrackUndef");
		values.put("060e2b34.04010101.0d0e0101.07020407", "AudioLayout1TrackAudio");
		values.put("060e2b34.04010101.0d0e0101.07020408", "AudioLayout2TrackAudio");
		values.put("060e2b34.04010101.0d0e0101.07020409", "AudioLayout1TrackAudio1TrackTime");
		values.put("060e2b34.04010101.0d0e0101.0702040a", "AudioLayout3TracksAudio");
		values.put("060e2b34.04010101.0d0e0101.0702040b", "AudioLayout2TrackAudio1TrackTime");
		values.put("060e2b34.04010101.0d0e0101.0702040c", "AudioLayout4TrackAudio");
		values.put("060e2b34.04010101.0d0e0101.0702040d", "AudioLayout3TrackAudio1TrackTime");
		values.put("060e2b34.04010101.0d0e0101.07020410", "Unknown");
		values.put("060e2b34.04010101.0d0e0101.07020411", "AudioLayoutEBU123_4b");
		values.put("060e2b34.04010101.0d0e0101.07020412", "AudioLayoutEBU123_4c");
		values.put("060e2b34.04010101.0d0e0101.07020413", "AudioLayoutEBU123_16c");
		values.put("060e2b34.04010101.0d0e0101.07020414", "AudioLayoutEBU123_16d");
		values.put("060e2b34.04010101.0d0e0101.07020415", "AudioLayoutEBU123_16f");
		values.put("060e2b34.04010101.0d0e0101.07020420", "Unknown");
	}

	public String getDescription(String ul) {
		String val = ul.replace("urn:smpte:ul:", "");
		return values.get(val);
	}

	public String getUl(String description) {
		Iterator<Entry<String, String>> hmIterator = values.entrySet().iterator();
		while (hmIterator.hasNext()) { 
			Map.Entry mapElement = (Map.Entry)hmIterator.next();
			if (mapElement.getValue().equals(description)) {
				return "urn:smpte:ul:" + (String) mapElement.getKey();
			}
		}
		return "urn:smpte:ul:" + "060e2b34.04010101.0d0e0101.07020420";
	}

	public Collection<String> getDescriptions(){
		return values.values();
	}
}
