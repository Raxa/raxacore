package org.bahmni.module.elisatomfeedclient.api;

import org.ict4h.atomfeed.client.factory.AtomFeedProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Properties;

@Component
public class FeedProperties extends AtomFeedProperties {

    private static final String FEED_URI = "feed.uri";
    private static final String OPEN_ELIS_URI = "openelis.uri";
    private static final String READ_TIMEOUT = "read.timeout";
    private static final String CONNECT_TIMEOUT = "connect.timeout";

    @Resource(name = "atomfeedProperties")
    private Properties atomFeedProperties;

    public String getFeedUri() {
        return atomFeedProperties.getProperty(FEED_URI);
    }

    public String getOpenElisUri() {
        return atomFeedProperties.getProperty(OPEN_ELIS_URI);
    }
}
