package org.bahmni.module.admin.auditLog.service;

import org.bahmni.module.admin.auditLog.model.AuditLog;

import java.util.Date;
import java.util.List;

public interface AuditLogDaoService {
    public List<AuditLog> getLogs(String username, String query, Date startDateTime, Integer lastAuditLogId, Boolean prev);

}
