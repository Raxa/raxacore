package org.bahmni.module.openerpatomfeedclient.api.client.impl;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.openerpatomfeedclient.api.OpenERPAtomFeedProperties;
import org.bahmni.module.openerpatomfeedclient.api.client.OpenERPSaleOrderFeedClient;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component("openERPSaleOrderProcessFeedClient")
public class OpenERPSaleOrderProcessFeedClientImpl extends OpenERPSaleOrderFeedClientImpl implements OpenERPSaleOrderFeedClient {

    @Autowired
    public OpenERPSaleOrderProcessFeedClientImpl(
            OpenERPAtomFeedProperties properties,
            BahmniDrugOrderService bahmniDrugOrderService,
            PlatformTransactionManager transactionManager) {
        super(properties,bahmniDrugOrderService,transactionManager);
    }

    @Override
    public void processFeed() {
        process(new ProcessFeed());
    }

    protected interface FeedProcessor {
        void process(FeedClient atomFeedClient);
    }

    private static class ProcessFeed implements FeedProcessor {
        public void process(FeedClient atomFeedClient){
            atomFeedClient.processEvents();
        }
    }

}
