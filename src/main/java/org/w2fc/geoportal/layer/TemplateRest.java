package org.w2fc.geoportal.layer;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.conf.Constants;
import org.w2fc.geoportal.dao.AddnsPopupTemplateDao;
import org.w2fc.geoportal.domain.AddnsPopupTemplate;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.spring.AbstractController;

@Controller
@RequestMapping(value = "/tmpl")
public class TemplateRest extends AbstractController<AddnsPopupTemplate, AddnsPopupTemplateDao, Long>{

	
	@Autowired
	private ServiceRegistry serviceRegistry;
	 
	@Qualifier("AddnsPopupTemplateDao")
	@Override
    public void setAutowiredDao (AddnsPopupTemplateDao dao) {
		setDao(dao);
	}

	
	 /*
     *      UI Templates
     */

    @RequestMapping(value="/")
    @Transactional(readOnly=true)
    public String getTemplateEditor(Model model){
    	boolean isGranded = false;
       	Collection<? extends GrantedAuthority> a = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority grantedAuthority : a) {
            if(Constants.GEOPORTAL_ROLE_EDITOR.equals(grantedAuthority.getAuthority())){
            	isGranded = true;
            }
        }
        if(!isGranded){
       		throw new AuthorizationServiceException("Доступ разрешен только пользователям с правами редактора");
       	}
        
       	model.addAttribute("templates", serviceRegistry.getGeoObjectDao().list());
        return "markup/layer/TemplatesEdit";
    }
    
    @RequestMapping(value="/{id}")
    public String getTemplateEditorForTemplate(@PathVariable Long id, Model model){
    	model.addAttribute("tmplId", id);
    	return getTemplateEditor(model);
    }
    
    @RequestMapping(value="/get/{id}")
    @Transactional(readOnly=true)
    @ResponseBody
    public AddnsPopupTemplate getTemplateById(@PathVariable Long id){
        return getDao().get(id);
    }
    
    @RequestMapping(value="/remove/{id}")
    @Transactional
    @ResponseBody
    public Boolean deleteTemplateById(@PathVariable Long id){
    	List<GeoLayer> layers = serviceRegistry.getLayerDao().list();
    	for(GeoLayer layer : layers){
    		if(layer.getPopupTemplate() != null){
    			if(layer.getPopupTemplate().getId() == id){
    				layer.setPopupTemplate(null);
    				serviceRegistry.getLayerDao().update(layer);
    			}
    		}
    	}
        getDao().remove(id, true);
        return true;
    }
    
    
    @RequestMapping(value="/createOrUpdate" , method = RequestMethod.POST)
    @CacheEvict(value="layerPermanent", allEntries = true)
    @Transactional
    public String updateTemplate(@ModelAttribute AddnsPopupTemplate tmpl){
    	if(tmpl.getId() != null){
    		//save
    		updateByObject(tmpl);
    	}else{
    		//create
    		addObject(tmpl);
    	}
    	return "redirect:/tmpl/";
    }

	
}
