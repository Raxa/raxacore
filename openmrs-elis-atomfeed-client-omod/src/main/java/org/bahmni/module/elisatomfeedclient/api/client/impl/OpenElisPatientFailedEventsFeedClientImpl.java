package org.bahmni.module.elisatomfeedclient.api.client.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisFeedClient;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisPatientFailedEventsFeedClient;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionMapper;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisAccessionEventWorker;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisPatientEventWorker;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisPatientFeedWorker;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.joda.time.DateTime;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component("openElisPatientFailedEventsFeedClient")
public class OpenElisPatientFailedEventsFeedClientImpl extends OpenElisFeedClient implements OpenElisPatientFailedEventsFeedClient {
    private BahmniPatientService bahmniPatientService;
    private PersonService personService;
    private EncounterTransactionMapper encounterTransactionMapper;
    private EmrEncounterService emrEncounterService;
    private Logger logger = Logger.getLogger(OpenElisPatientFailedEventsFeedClientImpl.class);


    @Autowired
    public OpenElisPatientFailedEventsFeedClientImpl(ElisAtomFeedProperties properties,
                                                     BahmniPatientService bahmniPatientService,
                                                     PersonService personService, EncounterTransactionMapper encounterTransactionMapper, EmrEncounterService emrEncounterService,
                                                     PlatformTransactionManager transactionManager) {
        super(properties, transactionManager);
        this.bahmniPatientService = bahmniPatientService;
        this.personService = personService;
        this.encounterTransactionMapper = encounterTransactionMapper;
        this.emrEncounterService = emrEncounterService;
    }

    @Override
    protected String getFeedUri(ElisAtomFeedProperties properties) {
        return properties.getFeedUri("patient.feed.uri");
    }

    @Override
    protected EventWorker createWorker(HttpClient authenticatedWebClient, ElisAtomFeedProperties properties) {
        PatientService patientService = Context.getService(PatientService.class);
        EncounterService encounterService = Context.getService(EncounterService.class);

        OpenElisAccessionEventWorker accessionEventWorker = new OpenElisAccessionEventWorker(properties, authenticatedWebClient, encounterService, emrEncounterService, new AccessionMapper(properties), encounterTransactionMapper);
        OpenElisPatientEventWorker openElisPatientEventWorker = new OpenElisPatientEventWorker(bahmniPatientService, personService, authenticatedWebClient, properties);
        return new OpenElisPatientFeedWorker(openElisPatientEventWorker, accessionEventWorker);
    }

    @Override
    public void processFailedEvents() {
        try {
            if(atomFeedClient == null) {
                initializeAtomFeedClient();
            }
            logger.info("openelisatomfeedclient:processing failed events " + DateTime.now());
            atomFeedClient.processFailedEvents();
        } catch (Exception e) {
            try {
                if (e != null && ExceptionUtils.getStackTrace(e).contains("HTTP response code: 401")) {
                    initializeAtomFeedClient();
                }
            }catch (Exception ex){
                logger.error("openelisatomfeedclient:failed feed execution while running failed events" + e, e);
                throw new RuntimeException(ex);
            }
        }
    }

}
