package org.bahmni.module.elisatomfeedclient.api.client.impl;

import org.apache.log4j.Logger;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisLabResultFeedClient;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisLabResultEventWorker;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

@Component("openElisLabResultFeedClient")
public class OpenElisLabResultFeedClientImpl implements OpenElisLabResultFeedClient {

    private AtomFeedClient atomFeedClient;
    private static Logger logger = Logger.getLogger(OpenElisPatientFeedClientImpl.class);

    @Autowired
    public OpenElisLabResultFeedClientImpl(ElisAtomFeedProperties properties,
                                           JdbcConnectionProvider jdbcConnectionProvider,
                                           OpenElisLabResultEventWorker openMRSEventWorker) {
        String feedUri = properties.getFeedUri("result.feed.uri");
        try {
            atomFeedClient = new AtomFeedClient(
                    new AllFeeds(properties, new HashMap<String, String>()),
                    new AllMarkersJdbcImpl(jdbcConnectionProvider),
                    new AllFailedEventsJdbcImpl(jdbcConnectionProvider),
                    properties,
                    jdbcConnectionProvider,
                    new URI(feedUri),
                    openMRSEventWorker);
        } catch (URISyntaxException e) {
            logger.error("openelisatomfeedclient:error instantiating client:" + e.getMessage(), e);
            throw new RuntimeException("error for uri:" + feedUri);
        }
    }

    @Override
    public void processFeed() {
        try {
            logger.info("openelisatomfeedclient:processing feed " + DateTime.now());
            atomFeedClient.processEvents();
        } catch (Exception e) {
            logger.error("openelisatomfeedclient:failed feed execution " + e, e);
            throw e;
        }
    }
}
