package org.bahmni.module.openerpatomfeedclient.api.client.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.openerpatomfeedclient.api.OpenERPAtomFeedProperties;
import org.bahmni.module.openerpatomfeedclient.api.client.OpenERPSaleOrderFeedClient;
import org.bahmni.module.openerpatomfeedclient.api.worker.SaleOrderFeedEventWorker;
import org.ict4h.atomfeed.client.factory.AtomFeedClientBuilder;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.joda.time.DateTime;
import org.openmrs.module.atomfeed.common.repository.OpenMRSJdbcConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;

@Component("openERPSaleOrderFeedClient")
public class OpenERPSaleOrderFeedClientImpl implements OpenERPSaleOrderFeedClient {
    private static Logger logger = Logger.getLogger(OpenERPSaleOrderFeedClient.class);

    private AtomFeedClient atomFeedClient;
    private JdbcConnectionProvider jdbcConnectionProvider;
    private OpenERPAtomFeedProperties properties;
    private BahmniDrugOrderService bahmniDrugOrderService;

    @Autowired
    public OpenERPSaleOrderFeedClientImpl(
            OpenERPAtomFeedProperties properties,
            BahmniDrugOrderService bahmniDrugOrderService) {
        this.jdbcConnectionProvider = new OpenMRSJdbcConnectionProvider();
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
    @Transactional
    public void processFeed() {
        process(new ProcessFeed());
    }

    @Override
    @Transactional
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
