package org.bahmni.module.referncedatafeedclient.worker;

import org.bahmni.module.referncedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referncedatafeedclient.domain.ReferenceDataConcept;
import org.bahmni.module.referncedatafeedclient.domain.Sample;
import org.bahmni.module.referncedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class SampleEventWorker implements EventWorker {
    public static final String LAB_SET = "LabSet";
    public static final String LABORATORY = "Laboratory";
    @Resource(name = "referenceDataHttpClient")
    private HttpClient httpClient;
    private final ReferenceDataFeedProperties referenceDataFeedProperties;
    private ConceptService conceptService;
    private ReferenceDataConceptService referenceDataConceptService;

    @Autowired
    public SampleEventWorker(HttpClient httpClient, ReferenceDataFeedProperties referenceDataFeedProperties, ConceptService conceptService, ReferenceDataConceptService referenceDataConceptService) {
        this.httpClient = httpClient;
        this.referenceDataFeedProperties = referenceDataFeedProperties;
        this.conceptService = conceptService;
        this.referenceDataConceptService = referenceDataConceptService;
    }

    @Override
    public void process(Event event) {
        try {
            Sample sample = httpClient.get(referenceDataFeedProperties.getReferenceDataUri() + event.getContent(), Sample.class);
            ReferenceDataConcept referenceDataConcept = new ReferenceDataConcept(sample.getId(), sample.getName(), sample.getDescription(), LAB_SET, ConceptDatatype.N_A_UUID);
            Concept sampleConcept = referenceDataConceptService.saveConcept(referenceDataConcept);
            Concept labConcept = conceptService.getConceptByName(LABORATORY);
            referenceDataConceptService.saveSetMembership(labConcept, sampleConcept);
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanUp(Event event) {

    }
}
