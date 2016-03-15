package org.w2fc.geoportal.user;

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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.geoportal.dao.GeoUserRoleDao;
import org.w2fc.geoportal.domain.GeoACL;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.domain.GeoUserRole;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.spring.AbstractController;


@Controller
@RequestMapping(value = "/admin/role")
public class UserRoleController extends AbstractController<GeoUserRole, GeoUserRoleDao, Long>{
    
    final Logger logger = LoggerFactory.getLogger(UserRoleController.class);
    
    
    @Autowired
    ServiceRegistry serviceRegistry;
    
    @Autowired
    @Override
    public void setAutowiredDao(GeoUserRoleDao dao) {
        setDao(dao);
    }
    
    
    @RequestMapping(value="/new", method = {RequestMethod.POST, RequestMethod.GET})
    public String newForm(Model model){
        return get(null, model, null);
    }
    
    
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    @Transactional(readOnly=true)
    public String get(
            @PathVariable Long id,
            Model model,
            @ModelAttribute("role") GeoRoleUIAdapter role) {
        
        if (null == id) {
            /* send new role form */
            model.addAttribute("role", new GeoRoleUIAdapter());
            model.addAttribute("command", UserManagementController.COMMAND_NEW_ENTRY);

        } else if(UserManagementController.COMMAND_NEW_ENTRY == id){
            /* send the same data - there were an error of processing */
            model.addAttribute("role", role);
            model.addAttribute("command", UserManagementController.COMMAND_NEW_ENTRY);

        } else {
            GeoUserRole role1 = getById(id);
            GeoRoleUIAdapter geoRoleUIAdapter = new GeoRoleUIAdapter(role1);
            model.addAttribute("role", geoRoleUIAdapter);
        
            Set<GeoUser> users = role1.getGeoUsers();
            geoRoleUIAdapter.setUserIds(new HashSet<Long>());
            for (GeoUser geoUser : users) {
                geoRoleUIAdapter.getUserIds().add(geoUser.getId());
            }
            
            Set<GeoACL> acls = role1.getGeoACLs();
            geoRoleUIAdapter.setAclIds(new HashSet<Long>());
            for (GeoACL geoACL : acls) {
                geoRoleUIAdapter.getAclIds().add(geoACL.getId());
            }
            
            model.addAttribute("command", role.getId());
        }
        
        // get all users of system
        List<GeoUser> userAll = serviceRegistry.getUserDao().list();
        Map<Long, String> m1 = new LinkedHashMap<Long, String>();
        for (GeoUser user1 : userAll) {
            m1.put(user1.getId(), user1.getLogin());
        }
        model.addAttribute("userList", m1);
        
        // get all ACLs of system
        List<GeoACL> aclAll = serviceRegistry.getACLDao().list();
        Map<Long, String> m2 = new LinkedHashMap<Long, String>();
        for (GeoACL acl2 : aclAll) {
            m2.put(acl2.getId(), acl2.getName());
        }
        model.addAttribute("aclList", m2);
        
        return "admin/role";
    }
    
    
    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value="/{id}", method = RequestMethod.POST)
    public String update(
            @PathVariable Long id, 
            Model model,
            @ModelAttribute("role") GeoRoleUIAdapter roleUI) {

        GeoUserRole role = roleUI.getRole();

        if(UserManagementController.COMMAND_NEW_ENTRY == id){
            role.setId(null);

            try {
                role = addObject(role);

            } catch(Throwable t) {
                logger.error("Error - ", t);
                String message = t.getMessage();
                model.addAttribute("exception", null!=message ? message : t.toString());
                return get(UserManagementController.COMMAND_NEW_ENTRY, model, roleUI);
            }

        }else{
            role = getById(roleUI.getId());
            role.copyValuesFrom(roleUI.getRole());
            getDao().update(role, true);
        }
        
        
        // add users
        List<GeoUser> allUsers = serviceRegistry.getUserDao().list();
        Set<GeoUser> currUsers = role.getGeoUsers();
        Set<Long> usersIds = roleUI.getUserIds();
        if(null == allUsers)
            allUsers = Collections.EMPTY_LIST;
        if(null == currUsers){
            currUsers = new HashSet<GeoUser>();
            role.setGeoUsers(currUsers);
        }
        if(null == usersIds)
            usersIds = Collections.EMPTY_SET;

        Set <GeoUser> removeUsers = new HashSet<GeoUser>();
        Set <GeoUser> addUsers = new HashSet<GeoUser>();
        for (GeoUser user : allUsers) {
            if(! usersIds.contains(user.getId()) && currUsers.contains(user)){
                removeUsers.add(user);
            }else if(usersIds.contains(user.getId()) && !currUsers.contains(user)){
                addUsers.add(user);
            }
        }
        
        currUsers.removeAll(removeUsers);
        for (GeoUser geoUser : removeUsers) {
            geoUser.getGeoUserRoles().remove(role);
        }
        
        currUsers.addAll(addUsers);
        for (GeoUser geoUser : addUsers) {
            geoUser.getGeoUserRoles().add(role);
        }
        
        
        //add acls
        List<GeoACL> allACLs = serviceRegistry.getACLDao().list();
        Set<GeoACL> currACLS = role.getGeoACLs();
        Set<Long> aclIds = roleUI.getAclIds();
        if(null == allACLs)
            allACLs = Collections.EMPTY_LIST;
        if(null == currACLS){
            currACLS = new HashSet<GeoACL>();
            role.setGeoACLs(currACLS);
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
        for (GeoACL geoACL : removeACLs) {
            geoACL.getGeoUserRoles().remove(role);
        }
        
        currACLS.addAll(addACLs);
        for (GeoACL geoACL : addACLs) {
            geoACL.getGeoUserRoles().add(role);
        }
        
        getDao().update(role, true);

        return "redirect:/admin/role/" + roleUI.getId();
    }
    
    
    @RequestMapping(value="/remove/{id}", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody boolean roleRemove(@PathVariable Long id) {
        GeoUserRole u = getDao().get(id);
        getDao().remove(u.getId());
        return true;
    }
    
}
