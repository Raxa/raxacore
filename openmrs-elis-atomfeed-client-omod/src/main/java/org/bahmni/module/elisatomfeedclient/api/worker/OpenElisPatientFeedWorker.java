package org.bahmni.module.elisatomfeedclient.api.worker;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.util.OpenmrsUtil;

import java.io.IOException;

public class OpenElisPatientFeedWorker implements EventWorker {
    public static final String PATIENT = "patient";
    public static final String ACCESSION = "accession";
    private OpenElisPatientEventWorker patientEventWorker;
    private OpenElisAccessionEventWorker accessionEventWorker;
    private Interpreter interpreter;

    private static Logger logger = Logger.getLogger(OpenElisPatientFeedWorker.class);

    public OpenElisPatientFeedWorker(OpenElisPatientEventWorker patientEventWorker, OpenElisAccessionEventWorker accessionEventWorker) {
        this.patientEventWorker = patientEventWorker;
        this.accessionEventWorker = accessionEventWorker;
        interpreter = new Interpreter();
    }

    @Override
    public void process(Event event) {
        getEventWorker(event).process(event);
    }

    private EventWorker getEventWorker(Event event) {
        Boolean shouldProcessPatientSyn = true;

        try {
            shouldProcessPatientSyn = (Boolean) interpreter.source(OpenmrsUtil.getApplicationDataDirectory() + "beanshell/open-elis-patient-feed-patient-syn.bsh");
        } catch (IOException | EvalError error) {
            logger.info("no file beanshell/open-elis-patient-feed-patient-syn.bsh");
        }

        if (PATIENT.equalsIgnoreCase(event.getTitle()) && shouldProcessPatientSyn) {
            return patientEventWorker;
        } else if (ACCESSION.equalsIgnoreCase(event.getTitle())) {
            return accessionEventWorker;
        }

        if(!shouldProcessPatientSyn) {
            logger.info("not processing patient feed from openelis");
        } else {
            logger.warn(String.format("Could not find a worker for event: %s, details: %s", event.getTitle(), event));
        }
        return new EmptyEventWorker();
    }

    @Override
    public void cleanUp(Event event) {
        getEventWorker(event).cleanUp(event);
    }
}
