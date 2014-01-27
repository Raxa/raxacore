package org.bahmni.module.elisatomfeedclient.api.worker;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniLabResultService;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisLabResult;
import org.bahmni.module.elisatomfeedclient.api.exception.OpenElisFeedException;
import org.bahmni.module.elisatomfeedclient.api.mapper.BahmniLabResultMapper;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

import java.io.IOException;

public class OpenElisLabResultEventWorker implements EventWorker {
    private static Logger logger = Logger.getLogger(OpenElisLabResultEventWorker.class);

    private BahmniLabResultService bahmniLabResultService;
    private HttpClient httpClient;
    private ElisAtomFeedProperties elisAtomFeedProperties;

    public OpenElisLabResultEventWorker(BahmniLabResultService bahmniLabResultService, HttpClient httpClient, ElisAtomFeedProperties elisAtomFeedProperties) {
        this.bahmniLabResultService = bahmniLabResultService;
        this.httpClient = httpClient;
        this.elisAtomFeedProperties = elisAtomFeedProperties;
    }

    @Override
    public void process(Event event) {
        String labResultUrl = elisAtomFeedProperties.getOpenElisUri() + event.getContent();
        logger.info("openelisatomfeedclient:Processing event : " + labResultUrl);
        try {
            OpenElisLabResult openElisLabResult = httpClient.get(labResultUrl, OpenElisLabResult.class);
            logger.info("openelisatomfeedclient:creating LabResult for event : " + labResultUrl);
            bahmniLabResultService.add(new BahmniLabResultMapper().map(openElisLabResult));
        } catch (IOException e) {
            logger.error("openelisatomfeedclient:error processing event : " + labResultUrl + e.getMessage(), e);
            throw new OpenElisFeedException("could not read lab result data", e);
        }
    }

    @Override
    public void cleanUp(Event event) {
    }
}
