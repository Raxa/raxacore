package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.AuditLogDao;
import org.bahmni.module.bahmnicore.model.AuditLog;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AuditLogDaoImpl implements AuditLogDao {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private BahmniPatientService bahmniPatientService;

    protected static Integer LIMIT = 50;

    @Override
    public List<AuditLog> getLogs(String username, String patientId, Date startDateTime,
                                  Integer lastAuditLogId, Boolean prev, Boolean defaultView) {
        // prev will be always not null boolean value
        List<AuditLog> logs = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AuditLog.class, "auditLog");
        criteria.createAlias("auditLog.user", "user");

        criteria.setMaxResults(LIMIT);
        if (prev || defaultView) {
            criteria.addOrder(Order.desc("auditLogId"));
        }
        if (lastAuditLogId != null) {
            criteria.add(prev ? Restrictions.lt("auditLogId", lastAuditLogId)
                    : Restrictions.gt("auditLogId", lastAuditLogId));
        }
        if (startDateTime != null) {
            criteria.add(Restrictions.ge("dateCreated", startDateTime));
        }
        if (username != null) {
            criteria.add(Restrictions.eq("user.username", username));
        }
        if (patientId != null) {
            List<Patient> patients = bahmniPatientService.get(patientId, true);
            if(patients.size() == 0){
                return logs;
            }
            criteria.add(Restrictions.eq("patient", patients.get(0)));
        }

        logs.addAll(criteria.list());
        if (prev) {
            Collections.reverse(logs);
        }
        return logs;
    }
}
