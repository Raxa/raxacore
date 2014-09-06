package org.bahmni.module.referencedata.model.event;

import org.ict4h.atomfeed.server.service.Event;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.api.context.Context;

import java.net.URISyntaxException;
import java.util.List;

public class LabConceptSetEvent extends ConceptOperationEvent {

    public LabConceptSetEvent() {
    }

    @Override
    protected boolean isResourceConcept(Concept concept) {
        return isLaboratoryConcept(concept) || isDepartmentConcept(concept) || isTestConcept(concept);
    }

    private boolean isTestConcept(Concept concept) {
        return concept.getName(Context.getLocale()) != null &&
               concept.getName(Context.getLocale()).getName().equals(TestEvent.TEST_PARENT_CONCEPT_NAME);
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
            if (!isResourceConcept(setMember)) {
                Context.getConceptService().saveConcept(setMember);
            }
        }
        return null;
    }
}
