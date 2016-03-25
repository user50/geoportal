package org.w2fc.geoportal.dao;

import org.w2fc.geoportal.domain.OperationStatus;
import org.w2fc.geoportal.reports.ReportRaw;

import java.util.List;

/**
 * @author Yevhen
 */
public interface OperationStatusRepository{
    Long save(OperationStatus operationStatus);
    List<OperationStatus> get(String pid);
    List<OperationStatus> list();
    List<ReportRaw> reports();
}
