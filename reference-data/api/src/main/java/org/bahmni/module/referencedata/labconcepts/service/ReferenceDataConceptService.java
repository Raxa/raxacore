package org.bahmni.module.referencedata.labconcepts.service;

import org.bahmni.module.referencedata.labconcepts.contract.Concept;

public interface ReferenceDataConceptService {
    public org.openmrs.Concept saveConcept(Concept concept) throws Throwable;
}
