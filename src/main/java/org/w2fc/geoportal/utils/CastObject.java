package org.w2fc.geoportal.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CastObject {

	private final Object[] list;

	public CastObject(Object[] objList) {
		this.list = objList;
	}

	public <T>T[] getObjList(T[] a) {
		if(list == null)return null;
		List<T> array = (List<T>) Arrays.asList(list);
		return Arrays.copyOf(list, list.length, (Class<? extends T[]>) a.getClass());
	}
	
	public <T>List<T> getArrayList(T[] a) {
		if(list == null)return null;
		List<T> array = new ArrayList<T>();
		array.addAll(Arrays.asList(Arrays.copyOf(list, list.length, (Class<? extends T[]>) a.getClass())));
		return array;
	}

}
