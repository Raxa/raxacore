package org.bahmni.module.admin.auditLog.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.admin.auditLog.dao.AuditLogDao;
import org.bahmni.module.admin.auditLog.model.AuditLog;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AuditLogDaoImpl implements AuditLogDao {
    @Autowired
    private SessionFactory sessionFactory;

    protected static Integer LIMIT = 50;

    @Override
    public List<AuditLog> getLogs(String username, String patientId, Date startDateTime, Integer lastAuditLogId, Boolean prev) {
        List<AuditLog> logs = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AuditLog.class);
        criteria.setMaxResults(LIMIT);
        if (startDateTime == null && lastAuditLogId == null) {
            criteria.addOrder(Order.desc("auditLogId"));
        } else {
            if (lastAuditLogId != null && StringUtils.isNotEmpty(lastAuditLogId.toString())) {
                criteria.add(prev ? Restrictions.between("auditLogId", lastAuditLogId - LIMIT, lastAuditLogId - 1) : Restrictions.gt("auditLogId", lastAuditLogId));
            }
            if (startDateTime != null) {
                criteria.add(Restrictions.ge("dateCreated", startDateTime));
            }
        }
        logs.addAll(criteria.list());
        return logs;
    }
}
