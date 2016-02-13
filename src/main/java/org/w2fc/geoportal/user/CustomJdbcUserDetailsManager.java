package org.w2fc.geoportal.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.w2fc.geoportal.utils.ServiceRegistry;

import com.vividsolutions.jts.geom.Geometry;

public class CustomJdbcUserDetailsManager extends JdbcUserDetailsManager {
    
    @SuppressWarnings("unused")
    private ServiceRegistry serviceRegistry;
    
    @Override
    public List<UserDetails> loadUsersByUsername(String username) {
        return getJdbcTemplate().query(getUsersByUsernameQuery(), new String[] {username}, new RowMapper<UserDetails>() {
            public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                String username = rs.getString(1);
                String password = rs.getString(2);
                boolean enabled = rs.getBoolean(3);
                String fullname = rs.getString(4);
                Long id = rs.getLong(5);
                
                return new CustomUserDetails(id, fullname, username, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES);
            }
        });
    }
    
    @Override
    protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
        String returnUsername = userFromUserQuery.getUsername();

        if (!isUsernameBasedPrimaryKey()) {
            returnUsername = username;
        }
        
        //GeoUser user = serviceRegistry.getUserDao().get(((CustomUserDetails)userFromUserQuery).getId());
                
        
        
        CustomUserDetails details = new CustomUserDetails(((CustomUserDetails)userFromUserQuery).getId(), ((CustomUserDetails)userFromUserQuery).getFullName(), returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(),
                true, true, true, combinedAuthorities);
        Map<String, Geometry> area = serviceRegistry.getUserDao().getPermissionArea(((CustomUserDetails)userFromUserQuery).getId());
        
        details.setPermissionArea(area);
        
        return details;
        
        
    }

    
    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}
