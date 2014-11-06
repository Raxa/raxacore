package org.bahmni.module.referencedata.labconcepts.model.event;

import org.openmrs.Concept;
import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.isLabTestConcept;

public class LabTestEvent extends ConceptOperationEvent {

    public LabTestEvent(String url, String category, String title) {
        super(url, category, title);
    }

    public boolean isResourceConcept(Concept concept) {
        return isLabTestConcept(concept);
    }

}
