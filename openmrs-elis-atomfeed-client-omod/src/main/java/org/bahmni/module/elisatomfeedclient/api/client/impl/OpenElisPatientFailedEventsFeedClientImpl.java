package org.bahmni.module.elisatomfeedclient.api.client.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisFeedClient;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisPatientFailedEventsFeedClient;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionHelper;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisAccessionEventWorker;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisPatientEventWorker;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisPatientFeedWorker;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.joda.time.DateTime;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component("openElisPatientFailedEventsFeedClient")
public class OpenElisPatientFailedEventsFeedClientImpl extends OpenElisFeedClient implements OpenElisPatientFailedEventsFeedClient {
    private OrderService orderService;
    private VisitService visitService;
    private BahmniPatientService bahmniPatientService;
    private PersonService personService;
    private ProviderService providerService;
    private ConceptService conceptService;
    private Logger logger = Logger.getLogger(OpenElisPatientFailedEventsFeedClientImpl.class);


    @Autowired
    public OpenElisPatientFailedEventsFeedClientImpl(ElisAtomFeedProperties properties,
                                                     BahmniPatientService bahmniPatientService,
                                                     PersonService personService,
                                                     ProviderService providerService,
                                                     ConceptService conceptService,
                                                     PlatformTransactionManager transactionManager,
                                                     OrderService orderService,
                                                     VisitService visitService) {
        super(properties, transactionManager);
        this.bahmniPatientService = bahmniPatientService;
        this.personService = personService;
        this.providerService = providerService;
        this.conceptService = conceptService;
        this.orderService = orderService;
        this.visitService = visitService;
    }

    @Override
    protected String getFeedUri(ElisAtomFeedProperties properties) {
        return properties.getFeedUri("patient.feed.uri");
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
                providerService);
        OpenElisPatientEventWorker openElisPatientEventWorker = new OpenElisPatientEventWorker(bahmniPatientService, personService, authenticatedWebClient, properties);
        return new OpenElisPatientFeedWorker(openElisPatientEventWorker, accessionEventWorker);
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
                }
            } catch (Exception ex) {
                logger.error("openelisatomfeedclient:failed feed execution while running failed events" + e, e);
                throw new RuntimeException(ex);
            }
        }
    }

}
