package org.bahmni.module.referencedatafeedclient.worker;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.domain.DrugForm;
import org.bahmni.module.referencedatafeedclient.domain.ReferenceDataConcept;
import org.bahmni.module.referencedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.ConceptDatatype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class DrugFormEventWorker implements EventWorker {
    public static final String MISC = "Misc";

    @Resource(name = "referenceDataHttpClient")
    private HttpClient httpClient;
    private final ReferenceDataFeedProperties referenceDataFeedProperties;
    private ReferenceDataConceptService referenceDataConceptService;

    @Autowired
    public DrugFormEventWorker(HttpClient httpClient, ReferenceDataFeedProperties referenceDataFeedProperties,
                           ReferenceDataConceptService referenceDataConceptService) {
        this.httpClient = httpClient;
        this.referenceDataFeedProperties = referenceDataFeedProperties;
        this.referenceDataConceptService = referenceDataConceptService;
    }

    @Override
    public void process(Event event) {
        try {
            DrugForm drugForm = httpClient.get(referenceDataFeedProperties.getReferenceDataUri() + event.getContent(), DrugForm.class);
            ReferenceDataConcept referenceDataConcept = new ReferenceDataConcept(drugForm.getId(), drugForm.getName(), MISC, ConceptDatatype.N_A_UUID);
            referenceDataConceptService.saveConcept(referenceDataConcept);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanUp(Event event) {

    }

}
