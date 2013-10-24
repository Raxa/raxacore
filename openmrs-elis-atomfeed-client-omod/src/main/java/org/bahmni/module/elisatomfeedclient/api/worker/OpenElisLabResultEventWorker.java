package org.bahmni.module.elisatomfeedclient.api.worker;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniLabResultService;
import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.client.WebClient;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisLabResult;
import org.bahmni.module.elisatomfeedclient.api.exception.OpenElisFeedException;
import org.bahmni.module.elisatomfeedclient.api.mapper.BahmniLabResultMapper;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import static org.bahmni.module.elisatomfeedclient.api.util.ObjectMapperRepository.objectMapper;

@Component
public class OpenElisLabResultEventWorker implements EventWorker {
    private static Logger logger = Logger.getLogger(OpenElisLabResultEventWorker.class);

    private BahmniLabResultService bahmniLabResultService;
    private WebClient webClient;
    private ElisAtomFeedProperties elisAtomFeedProperties;

    @Autowired
    public OpenElisLabResultEventWorker(BahmniLabResultService bahmniLabResultService, WebClient webClient, ElisAtomFeedProperties elisAtomFeedProperties) {
        this.bahmniLabResultService = bahmniLabResultService;
        this.webClient = webClient;
        this.elisAtomFeedProperties = elisAtomFeedProperties;
    }

    @Override
    public void process(Event event) {
        String labResultUrl = elisAtomFeedProperties.getOpenElisUri() + event.getContent();
        logger.info("openelisatomfeedclient:Processing event : " + labResultUrl);
        try {
            String response = webClient.get(URI.create(labResultUrl), new HashMap<String, String>());
            OpenElisLabResult openElisLabResult = objectMapper.readValue(response, OpenElisLabResult.class);

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
