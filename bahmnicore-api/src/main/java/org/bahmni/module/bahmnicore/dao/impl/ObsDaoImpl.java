package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Obs;
import org.openmrs.api.ConceptNameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ObsDaoImpl implements ObsDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Obs> getNumericObsByPerson(String personUUID) {
        Query query = sessionFactory.getCurrentSession().createQuery(
                "select obs from Obs as obs inner join fetch " +
                        "obs.concept as concept inner join fetch " +
                        "concept.datatype as datatype inner join " +
                        "obs.person as person " +
                        "where datatype.hl7Abbreviation = :hl7abrv  " +
                        "and person.uuid= :personUUID  " +
                        "and obs.voided = false ");
        query.setString("hl7abrv", ConceptDatatype.NUMERIC);
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
                        "where datatype.hl7Abbreviation = :hl7abrv " +
                        "and person.uuid = :personUUID " +
                        "and obs.voided = false ");
        query.setString("hl7abrv", ConceptDatatype.NUMERIC);
        query.setString("personUUID", personUUID);
        return query.list();

    }

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
                        " and cn.voided = false " +
                        " and obs.voided = false " +
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
                        " and cn.voided = false " +
                        " and obs.voided = false" +
                        " order by obs.obsDatetime desc ");

        queryToGetObservations.setMaxResults(limit);
        queryToGetObservations.setString("patientUuid", patientUuid);
        queryToGetObservations.setParameter("conceptName", conceptName);
        queryToGetObservations.setParameter("conceptNameType", ConceptNameType.FULLY_SPECIFIED);
        return queryToGetObservations.list();
    }

    @Override
    public List<Obs> getLatestObsForConceptSetByVisit(String patientUuid, String conceptName, Integer visitId) {
        if (visitId == null) return new ArrayList<>();

        String queryString =
                "select obs\n" +
                        "from Obs obs join obs.encounter enc join enc.visit v \n" +
                        "where obs.voided = false and obs.concept.conceptId in " +
                        "   ( select cs.concept.conceptId\n" +
                        "     from ConceptName cn, ConceptSet cs\n" +
                        "     where cs.conceptSet.conceptId = cn.concept.conceptId and cn.conceptNameType='FULLY_SPECIFIED' and cn.name=:conceptName)\n" +
                        "   and obs.person.uuid=:patientUuid and v.visitId =:visitId order by enc.encounterId desc";
        Query queryToGetObs = sessionFactory.getCurrentSession().createQuery(queryString);
        queryToGetObs.setString("conceptName", conceptName);
        queryToGetObs.setString("patientUuid", patientUuid);
        queryToGetObs.setInteger("visitId", visitId);

        return withUniqueConcepts(filterByRootConcept(queryToGetObs.list(), conceptName));
    }

    private List<Obs> filterByRootConcept(List<Obs> obs, String parentConceptName) {
        List<Obs> filteredList = new ArrayList<>();
        for (Obs ob : obs) {
            if (partOfParent(ob, parentConceptName)) {
                filteredList.add(ob);
            }
        }
        return filteredList;
    }

    private boolean partOfParent(Obs ob, String parentConceptName) {
        if (ob == null) return false;
        if (ob.getConcept().getName().getName().equals(parentConceptName)) return true;
        return partOfParent(ob.getObsGroup(), parentConceptName);
    }


    private List<Obs> withUniqueConcepts(List<Obs> observations) {
        Map<Integer, Integer> conceptToEncounterMap = new HashMap<>();
        List<Obs> filteredObservations = new ArrayList<>();
        for (Obs obs : observations) {
            Integer encounterId = conceptToEncounterMap.get(obs.getConcept().getId());
            if(encounterId == null) {
                conceptToEncounterMap.put(obs.getConcept().getId(), obs.getEncounter().getId());
                filteredObservations.add(obs);
            }
            else if (obs.getEncounter().getId().intValue() == encounterId.intValue()) {
                filteredObservations.add(obs);
            }
        }
        return filteredObservations;
    }

    private List<Integer> getVisitIdsFor(String patientUuid, Integer numberOfVisits) {
        Query queryToGetVisitIds = sessionFactory.getCurrentSession().createQuery(
                "select v.visitId " +
                        " from Visit as v " +
                        " where v.patient.uuid = :patientUuid " +
                        " and v.voided = false " +
                        "order by v.startDatetime desc");
        queryToGetVisitIds.setString("patientUuid", patientUuid);
        if (numberOfVisits != null) {
            queryToGetVisitIds.setMaxResults(numberOfVisits);
        }
        return queryToGetVisitIds.list();
    }


}
