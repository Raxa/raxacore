package org.bahmni.module.referncedatafeedclient.client;

import org.apache.log4j.Logger;

public class AtomFeedProcessor {
    private Logger logger = Logger.getLogger(AtomFeedProcessor.class);
    private final AtomFeedClientFactory atomFeedClientFactory;

    public AtomFeedProcessor(AtomFeedClientFactory atomFeedClientFactory) {
        this.atomFeedClientFactory = atomFeedClientFactory;
    }

    public void processFeed() {
        try {
            atomFeedClientFactory.getAtomFeedClient().processEvents();
        } catch (Throwable e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
}
