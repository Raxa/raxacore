package org.bahmni.module.admin.auditlog.service.impl;

import org.bahmni.module.admin.auditlog.dao.AuditLogDao;
import org.bahmni.module.admin.auditlog.model.AuditLog;
import org.bahmni.module.admin.auditlog.service.AuditLogDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<AuditLog> getLogs(String username, String patientId, Date startDateTime, Integer lastAuditLogId, Boolean prev) {
        return auditLogDao.getLogs(username, patientId, startDateTime, lastAuditLogId, prev);
    }
}
