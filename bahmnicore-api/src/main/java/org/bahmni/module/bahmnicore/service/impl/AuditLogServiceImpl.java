package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.auditLog.AuditLogResponse;
import org.bahmni.module.bahmnicore.contract.auditLog.BahmniAuditLog;
import org.bahmni.module.bahmnicore.dao.AuditLogDao;
import org.bahmni.module.bahmnicore.model.AuditLog;
import org.bahmni.module.bahmnicore.service.AuditLogService;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final String SQL = "INSERT INTO audit_log(user_id, patient_id, event_type, message, date_created, uuid) " +
            "VALUES( %s, %s, '%s', '%s', now(), uuid())";

    @Autowired
    PatientService patientService;

    @Autowired
    private AuditLogDao auditLogDao;

    @Override
    public ArrayList<AuditLogResponse> getLogs(String username, String patientId, Date startDateTime, Integer lastAuditLogId, Boolean prev, Boolean defaultView) {
        ArrayList<AuditLogResponse> auditLogResponses = new ArrayList<>();
        List<AuditLog> auditLogs = auditLogDao.getLogs(username, patientId, startDateTime, lastAuditLogId, prev, defaultView);
        auditLogs.forEach(auditLog -> auditLogResponses.add(new AuditLogResponse(auditLog.getAuditLogId(),
                auditLog.getDateCreated(), auditLog.getEventType(),
                auditLog.getPatient().getPatientIdentifier().getIdentifier(),
                auditLog.getUser().getUsername(), auditLog.getMessage())));
        return auditLogResponses;
    }

    @Override
    public void log(BahmniAuditLog log) {
        Integer userId = Context.getAuthenticatedUser().getId();
        Patient patient = patientService.getPatientByUuid(log.getPatientUuid());
        Integer patientId = null;
        if (patient != null) {
            patientId = patient.getId();
        }
        String sqlQuery = String.format(SQL, userId, patientId, log.getEvent(), log.getMessage());

        AdministrationService administrationService = Context.getAdministrationService();
        administrationService.executeSQL(sqlQuery, false);

    }


}
