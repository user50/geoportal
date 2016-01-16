package org.w2fc.geoportal.dao;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.domain.ReferenceSystemProj;


@Repository
@Service
public class ReferenceSystemProjDaoImpl extends AbstractDaoDefaulImpl<ReferenceSystemProj, String> implements ReferenceSystemProjDao {

	protected ReferenceSystemProjDaoImpl() {
		super(ReferenceSystemProj.class);
	}

}
