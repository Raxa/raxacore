package org.bahmni.module.bahmnicore.dao;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Visit;

import java.util.Collection;
import java.util.List;

public interface ObsDao {
    List<Obs> getNumericObsByPerson(String personUUID);

    List<Concept> getNumericConceptsForPerson(String personUUID);

    List<Obs> getObsFor(String patientUuid, List<String> conceptName, Integer numberOfVisits, List<String> obsIgnoreList, Boolean filterObsWithOrders);

    List<Obs> getLatestObsFor(String patientUuid, String conceptName, Integer limit);

    List<Obs> getInitialObsFor(String patientUuid, String conceptName, Integer numberOfVisits, Integer limit);

    List<Obs> getInitialObsByVisit(Visit visit, String conceptName, Integer limit);

    List<Obs> getLatestObsFor(String patientUuid, String conceptName, Integer numberOfVisits, Integer limit, List<String> obsIgnoreList, Boolean filterObsWithOrders);

    List<Obs> getLatestObsForConceptSetByVisit(String patientUuid, String conceptNames, Integer visitId);

    List<Obs> getLatestObsByVisit(Visit visit, String conceptName, Integer limit, List<String> obsIgnoreList, Boolean filterObsWithOrders);

}
