package org.bahmni.module.referencedatafeedclient.worker;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReferenceDataEventWorker implements EventWorker{
    private static Logger logger = Logger.getLogger(ReferenceDataEventWorker.class);

    @Autowired
    private DepartmentEventWorker departmentEventWorker;
    @Autowired
    private SampleEventWorker sampleEventWorker;
    @Autowired
    private TestEventWorker testEventWorker;
    @Autowired
    private PanelEventWorker panelEventWorker;
    @Autowired
    private DrugEventWorker drugEventWorker;

    @Override
    public void process(Event event) {
        EventWorker eventWorker = getEventWorker(event.getTitle());
        if(eventWorker != null) {
            eventWorker.process(event);
        } else {
            logger.warn("Could not process event : " + event);
        }
    }

    private EventWorker getEventWorker(String title) {
        switch (title) {
            case "department": return departmentEventWorker;
            case "sample": return sampleEventWorker;
            case "test": return testEventWorker;
            case "panel": return panelEventWorker;
            case "drug" : return drugEventWorker;
            default: return null;
        }
    }

    @Override
    public void cleanUp(Event event) {

    }
}
