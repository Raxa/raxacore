package org.bahmni.module.elisatomfeedclient.api.client.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisFeedClient;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisPatientFeedClient;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionHelper;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisAccessionEventWorker;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisPatientFeedWorker;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.joda.time.DateTime;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.impl.BahmniVisitAttributeSaveCommandImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component("openElisPatientFeedClient")
public class OpenElisPatientFeedClientImpl extends OpenElisFeedClient implements OpenElisPatientFeedClient {
    private BahmniPatientService bahmniPatientService;
    private BahmniVisitAttributeSaveCommandImpl bahmniVisitAttributeSaveCommand;
    private Logger logger = Logger.getLogger(OpenElisPatientFeedClientImpl.class);


    @Autowired
    public OpenElisPatientFeedClientImpl(ElisAtomFeedProperties properties,
                                         BahmniPatientService bahmniPatientService,
                                         PlatformTransactionManager transactionManager, BahmniVisitAttributeSaveCommandImpl bahmniVisitAttributeSaveCommand) {
        super(properties, transactionManager);
        this.bahmniPatientService = bahmniPatientService;
        this.bahmniVisitAttributeSaveCommand = bahmniVisitAttributeSaveCommand;
    }

    @Override
    protected String getFeedUri(ElisAtomFeedProperties properties) {
        return properties.getFeedUri("patient.feed.uri");
    }

    @Override
    protected EventWorker createWorker(HttpClient authenticatedWebClient, ElisAtomFeedProperties properties) {
        EncounterService encounterService = Context.getService(EncounterService.class);
        ConceptService conceptService = Context.getService(ConceptService.class);
        ProviderService providerService = Context.getProviderService();

        OpenElisAccessionEventWorker accessionEventWorker = new OpenElisAccessionEventWorker(properties,
                authenticatedWebClient, encounterService, conceptService, new AccessionHelper(properties),
                providerService, bahmniVisitAttributeSaveCommand);
        return new OpenElisPatientFeedWorker(accessionEventWorker);
    }

    @Override
    public void processFeed() {
        try {
            logger.info("openelisatomfeedclient:processing feed " + DateTime.now());
            getAtomFeedClient().processEvents();
        } catch (Exception e) {
            try {
                if (e != null && isUnauthorised(e)) {
                    getAtomFeedClient();
                }
            } catch (Exception ex) {
                logger.error("openelisatomfeedclient:failed feed execution " + e, e);
                throw new RuntimeException(ex);
            }
        }
    }

    private boolean isUnauthorised(Exception e) {
        return ExceptionUtils.getStackTrace(e).contains("HTTP response code: 401")
                || ExceptionUtils.getStackTrace(e).contains("HTTP response code: 403");
    }

}
