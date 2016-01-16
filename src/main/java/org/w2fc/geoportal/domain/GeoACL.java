package org.w2fc.geoportal.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "GEO_ACL")
public class GeoACL extends AbstractDomain<GeoACL>{

    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column (name = "name", nullable = false)
    private String name;
    
    @Column (name = "permissions", nullable = true)
    private Integer permissions;
    
    
    @Version 
    @Column(name = "VERSION")
    private Integer version;
    
    
    /*  GeoObject */
    @OneToOne
    @JoinColumn(name = "object_id", nullable=true)
    private GeoObject geoObject;
    
    /* GeoUser */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "geoACLs", fetch = FetchType.LAZY)
    private Set<GeoUser> geoUsers;
    
    /* GeoUserRole */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)  
    @JoinTable(
            name = "GEO_ROLE_TO_ACL", 
            joinColumns = {
                    @JoinColumn(name = "acl_id", nullable = false, updatable = false)}, 
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id", nullable = false, updatable = false)}
    )  
    private Set<GeoUserRole> geoUserRoles;
    
    
    
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

    public GeoObject getGeoObject() {
        return geoObject;
    }

    public void setGeoObject(GeoObject geoObject) {
        this.geoObject = geoObject;
    }

    public Set<GeoUser> getGeoUsers() {
        return geoUsers;
    }

    public void setGeoUsers(Set<GeoUser> geoUsers) {
        this.geoUsers = geoUsers;
    }

    public Set<GeoUserRole> getGeoUserRoles() {
        return geoUserRoles;
    }

    public void setGeoUserRoles(Set<GeoUserRole> geoUserRoles) {
        this.geoUserRoles = geoUserRoles;
    }

    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
}
