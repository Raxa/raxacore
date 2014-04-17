package org.bahmni.module.bahmnicore.dao;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.List;

public interface PersonObsDao {
    List<Obs> getObsByPerson(String personUUID);

    List<Concept> getNumericConceptsForPerson(String personUUID);
}
