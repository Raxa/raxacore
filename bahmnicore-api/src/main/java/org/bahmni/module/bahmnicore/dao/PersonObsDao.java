package org.bahmni.module.bahmnicore.dao;

import org.openmrs.Concept;
import org.openmrs.Obs;

import java.util.List;

public interface PersonObsDao {
    List<Obs> getNumericObsByPerson(String personUUID);

    List<Concept> getNumericConceptsForPerson(String personUUID);

    List<Obs> getObsFor(String patientUuid, String[] conceptName, Integer numberOfVisits);
}
