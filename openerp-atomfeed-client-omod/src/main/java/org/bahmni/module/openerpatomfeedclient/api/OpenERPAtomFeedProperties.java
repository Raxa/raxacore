package org.bahmni.module.openerpatomfeedclient.api;

import org.ict4h.atomfeed.client.factory.AtomFeedProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Properties;

@Component
public class OpenERPAtomFeedProperties extends AtomFeedProperties {

    private static final String OPENERP_URI = "openerp.uri";
    private static final String CONNECT_TIMEOUT = "feed.connectionTimeoutInMilliseconds";
    private static final String MAX_FAILED_EVENTS = "feed.maxFailedEvents";
    private static final String READ_TIMEOUT = "feed.replyTimeoutInMilliseconds";


    @Resource(name = "erpAtomFeedProperties")
    private Properties atomFeedProperties;

    public String getFeedUri(String propertyName) {
        return atomFeedProperties.getProperty(propertyName);
    }

    public String getOpenERPUri() {
        return atomFeedProperties.getProperty(OPENERP_URI);
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