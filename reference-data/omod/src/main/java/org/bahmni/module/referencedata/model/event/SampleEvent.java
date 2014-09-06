package org.bahmni.module.referencedata.model.event;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;

public class SampleEvent extends ConceptOperationEvent {
    public static final String SAMPLE_PARENT_CONCEPT_NAME = "Laboratory";

    public SampleEvent(String url, String title, String category) {
        super(url, title, category);
    }


    public Boolean isApplicable(String operation, Object[] arguments) {
        return this.operations().contains(operation) && isSampleConcept((Concept) arguments[0]);
    }

    private boolean isSampleConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getUuid().equals(ConceptClass.LABSET_UUID) && isChildOf(concept, SAMPLE_PARENT_CONCEPT_NAME);
    }
}
