package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.observation.ConceptDefinition;

import java.util.List;

public interface ConceptService {
    public static final String CONCEPT_DETAILS_CONCEPT_CLASS = "Concept Details";
    public static final String ABNORMAL_CONCEPT_CLASS = "Abnormal";
    public static final String DURATION_CONCEPT_CLASS = "Duration";

    public ConceptDefinition conceptsFor(List<String> conceptName);
}
