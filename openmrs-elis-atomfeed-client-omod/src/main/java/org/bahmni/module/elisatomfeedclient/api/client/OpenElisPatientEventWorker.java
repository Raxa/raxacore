package org.bahmni.module.elisatomfeedclient.api.client;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;

import org.bahmni.module.elisatomfeedclient.api.FeedProperties;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisPatient;
import org.bahmni.module.elisatomfeedclient.api.exception.OpenElisFeedException;
import org.bahmni.module.elisatomfeedclient.api.mapper.BahmniPatientMapper;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import static org.bahmni.module.elisatomfeedclient.api.util.ObjectMapperRepository.objectMapper;

@Component
public class OpenElisPatientEventWorker implements EventWorker {

    private Interpreter interpreter;
    private BahmniPatientService patientService;
    private PersonService personService;
    private WebClient webClient;
    private FeedProperties feedProperties;

    private static Logger logger = Logger.getLogger(OpenElisPatientEventWorker.class);

    @Autowired
    public OpenElisPatientEventWorker(BahmniPatientService bahmniPatientService, PersonService personService, WebClient webClient, FeedProperties feedProperties) {
        this.patientService = bahmniPatientService;
        this.personService = personService;
        this.webClient = webClient;
        this.feedProperties = feedProperties;
        interpreter = new Interpreter();
    }

    public OpenElisPatientEventWorker(BahmniPatientService bahmniPatientService, PersonService personService, WebClient webClient, FeedProperties feedProperties, Interpreter interpreter) {
        this(bahmniPatientService, personService, webClient, feedProperties);
        this.interpreter = interpreter;
    }

    @Override
    public void process(Event event) {
        String patientUrl = feedProperties.getOpenElisUri() + event.getContent();
        logger.info("openelisatomfeedclient:Processing event : " + patientUrl);
        try {
            String response = webClient.get(URI.create(patientUrl), new HashMap<String, String>());
            OpenElisPatient openElisPatient = objectMapper.readValue(response, OpenElisPatient.class);

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
