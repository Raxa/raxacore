package org.bahmni.module.referencedata.model.event;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.api.context.Context;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

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

    @Override
    public Event asAtomFeedEvent(Object[] arguments) throws URISyntaxException {
        Concept concept = (Concept) arguments[0];
        String url = String.format(this.url, title, concept.getUuid());
        List<Concept> setMembers = concept.getSetMembers();
        for (Concept setMember : setMembers) {
            if (!isResourceConcept(setMember)) {
                Context.getConceptService().saveConcept(setMember);
            }
        }
        return new Event(UUID.randomUUID().toString(), title, DateTime.now(), url, url, category);
    }
}
