package org.bahmni.module.elisatomfeedclient.api.client.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisFeedClient;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisPatientFailedEventsFeedClient;
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

@Component("openElisPatientFailedEventsFeedClient")
public class OpenElisPatientFailedEventsFeedClientImpl extends OpenElisFeedClient implements OpenElisPatientFailedEventsFeedClient {
    private ProviderService providerService;
    private ConceptService conceptService;
    private BahmniVisitAttributeSaveCommandImpl bahmniVisitAttributeSaveCommand;
    private Logger logger = Logger.getLogger(OpenElisPatientFailedEventsFeedClientImpl.class);


    @Autowired
    public OpenElisPatientFailedEventsFeedClientImpl(ElisAtomFeedProperties properties,
                                                     ProviderService providerService,
                                                     ConceptService conceptService,
                                                     PlatformTransactionManager transactionManager, BahmniVisitAttributeSaveCommandImpl bahmniVisitAttributeSaveCommand) {
        super(properties, transactionManager);
        this.providerService = providerService;
        this.conceptService = conceptService;
        this.bahmniVisitAttributeSaveCommand = bahmniVisitAttributeSaveCommand;
    }

    @Override
    protected String getFeedUri(ElisAtomFeedProperties properties) {
        return properties.getPatientFeedUri();
    }

    @Override
    protected EventWorker createWorker(HttpClient authenticatedWebClient, ElisAtomFeedProperties properties) {
        EncounterService encounterService = Context.getService(EncounterService.class);
        OpenElisAccessionEventWorker accessionEventWorker = new OpenElisAccessionEventWorker(
                properties,
                authenticatedWebClient,
                encounterService,
                conceptService,
                new AccessionHelper(properties),
                providerService, bahmniVisitAttributeSaveCommand);
        return new OpenElisPatientFeedWorker(accessionEventWorker);
    }

    @Override
    public void processFailedEvents() {
        try {
            logger.info("openelisatomfeedclient:processing failed events " + DateTime.now());
            getAtomFeedClient().processFailedEvents();
        } catch (Exception e) {
            try {
                if (e != null && ExceptionUtils.getStackTrace(e).contains("HTTP response code: 401")) {
                    getAtomFeedClient();
                }else{
                    throw e;
                }

            } catch (Exception ex) {
                logger.error("openelisatomfeedclient:failed feed execution while running failed events" + e, e);
                throw new RuntimeException(ex);
            }
        }
    }

}
