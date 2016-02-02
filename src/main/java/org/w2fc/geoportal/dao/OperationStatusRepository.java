package org.w2fc.geoportal.dao;

import org.w2fc.geoportal.domain.OperationStatus;

/**
 * @author Yevhen
 */
public interface OperationStatusRepository{
    Long save(OperationStatus operationStatus);
}
