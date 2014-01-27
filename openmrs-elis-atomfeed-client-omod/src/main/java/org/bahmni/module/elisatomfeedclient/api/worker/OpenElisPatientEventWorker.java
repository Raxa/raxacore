package org.bahmni.module.elisatomfeedclient.api.worker;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisPatient;
import org.bahmni.module.elisatomfeedclient.api.exception.OpenElisFeedException;
import org.bahmni.module.elisatomfeedclient.api.mapper.BahmniPatientMapper;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.util.OpenmrsUtil;

import java.io.IOException;
import java.util.List;

public class OpenElisPatientEventWorker implements EventWorker {

    private final HttpClient httpClient;
    private Interpreter interpreter;
    private BahmniPatientService patientService;
    private PersonService personService;
    private ElisAtomFeedProperties elisAtomFeedProperties;

    private static Logger logger = Logger.getLogger(OpenElisPatientEventWorker.class);

    public OpenElisPatientEventWorker(BahmniPatientService bahmniPatientService, PersonService personService, HttpClient httpClient, ElisAtomFeedProperties elisAtomFeedProperties) {
        this.patientService = bahmniPatientService;
        this.personService = personService;
        this.httpClient = httpClient;
        this.elisAtomFeedProperties = elisAtomFeedProperties;
        interpreter = new Interpreter();
    }

    public OpenElisPatientEventWorker(BahmniPatientService bahmniPatientService, PersonService personService, HttpClient httpClient, ElisAtomFeedProperties elisAtomFeedProperties, Interpreter interpreter) {
        this(bahmniPatientService, personService, httpClient, elisAtomFeedProperties);
        this.interpreter = interpreter;
    }

    @Override
    public void process(Event event) {
        String patientUrl = elisAtomFeedProperties.getOpenElisUri() + event.getContent();
        logger.info("openelisatomfeedclient:Processing event : " + patientUrl);
        try {
            OpenElisPatient openElisPatient = httpClient.get(patientUrl, OpenElisPatient.class);

            final List<PersonAttributeType> allPersonAttributeTypes = personService.getAllPersonAttributeTypes();

            interpreter.set("healthCenter", openElisPatient.getHealthCenter());
            Boolean shouldProcess = (Boolean) interpreter.source(OpenmrsUtil.getApplicationDataDirectory() + "beanshell/open-elis-patient-feed-filter.bsh");

            logger.info("openelisatomfeedclient:ignoring event : " + patientUrl);
            if (shouldProcess) {
                logger.info("openelisatomfeedclient:creating patient for event : " + patientUrl);
                patientService.createPatient(new BahmniPatientMapper(allPersonAttributeTypes).map(openElisPatient));
            }

        } catch (IOException | EvalError e) {
            logger.error("openelisatomfeedclient:error processing event : " + patientUrl + e.getMessage(), e);
            throw new OpenElisFeedException("could not read patient data", e);
        }
    }


    @Override
    public void cleanUp(Event event) {
    }
}
