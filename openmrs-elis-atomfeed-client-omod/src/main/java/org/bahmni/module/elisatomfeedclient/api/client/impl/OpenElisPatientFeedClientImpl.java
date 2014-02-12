package org.bahmni.module.elisatomfeedclient.api.client.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisFeedClient;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisPatientFeedClient;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionHelper;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisAccessionEventWorker;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisPatientEventWorker;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisPatientFeedWorker;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.joda.time.DateTime;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component("openElisPatientFeedClient")
public class OpenElisPatientFeedClientImpl extends OpenElisFeedClient implements OpenElisPatientFeedClient {
    private BahmniPatientService bahmniPatientService;
    private Logger logger = Logger.getLogger(OpenElisPatientFeedClientImpl.class);


    @Autowired
    public OpenElisPatientFeedClientImpl(ElisAtomFeedProperties properties,
                                         BahmniPatientService bahmniPatientService,
                                         PlatformTransactionManager transactionManager) {
            super(properties, transactionManager);
        this.bahmniPatientService = bahmniPatientService;
    }

    @Override
    protected String getFeedUri(ElisAtomFeedProperties properties) {
        return properties.getFeedUri("patient.feed.uri");
    }

    @Override
    protected EventWorker createWorker(HttpClient authenticatedWebClient, ElisAtomFeedProperties properties) {
        EncounterService encounterService = Context.getService(EncounterService.class);
        ConceptService conceptService = Context.getService(ConceptService.class);
        PersonService personService = Context.getPersonService();
        ProviderService providerService = Context.getProviderService();
        VisitService visitService = Context.getVisitService();

        OpenElisAccessionEventWorker accessionEventWorker = new OpenElisAccessionEventWorker(properties, authenticatedWebClient, encounterService, conceptService, new AccessionHelper(properties), providerService, visitService);
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
