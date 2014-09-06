package org.bahmni.module.referencedata.model.event;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;

public class DepartmentEvent extends ConceptOperationEvent {
    public static final String DEPARTMENT_PARENT_CONCEPT_NAME = "Lab Departments";

    public DepartmentEvent(String url, String category, String title) {
        super(url, category, title);
    }

    @Override
    protected boolean isResourceConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getUuid().equals(ConceptClass.CONVSET_UUID) && isChildOf(concept, DEPARTMENT_PARENT_CONCEPT_NAME);
    }
}
