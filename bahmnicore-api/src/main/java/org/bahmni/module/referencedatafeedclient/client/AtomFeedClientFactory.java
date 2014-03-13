package org.bahmni.module.referencedatafeedclient.client;

import org.ict4h.atomfeed.client.service.FeedClient;

public interface AtomFeedClientFactory {
    FeedClient getAtomFeedClient() throws Exception;
}
