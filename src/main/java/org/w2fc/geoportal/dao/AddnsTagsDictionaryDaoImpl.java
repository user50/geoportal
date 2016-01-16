package org.w2fc.geoportal.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.domain.AddnsTagsDictionary;


@Service
@Repository
public class AddnsTagsDictionaryDaoImpl extends AbstractDaoDefaulImpl<AddnsTagsDictionary, Long> implements AddnsTagsDictionaryDao {

    protected AddnsTagsDictionaryDaoImpl() {
        super(AddnsTagsDictionary.class);
    }

    
    @Override
    public AddnsTagsDictionary getByKey(String key) {
        return (AddnsTagsDictionary) getCurrentSession()
                .createQuery("from AddnsTagsDictionary where key=:key").setParameter("key", key).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> autocompleteKeys(String q) {
        return 
            getCurrentSession()
                .createQuery("select distinct d.key from AddnsTagsDictionary d where upper(d.key) like upper(:q) order by d.key")
                .setParameter("q", q + "%").list();
    }

    
    @SuppressWarnings("unchecked")
    @Override
    public List<String> autocompleteValues(String q, String fieldName) {
        return 
                getCurrentSession()
                    .createQuery("select distinct v.value from AddnsTagsDictionary d inner join d.values v "
                            + "where d.key=:fieldName and upper(v.value) like upper(:q) order by v.value")
                            .setParameter("fieldName", fieldName)
                            .setParameter("q", q + "%")
                            .list();
    }
}
