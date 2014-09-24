package org.bahmni.module.referencedata.model.event;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;

public class DepartmentEvent extends ConceptOperationEvent {
    public static final String DEPARTMENT_PARENT_CONCEPT_NAME = "Lab Departments";
    public static final String DEPARTMENT_CONCEPT_CLASS = "Department";

    public DepartmentEvent(String url, String category, String title) {
        super(url, category, title);
    }

    @Override
    public boolean isResourceConcept(Concept concept) {
        return isDepartmentConcept(concept);
    }

    public static boolean isDepartmentConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getName() != null && concept.getConceptClass().getName().equals(DEPARTMENT_CONCEPT_CLASS);
    }
}
