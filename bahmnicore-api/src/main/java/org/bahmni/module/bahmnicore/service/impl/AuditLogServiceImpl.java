package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.auditLog.AuditLogResponse;
import org.bahmni.module.bahmnicore.contract.auditLog.AuditLogPayload;
import org.bahmni.module.bahmnicore.dao.AuditLogDao;
import org.bahmni.module.bahmnicore.model.AuditLog;
import org.bahmni.module.bahmnicore.service.AuditLogService;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private AuditLogDao auditLogDao;

    @Autowired
    public AuditLogServiceImpl(AuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    @Override
    public ArrayList<AuditLogResponse> getLogs(String username, String patientId, Date startDateTime, Integer lastAuditLogId, Boolean prev, Boolean defaultView) {
        ArrayList<AuditLogResponse> auditLogResponses = new ArrayList<>();
        List<AuditLog> auditLogs = auditLogDao.getLogs(username, patientId, startDateTime, lastAuditLogId, prev, defaultView);
        auditLogs.forEach(auditLog -> {
            Patient patient = auditLog.getPatient();
            PatientIdentifier patientIdentifier = patient != null ? patient.getPatientIdentifier() : null;
            String identifier = patientIdentifier != null ? patientIdentifier.getIdentifier() : null;
            auditLogResponses.add(new AuditLogResponse(auditLog.getAuditLogId(),
                    auditLog.getDateCreated(), auditLog.getEventType(),
                    identifier,
                    auditLog.getUser().getUsername(), auditLog.getMessage()));
        });
        return auditLogResponses;
    }

    @Override
    public void createAuditLog(AuditLogPayload log) {
        User user = Context.getAuthenticatedUser();
        Patient patient = Context.getPatientService().getPatientByUuid(log.getPatientUuid());
        AuditLog auditLog = new AuditLog();
        auditLog.setEventType(log.getEventType());
        auditLog.setUser(user);
        auditLog.setEventType(log.getEventType());
        auditLog.setPatient(patient);
        auditLog.setDateCreated(new Date());
        auditLog.setMessage(log.getMessage());
        auditLog.setUuid(UUID.randomUUID().toString());
        auditLogDao.saveAuditLog(auditLog);

    }


}
