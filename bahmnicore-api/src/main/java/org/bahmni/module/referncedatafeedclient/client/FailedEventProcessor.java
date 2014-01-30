package org.bahmni.module.referncedatafeedclient.client;

import org.apache.log4j.Logger;

public class FailedEventProcessor {
    private Logger logger = Logger.getLogger(FailedEventProcessor.class);
    private final AtomFeedClientFactory atomFeedClientFactory;

    public FailedEventProcessor(AtomFeedClientFactory atomFeedClientFactory) {
        this.atomFeedClientFactory = atomFeedClientFactory;
    }

    public void processFeed() {
        try {
            atomFeedClientFactory.getAtomFeedClient().processFailedEvents();
        } catch (Throwable e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
}
