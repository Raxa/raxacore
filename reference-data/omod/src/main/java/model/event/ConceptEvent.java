package model.event;

import org.ict4h.atomfeed.server.service.Event;

import java.net.URISyntaxException;

public interface ConceptEvent {
    public Boolean isApplicable(String operation);

    public Event asAtomFeedEvent(Object[] arguments) throws URISyntaxException;
}
