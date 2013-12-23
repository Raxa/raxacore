package org.bahmni.module.elisatomfeedclient.api.client.impl;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisFeedClient;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisPatientFeedClient;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisPatientEventWorker;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("openElisPatientFeedClient")
public class OpenElisPatientFeedClientImpl extends OpenElisFeedClient implements OpenElisPatientFeedClient {

    private static Logger logger = Logger.getLogger(OpenElisPatientFeedClientImpl.class);
    private BahmniPatientService bahmniPatientService;
    private PersonService personService;

    @Autowired
    public OpenElisPatientFeedClientImpl(ElisAtomFeedProperties properties,
                                         JdbcConnectionProvider jdbcConnectionProvider,
                                         BahmniPatientService bahmniPatientService,
                                         PersonService personService) {
        super(jdbcConnectionProvider,properties);
        this.bahmniPatientService = bahmniPatientService;
        this.personService = personService;
    }

    @Override
    protected String getFeedUri(ElisAtomFeedProperties properties) {
        return properties.getFeedUri("patient.feed.uri");
    }

    @Override
    protected EventWorker createWorker(HttpClient authenticatedWebClient,ElisAtomFeedProperties properties) {
        return new OpenElisPatientEventWorker(bahmniPatientService, personService,  authenticatedWebClient, properties);
    }
}
