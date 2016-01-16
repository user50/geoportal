package org.w2fc.spring;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.geoportal.dao.AbstractDao;
import org.w2fc.geoportal.domain.AbstractDomain;


/**
 * Usage:
 * <blockquote><pre>
 * {@literal @}Controller
 * {@literal @}RequestMapping(value = "/some-mapping")
 * public class SomeController extends AbstractController &lt;SomeEntity, SomeDao, PrimaryIdType>{
 *    
 *     {@literal @}Autowired
 *     {@literal @}Override
 *     public void setAutowiredDao(SomeDao dao) {
 *       setDao(dao);
 *    }
 * }
 * </pre></blockquote>
 * 
 */
public abstract class AbstractController<T, D extends AbstractDao<T, I>, I extends Serializable> {


    protected D dao = null;


    /**
     * Usage:
     * <blockquote><pre>
     * {@literal @}Autowired
     * {@literal @}Override
     * public void setAutowiredDao(SomeDao dao) {
     *   setDao(dao);
     * }
     * </pre></blockquote>
     * @param dao
     */
    public abstract void setAutowiredDao(D dao);
    

    protected void setDao(D dao){
        if(this.dao == null)
            this.dao = dao;
    }
    
    
    public D getDao() {
        return dao;
    }


    @RequestMapping(value = "/rest/list", method = RequestMethod.GET)
    public @ResponseBody List<T> getList() {
        return dao.list();
    }
    
    
    @RequestMapping(value = "/rest/{id}", method = RequestMethod.GET)
    public @ResponseBody T getById(@PathVariable I id) {
        return dao.get(id);
    }

    
    @RequestMapping(value = "/rest/add", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody T addObject(@ModelAttribute T object) {
        return dao.add(object, true);
    }
    
    
    @RequestMapping(value = "/rest/update", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody T updateByObject(@ModelAttribute T object) {
        T obj1 = dao.get(object);
        copyBeanProperties(object, obj1);
        return dao.update(obj1, true);
    }


    @RequestMapping(value = "/rest/remove/{id}", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody boolean removeById(@PathVariable I id) {
        dao.remove(id, true);
        return true;
    }

    
    protected void copyBeanProperties(T source, T target) {
        BeanUtils.copyProperties(source, target, 
                concat(new String[]{"id", "version"}, AbstractDomain.getExcludeList(source).toArray(new String[0])));
    }


    private String[] concat(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
