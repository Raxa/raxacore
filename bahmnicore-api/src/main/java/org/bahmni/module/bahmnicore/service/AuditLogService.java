package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.auditLog.AuditLogResponse;
import org.bahmni.module.bahmnicore.contract.auditLog.AuditLogPayload;

import java.util.ArrayList;
import java.util.Date;

public interface AuditLogService {
    ArrayList<AuditLogResponse> getLogs(String username, String patientId, Date startDateTime, Integer lastAuditLogId, Boolean prev, Boolean defaultView);
    void createAuditLog(AuditLogPayload log);

}
