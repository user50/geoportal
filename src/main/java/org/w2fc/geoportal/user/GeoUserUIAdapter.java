package org.w2fc.geoportal.user;

import java.util.Set;
import java.util.UUID;

import org.w2fc.geoportal.domain.GeoUser;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GeoUserUIAdapter {

    private GeoUser geoUser = null;
    
    
    private String password = "";
    
    private String confirmPassword = "";
    
    private Set<Long> roleIds = null;
    
    private Set<Long> aclIds = null;
    
        
    public GeoUserUIAdapter() {
        this.geoUser = new GeoUser();
    }
    
    
    public GeoUserUIAdapter(GeoUser geoUser){
        this.geoUser = geoUser;
        geoUser.setToken(UUID.randomUUID().toString());
    }


    
    //==================================
    
    public String toString() {
        return geoUser.toString();
    }


    public String getFullName() {
        return geoUser.getFullName();
    }


    public Long getId() {
        return geoUser.getId();
    }


    public void setId(Long id) {
        geoUser.setId(id);
    }


    public String getExtSystem() {
        return geoUser.getExtSystem();
    }


    public void setExtSystem(String extSystem) {
        geoUser.setExtSystem(extSystem);
    }


    public String getLogin() {
        return geoUser.getLogin();
    }


    public void setLogin(String login) {
        geoUser.setLogin(login);
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public Boolean getEnabled() {
        return geoUser.getEnabled();
    }


    public void setEnabled(Boolean enabled) {
        geoUser.setEnabled(enabled);
    }


    public String getFirstName() {
        return geoUser.getFirstName();
    }


    public void setFirstName(String firstName) {
        geoUser.setFirstName(firstName);
    }


    public String getLastName() {
        return geoUser.getLastName();
    }


    public void setLastName(String lastName) {
        geoUser.setLastName(lastName);
    }


    public String getEmail() {
        return geoUser.getEmail();
    }


    public void setEmail(String email) {
        geoUser.setEmail(email);
    }


    public String getPhone() {
        return geoUser.getPhone();
    }


    public void setPhone(String phone) {
        geoUser.setPhone(phone);
    }

    @JsonIgnore
    public String getConfirmPassword() {
        return confirmPassword;
    }


    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @JsonIgnore
    public GeoUser getGeoUser() {
        return geoUser;
    }


    public Set<Long> getRoleIds() {
        return roleIds;
    }


    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }


    public Set<Long> getAclIds() {
        return aclIds;
    }


    public void setAclIds(Set<Long> aclIds) {
        this.aclIds = aclIds;
    }
}
