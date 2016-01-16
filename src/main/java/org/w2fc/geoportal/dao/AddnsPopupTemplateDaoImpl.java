package org.w2fc.geoportal.dao;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.domain.AddnsPopupTemplate;


@Service
@Repository
public class AddnsPopupTemplateDaoImpl extends AbstractDaoDefaulImpl<AddnsPopupTemplate, Long> implements AddnsPopupTemplateDao {

    protected AddnsPopupTemplateDaoImpl() {
        super(AddnsPopupTemplate.class);
    }
}
