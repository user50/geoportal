package org.w2fc.geoportal.utils;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.dao.*;
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
    @Qualifier("tokenizedGeoUserDao")
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

    @Autowired
    private OperationStatusRepository operationStatusRepository;
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

    public OperationStatusRepository getOperationStatusRepository() {
        return operationStatusRepository;
    }

    public void setOperationStatusRepository(OperationStatusRepository operationStatusRepository) {
        this.operationStatusRepository = operationStatusRepository;
    }
}
