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
import org.ict4h.atomfeed.client.service.FeedClient;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.net.URI;

@Component
public class ReferenceDataFeedClientFactory implements AtomFeedClientFactory {
    private final PlatformTransactionManager transactionManager;
    private ReferenceDataFeedProperties referenceDataFeedProperties;
    private ReferenceDataEventWorker referenceDataEventWorker;
    private FeedClient atomFeedClient;

    @Autowired
    public ReferenceDataFeedClientFactory(ReferenceDataFeedProperties referenceDataFeedProperties, ReferenceDataEventWorker referenceDataEventWorker, PlatformTransactionManager transactionManager) {
        this.referenceDataFeedProperties = referenceDataFeedProperties;
        this.referenceDataEventWorker = referenceDataEventWorker;
        this.transactionManager = transactionManager;
    }

    @Override
    public FeedClient getAtomFeedClient() throws Exception {
        if(atomFeedClient == null) {
            HttpClient referenceDataClient = WebClientFactory.createReferenceDataClient(referenceDataFeedProperties);
            URI feedUri = URI.create(referenceDataFeedProperties.getFeedUri());
            ClientCookies cookies = referenceDataClient.getCookies(feedUri);

            AtomFeedSpringTransactionManager txMgr = new AtomFeedSpringTransactionManager(transactionManager);

            AllFeeds allFeeds = new AllFeeds(referenceDataFeedProperties, cookies);
            AllFailedEventsJdbcImpl allFailedEvents = new AllFailedEventsJdbcImpl(txMgr);
            AllMarkersJdbcImpl allMarkers = new AllMarkersJdbcImpl(txMgr);

            atomFeedClient = new AtomFeedClient(
                    allFeeds,
                    allMarkers,
                    allFailedEvents,
                    referenceDataFeedProperties,
                    txMgr,
                    feedUri,
                    referenceDataEventWorker);

        }
        return atomFeedClient;
    }
}
