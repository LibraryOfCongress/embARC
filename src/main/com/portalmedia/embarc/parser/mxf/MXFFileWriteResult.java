package com.portalmedia.embarc.parser.mxf;

public class MXFFileWriteResult {
	private boolean success;
	private Exception exception;
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
