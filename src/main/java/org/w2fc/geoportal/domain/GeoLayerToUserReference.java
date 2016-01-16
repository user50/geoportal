package org.w2fc.geoportal.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "GEO_LAYER_TO_USER")
public class GeoLayerToUserReference extends AbstractDomain<GeoLayerToUserReference> {
    

    @EmbeddedId
    private GeoLayerToUserReferenceId referenceId;

    @Column (name = "permissions", nullable = true)
    private Integer permissions;

    
//    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER) 
//    @JoinColumn(name="layer_id", insertable=false, updatable=false)
//    private GeoLayer geoLayer;
//
//    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
//    @JoinColumn(name="user_id", insertable=false, updatable=false)
//    private GeoUser geoUser;
    
    
    public GeoLayerToUserReference() {}

    public GeoLayerToUserReference(GeoLayer geoLayer, GeoUser geoUser) {
        this.referenceId = new GeoLayerToUserReferenceId();
        this.referenceId.setGeoLayer(geoLayer);
        this.referenceId.setGeoUser(geoUser);
    }
    

    @Transient
    public GeoLayer getGeoLayer() {
        return referenceId.getGeoLayer();
    }

    public void setGeoLayer(GeoLayer geoLayer) {
        referenceId.setGeoLayer(geoLayer);
    }

    @Transient
    public GeoUser getGeoUser() {
        return referenceId.getGeoUser();
    }

    public void setGeoUser(GeoUser geoUser) {
        referenceId.setGeoUser(geoUser);
    }

    
    @Override
    public boolean equals(Object instance) {
        if (instance == null)
            return false;

        if (instance == this)
            return true;
        
        if (!(instance instanceof GeoLayerToUserReference))
            return false;
        
        if(!this.referenceId.equals(((GeoLayerToUserReference)instance).referenceId) 
                && !this.permissions.equals(((GeoLayerToUserReference)instance).permissions)){
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 11;
        hash = 57 * hash + (this.referenceId != null ? this.referenceId.hashCode() : 0);
        hash = 57 * hash + (this.permissions != null ? this.permissions.hashCode() : 0);
        return hash;
    }
    
    @Embeddable
    public static class GeoLayerToUserReferenceId implements Serializable {

        
        private static final long serialVersionUID = -4964336463962740175L;

        
        @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER) 
        @JoinColumn(name="layer_id")
        private GeoLayer geoLayer;

        @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
        @JoinColumn(name="user_id")
        private GeoUser geoUser;
        
        
        @Override
        public boolean equals(Object instance) {
            if (instance == null)
                return false;

            if (!(instance instanceof GeoLayerToUserReferenceId))
                return false;

            final GeoLayerToUserReferenceId other = (GeoLayerToUserReferenceId) instance;
            if (!(geoLayer.getId().equals(other.getGeoLayer().getId())))
                return false;

            if (!(geoUser.getId().equals(other.getGeoUser().getId())))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.geoLayer != null ? this.geoLayer.hashCode() : 0);
            hash = 47 * hash + (this.geoUser != null ? this.geoUser.hashCode() : 0);
            return hash;
        }

        
        //===================================================
        
        public GeoLayer getGeoLayer() {
            return geoLayer;
        }

        public void setGeoLayer(GeoLayer geoLayer) {
            this.geoLayer = geoLayer;
        }

        public GeoUser getGeoUser() {
            return geoUser;
        }

        public void setGeoUser(GeoUser geoUser) {
            this.geoUser = geoUser;
        }
    }

    
    //===================================================
    
    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
}
