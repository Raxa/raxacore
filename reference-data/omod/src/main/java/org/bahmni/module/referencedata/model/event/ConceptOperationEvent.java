package org.bahmni.module.referencedata.model.event;

import org.ict4h.atomfeed.server.service.Event;

import java.net.URISyntaxException;

public interface ConceptOperationEvent {
    public Boolean isApplicable(String operation, Object[] arguments);

    public Event asAtomFeedEvent(Object[] arguments) throws URISyntaxException;
}
