package org.bahmni.module.elisatomfeedclient.api.client.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniLabResultService;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisFeedClient;
import org.bahmni.module.elisatomfeedclient.api.client.OpenElisLabResultFailedEventsFeedClient;
import org.bahmni.module.elisatomfeedclient.api.worker.OpenElisLabResultEventWorker;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component("openElisLabResultFailedEventFeedClient")
@Scope("prototype")
public class OpenElisLabResultFailedEventsFeedClientImpl extends OpenElisFeedClient implements OpenElisLabResultFailedEventsFeedClient {

    private BahmniLabResultService bahmniLabResultService;
    private Logger logger = Logger.getLogger(OpenElisLabResultFailedEventsFeedClientImpl.class);


    @Autowired
    public OpenElisLabResultFailedEventsFeedClientImpl(ElisAtomFeedProperties properties,
                                                       BahmniLabResultService bahmniLabResultService,
                                                       PlatformTransactionManager transactionManager) {
        super(properties, transactionManager);
        this.bahmniLabResultService = bahmniLabResultService;
    }


    @Override
    protected String getFeedUri(ElisAtomFeedProperties properties) {
        return properties.getFeedUri("result.feed.uri");
    }

    @Override
    protected EventWorker createWorker(HttpClient authenticatedWebClient, ElisAtomFeedProperties properties) {
        return new OpenElisLabResultEventWorker(bahmniLabResultService, authenticatedWebClient,properties);
    }

    @Override
    public void processFailedEvents() {
        try {
            if(atomFeedClient == null) {
                initializeAtomFeedClient();
            }
            logger.info("openelisatomfeedclient:processing failed events " + DateTime.now());
            atomFeedClient.processFailedEvents();
        } catch (Exception e) {
            try {
                if (e != null && ExceptionUtils.getStackTrace(e).contains("HTTP response code: 401")) {
                    initializeAtomFeedClient();
                }
            }catch (Exception ex){
                logger.error("openelisatomfeedclient:failed feed execution while running failed events" + e, e);
                throw new RuntimeException(ex);
            }
        }
    }

}
