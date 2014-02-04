package org.bahmni.module.elisatomfeedclient.api.client.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisFeedClient;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisPatientFeedClient;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionMapper;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisAccessionEventWorker;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisPatientEventWorker;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisPatientFeedWorker;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.joda.time.DateTime;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component("openElisPatientFeedClient")
public class OpenElisPatientFeedClientImpl extends OpenElisFeedClient implements OpenElisPatientFeedClient {
    private BahmniPatientService bahmniPatientService;
    private EncounterTransactionMapper encounterTransactionMapper;
    private EmrEncounterService emrEncounterService;
    private Logger logger = Logger.getLogger(OpenElisPatientFeedClientImpl.class);


    @Autowired
    public OpenElisPatientFeedClientImpl(ElisAtomFeedProperties properties,
                                         BahmniPatientService bahmniPatientService,
                                         EncounterTransactionMapper encounterTransactionMapper,
                                         EmrEncounterService emrEncounterService,
                                        PlatformTransactionManager transactionManager) {
            super(properties, transactionManager);
        this.bahmniPatientService = bahmniPatientService;
        this.encounterTransactionMapper = encounterTransactionMapper;
        this.emrEncounterService = emrEncounterService;
    }

    @Override
    protected String getFeedUri(ElisAtomFeedProperties properties) {
        return properties.getFeedUri("patient.feed.uri");
    }

    @Override
    protected EventWorker createWorker(HttpClient authenticatedWebClient, ElisAtomFeedProperties properties) {
        EncounterService encounterService = Context.getService(EncounterService.class);
        ConceptService conceptService = Context.getService(ConceptService.class);
        VisitService visitService = Context.getVisitService();
        PersonService personService = Context.getPersonService();
        ProviderService providerService = Context.getProviderService();

        OpenElisAccessionEventWorker accessionEventWorker = new OpenElisAccessionEventWorker(properties, authenticatedWebClient, encounterService, emrEncounterService, conceptService, new AccessionMapper(properties), encounterTransactionMapper, visitService, providerService);
        OpenElisPatientEventWorker openElisPatientEventWorker = new OpenElisPatientEventWorker(bahmniPatientService, personService, authenticatedWebClient, properties);
        return new OpenElisPatientFeedWorker(openElisPatientEventWorker, accessionEventWorker);
    }

    @Override
    public void processFeed() {
        try {
            if(atomFeedClient == null) {
                initializeAtomFeedClient();
            }
            logger.info("openelisatomfeedclient:processing feed " + DateTime.now());
            atomFeedClient.processEvents();
        } catch (Exception e) {
            try {
                if (e != null && ExceptionUtils.getStackTrace(e).contains("HTTP response code: 401")) {
                    initializeAtomFeedClient();
                }
            }catch (Exception ex){
                logger.error("openelisatomfeedclient:failed feed execution " + e, e);
                throw new RuntimeException(ex);
            }
        }
    }

}
