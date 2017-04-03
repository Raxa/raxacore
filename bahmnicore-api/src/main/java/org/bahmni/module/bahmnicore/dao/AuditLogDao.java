package org.bahmni.module.bahmnicore.dao;


import org.bahmni.module.bahmnicore.model.AuditLog;

import java.util.Date;
import java.util.List;

public interface AuditLogDao {
    List<AuditLog> getLogs(String username, String patientId, Date startDateTime, Integer lastAuditLogId, Boolean prev, Boolean defaultView);
    void saveAuditLog(AuditLog auditLog);
}
