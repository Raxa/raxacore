package org.bahmni.module.referencedatafeedclient.client;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.domain.WebClientFactory;
import org.bahmni.module.referencedatafeedclient.worker.ReferenceDataEventWorker;
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

import java.net.URI;

@Component
public class ReferenceDataFeedClientFactory implements AtomFeedClientFactory {
    private ReferenceDataFeedProperties referenceDataFeedProperties;
    private ReferenceDataEventWorker referenceDataEventWorker;
    private OpenMRSJdbcConnectionProvider jdbcConnectionProvider;
    private AtomFeedClient atomFeedClient;

    @Autowired
    public ReferenceDataFeedClientFactory(ReferenceDataFeedProperties referenceDataFeedProperties, ReferenceDataEventWorker referenceDataEventWorker, PlatformTransactionManager transactionManager) {
        this.referenceDataFeedProperties = referenceDataFeedProperties;
        this.referenceDataEventWorker = referenceDataEventWorker;
        this.jdbcConnectionProvider = new OpenMRSJdbcConnectionProvider(transactionManager);
    }

    @Override
    public AtomFeedClient getAtomFeedClient() throws Exception {
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
}
