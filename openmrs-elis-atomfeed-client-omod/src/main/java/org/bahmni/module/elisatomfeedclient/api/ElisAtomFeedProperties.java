package org.bahmni.module.elisatomfeedclient.api;

import org.ict4h.atomfeed.client.factory.AtomFeedProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Properties;

@Component
public class ElisAtomFeedProperties extends AtomFeedProperties {

    private static final String OPEN_ELIS_URI = "openelis.uri";
    private static final String CONNECT_TIMEOUT = "feed.connectionTimeoutInMilliseconds";
    private static final String MAX_FAILED_EVENTS = "feed.maxFailedEvents";
    private static final String READ_TIMEOUT = "feed.replyTimeoutInMilliseconds";


    @Resource(name = "openElisAtomFeedProperties")
    private Properties atomFeedProperties;

    public String getFeedUri(String propertyName) {
        return atomFeedProperties.getProperty(propertyName);
    }

    public String getOpenElisUri() {
        return atomFeedProperties.getProperty(OPEN_ELIS_URI);
    }

    @Override
    public int getMaxFailedEvents() {
        return Integer.parseInt(atomFeedProperties.getProperty(MAX_FAILED_EVENTS));
    }

    @Override
    public int getReadTimeout() {
        return Integer.parseInt(atomFeedProperties.getProperty(READ_TIMEOUT));
    }

    @Override
    public int getConnectTimeout() {
        return Integer.parseInt(atomFeedProperties.getProperty(CONNECT_TIMEOUT));
    }
}
