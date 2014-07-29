package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.PersonObsDao;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Obs;
import org.openmrs.api.ConceptNameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PersonObsDaoImpl implements PersonObsDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Obs> getNumericObsByPerson(String personUUID) {
        Query query = sessionFactory.getCurrentSession().createQuery(
                        "select obs from Obs as obs inner join fetch " +
                                "obs.concept as concept inner join fetch " +
                                "concept.datatype as datatype inner join " +
                                "obs.person as person " +
                                "where datatype.hl7Abbreviation= '" + ConceptDatatype.NUMERIC + "' and person.uuid= :personUUID");
        query.setString("personUUID", personUUID);
        return query.list();

    }

    @Override
    public List<Concept> getNumericConceptsForPerson(String personUUID) {
        Query query = sessionFactory.getCurrentSession().createQuery(
                    "select concept " +
                        "from Obs as obs " +
                        "inner join obs.concept as concept " +
                        "inner join concept.datatype as datatype " +
                        "inner join obs.person as person " +
                        "where datatype.hl7Abbreviation = '" + ConceptDatatype.NUMERIC + "' and person.uuid = :personUUID");
        query.setString("personUUID", personUUID);
        return query.list();

    }

    @Override
    public List<Obs> getObsFor(String patientUuid, List<String> conceptNames, Integer numberOfVisits) {
        List<Integer> listOfVisitIds = getVisitIdsFor(patientUuid, numberOfVisits);
        if (listOfVisitIds == null || listOfVisitIds.isEmpty())
            return new ArrayList<>();

        Query queryToGetObservations = sessionFactory.getCurrentSession().createQuery(
                    "select obs " +
                        " from Obs as obs, ConceptName as cn " +
                        " where obs.person.uuid = :patientUuid " +
                        " and obs.encounter.visit.visitId in (:listOfVisitIds) " +
                        " and cn.concept = obs.concept.conceptId " +
                        " and cn.name in (:conceptNames) " +
                        " and cn.conceptNameType = :conceptNameType " +
                        " and cn.voided = 0 " +
                        " and obs.voided = 0 " +
                        " order by obs.obsDatetime desc ");
        queryToGetObservations.setString("patientUuid", patientUuid);
        queryToGetObservations.setParameterList("conceptNames", conceptNames);
        queryToGetObservations.setParameterList("listOfVisitIds", listOfVisitIds);
        queryToGetObservations.setParameter("conceptNameType", ConceptNameType.FULLY_SPECIFIED);
        return queryToGetObservations.list();
    }

    @Override
    public List<Obs> getLatestObsFor(String patientUuid, String conceptName, Integer limit) {
        Query queryToGetObservations = sessionFactory.getCurrentSession().createQuery(
                "select obs " +
                        " from Obs as obs, ConceptName as cn " +
                        " where obs.person.uuid = :patientUuid " +
                        " and cn.concept = obs.concept.conceptId " +
                        " and cn.name = (:conceptName) " +
                        " and cn.conceptNameType = :conceptNameType " +
                        " and obs.voided = false" +
                        " order by obs.obsDatetime desc ");

        queryToGetObservations.setMaxResults(limit);
        queryToGetObservations.setString("patientUuid", patientUuid);
        queryToGetObservations.setParameter("conceptName", conceptName);
        queryToGetObservations.setParameter("conceptNameType", ConceptNameType.FULLY_SPECIFIED);
        return queryToGetObservations.list();
    }

    private List<Integer> getVisitIdsFor(String patientUuid, Integer numberOfVisits) {
        Query queryToGetVisitIds = sessionFactory.getCurrentSession().createQuery(
                    "select v.visitId " +
                        " from Visit as v " +
                        " where v.patient.uuid = :patientUuid " +
                        " and v.voided = 0 " +
                        "order by v.startDatetime desc");
        queryToGetVisitIds.setString("patientUuid", patientUuid);
        if (numberOfVisits != null) {
            queryToGetVisitIds.setMaxResults(numberOfVisits);
        }
        return queryToGetVisitIds.list();
    }
}
