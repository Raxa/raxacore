package org.bahmni.module.admin.auditlog.service.impl;

import org.bahmni.module.admin.auditlog.dao.AuditLogDao;
import org.bahmni.module.admin.auditlog.mapper.AuditLogMapper;
import org.bahmni.module.admin.auditlog.model.AuditLog;
import org.bahmni.module.admin.auditlog.service.AuditLogDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AuditLogDaoServiceImpl implements AuditLogDaoService {

    private AuditLogDao auditLogDao;

    @Autowired
    public AuditLogDaoServiceImpl(AuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    @Override
    public ArrayList<AuditLogMapper> getLogs(String username, String patientId, Date startDateTime, Integer lastAuditLogId, Boolean prev, Boolean defaultView) {
        ArrayList<AuditLogMapper> auditLogMappers = new ArrayList<>();
        List<AuditLog> auditLogs = auditLogDao.getLogs(username, patientId, startDateTime, lastAuditLogId, prev, defaultView);
        auditLogs.forEach(auditLog -> auditLogMappers.add(new AuditLogMapper(auditLog.getAuditLogId(),
                auditLog.getDateCreated(), auditLog.getEventType(),
                auditLog.getPatient().getPatientIdentifier().getIdentifier(),
                auditLog.getUser().getUsername(), auditLog.getMessage())));
        return auditLogMappers;
    }
}
