package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.auditLog.AuditLogPayload;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.ArrayList;
import java.util.Date;

public interface AuditLogService {
    ArrayList<SimpleObject> getLogs(String username, String patientId, Date startDateTime, Integer lastAuditLogId,
                                    Boolean prev, Boolean defaultView);

    void createAuditLog(AuditLogPayload log);
}
