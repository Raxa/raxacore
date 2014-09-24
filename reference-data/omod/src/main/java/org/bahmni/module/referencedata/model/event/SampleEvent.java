package org.bahmni.module.referencedata.model.event;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;

public class SampleEvent extends ConceptOperationEvent {
    public static final String SAMPLE_PARENT_CONCEPT_NAME = "Lab Samples";
    public static final String SAMPLE_CONCEPT_CLASS = "Sample";

    public SampleEvent(String url, String category, String title) {
        super(url, category, title);
    }


    @Override
    public boolean isResourceConcept(Concept concept) {
        return isSampleConcept(concept);
    }

    public static boolean isSampleConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getName() != null && concept.getConceptClass().getName().equals(SAMPLE_CONCEPT_CLASS);
    }
}
