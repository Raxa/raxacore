package org.bahmni.module.elisatomfeedclient.api.worker;

import org.bahmni.module.elisatomfeedclient.api.ReferenceDataFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.domain.Department;
import org.bahmni.module.elisatomfeedclient.api.domain.Sample;
import org.bahmni.module.elisatomfeedclient.api.domain.Test;
import org.bahmni.module.elisatomfeedclient.api.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class TestEventWorkerIT extends BaseModuleWebContextSensitiveTest {
    @Mock
    private HttpClient httpClient;
    @Mock
    private ReferenceDataFeedProperties referenceDataFeedProperties;
    private final String referenceDataUri = "http://localhost";

    @Autowired
    private ConceptService conceptService;
    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;
    private TestEventWorker testEventWorker;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        testEventWorker = new TestEventWorker(httpClient, referenceDataFeedProperties, conceptService, referenceDataConceptService);
        when(referenceDataFeedProperties.getReferenceDataUri()).thenReturn(referenceDataUri);
        executeDataSet("testEventWorkerTestData.xml");
    }

    @org.junit.Test
    public void shouldCreateNewConceptForGivenTest() throws Exception {
        Event event = new Event("xxxx-yyyyy", "/reference-data/test/8471dbe5-0465-4eac-94ba-8f8708f3f529");
        Sample sample = new Sample("dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        Department department = new Department("e060cf44-3d3d-11e3-bf2b-0800271c1b75");
        Test test = new Test("59474920-8734-11e3-baa7-0800200c9a66", "Haemoglobin", "Haemoglobin Description", "Hb", "Numeric", sample, department);

        when(httpClient.get(referenceDataUri + event.getContent(), Test.class)).thenReturn(test);

        testEventWorker.process(event);

        Concept testConcept = conceptService.getConceptByUuid(test.getId());
        assertNotNull(testConcept);
        assertEquals(2, testConcept.getNames().size());
        assertEquals(test.getName(), testConcept.getFullySpecifiedName(Locale.ENGLISH).getName());
        assertEquals(test.getShortName(), testConcept.getShortNameInLocale(Locale.ENGLISH).getName());
        assertEquals(1, testConcept.getDescriptions().size());
        assertEquals(test.getDescription(), testConcept.getDescription().getDescription());
        assertEquals(ConceptDatatype.NUMERIC_UUID, testConcept.getDatatype().getUuid());
        assertEquals(TestEventWorker.TEST, testConcept.getConceptClass().getName());
        Concept sampleConcept = conceptService.getConceptByUuid(sample.getId());
        assertTrue(sampleConcept.getSetMembers().contains(testConcept));
        Concept departmentConcept = conceptService.getConceptByUuid(department.getId());
        assertTrue(departmentConcept.getSetMembers().contains(testConcept));
    }

    @org.junit.Test
    public void shouldUpdateConceptForGivenTest() throws Exception {
        Event event = new Event("xxxx-yyyyy", "/reference-data/test/4923d0e0-8734-11e3-baa7-0800200c9a66");
        Sample sample = new Sample("dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        Department department = new Department("e060cf44-3d3d-11e3-bf2b-0800271c1b75");
        Test test = new Test("4923d0e0-8734-11e3-baa7-0800200c9a66", "Blood Group Updated", "Blood Group Description updated", "BG(U)", "Text", sample, department);
        when(httpClient.get(referenceDataUri+event.getContent(), Test.class)).thenReturn(test);

        testEventWorker.process(event);

        Concept testConcept = conceptService.getConceptByUuid(test.getId());
        assertNotNull(testConcept);
        assertEquals(2, testConcept.getNames().size());
        assertEquals(test.getName(), testConcept.getFullySpecifiedName(Locale.ENGLISH).getName());
        assertEquals(test.getShortName(), testConcept.getShortNameInLocale(Locale.ENGLISH).getName());
        assertEquals(1, testConcept.getDescriptions().size());
        assertEquals(test.getDescription(), testConcept.getDescription().getDescription());
        assertEquals(ConceptDatatype.TEXT_UUID, testConcept.getDatatype().getUuid());
        assertEquals(TestEventWorker.TEST, testConcept.getConceptClass().getName());
        Concept sampleConcept = conceptService.getConceptByUuid(sample.getId());
        assertTrue(sampleConcept.getSetMembers().contains(testConcept));
        Concept departmentConcept = conceptService.getConceptByUuid(department.getId());
        assertTrue(departmentConcept.getSetMembers().contains(testConcept));
    }
}
