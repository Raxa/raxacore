package org.bahmni.module.bahmnicore.dao;

import org.openmrs.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface ObsDao {
    List<Obs> getNumericObsByPerson(String personUUID);

    List<Concept> getNumericConceptsForPerson(String personUUID);

    List<Obs> getObsFor(String patientUuid, List<String> conceptName, Integer numberOfVisits, List<String> obsIgnoreList, Boolean filterOutOrderObs);

    List<Obs> getLatestObsFor(String patientUuid, String conceptName, Integer limit);

    List<Obs> getInitialObsFor(String patientUuid, String conceptName, Integer numberOfVisits, Integer limit,List<String> obsIgnoreList, Boolean filterOutOrderObs);

    List<Obs> getInitialObsByVisit(Visit visit, String conceptName, Integer limit, List<String> obsIgnoreList, Boolean filterObsWithOrders);

    List<Obs> getLatestObsFor(String patientUuid, String conceptName, Integer numberOfVisits, Integer limit, List<String> obsIgnoreList, Boolean filterOutOrderObs);

    List<Obs> getLatestObsForConceptSetByVisit(String patientUuid, String conceptNames, Integer visitId);

    List<Obs> getLatestObsByVisit(Visit visit, String conceptName, Integer limit, List<String> obsIgnoreList, Boolean filterOutOrderObs);

    List<Obs> getObsForOrder(String orderUuid);

    List<Obs> getObsForVisits(List<Person> persons, ArrayList<Encounter> visit, List<Concept> conceptsForNames,  Collection<Concept> obsIgnoreList, boolean filterOutOrders);
}
