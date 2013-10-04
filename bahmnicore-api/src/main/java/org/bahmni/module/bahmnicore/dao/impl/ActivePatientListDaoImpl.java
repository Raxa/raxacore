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
    private static String ADMIT_PATIENT_CONCEPT_NAME = "Admit Patient";

    @Autowired
    public ActivePatientListDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ResultList getPatientList() {
        SQLQuery sqlQuery = sessionFactory
                .getCurrentSession()
                .createSQLQuery("select distinct pn.given_name, pn.family_name, pi.identifier, concat(\"\",p.uuid), concat(\"\",v.uuid) " +
                        "from visit v " +
                        "join person_name pn on v.patient_id = pn.person_id and pn.voided = 0 " +
                        "join patient_identifier pi on v.patient_id = pi.patient_id " +
                        "join person p on p.person_id = v.patient_id " +
                        "where v.date_stopped is null");
        return new ResultList(sqlQuery.list());
    }

    @Override
    public ResultList getPatientsForAdmission() {
        String query = "select distinct pn.given_name, pn.family_name, pi.identifier, concat(\"\",p.uuid), concat(\"\",v.uuid) " +
        "from visit v " +
        "join person_name pn on v.patient_id = pn.person_id and pn.voided = 0 " +
        "join patient_identifier pi on v.patient_id = pi.patient_id " +
        "join person p on v.patient_id = p.person_id " +
        "join encounter e on v.visit_id = e.visit_id " +
        "join obs o on e.encounter_id = o.encounter_id " +
        "join concept c on o.value_coded = c.concept_id " +
        "join concept_name cn on c.concept_id = cn.concept_id " +
        "where v.date_stopped is null and cn.name = :conceptName";

        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(query);
        sqlQuery.setParameter("conceptName", ADMIT_PATIENT_CONCEPT_NAME);
        return new ResultList(sqlQuery.list());
    }
}
