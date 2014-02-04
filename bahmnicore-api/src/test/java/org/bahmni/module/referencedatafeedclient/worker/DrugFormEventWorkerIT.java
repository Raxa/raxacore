package org.bahmni.module.referencedatafeedclient.worker;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.domain.DrugForm;
import org.bahmni.module.referencedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"})
public class DrugFormEventWorkerIT extends BaseModuleWebContextSensitiveTest {
    @Mock
    private HttpClient httpClient;
    @Mock
    private ReferenceDataFeedProperties referenceDataFeedProperties;
    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;
    @Autowired
    private ConceptService conceptService;

    private final String referenceDataUri = "http://localhost";
    private DrugFormEventWorker drugFormEventWorker;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(referenceDataFeedProperties.getReferenceDataUri()).thenReturn(referenceDataUri);
        drugFormEventWorker = new DrugFormEventWorker(httpClient, referenceDataFeedProperties, referenceDataConceptService);
    }

    @Test
    public void shouldCreateConceptForDrugForm() throws IOException {
        Event event = new Event("xxxx-yyyyy", "/reference-data/drug_form/412fa577-5ed0-4738-b550-a83ec144a84b");
        DrugForm drugForm = new DrugForm("412fa577-5ed0-4738-b550-a83ec144a84b", "Tablet");
        when(httpClient.get(referenceDataUri + event.getContent(), DrugForm.class)).thenReturn(drugForm);

        drugFormEventWorker.process(event);

        Concept drugFormConcept = conceptService.getConceptByUuid(drugForm.getId());

        assertEquals(drugForm.getName(), drugFormConcept.getName().getName());
    }
}
