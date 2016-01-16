package org.w2fc.geoportal.integration.ru.infor;

import java.util.Comparator;

import org.w2fc.geoportal.domain.GeoLayer;

public class LayerByNameComp implements Comparator<GeoLayer> {

	@Override
	public int compare(GeoLayer o1, GeoLayer o2) {
		return o1.getName().compareTo(o2.getName());
	}

}
