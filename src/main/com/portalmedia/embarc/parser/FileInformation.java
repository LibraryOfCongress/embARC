package com.portalmedia.embarc.parser;

import java.io.Serializable;

public class FileInformation<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name, type, hash, path, uuid;
	
	private int id;
	
	private T fileData;

	private boolean isEdited = false;
	
	public FileInformation() {
		super();
	}
	
	public FileInformation(String name, String type, String hash, String path, String uuid, int id) {
		super();
		this.name = name; 
		this.type = type;
		this.hash = hash;
		this.path = path;
		this.uuid = uuid;
		this.id = id;
	}

	public boolean isEdited() {
		return isEdited;
	}

	public void setEdited(boolean isEdited) {
		this.isEdited = isEdited;
	}

	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getHash() {
		return this.hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getUUID() {
		return this.uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public T getFileData() {
		return fileData;
	}

	public void setFileData(T data) {
		this.fileData = data;
	}

	public String getProp(String colName) {
		// TODO Auto-generated method stub
		return null;
	}

}
