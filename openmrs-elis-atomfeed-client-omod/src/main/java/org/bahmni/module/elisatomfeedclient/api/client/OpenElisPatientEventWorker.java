package org.bahmni.module.elisatomfeedclient.api.client;

import org.bahmni.module.bahmnicore.service.BahmniPatientService;

import org.bahmni.module.elisatomfeedclient.api.FeedProperties;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisPatient;
import org.bahmni.module.elisatomfeedclient.api.exception.OpenElisFeedException;
import org.bahmni.module.elisatomfeedclient.api.mapper.BahmniPatientMapper;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import static org.bahmni.module.elisatomfeedclient.api.util.ObjectMapperRepository.objectMapper;

@Component
public class OpenElisPatientEventWorker implements EventWorker {

    private BahmniPatientService patientService;
    private WebClient webClient;
    private FeedProperties feedProperties;

    @Autowired
    public OpenElisPatientEventWorker(BahmniPatientService bahmniPatientService, WebClient webClient, FeedProperties feedProperties) {
        this.patientService = bahmniPatientService;
        this.webClient = webClient;
        this.feedProperties = feedProperties;
    }

    @Override
    public void process(Event event) {
        String patientUrl = feedProperties.getOpenElisUri() + event.getContent();
        try {
            String response = webClient.get(URI.create(patientUrl), new HashMap<String, String>());
            OpenElisPatient openElisPatient = objectMapper.readValue(response, OpenElisPatient.class);
            patientService.createPatient(new BahmniPatientMapper().map(openElisPatient));
        } catch (IOException e) {
            throw new OpenElisFeedException("could not read patient data", e);
        }
    }
}
