package org.w2fc.geoportal.utils;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.dao.AddnsPopupTemplateDao;
import org.w2fc.geoportal.dao.AddnsRatingDao;
import org.w2fc.geoportal.dao.GeoACLDao;
import org.w2fc.geoportal.dao.GeoLayerDao;
import org.w2fc.geoportal.dao.GeoLayerTypeDao;
import org.w2fc.geoportal.dao.GeoObjectDao;
import org.w2fc.geoportal.dao.GeoObjectPropertiesDao;
import org.w2fc.geoportal.dao.GeoSettingsDao;
import org.w2fc.geoportal.dao.GeoUserDao;
import org.w2fc.geoportal.dao.GeoUserRoleDao;
import org.w2fc.geoportal.dao.ReferenceSystemProjDao;
import org.w2fc.geoportal.user.CustomJdbcUserDetailsManager;
import org.w2fc.geoportal.wms.WMSService;
import org.w2fc.geoportal.ws.geocoder.GeoCoder;


@Service
public class ServiceRegistry {

	@Autowired
    private GeoObjectPropertiesDao geoObjectPropertiesDao;

	@Autowired
    private GeoLayerDao geoLayerDao;
    
	@Autowired
    private GeoLayerTypeDao geoLayerTypeDao;
	
    @Autowired
    private GeoObjectDao geoObjectDao;

    @Autowired
    private GeoUserDao geoUserDao;
    
    @Autowired
    private GeoUserRoleDao geoUserRoleDao;

    @Autowired
    private CustomJdbcUserDetailsManager userDetailsManager;

    @Autowired
    private GeoACLDao geoACLDao;

    @Autowired
    private AddnsPopupTemplateDao addnsPopupTemplateDao;
    
    @Autowired
    private AddnsRatingDao addnsRatingDao;

    @Autowired
    private WMSService wms;

    @Autowired
    private ServletContext servletContext;
    
    @Autowired
    private ReferenceSystemProjDao referenceSystemProjDao;
    
    @Autowired
    private GeoSettingsDao geoSettingsDao;

    @Autowired
    private GeoCoder geoCoder;

    //==========================================
    
    public GeoLayerDao getLayerDao() {
        return geoLayerDao;
    }

    public GeoObjectDao getGeoObjectDao() {
        return geoObjectDao;
    }

    public GeoUserDao getUserDao() {
        return geoUserDao;
    }

    public GeoUserRoleDao getUserRoleDao() {
        return geoUserRoleDao;
    }

    public CustomJdbcUserDetailsManager getUserDetailsManager() {
        return userDetailsManager;
    }

    public GeoACLDao getACLDao() {
        return geoACLDao;
    }

    public AddnsPopupTemplateDao getPopupTemplateDao() {
        return addnsPopupTemplateDao;
    }

    public AddnsRatingDao getRatingDao() {
        return addnsRatingDao;
    }

    public WMSService getWms() {
        return wms;
    }

	public ServletContext getServletContext() {
		return servletContext;
	}

	public GeoObjectPropertiesDao getGeoObjectPropertiesDao() {
		return geoObjectPropertiesDao;
	}    
	
    public GeoLayerTypeDao getLayerTypeDao() {
        return geoLayerTypeDao;
    }

	public ReferenceSystemProjDao getReferenceSystemProjDao() {
		return referenceSystemProjDao;
	}

	public void setReferenceSystemProjDao(
			ReferenceSystemProjDao referenceSystemProjDao) {
		this.referenceSystemProjDao = referenceSystemProjDao;
	}

	public GeoSettingsDao getGeoSettingsDao() {
		return geoSettingsDao;
	}

	public void setGeoSettingsDao(GeoSettingsDao geoSettingsDao) {
		this.geoSettingsDao = geoSettingsDao;
	}

    public GeoCoder getGeoCoder() {
        return geoCoder;
    }
}
