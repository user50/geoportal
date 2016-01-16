package org.w2fc.geoportal.ws.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.w2fc.geoportal.layer.GeoLayerUI;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/layers/GetLayersResponse")
public class GetLayersResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5286057713348266237L;
	
	@XmlElement(name = "GeoLayer")
    private List<GeoLayerUI> list;

	public List<GeoLayerUI> getList() {
		return list;
	}

	public void setList(List<GeoLayerUI> list) {
		this.list = list;
	}


}
