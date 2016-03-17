package org.w2fc.geoportal.user;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.dao.GeoUserDao;
import org.w2fc.geoportal.domain.GeoACL;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.domain.GeoUserRole;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.spring.AbstractController;

import javax.annotation.PostConstruct;


@Controller
@RequestMapping(value = "/admin")
public class UserManagementController extends AbstractController<GeoUser, GeoUserDao, Long>{
    
    final Logger logger = LoggerFactory.getLogger(UserManagementController.class);
    
    static final long COMMAND_NEW_ENTRY = -1L;

    
    @Autowired
    ServiceRegistry serviceRegistry;
    
    @Override
    public void setAutowiredDao(GeoUserDao dao) {
        setDao(dao);
    }

    @PostConstruct
    public void init(){
        setAutowiredDao(serviceRegistry.getUserDao());
    }

    @RequestMapping(value = "/userProfile", method = RequestMethod.GET)
    public String userProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        
        if(auth instanceof AnonymousAuthenticationToken){
            model.addAttribute("userName", "Пользователь");

        }else{
            CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
            String fullName =  ud.getFullName();
            if (fullName.isEmpty()) fullName = userName;
            
            model.addAttribute("userName", fullName);
        }
        
        return "admin/userProfile";
    }
    
    
    @RequestMapping(value = "/userProfileLite", method = RequestMethod.GET)
    public String userProfileLite(Model model) {
        userProfile(model);
        return "admin/userProfileLite";
    }
    
    
    /*
     *      MVC
     */

    @RequestMapping(value="/users", method = RequestMethod.GET)
    public String userListAjaxForm(Model model) {
        return "admin/users";
    }
    
    @RequestMapping(value="/user", method = RequestMethod.GET)
    public String userList(Model model) {
        model.addAttribute("userList", getDao().list());
        return "admin/userslist";
    }


    @RequestMapping(value="/user/new", method = {RequestMethod.POST, RequestMethod.GET})
    public String userNewForm(Model model) {
        return userGet(null, model, null);
    }


    @RequestMapping(value="/user/passwd/{id}", method = RequestMethod.GET)
    public String userChangePasswdForm(@PathVariable Long id, Model model) 
            throws IllegalAccessException, InvocationTargetException {

        try {
            GeoUser user1 = getById(id);
            model.addAttribute("user", new GeoUserUIAdapter(user1));
            model.addAttribute("command", user1.getId());
        } catch(Throwable t) {
            logger.error("Error - ", t);
            String message = t.getMessage();
            model.addAttribute("exception", null!=message ? message : t.toString());
            return "redirect:/admin/user/" + id;
        }

        return "/admin/changePasswd";
    }


    @RequestMapping(value="/user/passwd/{id}", method = RequestMethod.POST)
    public String userChangePasswd(
            @PathVariable Long id,
            @ModelAttribute("user") GeoUserUIAdapter user, 
            Model model, 
            BindingResult result) 
                    throws IllegalAccessException, InvocationTargetException {

        try {

            GeoUser geoUser = getById(user.getGeoUser().getId());
            geoUser.setPassword(calcPasswd(user));
            getDao().update(geoUser, true);

        } catch(Throwable t) {
            logger.error("Error - ", t);
            String message = t.getMessage();
            model.addAttribute("exception", null!=message ? message : t.toString());
            return userGet(COMMAND_NEW_ENTRY, model, user);
        }

        return "redirect:/admin/user/" + user.getId();
    }

    private String calcPasswd(GeoUserUIAdapter user) {
        
        if(! user.getConfirmPassword().equals(user.getPassword())){
            throw new RuntimeException("Введенные пароли не совпадают");
        }
        
        return new BCryptPasswordEncoder().encode(user.getPassword());
    }


    @RequestMapping(value="/user/remove/{id}", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody boolean userRemove(@PathVariable Long id) {
        GeoUser u = getDao().get(id);
        u.setEnabled(false);
        getDao().update(u, true);
        return true;
    }
    

    @RequestMapping(value="/user/{id}", method = RequestMethod.GET)
    @Transactional(readOnly=true)
    public String userGet(
            @PathVariable Long id, 
            Model model,
            @ModelAttribute("user") GeoUserUIAdapter user) {

        if (null == id) {
            /* send new user form */
            model.addAttribute("user", new GeoUserUIAdapter());
            model.addAttribute("command", COMMAND_NEW_ENTRY);
            model.addAttribute("changePasswd", true);

        } else if(COMMAND_NEW_ENTRY == id){
            /* send the same data - there were an error of processing */
            model.addAttribute("user", user);
            model.addAttribute("command", COMMAND_NEW_ENTRY);
            model.addAttribute("changePasswd", true);

        } else {
            /* get user */
            user = new GeoUserUIAdapter(getById(id));
            model.addAttribute("user", user);
            
            //get assigned roles
            Set<GeoUserRole> roles = user.getGeoUser().getGeoUserRoles();
            user.setRoleIds(new HashSet<Long>());
            for (GeoUserRole role : roles) {
                user.getRoleIds().add(role.getId());
            }

          //get assigned ACLs
            Set<GeoACL> acls = user.getGeoUser().getGeoACLs();
            user.setAclIds(new HashSet<Long>());
            for (GeoACL acl : acls) {
                user.getAclIds().add(acl.getId());
            }
            
            model.addAttribute("command", user.getId());
        }

        // get all roles of system
        List<GeoUserRole> userRolesAll = serviceRegistry.getUserRoleDao().list();
        Map<Long, String> m1 = new LinkedHashMap<Long, String>();
        for (GeoUserRole userRole1 : userRolesAll) {
            m1.put(userRole1.getId(), userRole1.getName());
        }
        model.addAttribute("roleList", m1);
        
        // get all ACLs of system
        List<GeoACL> aclAll = serviceRegistry.getACLDao().list();
        Map<Long, String> m2 = new LinkedHashMap<Long, String>();
        for (GeoACL acl2 : aclAll) {
            m2.put(acl2.getId(), acl2.getName());
        }
        model.addAttribute("aclList", m2);
        
        return "admin/user";
    }

    
    @SuppressWarnings("unchecked")
    @CacheEvict(value="layerPermanent", allEntries = true)
    @Transactional
    @RequestMapping(value="/user/{id}", method = RequestMethod.POST)
    public String userUpdate(
                @PathVariable Long id, 
                Model model, 
                @ModelAttribute("user") GeoUserUIAdapter userUI, 
                BindingResult result) 
                        throws IllegalAccessException, InvocationTargetException {
        
        GeoUser user = userUI.getGeoUser();
        
        if(COMMAND_NEW_ENTRY == id){
            user.setId(null);
            user.setPassword(calcPasswd(userUI));
            user.setEnabled(true);
            
            try {
                if(getDao().get(user.getLogin()) != null)throw new Exception("Указан не уникальный логин!");
            	user = addObject(user);
                
            } catch(Throwable t) {
                logger.error("Error - ", t);
                String message = t.getMessage();
                model.addAttribute("exception", null!=message ? message : t.toString());
                return userGet(COMMAND_NEW_ENTRY, model, userUI);
            }
            
        }else{
            user = getById(userUI.getId());
            user.copyValuesFrom(userUI.getGeoUser(), "password", "enabled");
            getDao().update(user, true);
        }
        
        // add roles
        List<GeoUserRole> allRoles = serviceRegistry.getUserRoleDao().list();
        Set<GeoUserRole> currRoles = user.getGeoUserRoles();
        Set<Long> rolesIds = userUI.getRoleIds();
        if(null == allRoles)
            allRoles = Collections.EMPTY_LIST;
        if(null == currRoles){
            currRoles = new HashSet<GeoUserRole>();
            user.setGeoUserRoles(currRoles);
        }
        if(null == rolesIds)
            rolesIds = Collections.EMPTY_SET;

        Set <GeoUserRole> removeRoles = new HashSet<GeoUserRole>();
        Set <GeoUserRole> addRoles = new HashSet<GeoUserRole>();
        for (GeoUserRole userRole : allRoles) {
            if(! rolesIds.contains(userRole.getId()) && currRoles.contains(userRole)){
                removeRoles.add(userRole);
            }else if(rolesIds.contains(userRole.getId()) && !currRoles.contains(userRole)){
                addRoles.add(userRole);
            }
        }
        currRoles.removeAll(removeRoles);
        currRoles.addAll(addRoles);

        //add acls
        List<GeoACL> allACLs = serviceRegistry.getACLDao().list();
        Set<GeoACL> currACLS = user.getGeoACLs();
        Set<Long> aclIds = userUI.getAclIds();
        if(null == allACLs)
            allACLs = Collections.EMPTY_LIST;
        if(null == currACLS){
            currACLS = new HashSet<GeoACL>();
            user.setGeoACLs(currACLS);
        }
        if(null == aclIds)
            aclIds = Collections.EMPTY_SET;
        
        Set <GeoACL> removeACLs = new HashSet<GeoACL>();
        Set <GeoACL> addACLs = new HashSet<GeoACL>();
        for (GeoACL acl : allACLs) {
            if(! aclIds.contains(acl.getId()) && currACLS.contains(acl)){
                removeACLs.add(acl);
            }else if(aclIds.contains(acl.getId()) && !currACLS.contains(acl)){
                addACLs.add(acl);
            }
        }
        currACLS.removeAll(removeACLs);
        currACLS.addAll(addACLs);
        
        getDao().update(user, true);
        
        return "redirect:/admin/user/" + userUI.getId();
    }
    
    
    @RequestMapping(value = "/addPermissionArea/{id}", method = RequestMethod.GET)
    @Transactional
    @ResponseBody
   	public Boolean addPermissionArea(@PathVariable Long id) {
    	GeoObject obj = serviceRegistry.getGeoObjectDao().get(id);
    	String name = obj.getName();
    	if(name == null || name.isEmpty()){
    		name = "Объект " + obj.getId();
    	}
    	GeoACL acl = ObjectFactory.createGeoACL(obj);
		try{
			serviceRegistry.getACLDao().add(acl, true);
		}catch(Exception e){
			logger.error(e.getLocalizedMessage());
			return false;
		}
		return true;
   	}
    
    @RequestMapping(value = "/settings")
    @Transactional
    public String getSettings(Model model){
    	
    	model.addAttribute("settings", serviceRegistry.getGeoSettingsDao().list());
    	
    	return "admin/settings";
    }
    
}
