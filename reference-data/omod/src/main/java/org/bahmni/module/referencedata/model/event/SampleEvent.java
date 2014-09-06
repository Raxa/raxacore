package org.bahmni.module.referencedata.model.event;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;

public class SampleEvent extends ConceptOperationEvent {
    public static final String SAMPLE_PARENT_CONCEPT_NAME = "Laboratory";

    public SampleEvent(String url, String category, String title) {
        super(url, title, category);
    }


    @Override
    public boolean isResourceConcept(Concept concept) {
        return isSampleConcept(concept);
    }

    public static boolean isSampleConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getUuid().equals(ConceptClass.LABSET_UUID) && isChildOf(concept, SAMPLE_PARENT_CONCEPT_NAME);
    }
}
