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
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.io.IOException;


public class OpenElisAccessionEventWorker implements EventWorker {
    private ElisAtomFeedProperties atomFeedProperties;
    private HttpClient httpClient;
    private EncounterService encounterService;
    private EmrEncounterService emrEncounterService;
    private AccessionMapper accessionMapper;
    private EncounterTransactionMapper encounterTransactionMapper;

    private static Logger logger = Logger.getLogger(OpenElisAccessionEventWorker.class);

    public OpenElisAccessionEventWorker(ElisAtomFeedProperties atomFeedProperties, HttpClient httpClient, EncounterService encounterService, EmrEncounterService emrEncounterService, AccessionMapper accessionMapper, EncounterTransactionMapper encounterTransactionMapper) {

        this.atomFeedProperties = atomFeedProperties;
        this.httpClient = httpClient;
        this.encounterService = encounterService;
        this.emrEncounterService = emrEncounterService;
        this.accessionMapper = accessionMapper;
        this.encounterTransactionMapper = encounterTransactionMapper;
    }

    @Override
    public void process(Event event) {
        String accessionUrl = atomFeedProperties.getOpenElisUri() + event.getContent();
        logger.info("openelisatomfeedclient:Processing event : " + accessionUrl);
        try {
            OpenElisAccession openElisAccession = httpClient.get(accessionUrl, OpenElisAccession.class);

            Encounter previousEncounter = encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid());
            AccessionDiff diff = null;
            if (previousEncounter != null) {
                diff = openElisAccession.getDiff(previousEncounter);
            }
            Encounter encounterFromAccession = null;
            if (diff == null) {
                logger.info("openelisatomfeedclient:creating new encounter for accession : " + accessionUrl);
                encounterFromAccession = accessionMapper.mapToNewEncounter(openElisAccession);
            } else if (diff.getRemovedTestDetails().size() > 0 || diff.getAddedTestDetails().size() > 0) {
                logger.info("openelisatomfeedclient:updating encounter for accession : " + accessionUrl);
                encounterFromAccession = accessionMapper.mapToExistingEncounter(openElisAccession, diff, previousEncounter);
            }

            if (encounterFromAccession != null) {
                EncounterTransaction encounterTransaction = encounterTransactionMapper.map(encounterFromAccession, true);
                emrEncounterService.save(encounterTransaction);
            }
        } catch (IOException e) {
            logger.error("openelisatomfeedclient:error processing event : " + accessionUrl + e.getMessage(), e);
            throw new OpenElisFeedException("could not read accession data", e);
        }
    }

    @Override
    public void cleanUp(Event event) {

    }
}
