package org.bahmni.module.referencedata.labconcepts.model.event;

import org.openmrs.Concept;

import static org.bahmni.module.referencedata.labconcepts.mapper.MapperUtils.isDepartmentConcept;

public class DepartmentEvent extends ConceptOperationEvent {

    public DepartmentEvent(String url, String category, String title) {
        super(url, category, title);
    }

    @Override
    public boolean isResourceConcept(Concept concept) {
        return isDepartmentConcept(concept);
    }


}
