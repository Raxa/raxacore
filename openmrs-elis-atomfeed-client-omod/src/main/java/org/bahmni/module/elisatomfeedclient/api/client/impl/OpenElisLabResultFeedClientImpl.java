package org.bahmni.module.elisatomfeedclient.api.client.impl;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniLabResultService;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisFeedClient;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisLabResultFeedClient;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisLabResultEventWorker;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("openElisLabResultFeedClient")
public class OpenElisLabResultFeedClientImpl extends OpenElisFeedClient implements OpenElisLabResultFeedClient {

    private static Logger logger = Logger.getLogger(OpenElisPatientFeedClientImpl.class);
    private BahmniLabResultService bahmniLabResultService;

    @Autowired
    public OpenElisLabResultFeedClientImpl(ElisAtomFeedProperties properties,
                                           JdbcConnectionProvider jdbcConnectionProvider,
                                           BahmniLabResultService bahmniLabResultService) {
        super(jdbcConnectionProvider, properties);
        this.bahmniLabResultService = bahmniLabResultService;
    }


    @Override
    protected String getFeedUri(ElisAtomFeedProperties properties) {
        return properties.getFeedUri("result.feed.uri");
    }

    @Override
    protected EventWorker createWorker(HttpClient authenticatedWebClient, ElisAtomFeedProperties properties) {
        return new OpenElisLabResultEventWorker(bahmniLabResultService, authenticatedWebClient,properties);
    }
}
