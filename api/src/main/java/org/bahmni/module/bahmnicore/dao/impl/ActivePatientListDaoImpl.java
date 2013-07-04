package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.ActivePatientListDao;
import org.bahmni.module.bahmnicore.model.ResultList;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ActivePatientListDaoImpl implements ActivePatientListDao {
    private SessionFactory sessionFactory;

    @Autowired
    public ActivePatientListDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ResultList getPatientList() {
        SQLQuery sqlQuery = sessionFactory
                .getCurrentSession()
                .createSQLQuery("select distinct pn.given_name, pn.family_name, pi.identifier,concat(\"\",p.uuid) from visit v " +
                        "join person_name pn on v.patient_id = pn.person_id and pn.voided = 0 " +
                        "join patient_identifier pi on v.patient_id = pi.patient_id " +
                        "join person p on p.person_id = v.patient_id " +
                        "where DATE(v.date_created) = DATE(NOW())");
        return new ResultList(sqlQuery.list());
    }
}
