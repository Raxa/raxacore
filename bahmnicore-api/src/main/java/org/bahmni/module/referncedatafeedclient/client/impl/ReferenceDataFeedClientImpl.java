package org.bahmni.module.referncedatafeedclient.client.impl;

import org.apache.log4j.Logger;
import org.bahmni.module.referncedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referncedatafeedclient.client.ReferenceDataFeedClient;
import org.bahmni.module.referncedatafeedclient.domain.WebClientFactory;
import org.bahmni.module.referncedatafeedclient.worker.ReferenceDataEventWorker;
import org.bahmni.webclients.ClientCookies;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.openmrs.module.atomfeed.common.repository.OpenMRSJdbcConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.net.URI;

@Component("referenceDataFeedClient")
public class ReferenceDataFeedClientImpl implements ReferenceDataFeedClient {
    private final OpenMRSJdbcConnectionProvider jdbcConnectionProvider;
    private AtomFeedClient atomFeedClient;
    private ReferenceDataFeedProperties referenceDataFeedProperties;
    private Logger logger = Logger.getLogger(ReferenceDataFeedClientImpl.class);
    private ReferenceDataEventWorker referenceDataEventWorker;

    @Autowired
    public ReferenceDataFeedClientImpl(ReferenceDataFeedProperties referenceDataFeedProperties, ReferenceDataEventWorker referenceDataEventWorker,PlatformTransactionManager transactionManager) {
        this.referenceDataEventWorker = referenceDataEventWorker;
        this.jdbcConnectionProvider = new OpenMRSJdbcConnectionProvider(transactionManager);
        this.referenceDataFeedProperties = referenceDataFeedProperties;
    }

    private AtomFeedClient getAtomFeedClient() throws IOException {
        if(atomFeedClient == null) {
            HttpClient referenceDataClient = WebClientFactory.createReferenceDataClient(referenceDataFeedProperties);
            URI feedUri = URI.create(referenceDataFeedProperties.getFeedUri());
            ClientCookies cookies = referenceDataClient.getCookies(feedUri);
            AllFeeds allFeeds = new AllFeeds(referenceDataFeedProperties, cookies);
            AllMarkersJdbcImpl allMarkers = new AllMarkersJdbcImpl(jdbcConnectionProvider);
            AllFailedEventsJdbcImpl allFailedEvents = new AllFailedEventsJdbcImpl(jdbcConnectionProvider);
            atomFeedClient = new AtomFeedClient(allFeeds, allMarkers, allFailedEvents, referenceDataFeedProperties, jdbcConnectionProvider, feedUri, referenceDataEventWorker);
        }
        return atomFeedClient;
    }

    @Override
    public void processFeed() {
        try {
            getAtomFeedClient().processEvents();
        } catch (Throwable e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
}
