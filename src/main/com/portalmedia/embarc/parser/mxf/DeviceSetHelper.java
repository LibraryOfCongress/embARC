package com.portalmedia.embarc.parser.mxf;

import java.util.ArrayList;
import java.util.List;

import tv.amwa.maj.exception.PropertyNotPresentException;
import tv.amwa.maj.model.impl.AS07CoreDMSDeviceObjectsImpl;

public class DeviceSetHelper {
	private String comma = ",,,,";
	private String slash = "////";


	public ArrayList<AS07CoreDMSDeviceObjectsImpl> createDeviceListFromString(String values) {
		ArrayList<AS07CoreDMSDeviceObjectsImpl> devices = new ArrayList<AS07CoreDMSDeviceObjectsImpl>();
		List<String> valList = DelimitedListCodec.splitOnDelimiterRun(values, slash.charAt(0), slash.length());
		for (String v : valList) {
			if (!v.isEmpty()) devices.add(createDeviceFromString(v));
		}
		return devices;
	}

	public AS07CoreDMSDeviceObjectsImpl createDeviceFromString(String values) {
		List<String> valList = DelimitedListCodec.splitOnDelimiterRun(values, comma.charAt(0), comma.length());
		AS07CoreDMSDeviceObjectsImpl device = new AS07CoreDMSDeviceObjectsImpl();
		if(valList.size()>0) device.setDeviceType(DelimitedListCodec.unescapeField(valList.get(0)));
		if(valList.size()>1) device.setManufacturer(DelimitedListCodec.unescapeField(valList.get(1)));
		if(valList.size()>2) device.setModel(DelimitedListCodec.unescapeField(valList.get(2)));
		if(valList.size()>3) device.setSerialNumber(DelimitedListCodec.unescapeField(valList.get(3)));
		if(valList.size()>4) device.setUsageDescription(DelimitedListCodec.unescapeField(valList.get(4)));
		return device;
	}
	
	public String devicesToString(List<AS07CoreDMSDeviceObjectsImpl> devices) {
		String toReturn =  "";
		for(int i = 0; i< devices.size(); i++) {
			AS07CoreDMSDeviceObjectsImpl device = devices.get(i);
			toReturn += deviceToString(device);
			if(i < devices.size() - 1) {
				toReturn += slash;
			}
		}
		return toReturn;
	}

	public String deviceToString(AS07CoreDMSDeviceObjectsImpl device) {
		String type = "";
		try {
			type = device.getDeviceType();
		} catch(PropertyNotPresentException pex) {}

		String manu = "";
		try {
			manu = device.getManufacturer();
		} catch(PropertyNotPresentException pex) {}

		String model = "";
		try {
			model = device.getModel();
		} catch(PropertyNotPresentException pex) {}

		String serial = "";
		try {
			serial = device.getSerialNumber();
		} catch(PropertyNotPresentException pex) {}

		String usage = "";
		try {
			usage = device.getUsageDescription();
		} catch(PropertyNotPresentException pex) {}

		return DelimitedListCodec.escapeField(type) + comma + DelimitedListCodec.escapeField(manu) + comma
				+ DelimitedListCodec.escapeField(model) + comma + DelimitedListCodec.escapeField(serial) + comma
				+ DelimitedListCodec.escapeField(usage);
	}
}
