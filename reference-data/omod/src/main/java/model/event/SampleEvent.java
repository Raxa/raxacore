package model.event;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptSet;
import org.openmrs.api.context.Context;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;

public class SampleEvent implements ConceptOperationEvent {
    public static final String SAMPLE_PARENT_CONCEPT_NAME = "Laboratory";
    private final String url;


    public SampleEvent(String url) {
        this.url = url;
    }

    private List<String> operations() {
        return asList("saveConcept", "updateConcept", "retireConcept", "purgeConcept");
    }

    public Boolean isApplicable(String operation) {
        return this.operations().contains(operation);
    }

    @Override
    public Event asAtomFeedEvent(Object[] arguments) throws URISyntaxException {
        Concept concept = (Concept) arguments[0];
        if (isSampleConcept(concept)) {
            String url = String.format(this.url, "sample", concept.getUuid());
            return new Event(UUID.randomUUID().toString(), "sample", DateTime.now(), url, url, "lab");
        }
        return null;
    }

    private boolean isSampleConcept(Concept concept) {
        return concept.getConceptClass().getUuid().equals(ConceptClass.LABSET_UUID) && isChildOf(concept);
    }

    private boolean isChildOf(Concept concept) {
        List<ConceptSet> conceptSets = Context.getConceptService().getSetsContainingConcept(concept);
        for (ConceptSet conceptSet : conceptSets) {
            if (conceptSet.getConceptSet().getName(Context.getLocale()).getName().equals(SAMPLE_PARENT_CONCEPT_NAME)) {
                return true;
            }
        }
        return false;
    }
}
