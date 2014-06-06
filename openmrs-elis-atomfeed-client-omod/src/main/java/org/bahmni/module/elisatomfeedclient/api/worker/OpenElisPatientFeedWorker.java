package org.bahmni.module.elisatomfeedclient.api.worker;

import org.bahmni.module.elisatomfeedclient.api.filter.OpenElisPatientFeedPrefetchFilter;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

import java.util.HashMap;

public class OpenElisPatientFeedWorker implements EventWorker {
    public HashMap<String, EventWorker> workers = new HashMap<>();
    private OpenElisPatientFeedPrefetchFilter prefetchFilter;

    public OpenElisPatientFeedWorker(OpenElisPatientEventWorker patientEventWorker, OpenElisAccessionEventWorker accessionEventWorker) {
        this(patientEventWorker, accessionEventWorker, new OpenElisPatientFeedPrefetchFilter());
    }

    public OpenElisPatientFeedWorker(OpenElisPatientEventWorker patientEventWorker, OpenElisAccessionEventWorker accessionEventWorker, OpenElisPatientFeedPrefetchFilter prefetchFilter) {
        workers.put("patient", patientEventWorker);
        workers.put("accession", accessionEventWorker);
        this.prefetchFilter = prefetchFilter;
    }

    @Override
    public void process(Event event) {
        getEventWorker(event).process(event);
    }

    private EventWorker getEventWorker(Event event) {
        return prefetchFilter.allows(event)? workerFor(event): new IgnoreEventWorker("Prefetch filter does not allow processing of event: " + event);
    }

    private EventWorker workerFor(Event event) {
        Object worker = workers.get(event.getTitle());
        return worker == null? new IgnoreEventWorker("No worker found for event: " + event): (EventWorker) worker;
    }

    @Override
    public void cleanUp(Event event) {
        getEventWorker(event).cleanUp(event);
    }
}