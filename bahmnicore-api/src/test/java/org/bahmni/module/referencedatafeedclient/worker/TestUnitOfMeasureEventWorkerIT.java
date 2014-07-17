package org.bahmni.module.referencedatafeedclient.worker;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.dao.BahmniTestUnitsDao;
import org.bahmni.module.referencedatafeedclient.domain.TestUnitOfMeasure;
import org.bahmni.module.referencedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class TestUnitOfMeasureEventWorkerIT extends BaseModuleWebContextSensitiveTest {
    @Mock
    private HttpClient httpClient;
    @Mock
    private ReferenceDataFeedProperties referenceDataFeedProperties;
    private final String referenceDataUri = "http://localhost";

    @Autowired
    private ConceptService conceptService;
    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;
    @Autowired
    private EventWorkerUtility eventWorkerUtility;
    @Autowired
    private BahmniTestUnitsDao bahmniTestUnitsDao;

    private TestUnitOfMeasureEventWorker testUnitOfMeasureEventWorker;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        testUnitOfMeasureEventWorker = new TestUnitOfMeasureEventWorker(httpClient, referenceDataFeedProperties, conceptService, referenceDataConceptService, new EventWorkerUtility(conceptService), bahmniTestUnitsDao);
        when(referenceDataFeedProperties.getReferenceDataUri()).thenReturn(referenceDataUri);
        executeDataSet("testUnitOfMeasureEventWorkerTestData.xml");
    }
//
//    @Override
//    public Properties getRuntimeProperties() {
//        Properties props = super.getRuntimeProperties();
//        props.setProperty("hibernate.show_sql", "true");
//        props.setProperty("hibernate.format_sql", "true");
//        return props;
//    }

    @org.junit.Test
    @Ignore
    public void shouldCreateNewConceptForGivenTest() throws Exception {
        TestUnitOfMeasure testUnitOfMeasure = new TestUnitOfMeasure("5463d0e4-8254-12e3-baa7-0830200e9a66");
        testUnitOfMeasure.setName("mg/dl");

        Event testUnitOfMeasureEvent = new Event("xxxx-yyyyy-2", "/reference-data/test_unit_of_measure/5463d0e4-8254-12e3-baa7-0830200e9a66");
        when(httpClient.get(referenceDataUri + testUnitOfMeasureEvent.getContent(), TestUnitOfMeasure.class)).thenReturn(testUnitOfMeasure);
        testUnitOfMeasureEventWorker.process(testUnitOfMeasureEvent);

        Concept testUnitOfMeasureConcept = conceptService.getConceptByUuid("5463d0e4-8254-12e3-baa7-0830200e9a66");
        Assert.assertEquals("mg/dl", testUnitOfMeasureConcept.getName().getName());
        Assert.assertEquals("mg/dl", testUnitOfMeasureConcept.getDescription().getDescription());
        Assert.assertFalse(testUnitOfMeasureConcept.isRetired());
    }

    @org.junit.Test
    @Ignore
    public void shouldUpdateConceptIfAlreadyPresent() throws Exception {
        TestUnitOfMeasure testUnitOfMeasure = new TestUnitOfMeasure("7463d0e4-8254-12e3-baa7-0830200e9a67");
        testUnitOfMeasure.setName("mg/dl");

        Concept testUnitOfMeasureConcept = conceptService.getConceptByUuid("7463d0e4-8254-12e3-baa7-0830200e9a67");
        Assert.assertEquals("mg", testUnitOfMeasureConcept.getName().getName());

        Event testUnitOfMeasureEvent = new Event("xxxx-yyyyy-2", "/reference-data/test_unit_of_measure/7463d0e4-8254-12e3-baa7-0830200e9a67");
        when(httpClient.get(referenceDataUri + testUnitOfMeasureEvent.getContent(), TestUnitOfMeasure.class)).thenReturn(testUnitOfMeasure);
        testUnitOfMeasureEventWorker.process(testUnitOfMeasureEvent);

        testUnitOfMeasureConcept = conceptService.getConceptByUuid("7463d0e4-8254-12e3-baa7-0830200e9a67");
        Assert.assertEquals("mg/dl", testUnitOfMeasureConcept.getName().getName());
        Assert.assertEquals("mg/dl", testUnitOfMeasureConcept.getDescription().getDescription());
        Assert.assertFalse(testUnitOfMeasureConcept.isRetired());
    }

    @org.junit.Test
    @Ignore
    public void shouldUpdateConceptAndAllTestWithUnitIfAlreadyPresent() throws Exception {
        Concept testUnitOfMeasureConcept = conceptService.getConceptByUuid("7463d0e4-8254-12e3-baa7-0830200e9a67");
        ConceptNumeric testConcept = (ConceptNumeric)conceptService.getConcept(105);
        Assert.assertEquals("mg", testUnitOfMeasureConcept.getName().getName());
        Assert.assertEquals("mg", testConcept.getUnits());

        TestUnitOfMeasure testUnitOfMeasure = new TestUnitOfMeasure("7463d0e4-8254-12e3-baa7-0830200e9a67");
        testUnitOfMeasure.setName("mg/dl");

        Event testUnitOfMeasureEvent = new Event("xxxx-yyyyy-2", "/reference-data/test_unit_of_measure/7463d0e4-8254-12e3-baa7-0830200e9a67");
        when(httpClient.get(referenceDataUri + testUnitOfMeasureEvent.getContent(), TestUnitOfMeasure.class)).thenReturn(testUnitOfMeasure);
        testUnitOfMeasureEventWorker.process(testUnitOfMeasureEvent);

        testUnitOfMeasureConcept = conceptService.getConceptByUuid("7463d0e4-8254-12e3-baa7-0830200e9a67");
        testConcept = (ConceptNumeric)conceptService.getConcept(105);
        Assert.assertEquals("mg/dl", testConcept.getUnits());
        Assert.assertFalse(testUnitOfMeasureConcept.isRetired());
    }

    @org.junit.Test
    @Ignore
    public void shouldSetTestUnitsToNullIfTUOMIsRetiered() throws Exception {
        Concept testUnitOfMeasureConcept = conceptService.getConceptByUuid("7463d0e4-8254-12e3-baa7-0830200e9a67");
        ConceptNumeric testConcept = (ConceptNumeric)conceptService.getConcept(105);
        Assert.assertEquals("mg", testUnitOfMeasureConcept.getName().getName());
        Assert.assertEquals("mg", testConcept.getUnits());

        TestUnitOfMeasure testUnitOfMeasure = new TestUnitOfMeasure("7463d0e4-8254-12e3-baa7-0830200e9a67");
        testUnitOfMeasure.setName("mg");
        testUnitOfMeasure.setIsActive(false);

        Event testUnitOfMeasureEvent = new Event("xxxx-yyyyy-2", "/reference-data/test_unit_of_measure/7463d0e4-8254-12e3-baa7-0830200e9a67");
        when(httpClient.get(referenceDataUri + testUnitOfMeasureEvent.getContent(), TestUnitOfMeasure.class)).thenReturn(testUnitOfMeasure);
        testUnitOfMeasureEventWorker.process(testUnitOfMeasureEvent);

        testUnitOfMeasureConcept = conceptService.getConceptByUuid("7463d0e4-8254-12e3-baa7-0830200e9a67");
        testConcept = (ConceptNumeric)conceptService.getConcept(105);
        Assert.assertEquals(null, testConcept.getUnits());
        Assert.assertTrue(testUnitOfMeasureConcept.isRetired());
    }
}