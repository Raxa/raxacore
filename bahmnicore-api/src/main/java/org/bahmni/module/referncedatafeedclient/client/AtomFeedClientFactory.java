package org.bahmni.module.referncedatafeedclient.client;

import org.ict4h.atomfeed.client.service.AtomFeedClient;

public interface AtomFeedClientFactory {
    AtomFeedClient getAtomFeedClient() throws Exception;
}
