package org.bahmni.module.referencedata.model;

import org.bahmni.module.referencedata.model.event.ConceptOperationEvent;
import org.ict4h.atomfeed.server.service.Event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.addIgnoreNull;
import static org.bahmni.module.referencedata.model.event.ConceptEventFactory.*;

public class Operation {

    private String name;
    private static final List<ConceptOperationEvent> events = asList(
            sampleEvent(),
            departmentEvent(),
            testEvent(),
            panelEvent(),
            labConceptSetEvent()
    );


    public Operation(Method method) {
        this.name = method.getName();
    }

    public List<Event> apply(Object[] arguments) throws Exception {
        List<Event> atomFeedEvents = new ArrayList<>();
        for (ConceptOperationEvent event : events) {
            if (event.isApplicable(name, arguments)) {
                addIgnoreNull(atomFeedEvents, event.asAtomFeedEvent(arguments));
            }
        }
        return atomFeedEvents;
    }
}
