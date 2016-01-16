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


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "GEO_LAYER_TO_ROLE")
public class GeoLayerToRoleReference extends AbstractDomain<GeoLayerToRoleReference>{

    @EmbeddedId
    private GeoLayerToRoleReferenceId referenceId;

    @Column (name = "permissions", nullable = true)
    private Integer permissions;


    public GeoLayerToRoleReference() {}

    public GeoLayerToRoleReference(GeoLayer geoLayer, GeoUserRole geoUserRole) {
        this.referenceId = new GeoLayerToRoleReferenceId();
        this.referenceId.setGeoLayer(geoLayer);
        this.referenceId.setGeoUserRole(geoUserRole);
    }


    public GeoLayer getGeoLayer() {
        return referenceId.getGeoLayer();
    }

    public void setGeoLayer(GeoLayer geoLayer) {
        referenceId.setGeoLayer(geoLayer);
    }

    public GeoUserRole getGeoUserRole() {
        return referenceId.getGeoUserRole();
    }

    public void setGeoUserRole(GeoUserRole geoUserRole) {
        referenceId.setGeoUserRole(geoUserRole);
    }


    @Override
    public boolean equals(Object instance) {
        if (instance == null)
            return false;

        if (instance == this)
            return true;
        
        if (!(instance instanceof GeoLayerToRoleReference))
            return false;
        
        if(!this.referenceId.equals(((GeoLayerToRoleReference)instance).referenceId) 
                && !this.permissions.equals(((GeoLayerToRoleReference)instance).permissions)){
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
    public static class GeoLayerToRoleReferenceId implements Serializable {

        private static final long serialVersionUID = -648638804875750564L;

        
        @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
        @JoinColumn(name="layer_id")
        private GeoLayer geoLayer;

        @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
        @JoinColumn(name="role_id")
        private GeoUserRole geoUserRole;


        @Override
        public boolean equals(Object instance) {
            if (instance == null)
                return false;

            if (!(instance instanceof GeoLayerToRoleReferenceId))
                return false;

            final GeoLayerToRoleReferenceId other = (GeoLayerToRoleReferenceId) instance;
            if (!(geoLayer.getId().equals(other.getGeoLayer().getId())))
                return false;

            if (!(geoUserRole.getId().equals(other.getGeoUserRole().getId())))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.geoLayer != null ? this.geoLayer.hashCode() : 0);
            hash = 47 * hash + (this.geoUserRole != null ? this.geoUserRole.hashCode() : 0);
            return hash;
        }


        //=====================================

        public GeoLayer getGeoLayer() {
            return geoLayer;
        }

        public void setGeoLayer(GeoLayer geoLayer) {
            this.geoLayer = geoLayer;
        }

        public GeoUserRole getGeoUserRole() {
            return geoUserRole;
        }

        public void setGeoUserRole(GeoUserRole geoUserRole) {
            this.geoUserRole = geoUserRole;
        }
    }


    //=========================================

    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
}
