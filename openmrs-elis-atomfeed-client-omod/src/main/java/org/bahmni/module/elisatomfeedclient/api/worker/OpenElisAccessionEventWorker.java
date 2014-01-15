package org.bahmni.module.elisatomfeedclient.api.worker;

import org.apache.log4j.Logger;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.domain.AccessionDiff;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.exception.OpenElisFeedException;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionMapper;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;

import java.io.IOException;
import java.net.URI;

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
            AccessionDiff diff = null;
            if (previousEncounter != null) {
                diff = openElisAccession.getDiff(previousEncounter);
            }
            Encounter encounterFromAccession = null;
            if (diff == null) {
                logger.info("openelisatomfeedclient:creating new encounter for accession : " + patientUrl);
                encounterFromAccession = accessionMapper.mapToNewEncounter(openElisAccession);
            } else if (diff.getRemovedTestDetails().size() > 0 || diff.getAddedTestDetails().size() > 0) {
                logger.info("openelisatomfeedclient:updating encounter for accession : " + patientUrl);
                encounterFromAccession = accessionMapper.mapToExistingEncounter(openElisAccession, diff, previousEncounter);
            }
            encounterService.saveEncounter(encounterFromAccession);
        } catch (IOException e) {
            logger.error("openelisatomfeedclient:error processing event : " + patientUrl + e.getMessage(), e);
            throw new OpenElisFeedException("could not read accession data", e);
        }
    }

    @Override
    public void cleanUp(Event event) {

    }
}
