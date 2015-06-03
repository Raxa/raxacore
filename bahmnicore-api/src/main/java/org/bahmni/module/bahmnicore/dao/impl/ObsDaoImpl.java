package org.bahmni.module.bahmnicore.dao.impl;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.openmrs.*;
import org.openmrs.Order;
import org.openmrs.api.ConceptNameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ObsDaoImpl implements ObsDao {
    @Autowired
    private SessionFactory sessionFactory;
    private enum OrderBy {ASC,DESC};

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

    public List<Obs> getObsFor(String patientUuid, List<String> conceptNames, Integer numberOfVisits, Integer limit, OrderBy sortOrder, List<String> obsIgnoreList, Boolean filterObsWithOrders, Order order) {
        List<Integer> listOfVisitIds = getVisitIdsFor(patientUuid, numberOfVisits);
        if (listOfVisitIds == null || listOfVisitIds.isEmpty())
            return new ArrayList<>();

        return getObsByPatientAndVisit(patientUuid, conceptNames, listOfVisitIds, limit, sortOrder, obsIgnoreList, filterObsWithOrders, order);
    }

    private List<Obs> getObsByPatientAndVisit(String patientUuid, List<String> conceptNames, List<Integer> listOfVisitIds, Integer limit, OrderBy sortOrder, List<String> obsIgnoreList, Boolean filterOutOrderObs, Order order) {

        StringBuilder query = new StringBuilder("select obs from Obs as obs, ConceptName as cn " +
                " where obs.person.uuid = :patientUuid " +
                " and obs.encounter.visit.visitId in (:listOfVisitIds) " +
                " and cn.concept = obs.concept.conceptId " +
                " and cn.name in (:conceptNames) " +
                " and cn.conceptNameType = :conceptNameType " +
                " and cn.voided = false and obs.voided = false ");

        if(CollectionUtils.isNotEmpty(obsIgnoreList)) {
            query.append(" and cn.name not in (:obsIgnoreList) ");
        }
        if(filterOutOrderObs) {
            query.append( " and obs.order.orderId is null ");
        }
        if(null != order) {
                    query.append( " and obs.order = (:order) ");
                }
        if(sortOrder == OrderBy.ASC){
            query.append(" order by obs.obsDatetime asc ");
        }else{
            query.append(" order by obs.obsDatetime desc ");
        }


        Query queryToGetObservations = sessionFactory.getCurrentSession().createQuery(query.toString());
        queryToGetObservations.setMaxResults(limit);
        queryToGetObservations.setString("patientUuid", patientUuid);
        queryToGetObservations.setParameterList("conceptNames", conceptNames);
        queryToGetObservations.setParameterList("listOfVisitIds", listOfVisitIds);
        queryToGetObservations.setParameter("conceptNameType", ConceptNameType.FULLY_SPECIFIED);
        if (null != obsIgnoreList && obsIgnoreList.size() > 0) {
            queryToGetObservations.setParameterList("obsIgnoreList", obsIgnoreList);
        }
        if (null != order) {
            queryToGetObservations.setParameter("order", order);
        }
        return queryToGetObservations.list();
    }

    public List<Obs> getInitialObsFor(String patientUuid, String conceptName, Integer numberOfVisits, Integer limit,List<String> obsIgnoreList, Boolean filterOutOrderObs) {
        return getObsFor(patientUuid, Arrays.asList(conceptName), numberOfVisits, limit, OrderBy.ASC, obsIgnoreList, filterOutOrderObs, null);
    }

    @Override
    public List<Obs> getInitialObsByVisit(Visit visit, String conceptName, Integer limit, List<String> obsIgnoreList, Boolean filterObsWithOrders) {
        return getObsByPatientAndVisit(visit.getPatient().getUuid(), Arrays.asList(conceptName), Arrays.asList(visit.getVisitId()), limit, OrderBy.ASC, obsIgnoreList, filterObsWithOrders, null);
    }

    @Override
    public List<Obs> getObsFor(String patientUuid, List<String> conceptNames, Integer numberOfVisits, List<String> obsIgnoreList, Boolean filterOutOrderObs) {
        return getObsFor(patientUuid,conceptNames,numberOfVisits,-1, OrderBy.DESC, obsIgnoreList, filterOutOrderObs, null);
    }

    public List<Obs> getLatestObsFor(String patientUuid, String conceptName, Integer numberOfVisits, Integer limit, List<String> obsIgnoreList, Boolean filterOutOrderObs, Order order) {
        return getObsFor(patientUuid,Arrays.asList(conceptName),numberOfVisits, limit, OrderBy.DESC, obsIgnoreList, filterOutOrderObs, order);
    }

    public List<Obs> getLatestObsByVisit(Visit visit, String conceptName, Integer limit, List<String> obsIgnoreList, Boolean filterOutOrderObs){
        return getObsByPatientAndVisit(visit.getPatient().getUuid(), Arrays.asList(conceptName), Arrays.asList(visit.getVisitId()), limit, OrderBy.DESC, obsIgnoreList, filterOutOrderObs, null);
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

    @Override
    public List<Obs> getObsForOrder(String orderUuid) {
        String queryString = "from Obs obs where obs.voided = false and obs.order.uuid = :orderUuid order by obs.obsDatetime desc" ;
        Query queryToGetObs = sessionFactory.getCurrentSession().createQuery(queryString);
        queryToGetObs.setString("orderUuid", orderUuid);

        return queryToGetObs.list();
    }

    @Override
    public List<Obs> getObsForVisits(List<Person> persons, ArrayList<Encounter> encounters, List<Concept> conceptsForNames,  Collection<Concept> obsIgnoreList, boolean filterOutOrders) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class, "obs");
        if(CollectionUtils.isNotEmpty(persons)) {
            criteria.add(Restrictions.in("person", persons));
        }

        if(CollectionUtils.isNotEmpty(encounters)) {
            criteria.add(Restrictions.in("encounter", encounters));
        }
        if(CollectionUtils.isNotEmpty(conceptsForNames)) {
            criteria.add(Restrictions.in("concept", conceptsForNames));
        }
        if(CollectionUtils.isNotEmpty(obsIgnoreList)) {
            criteria.add(Restrictions.not(Restrictions.in("concept", obsIgnoreList)));
        }
        if(filterOutOrders){
            criteria.add(Restrictions.isNull("order"));
        }
        criteria.add(Restrictions.eq("voided", Boolean.valueOf(false)));

        criteria.addOrder(org.hibernate.criterion.Order.desc("obsDatetime"));

        return criteria.list();
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
