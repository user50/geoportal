package org.w2fc.geoportal.dao;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.domain.AddnsRating;


@Service
@Repository
public class AddnsRatingDaoImpl extends AbstractDaoDefaulImpl<AddnsRating, Long> implements AddnsRatingDao {

    protected AddnsRatingDaoImpl() {
        super(AddnsRating.class);
    }
}
