package com.portalmedia.embarc.gui.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author PortalMedia
 * @since 2020-07-22
 **/

@XmlRootElement(name="AS07CoreDMSDeviceObjects")
@SuppressWarnings("ucd")
public class AS07CoreDMSDeviceObjects {

	private String DeviceType;
	private String Manufacturer;
	private String Model;
	private String SerialNumber;
	private String UsageDescription;

	public String getDeviceType(){
		return DeviceType;
	}

	@XmlElement(name="DeviceType")
	public void setDeviceType(String DeviceType){
		this.DeviceType = DeviceType;
	}

	public String getDeviceTypeDisplayName() {
		return "Device Type";
	}

	public String getDeviceTypeHelpText() {
		return "The kind of device used to capture or create the content (as either a commonly known name or as a locally defined name; e.g., Radio-camera)";
	}

	public String getManufacturer(){
		return Manufacturer;
	}

	@XmlElement(name="Manufacturer")
	public void setManufacturer(String Manufacturer){
		this.Manufacturer = Manufacturer;
	}

	public String getManufacturerDisplayName() {
		return "Manufacturer";
	}

	public String getManufacturerHelpText() {
		return "The manufacturer or maker of the device";
	}

	public String getModel(){
		return Model;
	}

	@XmlElement(name="Model")
	public void setModel(String Model){
		this.Model = Model;
	}

	public String getModelDisplayName() {
		return "Model";
	}

	public String getModelHelpText() {
		return "Identifies the device model used in capturing or generating the essence.";
	}

	public String getSerialNumber(){
		return SerialNumber;
	}

	@XmlElement(name="SerialNumber")
	public void setSerialNumber(String SerialNumber){
		this.SerialNumber = SerialNumber;
	}

	public String getSerialNumberDisplayName() {
		return "Serial Number";
	}

	public String getSerialNumberHelpText() {
		return "Alphanumeric serial number identifying the individual device";
	}

	public String getUsageDescription(){
		return UsageDescription;
	}

	@XmlElement(name="UsageDescription")
	public void setUsageDescription(String UsageDescription){
		this.UsageDescription = UsageDescription;
	}

	public String getUsageDescriptionDisplayName() {
		return "Usage Description";
	}

	public String getUsageDescriptionHelpText() {
		return "Free text description of the function or use of the device in the production of a specific content item";
	}
	
}
