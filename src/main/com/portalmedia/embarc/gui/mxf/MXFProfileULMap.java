package com.portalmedia.embarc.gui.mxf;

import java.util.HashMap;

public class MXFProfileULMap {

	HashMap<String, String> values = new HashMap<String, String>();

	public MXFProfileULMap() {
		values.put("060e2b34.04010101.0d010201.01010000", "MXF OP1a SingleItem SinglePackage");
		values.put("060e2b34.04010101.0d010201.01010100", "MXF OP1a SingleItem SinglePackage UniTrack Stream Internal");
		values.put("060e2b34.04010101.0d010201.01010300", "MXF OP1a SingleItem SinglePackage UniTrack Stream External");
		values.put("060e2b34.04010101.0d010201.01010500", "MXF OP1a SingleItem SinglePackage UniTrack NonStream Internal");
		values.put("060e2b34.04010101.0d010201.01010700", "MXF OP1a SingleItem SinglePackage UniTrack NonStream External");
		values.put("060e2b34.04010101.0d010201.01010900", "MXF OP1a SingleItem SinglePackage MultiTrack Stream Internal");
		values.put("060e2b34.04010101.0d010201.01010b00", "MXF OP1a SingleItem SinglePackage MultiTrack Stream External");
		values.put("060e2b34.04010101.0d010201.01010d00", "MXF OP1a SingleItem SinglePackage MultiTrack NonStream Internal");
		values.put("060e2b34.04010101.0d010201.01010f00", "MXF OP1a SingleItem SinglePackage MultiTrack NonStream External");
		values.put("060e2b34.04010101.0d010201.01020000", "MXF OP1b SingleItem GangedPackages");
		values.put("060e2b34.04010101.0d010201.01020100", "MXF OP1b SingleItem GangedPackages UniTrack Stream Internal");
		values.put("060e2b34.04010101.0d010201.01020300", "MXF OP1b SingleItem GangedPackages UniTrack Stream External");
		values.put("060e2b34.04010101.0d010201.01020500", "MXF OP1b SingleItem GangedPackages UniTrack NonStream Internal");
		values.put("060e2b34.04010101.0d010201.01020700", "MXF OP1b SingleItem GangedPackages UniTrack NonStream External");
		values.put("060e2b34.04010101.0d010201.01020900", "MXF OP1b SingleItem GangedPackages MultiTrack Stream Internal");
		values.put("060e2b34.04010101.0d010201.01020b00", "MXF OP1b SingleItem GangedPackages MultiTrack Stream External");
		values.put("060e2b34.04010101.0d010201.01020d00", "MXF OP1b SingleItem GangedPackages MultiTrack NonStream Internal");
		values.put("060e2b34.04010101.0d010201.01020f00", "MXF OP1b SingleItem GangedPackages MultiTrack NonStream External");
		values.put("060e2b34.04010101.0d010201.03030000", "MXF OP3c EditItems AlternatePackages");
		values.put("060e2b34.04010101.0d010201.03030100", "MXF OP3c EditItems AlternatePackages UniTrack Stream Internal NoProcessing");
		values.put("060e2b34.04010101.0d010201.03030110", "MXF OP3c EditItems AlternatePackages UniTrack Stream Internal MayProcess");
		values.put("060e2b34.04010101.0d010201.03030300", "MXF OP3c EditItems AlternatePackages UniTrack Stream External NoProcessing");
		values.put("060e2b34.04010101.0d010201.03030310", "MXF OP3c EditItems AlternatePackages UniTrack Stream External MayProcess");
		values.put("060e2b34.04010101.0d010201.03030500", "MXF OP3c EditItems AlternatePackages UniTrack NonStream Internal NoProcessing");
		values.put("060e2b34.04010101.0d010201.03030510", "MXF OP3c EditItems AlternatePackages UniTrack NonStream Internal MayProcess");
		values.put("060e2b34.04010101.0d010201.03030700", "MXF OP3c EditItems AlternatePackages UniTrack NonStream External NoProcessing");
		values.put("060e2b34.04010101.0d010201.03030710", "MXF OP3c EditItems AlternatePackages UniTrack NonStream External MayProcess");
		values.put("060e2b34.04010101.0d010201.03030900", "MXF OP3c EditItems AlternatePackages MultiTrack Stream Internal NoProcessing");
		values.put("060e2b34.04010101.0d010201.03030910", "MXF OP3c EditItems AlternatePackages MultiTrack Stream Internal MayProcess");
		values.put("060e2b34.04010101.0d010201.03030b00", "MXF OP3c EditItems AlternatePackages MultiTrack Stream External NoProcessing");
		values.put("060e2b34.04010101.0d010201.03030b10", "MXF OP3c EditItems AlternatePackages MultiTrack Stream External MayProcess");
		values.put("060e2b34.04010101.0d010201.03030d00", "MXF OP3c EditItems AlternatePackages MultiTrack NonStream Internal NoProcessing");
		values.put("060e2b34.04010101.0d010201.03030d10", "MXF OP3c EditItems AlternatePackages MultiTrack NonStream Internal MayProcess");
		values.put("060e2b34.04010101.0d010201.03030f00", "MXF OP3c EditItems AlternatePackages MultiTrack NonStream External NoProcessing");
		values.put("060e2b34.04010101.0d010201.03030f10", "MXF OP3c EditItems AlternatePackages MultiTrack NonStream External MayProcess");
	}

	public HashMap<String, String> getMap() {
		return values;
	}
}
