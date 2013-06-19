package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.ActivePatientListDao;
import org.bahmni.module.bahmnicore.model.ResultList;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class ActivePatientListDaoImpl implements ActivePatientListDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public ResultList getUnique(String location) {
        SQLQuery sqlQuery = sessionFactory
                .getCurrentSession()
                .createSQLQuery(
                        "select distinct pn.given_name , pn.family_name, pi.identifier,concat(\"\",p.uuid) \n" +
                                "from visit v \n" +
                                "join person_name pn on v.patient_id = pn.person_id \n" +
                                "join patient_identifier pi on v.patient_id = pi.patient_id \n" +
                                "join person p on  p.person_id = v.patient_id\n" +
                                "where v.date_stopped is null and v.voided=0 and v.location_id in \n" +
                                "(select location_id from location where name = :location)");
        sqlQuery.setParameter("location", location);
        return new ResultList(sqlQuery.list());
    }
}
