package org.bahmni.module.admin.auditlog.service;

import org.bahmni.module.admin.auditlog.mapper.AuditLogMapper;

import java.util.ArrayList;
import java.util.Date;

public interface AuditLogDaoService {
    public ArrayList<AuditLogMapper> getLogs(String username, String query, Date startDateTime, Integer lastAuditLogId, Boolean prev, Boolean defaultView);
}
