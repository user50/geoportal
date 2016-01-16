package org.w2fc.geoportal.domain;

import org.hibernate.search.bridge.StringBridge;

public class GeoObjectTagBridge implements StringBridge {

	@Override
	public String objectToString(Object objTag) {
		return objTag.toString();
	}

}
