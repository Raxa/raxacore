package org.bahmni.module.openerpatomfeedclient.api.client.impl;

import org.apache.commons.lang3.exception.*;
import org.apache.log4j.*;
import org.bahmni.module.bahmnicore.service.*;
import org.bahmni.module.openerpatomfeedclient.api.*;
import org.bahmni.module.openerpatomfeedclient.api.client.*;
import org.bahmni.module.openerpatomfeedclient.api.worker.*;
import org.ict4h.atomfeed.client.factory.*;
import org.ict4h.atomfeed.client.service.*;
import org.ict4h.atomfeed.jdbc.*;
import org.joda.time.*;
import org.openmrs.module.atomfeed.common.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.net.*;

@Component("openERPSaleOrderFeedClient")
public class OpenERPSaleOrderFeedClientImpl implements OpenERPSaleOrderFeedClient {
    private static Logger logger = Logger.getLogger(OpenERPSaleOrderFeedClient.class);

    private AtomFeedClient atomFeedClient;
    private JdbcConnectionProvider jdbcConnectionProvider;
    private OpenERPAtomFeedProperties properties;
    private BahmniDrugOrderService bahmniDrugOrderService;

    @Autowired
    public OpenERPSaleOrderFeedClientImpl(OpenMRSJdbcConnectionProvider jdbcConnectionProvider, OpenERPAtomFeedProperties properties, BahmniDrugOrderService bahmniDrugOrderService) {
        this.jdbcConnectionProvider = jdbcConnectionProvider;
        this.properties = properties;
        this.bahmniDrugOrderService = bahmniDrugOrderService;
    }

    protected void initializeAtomFeedClient() {
        String feedUri = properties.getFeedUri("sale.order.feed.uri");
        try {
            atomFeedClient = new AtomFeedClientBuilder().
                    forFeedAt(new URI(feedUri)).
                    processedBy(new SaleOrderFeedEventWorker(bahmniDrugOrderService)).
                    usingConnectionProvider(jdbcConnectionProvider).
                    with(properties).
                    build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Is not a valid URI - %s", feedUri));
        }
    }

    @Override
    public void processFeed() {
        process(new ProcessFeed());
    }

    @Override
    public void processFailedFeed() {
        process(new ProcessFailedFeed());
    }


    private void process(FeedProcessor feedProcessor) {
        try {
            if(atomFeedClient == null) {
                initializeAtomFeedClient();
            }
            logger.info("openerpatomfeedclient:processing feed " + DateTime.now());
            feedProcessor.process(atomFeedClient);
        } catch (Exception e) {
            try {
                if (e != null && ExceptionUtils.getStackTrace(e).contains("HTTP response code: 401")) {
                    initializeAtomFeedClient();
                }
            }catch (Exception ex){
                logger.error("openerpatomfeedclient:failed feed execution " + e, e);
                throw new RuntimeException(ex);
            }
        }
    }

    private interface FeedProcessor {
        void process(AtomFeedClient atomFeedClient);
    }

    private static class ProcessFailedFeed implements FeedProcessor {
        public void process(AtomFeedClient atomFeedClient){
            atomFeedClient.processFailedEvents();
        }
    }

    private static class ProcessFeed implements FeedProcessor {
        public void process(AtomFeedClient atomFeedClient){
            atomFeedClient.processEvents();
        }
    }
}
