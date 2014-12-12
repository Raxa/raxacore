package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.VisitDao;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class VisitDaoImpl implements VisitDao {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public Visit getLatestVisit(String patientUuid, String conceptName) {
        String queryString = "select v\n" +
                "from Obs obs join obs.encounter enc join enc.visit v, ConceptName cn \n" +
                "where cn.concept.conceptId = obs.concept.conceptId and cn.name=:conceptName and cn.conceptNameType='FULLY_SPECIFIED' and obs.person.uuid=:patientUuid\n" +
                "order by v.startDatetime desc";
        Query queryToGetVisitId = sessionFactory.getCurrentSession().createQuery(queryString);
        queryToGetVisitId.setString("conceptName", conceptName);
        queryToGetVisitId.setString("patientUuid", patientUuid);
        queryToGetVisitId.setMaxResults(1);
        
        return (Visit) queryToGetVisitId.uniqueResult();
    }
    
}
