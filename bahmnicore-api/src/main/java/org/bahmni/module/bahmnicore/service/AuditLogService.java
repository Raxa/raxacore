package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.auditLog.AuditLogResponse;
import org.bahmni.module.bahmnicore.contract.auditLog.BahmniAuditLog;

import java.util.ArrayList;
import java.util.Date;

public interface AuditLogService {
    public ArrayList<AuditLogResponse> getLogs(String username, String patientId, Date startDateTime, Integer lastAuditLogId, Boolean prev, Boolean defaultView);
    public void log(BahmniAuditLog log);
}
