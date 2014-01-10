package org.bahmni.module.elisatomfeedclient.api.worker;

import org.apache.log4j.Logger;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.exception.OpenElisFeedException;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionMapper;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.api.EncounterService;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static org.bahmni.module.elisatomfeedclient.api.util.ObjectMapperRepository.objectMapper;


public class OpenElisAccessionEventWorker implements EventWorker {
    private ElisAtomFeedProperties atomFeedProperties;
    private HttpClient httpClient;
    private EncounterService encounterService;
    private AccessionMapper accessionMapper;

    private static Logger logger = Logger.getLogger(OpenElisAccessionEventWorker.class);

    public OpenElisAccessionEventWorker(ElisAtomFeedProperties atomFeedProperties, HttpClient httpClient, EncounterService encounterService, AccessionMapper accessionMapper) {

        this.atomFeedProperties = atomFeedProperties;
        this.httpClient = httpClient;
        this.encounterService = encounterService;
        this.accessionMapper = accessionMapper;
    }

    @Override
    public void process(Event event) {
        String patientUrl = atomFeedProperties.getOpenElisUri() + event.getContent();
        logger.info("openelisatomfeedclient:Processing event : " + patientUrl);
        try {
            String response = httpClient.get(URI.create(patientUrl));
            OpenElisAccession openElisAccession = objectMapper.readValue(response, OpenElisAccession.class);
            Encounter previousEncounter = encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid());
            Encounter encounterFromAccession = accessionMapper.map(openElisAccession);
            Set<Order> previousOrders = new HashSet<>();
            if (previousEncounter != null) {
                previousOrders = previousEncounter.getOrders();
            }
            Set<Order> ordersFromAccession = encounterFromAccession.getOrders();
            if (previousOrders.size() != ordersFromAccession.size()){
                logger.info("openelisatomfeedclient:creating encounter for accession : " + patientUrl);
                encounterService.saveEncounter(encounterFromAccession);
            }
        } catch (IOException e) {
            logger.error("openelisatomfeedclient:error processing event : " + patientUrl + e.getMessage(), e);
            throw new OpenElisFeedException("could not read accession data", e);
        }
    }

    @Override
    public void cleanUp(Event event) {

    }
}
