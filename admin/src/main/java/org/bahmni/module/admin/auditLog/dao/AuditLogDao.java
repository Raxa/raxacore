package org.bahmni.module.admin.auditLog.dao;

import org.bahmni.module.admin.auditLog.model.AuditLog;

import java.util.Date;
import java.util.List;

public interface AuditLogDao {
    public List<AuditLog> getLogs(String username, String patientId, Date startDateTime, Integer lastAuditLogId, Boolean prev);
}
