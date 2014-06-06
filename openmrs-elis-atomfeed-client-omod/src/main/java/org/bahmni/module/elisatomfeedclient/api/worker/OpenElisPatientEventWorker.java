package org.bahmni.module.elisatomfeedclient.api.worker;

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

import java.io.IOException;
import java.util.List;

public class OpenElisPatientEventWorker implements EventWorker {

    private final HttpClient httpClient;
    private BahmniPatientService patientService;
    private PersonService personService;
    private ElisAtomFeedProperties elisAtomFeedProperties;

    private static Logger logger = Logger.getLogger(OpenElisPatientEventWorker.class);

    public OpenElisPatientEventWorker(BahmniPatientService bahmniPatientService, PersonService personService, HttpClient httpClient, ElisAtomFeedProperties elisAtomFeedProperties) {
        this.patientService = bahmniPatientService;
        this.personService = personService;
        this.httpClient = httpClient;
        this.elisAtomFeedProperties = elisAtomFeedProperties;
    }

    @Override
    public void process(Event event) {
        String patientUrl = elisAtomFeedProperties.getOpenElisUri() + event.getContent();
        logger.info("openelisatomfeedclient:Processing event : " + patientUrl);
        try {
            OpenElisPatient openElisPatient = httpClient.get(patientUrl, OpenElisPatient.class);

            final List<PersonAttributeType> allPersonAttributeTypes = personService.getAllPersonAttributeTypes();

                patientService.createPatient(new BahmniPatientMapper(allPersonAttributeTypes).map(openElisPatient));

        } catch (IOException e) {
            logger.error("openelisatomfeedclient:error processing event : " + patientUrl + e.getMessage(), e);
            throw new OpenElisFeedException("could not read patient data", e);
        }
    }


    @Override
    public void cleanUp(Event event) {
    }
}
