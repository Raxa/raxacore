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
import org.springframework.transaction.PlatformTransactionManager;

import java.net.URI;
import java.net.URISyntaxException;

public class OpenERPSaleOrderFeedClientImpl  {
    private static Logger logger = Logger.getLogger(OpenERPSaleOrderFeedClient.class);

    private AtomFeedClient atomFeedClient;
    private JdbcConnectionProvider jdbcConnectionProvider;
    private OpenERPAtomFeedProperties properties;
    private BahmniDrugOrderService bahmniDrugOrderService;

    public OpenERPSaleOrderFeedClientImpl(
            OpenERPAtomFeedProperties properties,
            BahmniDrugOrderService bahmniDrugOrderService,
            PlatformTransactionManager transactionManager) {
        this.jdbcConnectionProvider = new OpenMRSJdbcConnectionProvider(transactionManager);
        this.properties = properties;
        this.bahmniDrugOrderService = bahmniDrugOrderService;
    }

    protected void initializeAtomFeedClient() {
        String feedUri = properties.getFeedUri("sale.order.feed.uri");
        try {
            atomFeedClient = new AtomFeedClientBuilder().
                    forFeedAt(new URI(feedUri)).
                    processedBy(new SaleOrderFeedEventWorker(bahmniDrugOrderService, properties)).
                    usingConnectionProvider(jdbcConnectionProvider).
                    with(properties).
                    build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Is not a valid URI - %s", feedUri));
        }
    }

    protected void process(OpenERPSaleOrderProcessFeedClientImpl.FeedProcessor feedProcessor) {
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


}
