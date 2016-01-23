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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "GEO_USER")
public class GeoUser extends AbstractDomain<GeoUser>{

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column (name = "ext_system", nullable = false)
    private String extSystem;
    
    @Column (name = "login", nullable = false)
    private String login;
    
    @JsonIgnore
    @Column (name = "password", nullable = false)
    private String password;
    
    @Column (name = "enabled", nullable = true)
    private Boolean enabled;
    
    @Column (name = "first_name", nullable = false)
    private String firstName;

    @Column (name = "token", nullable = true)
    private String token;

    @Column (name = "last_name", nullable = false)
    private String lastName;
    
    @Column (name = "email", nullable = false)
    private String email;
    
    @Column (name = "phone", nullable = false)
    private String phone;
    
    
    @Version 
    @Column(name = "VERSION")
    private Integer version;
    
    
    /* GeoLayer */
    @JsonIgnore
    @OneToMany(cascade={CascadeType.ALL}, mappedBy="referenceId.geoUser", orphanRemoval = true)
    private Set<GeoLayerToUserReference> layerToUserReferences;

    /* GeoUserRole */
    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)  
    @JoinTable(
            name = "GEO_USER_TO_ROLE", 
            joinColumns = {
                    @JoinColumn(name = "user_id", nullable = false, updatable = false)}, 
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id", nullable = false, updatable = false)}
    )  
    private Set<GeoUserRole> geoUserRoles;
    
    /* GeoACL */
    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)  
    @JoinTable(
            name = "GEO_USER_TO_ACL", 
            joinColumns = {
                    @JoinColumn(name = "user_id", nullable = false, updatable = false)}, 
            inverseJoinColumns = {
                    @JoinColumn(name = "acl_id", nullable = false, updatable = false)}
    )  
    private Set<GeoACL> geoACLs;
    
    
    public String getFullName() {
        return getLastName() + " " + getFirstName();
    }
    
    
    
    //=================================================

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getExtSystem() {
        return extSystem;
    }


    public void setExtSystem(String extSystem) {
        this.extSystem = extSystem;
    }


    public String getLogin() {
        return login;
    }


    public void setLogin(String login) {
        this.login = login;
    }


    @JsonIgnore
    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public Boolean getEnabled() {
        return enabled;
    }


    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }


    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Integer getVersion() {
        return version;
    }


    public void setVersion(Integer version) {
        this.version = version;
    }


    public Set<GeoUserRole> getGeoUserRoles() {
        return geoUserRoles;
    }


    public void setGeoUserRoles(Set<GeoUserRole> geoUserRoles) {
        this.geoUserRoles = geoUserRoles;
    }


    public Set<GeoACL> getGeoACLs() {
        return geoACLs;
    }


    public void setGeoACLs(Set<GeoACL> geoACLs) {
        this.geoACLs = geoACLs;
    }



    public Set<GeoLayerToUserReference> getLayerToUserReferences() {
        return layerToUserReferences;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setLayerToUserReferences(Set<GeoLayerToUserReference> layerToUserReferences) {
        this.layerToUserReferences = layerToUserReferences;
    }  
}
