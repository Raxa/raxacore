package org.bahmni.module.referencedata.labconcepts.model;

import org.bahmni.module.referencedata.labconcepts.model.event.ConceptServiceOperationEvent;
import org.ict4h.atomfeed.server.service.Event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.addIgnoreNull;
import static org.bahmni.module.referencedata.labconcepts.model.event.ConceptServiceEventFactory.*;

public class Operation {

    private String name;
    private static final List<ConceptServiceOperationEvent> events = asList(
            sampleEvent(),
            departmentEvent(),
            testEvent(),
            panelEvent(),
            labConceptSetEvent(),
            allTestsAndPanelsConceptSetEvent(),
            drugEvent()
    );

    public Operation(Method method) {
        this.name = method.getName();
    }

    public List<Event> apply(Object[] arguments) throws Exception {
        List<Event> atomFeedEvents = new ArrayList<>();
        for (ConceptServiceOperationEvent event : events) {
            if (event.isApplicable(name, arguments)) {
                addIgnoreNull(atomFeedEvents, event.asAtomFeedEvent(arguments));
            }
        }
        return atomFeedEvents;
    }
}
