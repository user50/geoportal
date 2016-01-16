package org.w2fc.geoportal.layer;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

public class MassEditorSessionModel {

	private static final String CHECKED_OBJS = "checkedObjs";
	static final String CHECKED_TAGS = "checkedTags";
	static final String NAME_FILTER = "nameFilter";
	
	private final HttpSession session;
	private final Long layerId;

	public MassEditorSessionModel(HttpSession session, Long LayerId) {
		this.session = session;
		this.layerId = LayerId;
	}

	@SuppressWarnings("unused")
	private MassEditorSessionModel() {
		session = null;
		layerId = null;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getTagKeys() {
		Object checkedTags = session.getAttribute(layerId + CHECKED_TAGS);
		if (checkedTags != null) {
			return (List<String>) checkedTags;
		}
		return new ArrayList<String>();
	}

	public void setTagKeys(List<String> tag_keys) {
		session.setAttribute(layerId + CHECKED_TAGS, tag_keys);
	}

	public void setNameFilter(String nameFilter) {
		if(nameFilter != null){
			session.setAttribute(layerId + NAME_FILTER, nameFilter);
		}else{
			session.removeAttribute(layerId + NAME_FILTER);
		}
	}

	public void setCheckedObjs(List<Long> arrayList) {
		session.setAttribute(layerId + CHECKED_OBJS, arrayList);
	}

	@SuppressWarnings("unchecked")
	public List<Long> getCheckedObjs() {
		Object checkedObjs = session.getAttribute(layerId + CHECKED_OBJS);
		if (checkedObjs != null) {
			return (List<Long>) checkedObjs;
		}
		return new ArrayList<Long>();
	}

	public String getNameFilter() {
		if(session.getAttribute(layerId +NAME_FILTER) != null){
			return (String) session.getAttribute(layerId +NAME_FILTER);
		}
		return null;
	}

}
