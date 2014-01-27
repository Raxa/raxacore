package org.bahmni.module.elisatomfeedclient.api.worker;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReferenceDataEventWorker implements EventWorker{
    private static Logger logger = Logger.getLogger(ReferenceDataEventWorker.class);

    @Autowired
    private DepartmentEventWorker departmentEventWorker;

    @Override
    public void process(Event event) {
        if(event.getTitle().equalsIgnoreCase("department")) {
            departmentEventWorker.process(event);
        } else {
            logger.warn("Could not process event : " + event);
        }
    }

    @Override
    public void cleanUp(Event event) {

    }
}
