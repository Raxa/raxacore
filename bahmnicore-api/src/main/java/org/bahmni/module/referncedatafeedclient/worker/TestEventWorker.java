package org.bahmni.module.referncedatafeedclient.worker;

import org.bahmni.module.referncedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referncedatafeedclient.domain.ReferenceDataConcept;
import org.bahmni.module.referncedatafeedclient.domain.Test;
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
public class TestEventWorker implements EventWorker {
    public static final String TEST = "Test";
    public static final String LABORATORY = "Laboratory";
    @Resource(name = "referenceDataHttpClient")
    private HttpClient httpClient;
    private final ReferenceDataFeedProperties referenceDataFeedProperties;
    private ConceptService conceptService;
    private ReferenceDataConceptService referenceDataConceptService;

    @Autowired
    public TestEventWorker(HttpClient httpClient, ReferenceDataFeedProperties referenceDataFeedProperties, ConceptService conceptService, ReferenceDataConceptService referenceDataConceptService) {
        this.httpClient = httpClient;
        this.referenceDataFeedProperties = referenceDataFeedProperties;
        this.conceptService = conceptService;
        this.referenceDataConceptService = referenceDataConceptService;
    }

    @Override
    public void process(Event event) {
        try {
            Test test = httpClient.get(referenceDataFeedProperties.getReferenceDataUri() + event.getContent(), Test.class);
            ConceptDatatype conceptDataType = conceptService.getConceptDatatypeByName(test.getResultType());
            ReferenceDataConcept referenceDataConcept = new ReferenceDataConcept(test.getId(), test.getName(), test.getDescription(), TEST, conceptDataType.getUuid(), test.getShortName());
            Concept testConcept = referenceDataConceptService.saveConcept(referenceDataConcept);
            referenceDataConceptService.saveSetMembership(conceptService.getConceptByUuid(test.getSample().getId()), testConcept);
            referenceDataConceptService.saveSetMembership(conceptService.getConceptByUuid(test.getDepartment().getId()), testConcept);
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanUp(Event event) {

    }
}
