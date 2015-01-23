package org.bahmni.module.bahmnicore.service;

import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

public interface BahmniConceptService {
    EncounterTransaction.Concept getConceptByName(String conceptName);
}
