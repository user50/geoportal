package org.w2fc.geoportal.gis.model;

import java.io.Serializable;
import java.util.Date;

public class HistoryModel implements Serializable {

	private Date revDate;
	private long revTime;
	private int revId;
	private String revType;

	public void setRevDate(Date revisionDate) {
		this.revDate = revisionDate;
		
	}

	public void setRevTime(long timestamp) {
		this.revTime = timestamp;
		
	}

	public void setRevId(int id) {
		revId = id;
		
	}

	public void setRevType(String type) {
		revType = type;
		
	}

	public Date getRevDate() {
		return revDate;
	}

	public long getRevTime() {
		return revTime;
	}

	public int getRevId() {
		return revId;
	}

	public String getRevType() {
		return revType;
	}
	
}
