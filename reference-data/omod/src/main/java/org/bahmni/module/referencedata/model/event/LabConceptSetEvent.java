package org.bahmni.module.referencedata.model.event;

import org.ict4h.atomfeed.server.service.Event;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

import java.net.URISyntaxException;
import java.util.List;

public class LabConceptSetEvent extends ConceptOperationEvent {

    public LabConceptSetEvent() {
    }


    public Boolean isApplicable(String operation, Object[] arguments) {
        return this.operations().contains(operation) && isLabSetConcept((Concept) arguments[0]);
    }

    private boolean isLabSetConcept(Concept concept) {
        return isLaboratoryConcept(concept) || isDepartmentConcept(concept);
    }

    private boolean isDepartmentConcept(Concept concept) {
        return concept.getName(Context.getLocale()) != null && concept.getName(Context.getLocale()).getName().equals(DepartmentEvent.DEPARTMENT_PARENT_CONCEPT_NAME);
    }

    private boolean isLaboratoryConcept(Concept concept) {
        return concept.getName(Context.getLocale()) != null && concept.getName(Context.getLocale()).getName().equals(SampleEvent.SAMPLE_PARENT_CONCEPT_NAME);
    }


    @Override
    public Event asAtomFeedEvent(Object[] arguments) throws URISyntaxException {
        Concept concept = (Concept) arguments[0];
        List<Concept> setMembers = concept.getSetMembers();
        for (Concept setMember : setMembers) {
            if (!isLabSetConcept(setMember)) {
                Context.getConceptService().saveConcept(setMember);
            }
        }
        return null;
    }
}
