package org.w2fc.geoportal.user;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.vividsolutions.jts.geom.Geometry;

public class CustomUserDetails extends User {

    private static final long serialVersionUID = 7123061924039340395L;
    
    private String fullName = null;

    private Long id;

	private Map<String, Geometry> permissionArea = null;

    
    public CustomUserDetails(Long id, String fullname, String username, String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.fullName = fullname;
        this.id = id;
    }

    


	public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

	public void setPermissionArea(Map<String, Geometry> area) {
		permissionArea = area;
		
	}
	
    public Map<String, Geometry> getPermissionArea() {
		return permissionArea;
	}

}
