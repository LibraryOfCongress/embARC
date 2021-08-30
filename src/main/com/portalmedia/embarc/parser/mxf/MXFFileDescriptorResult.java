package com.portalmedia.embarc.parser.mxf;

import java.util.ArrayList;
import java.util.List;

import tv.amwa.maj.model.impl.AS07DateTimeDescriptorImpl;
import tv.amwa.maj.model.impl.AncillaryPacketsDescriptorImpl;
import tv.amwa.maj.model.impl.CDCIDescriptorImpl;
import tv.amwa.maj.model.impl.PictureDescriptorImpl;
import tv.amwa.maj.model.impl.RGBADescriptorImpl;
import tv.amwa.maj.model.impl.STLDescriptorImpl;
import tv.amwa.maj.model.impl.SoundDescriptorImpl;
import tv.amwa.maj.model.impl.TimedTextDescriptorImpl;
import tv.amwa.maj.model.impl.VBIDescriptorImpl;
import tv.amwa.maj.model.impl.WAVEPCMDescriptorImpl;

public class MXFFileDescriptorResult{
	List<CDCIDescriptorImpl> cdciDescriptors = new ArrayList<CDCIDescriptorImpl>();
	List<WAVEPCMDescriptorImpl> wavePCMDescriptors = new ArrayList<WAVEPCMDescriptorImpl>();
	List<AncillaryPacketsDescriptorImpl> ancillaryPacketsDescriptors = new ArrayList<AncillaryPacketsDescriptorImpl>();
	List<AS07DateTimeDescriptorImpl> dateTimeDescriptors = new ArrayList<AS07DateTimeDescriptorImpl>();
	List<TimedTextDescriptorImpl> timedTextDescriptors = new ArrayList<TimedTextDescriptorImpl>();
	List<RGBADescriptorImpl> rgbaDescriptors = new ArrayList<RGBADescriptorImpl>();
	List<PictureDescriptorImpl> pictureDescriptors = new ArrayList<PictureDescriptorImpl>();
	List<SoundDescriptorImpl> soundDescriptors = new ArrayList<SoundDescriptorImpl>();
	List<STLDescriptorImpl> stlDescriptors = new ArrayList<STLDescriptorImpl>();
	List<VBIDescriptorImpl> vbiDescriptors = new ArrayList<VBIDescriptorImpl>();
	
	public void addCDCIDescriptor(CDCIDescriptorImpl cdci) {
		cdciDescriptors.add(cdci);
	}
	public List<CDCIDescriptorImpl> getCDCIDescriptor(){
		return cdciDescriptors;
	}

	public void addWavePCMDescriptors(WAVEPCMDescriptorImpl wave) {
		wavePCMDescriptors.add(wave);
	}
	public List<WAVEPCMDescriptorImpl> getWavePCMDescriptors() {
		return wavePCMDescriptors;
	}
	public void addAncillaryPacketsDescriptors(AncillaryPacketsDescriptorImpl packetDescriptor) {
		ancillaryPacketsDescriptors.add(packetDescriptor);
	}
	public List<AncillaryPacketsDescriptorImpl> getAncillaryPacketsDescriptors() {
		return ancillaryPacketsDescriptors;
	}

	public void addAS07DateTimeDescriptor(AS07DateTimeDescriptorImpl datetimeDescriptor) {
		dateTimeDescriptors.add(datetimeDescriptor);
	}
	public List<AS07DateTimeDescriptorImpl> getAS07DateTimeDescriptor() {
		return dateTimeDescriptors;
	}

	public void addTimedTextDescriptor(TimedTextDescriptorImpl timedTextDescriptor) {
		timedTextDescriptors.add(timedTextDescriptor);
	}
	public List<TimedTextDescriptorImpl> getTimedTextDescriptor() {
		return timedTextDescriptors;
	}
	public void addRGBADescriptor(RGBADescriptorImpl descriptor) {
		rgbaDescriptors.add(descriptor);
	}
	public List<RGBADescriptorImpl> getRGBADescriptors() {
		return rgbaDescriptors;
	}
	public void addSTLDescriptor(STLDescriptorImpl descriptor) {
		stlDescriptors.add(descriptor);
	}
	public List<STLDescriptorImpl> getSTLDescriptors() {
		return stlDescriptors;
	}
	public void addPictureDescriptor(PictureDescriptorImpl descriptor) {
		pictureDescriptors.add(descriptor);
	}
	public List<PictureDescriptorImpl> getPictureDescriptors() {
		return pictureDescriptors;
	}
	public void addSoundDescriptor(SoundDescriptorImpl descriptor) {
		soundDescriptors.add(descriptor);
	}
	public List<SoundDescriptorImpl> getSoundDescriptors() {
		return soundDescriptors;
	}
	public void addVBIDescriptor(VBIDescriptorImpl descriptor) {
		vbiDescriptors.add(descriptor);
	}
	public List<VBIDescriptorImpl> getVBIDescriptors() {
		return vbiDescriptors;
	}
}
