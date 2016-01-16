package org.w2fc.geoportal.user;

import java.util.Set;

import org.w2fc.geoportal.domain.GeoUserRole;

public class GeoRoleUIAdapter {

    private GeoUserRole role = null;
    
    private Set<Long> userIds = null;
    
    private Set<Long> aclIds = null;
    

    public GeoRoleUIAdapter() {
        role = new GeoUserRole();
    }

    public GeoRoleUIAdapter(GeoUserRole role) {
        this.role = role;
    }

    public Long getId() {
        return role.getId();
    }

    public void setId(Long id) {
        role.setId(id);
    }

    public String getName() {
        return role.getName();
    }

    public void setName(String name) {
        role.setName(name);
    }

    public GeoUserRole getRole() {
        return role;
    }

    public void setRole(GeoUserRole role) {
        this.role = role;
    }

    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }

    public Set<Long> getAclIds() {
        return aclIds;
    }

    public void setAclIds(Set<Long> aclIds) {
        this.aclIds = aclIds;
    }
    
}
