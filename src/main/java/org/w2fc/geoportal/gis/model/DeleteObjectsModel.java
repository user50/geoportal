package org.w2fc.geoportal.gis.model;

import java.io.Serializable;
import java.util.List;

public class DeleteObjectsModel implements Serializable{
	Long layerId;
	List<Long> objIds;
	public Long getLayerId() {
		return layerId;
	}
	public void setLayerId(Long layerId) {
		this.layerId = layerId;
	}
	public List<Long> getObjIds() {
		return objIds;
	}
	public void setObjIds(List<Long> objIds) {
		this.objIds = objIds;
	}
}
