package org.bahmni.module.referencedatafeedclient.worker;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.domain.Department;
import org.bahmni.module.referencedatafeedclient.domain.Sample;
import org.bahmni.module.referencedatafeedclient.domain.Test;
import org.bahmni.module.referencedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.module.referencedatafeedclient.worker.util.FileReader;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
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
        testEventWorker = new TestEventWorker(httpClient, referenceDataFeedProperties, conceptService, referenceDataConceptService, new EventWorkerUtility(conceptService));
        when(referenceDataFeedProperties.getReferenceDataUri()).thenReturn(referenceDataUri);
        executeDataSet("testEventWorkerTestData.xml");
    }

    @org.junit.Test
    public void shouldCreateNewConceptForGivenTest() throws Exception {
        Event event = new Event("xxxx-yyyyy", "/reference-data/test/8471dbe5-0465-4eac-94ba-8f8708f3f529");
        Sample sample = new Sample("dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        Department department = new Department("e060cf44-3d3d-11e3-bf2b-0800271c1b75");
        Test test = new Test("59474920-8734-11e3-baa7-0800200c9a66", "Haemoglobin", "Haemoglobin Description", "Hb", "Numeric", sample, department, true);

        when(httpClient.get(referenceDataUri + event.getContent(), Test.class)).thenReturn(test);

        testEventWorker.process(event);

        Concept testConcept = conceptService.getConceptByUuid(test.getId());
        assertNotNull(testConcept);
        assertEquals(2, testConcept.getNames().size());
        assertEquals(test.getName(), testConcept.getFullySpecifiedName(Locale.ENGLISH).getName());
        assertEquals(test.getShortName(), testConcept.getShortNameInLocale(Locale.ENGLISH).getName());
        assertEquals(1, testConcept.getDescriptions().size());
        assertEquals(test.getDescription(), testConcept.getDescription().getDescription());
        assertEquals(false, testConcept.isRetired());
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
        Test test = new Test("4923d0e0-8734-11e3-baa7-0800200c9a66", "Blood Group Updated", "Blood Group Description updated", null, "NNNN", sample, department, false);
        when(httpClient.get(referenceDataUri+event.getContent(), Test.class)).thenReturn(test);

        testEventWorker.process(event);

        Concept testConcept = conceptService.getConceptByUuid(test.getId());
        assertNotNull(testConcept);
        assertEquals(1, testConcept.getNames().size());
        assertEquals(test.getName(), testConcept.getFullySpecifiedName(Locale.ENGLISH).getName());
        assertEquals(test.getShortName(), testConcept.getShortNameInLocale(Locale.ENGLISH));
        assertEquals(1, testConcept.getDescriptions().size());
        assertEquals(test.getDescription(), testConcept.getDescription().getDescription());
        assertEquals(ConceptDatatype.TEXT_UUID, testConcept.getDatatype().getUuid());
        assertEquals(TestEventWorker.TEST, testConcept.getConceptClass().getName());
        assertEquals(true, testConcept.isRetired());
        Concept sampleConcept = conceptService.getConceptByUuid(sample.getId());
        assertFalse(sampleConcept.getSetMembers().contains(testConcept));
        Concept departmentConcept = conceptService.getConceptByUuid(department.getId());
        assertFalse(departmentConcept.getSetMembers().contains(testConcept));
    }


    @org.junit.Test
    public void updating_sample_for_test_moves_the_test_from_oldsample_to_newsample() throws Exception {
        Sample oldBloodSample = new Sample("dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        Department bioChemistry = new Department("e060cf44-3d3d-11e3-bf2b-0800271c1b76");
        Test test = new Test("4923d0e0-8734-11e3-baa7-0800200c9a66", "Blood Group Updated", "Blood Group Description updated", null, "NNNN", oldBloodSample, bioChemistry, true);

        Event testEvent = new Event("xxxx-yyyyy-1", "/reference-data/test/59474920-8734-11e3-baa7-0800200c9a66");
        when(httpClient.get(referenceDataUri + testEvent.getContent(), Test.class)).thenReturn(test);
        testEventWorker.process(testEvent);

        Sample newUrineSample = new Sample("788ac8c0-8716-11e3-baa7-0800200c9a66");
        test.setSample(newUrineSample);

        Event routineBloodUpdatedEvent = new Event("xxxx-yyyyy-2", "/reference-data/test/59474920-8734-11e3-baa7-0800200c9a66");
        when(httpClient.get(referenceDataUri + routineBloodUpdatedEvent.getContent(), Test.class)).thenReturn(test);
        testEventWorker.process(routineBloodUpdatedEvent);

        Concept bloodSampleConcept = conceptService.getConceptByUuid(oldBloodSample.getId());
        Concept bloodGroupTest = conceptService.getConceptByUuid(test.getId());
        assertFalse("Older sample should not contain the test", bloodSampleConcept.getSetMembers().contains(bloodGroupTest));

        Concept newUrineConcept = conceptService.getConceptByUuid(newUrineSample.getId());
        assertTrue("New Sample should contain the test", newUrineConcept.getSetMembers().contains(bloodGroupTest));
    }

    @org.junit.Test
    public void updating_department_for_test_moves_the_test_from_old_department_to_new_department() throws Exception {
        Sample bloodSample = new Sample("dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        Department bioChemistry = new Department("e060cf44-3d3d-11e3-bf2b-0800271c1b76");
        Test test = new Test("4923d0e0-8734-11e3-baa7-0800200c9a66", "Blood Group Updated", "Blood Group Description updated", null, "NNNN", bloodSample, bioChemistry, true);

        Event testEvent = new Event("xxxx-yyyyy-1", "/reference-data/test/59474920-8734-11e3-baa7-0800200c9a66");
        when(httpClient.get(referenceDataUri + testEvent.getContent(), Test.class)).thenReturn(test);
        testEventWorker.process(testEvent);

        Department microBiology = new Department("e060cf44-3d3d-11e3-bf2b-0800271c1b77");
        test.setDepartment(microBiology);

        Event updatedTestEvent = new Event("xxxx-yyyyy-2", "/reference-data/test/59474920-8734-11e3-baa7-0800200c9a66");
        when(httpClient.get(referenceDataUri + updatedTestEvent.getContent(), Test.class)).thenReturn(test);
        testEventWorker.process(updatedTestEvent);

        Concept bioChemistryDepartment = conceptService.getConceptByUuid(bioChemistry.getId());
        Concept bloodGroupTest = conceptService.getConceptByUuid(test.getId());
        assertFalse("Older Department should not contain the test", bioChemistryDepartment.getSetMembers().contains(bloodGroupTest));

        Concept microBiologyDepartment = conceptService.getConceptByUuid(microBiology.getId());
        assertTrue("New Department should contain the test", microBiologyDepartment.getSetMembers().contains(bloodGroupTest));
    }

    @org.junit.Test
    public void remove_the_test_from_panel_when_test_is_inactivated() throws Exception {
        Sample sample = new Sample("dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        Department department = new Department("e060cf44-3d3d-11e3-bf2b-0800271c1b77");
        Test test = new Test("5923d0e0-8734-11e3-baa7-0800200c9a66");
        test.setIsActive(false);
        test.setSample(sample);
        test.setDepartment(department);
        test.setResultType("Numeric");
        test.setName("Test");

        Event updateTestEvent = new Event("xxxx-yyyyy-2", "/reference-data/test/5923d0e0-8734-11e3-baa7-0800200c9a66");
        when(httpClient.get(referenceDataUri + updateTestEvent.getContent(), Test.class)).thenReturn(test);
        testEventWorker.process(updateTestEvent);

        Concept testConcept = conceptService.getConceptByUuid("5923d0e0-8734-11e3-baa7-0800200c9a66");
        Assert.assertTrue(testConcept.isRetired());

        Concept panelConcept = conceptService.getConceptByUuid("e5e25a7d-b3b2-40f4-9081-d440a7f98b77");
        Assert.assertFalse(panelConcept.getSetMembers().contains(testConcept));
    }
}
