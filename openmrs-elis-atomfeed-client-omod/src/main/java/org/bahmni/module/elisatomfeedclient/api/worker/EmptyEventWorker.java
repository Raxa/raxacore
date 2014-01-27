package org.bahmni.module.elisatomfeedclient.api.worker;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

public class EmptyEventWorker implements EventWorker {

    private static Logger logger = Logger.getLogger(EmptyEventWorker.class);

    @Override
    public void process(Event event) {
         logger.warn("Ignoring event"+event);
    }

    @Override
    public void cleanUp(Event event) {

    }
}
