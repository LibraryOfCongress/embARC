package com.portalmedia.embarc.parser.dpx;

import java.util.Date;
import java.util.HashMap;

/**
 * DPX data template
 *
 * @author PortalMedia
 * @version 0.1.4+
 * @since 2019-07-24
 */
public class DPXDataTemplate {
	private String name;
	private HashMap<DPXColumn, String> values;
	private Date insertDate;
	private Date updateDate;
	
	public DPXDataTemplate() {
		setInsertDate(new Date());
		setUpdateDate(new Date());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<DPXColumn, String> getValues() {
		return values;
	}

	public void setValues(HashMap<DPXColumn, String> values) {
		setUpdateDate(new Date());
		this.values = values;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}
