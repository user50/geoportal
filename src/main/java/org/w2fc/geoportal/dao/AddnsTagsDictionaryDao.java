package org.w2fc.geoportal.dao;

import java.util.List;

import org.w2fc.geoportal.domain.AddnsTagsDictionary;


public interface AddnsTagsDictionaryDao extends AbstractDao<AddnsTagsDictionary, Long> {

    public AddnsTagsDictionary getByKey(String key);
    public List<String> autocompleteKeys(String q);
    public List<String> autocompleteValues(String q, String fieldName);
}
