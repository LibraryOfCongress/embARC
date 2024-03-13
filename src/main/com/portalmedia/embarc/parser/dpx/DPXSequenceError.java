package com.portalmedia.embarc.parser.dpx;


/**
 * Store error details for sequence analysis
 *
 * @author PortalMedia
 * @since 2023-07-31
 **/
public class DPXSequenceError {
	private String filename;
	private String column;
	private String error;
	public String errorDescription;
	public DPXSequenceError(String filename, String column, String error, String errorDescription) {
		this.filename = filename;
		this.column = column;
		this.error = error;
		this.errorDescription = errorDescription;
	}
	
	public String getFilename() {
		return filename;
	}
	public String getColumn() {
		return column;
	}
	public String getError() {
		return error;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
}
