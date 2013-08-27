package org.bahmni.module.elisatomfeedclient.api.client;

import org.apache.log4j.Logger;
import org.bahmni.module.elisatomfeedclient.api.FeedProperties;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.joda.time.DateTime;
import org.omg.IOP.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

@Component("openElisFeedClient")
public class OpenElisFeedClient implements OpenElisFeedClientInterface {

    private AtomFeedClient atomFeedClient;

    private static Logger logger = Logger.getLogger(OpenElisFeedClient.class);

    @Autowired
    public OpenElisFeedClient(FeedProperties properties, JdbcConnectionProvider jdbcConnectionProvider,
                              OpenElisPatientEventWorker openMRSEventWorker) {
        String feedUri = properties.getFeedUri();
        try {
            atomFeedClient = new AtomFeedClient(new AllFeeds(properties, new HashMap<String, String>()), new AllMarkersJdbcImpl(jdbcConnectionProvider),
                    new AllFailedEventsJdbcImpl(jdbcConnectionProvider), properties, jdbcConnectionProvider, new URI(feedUri), openMRSEventWorker);
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
