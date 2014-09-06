package org.bahmni.module.referencedata.model.event;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;

public class TestEvent extends ConceptOperationEvent {
    public static final String TEST_PARENT_CONCEPT_NAME = "All_Tests_and_Panels";

    public TestEvent(String url, String category, String title) {
        super(url, category, title);
    }

    protected boolean isResourceConcept(Concept concept) {
        return concept.getConceptClass() != null &&
                concept.getConceptClass().getUuid().equals(ConceptClass.TEST_UUID) &&
                isChildOf(concept, TEST_PARENT_CONCEPT_NAME);
    }

}
