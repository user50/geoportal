package org.w2fc.geoportal.ws.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/objects/getObjectsResponse")
public class GetObjectsResponse implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = -6648352871688641490L;
	@XmlElement(name = "GeoObject")
     private List<GeoObjectFullAdapter> list;
	
	public List<GeoObjectFullAdapter> getList() {
		return list;
	}
	public void setList(List<GeoObjectFullAdapter> list) {
		this.list = list;
	}
}
