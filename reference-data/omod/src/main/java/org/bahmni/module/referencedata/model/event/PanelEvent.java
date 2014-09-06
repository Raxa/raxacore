package org.bahmni.module.referencedata.model.event;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;

public class PanelEvent extends ConceptOperationEvent {

    public PanelEvent(String url, String category, String title) {
        super(url, category, title);
    }

    @Override
    public boolean isResourceConcept(Concept concept) {
        return isPanelConcept(concept);
    }

    private boolean isPanelConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getUuid().equals(ConceptClass.LABSET_UUID) && isChildOf(concept, TestEvent.TEST_PARENT_CONCEPT_NAME);
    }
}
