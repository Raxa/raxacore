package org.bahmni.module.referencedatafeedclient.worker;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.domain.Sample;
import org.bahmni.module.referencedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class SampleEventWorkerIT extends BaseModuleWebContextSensitiveTest {
    @Mock
    private HttpClient httpClient;
    @Mock
    private ReferenceDataFeedProperties referenceDataFeedProperties;
    private final String referenceDataUri = "http://localhost";

    @Autowired
    private ConceptService conceptService;
    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;
    private SampleEventWorker sampleEventWorker;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        sampleEventWorker = new SampleEventWorker(httpClient, referenceDataFeedProperties, conceptService, referenceDataConceptService, new EventWorkerUtility());
        when(referenceDataFeedProperties.getReferenceDataUri()).thenReturn(referenceDataUri);
        executeDataSet("sampleEventWorkerTestData.xml");
    }

    @Test
    public void shouldCreateNewConceptForGivenSample() throws Exception {
        Event event = new Event("xxxx-yyyyy", "/reference-data/sample/8471dbe5-0465-4eac-94ba-8f8708f3f529");
        Sample sample = new Sample("8471dbe5-0465-4eac-94ba-8f8708f3f529", "Urine Microscopy", "Urine Microscopy Sample Description", true);
        when(httpClient.get(referenceDataUri + event.getContent(), Sample.class)).thenReturn(sample);

        sampleEventWorker.process(event);

        Concept sampleConcept = conceptService.getConceptByUuid(sample.getId());
        assertNotNull(sampleConcept);
        assertEquals(2, sampleConcept.getNames().size());
        assertEquals(sample.getName(), sampleConcept.getName(Locale.ENGLISH).getName());
        assertEquals(sample.getShortName(), sampleConcept.getShortNameInLocale(Locale.ENGLISH).getName());
        assertEquals(ConceptDatatype.N_A_UUID, sampleConcept.getDatatype().getUuid());
        assertEquals(SampleEventWorker.LAB_SET, sampleConcept.getConceptClass().getName());
        assertEquals(true, sampleConcept.isSet());
        assertEquals(false, sampleConcept.isRetired());
        Concept labConcept = conceptService.getConceptByName(SampleEventWorker.LABORATORY);
        assertTrue(labConcept.getSetMembers().contains(sampleConcept));
    }

    @Test
    public void shouldUpdateConceptForGivenSample() throws Exception {
        Event event = new Event("xxxx-yyyyy", "/reference-data/sample/dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        Sample sample = new Sample("dc8ac8c0-8716-11e3-baa7-0800200c9a66", "Blood Sample Updated", null, false);
        when(httpClient.get(referenceDataUri+event.getContent(), Sample.class)).thenReturn(sample);

        sampleEventWorker.process(event);

        Concept sampleConcept = conceptService.getConceptByUuid(sample.getId());
        assertNotNull(sampleConcept);
        assertEquals(1, sampleConcept.getNames().size());
        assertEquals(sample.getName(), sampleConcept.getName(Locale.ENGLISH).getName());
        assertEquals(sample.getShortName(), sampleConcept.getShortNameInLocale(Locale.ENGLISH));
        assertEquals(ConceptDatatype.N_A_UUID, sampleConcept.getDatatype().getUuid());
        assertEquals(SampleEventWorker.LAB_SET, sampleConcept.getConceptClass().getName());
        assertEquals(true, sampleConcept.isSet());
        assertEquals(true, sampleConcept.isRetired());
        Concept labConcept = conceptService.getConceptByName(SampleEventWorker.LABORATORY);
        assertTrue(labConcept.getSetMembers().contains(sampleConcept));
    }

    @org.junit.Test
    public void updating_sample_name_keeps_the_test_in_the_same_sample() throws Exception {
        Sample bloodSample = new Sample("dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        bloodSample.setName("newBlood");

        Event updatedSampleEvent = new Event("xxxx-yyyyy-2", "/reference-data/sample/dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        when(httpClient.get(referenceDataUri + updatedSampleEvent.getContent(), Sample.class)).thenReturn(bloodSample);
        sampleEventWorker.process(updatedSampleEvent);

        Concept bloodSampleConcept = conceptService.getConceptByUuid(bloodSample.getId());
        Concept testConcept = conceptService.getConceptByUuid("e060cf44-3d3d-11e3-bf2b-0800271c1b77");
        assertTrue("Sample should contain the test", bloodSampleConcept.getSetMembers().contains(testConcept));
    }

}
