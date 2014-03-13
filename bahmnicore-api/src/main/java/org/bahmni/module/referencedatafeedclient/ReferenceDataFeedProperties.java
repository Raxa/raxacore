package org.bahmni.module.referencedatafeedclient;

import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Properties;

@Component
public class ReferenceDataFeedProperties extends AtomFeedProperties {
    private static final String REFERENCE_DATA_URI = "referenceData.uri";
    private static final String CONNECT_TIMEOUT = "feed.connectionTimeoutInMilliseconds";
    private static final String MAX_FAILED_EVENTS = "feed.maxFailedEvents";
    private static final String READ_TIMEOUT = "feed.replyTimeoutInMilliseconds";
    private static final String FEED_URI = "referenceData.feed.uri";

    @Resource(name = "referenceDataAtomFeedProperties")
    private Properties atomFeedProperties;

    public String getReferenceDataUri() {
        return atomFeedProperties.getProperty(REFERENCE_DATA_URI);
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


    public String getFeedUri() {
        return atomFeedProperties.getProperty(FEED_URI);
    }
}
