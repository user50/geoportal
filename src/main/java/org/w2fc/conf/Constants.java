package org.w2fc.conf;

import java.util.Collection;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w2fc.geoportal.user.CustomUserDetails;


public class Constants {

    /*
     *      AUTHENTICATION
     */
    
    public static final String GEOPORTAL_ROLE_ADMIN = "Администратор геопортала";
    
    public static final Long GEOPORTAL_ROLE_ADMIN_ID = 1L;

    public static final String GEOPORTAL_ROLE_EDITOR = "Редактор геопортала";
    
    public static final Long GEOPORTAL_ROLE_EDITOR_ID = 2L;
    

    public static final String GEOPORTAL_USER_ANONYMOUS_ALIAS = "Все";
    
    public static final Long GEOPORTAL_USER_ANONYMOUS_ID = 0L;
    
    
    public static boolean is(String roleAlias){
        Collection<? extends GrantedAuthority> a = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority grantedAuthority : a) {
            if(roleAlias.equals(grantedAuthority.getAuthority())){
                return true;
            }
        }

        return false;
    }
    
    
    public static boolean isAnonymous(){
        return SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken;
    }
    
    public static boolean isAdministrator(){
        return is(GEOPORTAL_ROLE_ADMIN);
    }
    
    public static boolean isEditor(){
        return is(GEOPORTAL_ROLE_EDITOR);
    }
    
    public static boolean permissionArea(Authentication auth){
    	Object user = auth.getPrincipal();
    	if(user instanceof CustomUserDetails){
    		return ((CustomUserDetails)user).getPermissionArea() != null;
    	}
    	return false;
    }
}
