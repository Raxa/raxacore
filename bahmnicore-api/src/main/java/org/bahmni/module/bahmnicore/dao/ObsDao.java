package org.bahmni.module.bahmnicore.dao;

import org.openmrs.Concept;
import org.openmrs.Obs;

import java.util.List;

public interface ObsDao {
    List<Obs> getNumericObsByPerson(String personUUID);

    List<Concept> getNumericConceptsForPerson(String personUUID);

    List<Obs> getObsFor(String patientUuid, List<String> conceptName, Integer numberOfVisits);

    List<Obs> getObsFor(String patientUuid, List<String> conceptNames, Integer numberOfVisits, boolean getOrphanedObservations);

    List<Obs> getLatestObsFor(String patientUuid, String conceptName, Integer limit);

    List<Obs> getLatestObsForConceptSetByVisit(String patientUuid, String conceptNames);
}
