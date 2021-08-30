package com.portalmedia.embarc.report;

/**
 * Stores the hashed image values for reporting purposes
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class HashReportValue {
	String fileName;
	String originalHash;
	String newHash;

	public HashReportValue(String fileName, String originalHash, String newHash) {
		this.fileName = fileName;
		this.originalHash = originalHash;
		this.newHash = newHash;
	}

	public String getFileName() {
		return fileName;
	}

	public String getNewHash() {
		return newHash;
	}

	public String getOriginalHash() {
		return originalHash;
	}
}
