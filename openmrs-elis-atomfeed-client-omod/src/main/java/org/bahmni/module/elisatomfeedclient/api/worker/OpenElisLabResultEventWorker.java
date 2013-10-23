package org.bahmni.module.elisatomfeedclient.api.worker;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.springframework.stereotype.Component;

@Component
public class OpenElisLabResultEventWorker implements EventWorker {
    private static Logger logger = Logger.getLogger(OpenElisLabResultEventWorker.class);

    @Override
    public void process(Event event) {
    }

    @Override
    public void cleanUp(Event event) {
    }
}
