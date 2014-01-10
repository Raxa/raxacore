package org.bahmni.module.elisatomfeedclient.api.client.impl;

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
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("openElisPatientFeedClient")
public class OpenElisPatientFeedClientImpl extends OpenElisFeedClient implements OpenElisPatientFeedClient {
    private BahmniPatientService bahmniPatientService;
    private PersonService personService;

    @Autowired
    public OpenElisPatientFeedClientImpl(ElisAtomFeedProperties properties,
                                         BahmniPatientService bahmniPatientService,
                                         PersonService personService) {
        super(properties);
        this.bahmniPatientService = bahmniPatientService;
        this.personService = personService;
    }

    @Override
    protected String getFeedUri(ElisAtomFeedProperties properties) {
        return properties.getFeedUri("patient.feed.uri");
    }

    @Override
    protected EventWorker createWorker(HttpClient authenticatedWebClient,ElisAtomFeedProperties properties) {
        OpenElisAccessionEventWorker accessionEventWorker = new OpenElisAccessionEventWorker(properties, authenticatedWebClient, Context.getService(EncounterService.class), new AccessionMapper());
        OpenElisPatientEventWorker openElisPatientEventWorker = new OpenElisPatientEventWorker(bahmniPatientService, personService, authenticatedWebClient, properties);
        return  new OpenElisPatientFeedWorker(openElisPatientEventWorker, accessionEventWorker);
    }
}
