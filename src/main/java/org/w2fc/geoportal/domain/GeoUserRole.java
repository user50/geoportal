package org.w2fc.geoportal.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "GEO_USER_ROLE")
public class GeoUserRole extends AbstractDomain<GeoUserRole>{

    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column (name = "name", nullable = false)
    private String name;
    
    
    @Version 
    @Column(name = "VERSION")
    private Integer version;
    
    
    /* GeoLayer */
    @JsonIgnore
    @OneToMany(cascade={CascadeType.ALL}, mappedBy="referenceId.geoUserRole", orphanRemoval = true)
    private Set<GeoLayerToRoleReference> layerToRoleReferences;
    
    /* GeoUser */
    @JsonIgnore
    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "geoUserRoles", fetch = FetchType.LAZY)
    private Set<GeoUser> geoUsers;
    
    /* GeoACL */
    @JsonIgnore
    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "geoUserRoles", fetch = FetchType.LAZY)
    private Set<GeoACL> geoACLs;

    
    
    //=================================================    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<GeoUser> getGeoUsers() {
        return geoUsers;
    }

    public void setGeoUsers(Set<GeoUser> geoUsers) {
        this.geoUsers = geoUsers;
    }

    public Set<GeoACL> getGeoACLs() {
        return geoACLs;
    }

    public void setGeoACLs(Set<GeoACL> geoACLs) {
        this.geoACLs = geoACLs;
    }

    public Set<GeoLayerToRoleReference> getLayerToRoleReferences() {
        return layerToRoleReferences;
    }

    public void setLayerToRoleReferences(Set<GeoLayerToRoleReference> layerToRoleReferences) {
        this.layerToRoleReferences = layerToRoleReferences;
    }
}
