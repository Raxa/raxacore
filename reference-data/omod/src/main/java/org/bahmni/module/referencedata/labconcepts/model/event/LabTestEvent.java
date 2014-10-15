package org.bahmni.module.referencedata.labconcepts.model.event;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;

public class LabTestEvent extends ConceptOperationEvent {

    public LabTestEvent(String url, String category, String title) {
        super(url, category, title);
    }

    public boolean isResourceConcept(Concept concept) {
        return concept.getConceptClass() != null &&
                concept.getConceptClass().getUuid().equals(ConceptClass.TEST_UUID);
    }

}
