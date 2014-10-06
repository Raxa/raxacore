package org.bahmni.module.referencedata.labconcepts.service;

import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;

public interface ReferenceDataConceptService {
    public org.openmrs.Concept saveConcept(Concept concept);

    public org.openmrs.Concept saveConceptSet(ConceptSet conceptSet);
}
