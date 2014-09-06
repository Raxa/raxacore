package org.bahmni.module.referencedata.model.event;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;

public class DepartmentEvent extends ConceptOperationEvent {
    public static final String DEPARTMENT_PARENT_CONCEPT_NAME = "Lab Departments";

    public DepartmentEvent(String url, String title, String category) {
        super(url, title, category);
    }


    public Boolean isApplicable(String operation, Object[] arguments) {
        return this.operations().contains(operation) && isDepartmentConcept((Concept) arguments[0]);
    }

    private boolean isDepartmentConcept(Concept concept) {
        return concept.getConceptClass() != null && concept.getConceptClass().getUuid().equals(ConceptClass.CONVSET_UUID) && isChildOf(concept, DEPARTMENT_PARENT_CONCEPT_NAME);
    }
}
