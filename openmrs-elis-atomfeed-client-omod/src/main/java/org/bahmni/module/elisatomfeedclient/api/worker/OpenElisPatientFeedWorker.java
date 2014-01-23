package org.bahmni.module.elisatomfeedclient.api.worker;

import org.bahmni.module.elisatomfeedclient.api.exception.OpenElisFeedException;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

public class OpenElisPatientFeedWorker implements EventWorker {
    public static final String PATIENT = "patient";
    public static final String ACCESSION = "accession";
    private OpenElisPatientEventWorker patientEventWorker;
    private OpenElisAccessionEventWorker accessionEventWorker;

    public OpenElisPatientFeedWorker(OpenElisPatientEventWorker patientEventWorker, OpenElisAccessionEventWorker accessionEventWorker) {
        this.patientEventWorker = patientEventWorker;
        this.accessionEventWorker = accessionEventWorker;
    }

    @Override
    public void process(Event event) {
        getEventWorker(event).process(event);
    }

    private EventWorker getEventWorker(Event event) {
        if (PATIENT.equalsIgnoreCase(event.getTitle())) {
            return patientEventWorker;
        } else if (ACCESSION.equalsIgnoreCase(event.getTitle())) {
            return accessionEventWorker;
        }
        throw new OpenElisFeedException(String.format("Could not find a worker for event: %s, details: %s", event.getTitle(),event));
    }

    @Override
    public void cleanUp(Event event) {
        getEventWorker(event).cleanUp(event);
    }
}
