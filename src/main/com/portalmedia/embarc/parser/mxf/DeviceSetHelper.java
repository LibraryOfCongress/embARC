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
		String[] valList = values.split(slash);
		for (String v : valList) {
			if (v != "") devices.add(createDeviceFromString(v));
		}
		return devices;
	}

	public AS07CoreDMSDeviceObjectsImpl createDeviceFromString(String values) {
		String[] valList = values.split(comma);
		AS07CoreDMSDeviceObjectsImpl device = new AS07CoreDMSDeviceObjectsImpl();
		if(valList.length>0) device.setDeviceType(valList[0]);
		if(valList.length>1) device.setManufacturer(valList[1]);
		if(valList.length>2) device.setModel(valList[2]);
		if(valList.length>3) device.setSerialNumber(valList[3]);
		if(valList.length>4) device.setUsageDescription(valList[4]);
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

		return type + comma + manu + comma + model + comma + serial + comma + usage;
	}
}
