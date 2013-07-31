package org.bahmni.module.elisatomfeedclient.api.client;

import org.apache.log4j.Logger;
import org.bahmni.module.elisatomfeedclient.api.FeedProperties;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;

public class OpenElisFeedClient {

    private AtomFeedClient atomFeedClient;

    private static Logger logger = Logger.getLogger(OpenElisFeedClient.class);

    @Autowired
    public OpenElisFeedClient(FeedProperties properties, JdbcConnectionProvider jdbcConnectionProvider,
                              OpenElisPatientEventWorker openMRSEventWorker) {
        String feedUri = properties.getFeedUri();
        try {

            atomFeedClient = new AtomFeedClient(new AllFeeds(properties), new AllMarkersJdbcImpl(jdbcConnectionProvider),
                    new AllFailedEventsJdbcImpl(jdbcConnectionProvider), new URI(feedUri), openMRSEventWorker);
        } catch (URISyntaxException e) {
            logger.error(e);
            throw new RuntimeException("error for uri:" + feedUri);
        }
    }

    public void processFeed() {
        try {
            logger.info("Processing Customer Feed " + DateTime.now());
            atomFeedClient.processEvents();
        } catch (Exception e) {
            logger.error("failed customer feed execution " + e);
        }
    }


}
