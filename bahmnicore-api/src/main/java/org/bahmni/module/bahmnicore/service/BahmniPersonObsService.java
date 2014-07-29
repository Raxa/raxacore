package org.bahmni.module.bahmnicore.service;

import org.openmrs.Concept;
import org.openmrs.Obs;

import java.util.List;

public interface BahmniPersonObsService {
    public List<Obs> getObsForPerson(String identifier);

    public List<Obs> observationsFor(String patientUuid, List<String> conceptName, Integer numberOfVisits);
    public List<Obs> getLatest(String patientUuid, List<String> conceptNames);
    public List<Concept> getNumericConceptsForPerson(String personUUID);
}
