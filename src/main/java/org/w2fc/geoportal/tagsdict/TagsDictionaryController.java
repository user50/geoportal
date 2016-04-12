package org.w2fc.geoportal.tagsdict;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.w2fc.geoportal.dao.AddnsTagsDictionaryDao;
import org.w2fc.geoportal.domain.AddnsTagsDictionary;
import org.w2fc.geoportal.domain.AddnsTagsDictionaryValues;
import org.w2fc.spring.AbstractController;


@Controller
@RequestMapping(value = "/tags")
public class TagsDictionaryController extends AbstractController<AddnsTagsDictionary, AddnsTagsDictionaryDao, Long>{
  

    @Autowired
    @Override
    public void setAutowiredDao(AddnsTagsDictionaryDao dao) {
        setDao(dao);
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String getList1(Model model) {
        return "tags/dictionary";
    }
    
    
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody AddnsTagsDictionary  saveOrUpdate(@RequestBody AddnsTagsDictionary dictionary){
        
        AddnsTagsDictionary dictionary1 = null;

        if(null == dictionary.getId()) {
            dictionary1 = dictionary;
            
        }else{
            dictionary1 = getById(dictionary.getId());

            // copy data
            copyBeanProperties(dictionary, dictionary1);
            
            // remove old values
            dictionary1.getValues().clear();

            // add new values
            dictionary1.getValues().addAll(dictionary.getValues());
        }

        // add parent to children
        for (AddnsTagsDictionaryValues v : dictionary1.getValues()) {
            v.setDictionary(dictionary1);
        }

        return addObject(dictionary1);
    }
    

    @RequestMapping(value = "/getByKey", method = RequestMethod.GET)
    public @ResponseBody AddnsTagsDictionary  getByKey(@RequestParam String key){
        return getDao().getByKey(key);
    }
    
    
    @RequestMapping(value = "/autocompleteKey", method = RequestMethod.GET)
    public @ResponseBody List<String>  getAutocompleteKey(@RequestParam("term") String q){
        return getDao().autocompleteKeys(q);
    }
    
    
    @RequestMapping(value = "/autocompleteValue", method = RequestMethod.GET)
    public @ResponseBody List<String>  getAutocompleteValue(
            @RequestParam("term") String q, 
            @RequestParam("f") String fieldName){
        return getDao().autocompleteValues(q, fieldName);
    }
}
