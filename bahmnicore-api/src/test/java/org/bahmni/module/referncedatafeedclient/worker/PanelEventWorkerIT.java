package org.bahmni.module.referncedatafeedclient.worker;

import org.bahmni.module.referncedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referncedatafeedclient.domain.Panel;
import org.bahmni.module.referncedatafeedclient.domain.Sample;
import org.bahmni.module.referncedatafeedclient.domain.Test;
import org.bahmni.module.referncedatafeedclient.service.ReferenceDataConceptService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class PanelEventWorkerIT extends BaseModuleWebContextSensitiveTest {
    @Mock
    private HttpClient httpClient;
    @Mock
    private ReferenceDataFeedProperties referenceDataFeedProperties;
    private final String referenceDataUri = "http://localhost";

    @Autowired
    private ConceptService conceptService;
    @Autowired
    private ReferenceDataConceptService referenceDataConceptService;
    private PanelEventWorker panelEventWorker;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        panelEventWorker = new PanelEventWorker(httpClient, referenceDataFeedProperties, conceptService, referenceDataConceptService);
        when(referenceDataFeedProperties.getReferenceDataUri()).thenReturn(referenceDataUri);
        executeDataSet("panelEventWorkerTestData.xml");
    }

    @org.junit.Test
    public void shouldCreateNewConceptForGivenPanel() throws Exception {
        Event event = new Event("xxxx-yyyyy", "/reference-data/panel/8471dbe5-0465-4eac-94ba-8f8708f3f529");
        Sample sample = new Sample("dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        Test test1 = new Test("5923d0e0-8734-11e3-baa7-0800200c9a66");
        Test test2 = new Test("7923d0e0-8734-11e3-baa7-0800200c9a66");
        HashSet<Test> tests = new HashSet<>(Arrays.asList(test1, test2));
        Panel panel = new Panel("59474920-8734-11e3-baa7-0800200c9a66", "Routine Blood", "Routine Blood Description", "RB", sample, tests);
        when(httpClient.get(referenceDataUri + event.getContent(), Panel.class)).thenReturn(panel);

        panelEventWorker.process(event);

        Concept panelConcept = conceptService.getConceptByUuid(panel.getId());
        assertNotNull(panelConcept);
        assertEquals(2, panelConcept.getNames().size());
        assertEquals(panel.getName(), panelConcept.getFullySpecifiedName(Locale.ENGLISH).getName());
        assertEquals(panel.getShortName(), panelConcept.getShortNameInLocale(Locale.ENGLISH).getName());
        assertEquals(1, panelConcept.getDescriptions().size());
        assertEquals(panel.getDescription(), panelConcept.getDescription().getDescription());
        assertEquals(ConceptDatatype.N_A_UUID, panelConcept.getDatatype().getUuid());
        assertEquals(PanelEventWorker.LAB_SET, panelConcept.getConceptClass().getName());
        Concept sampleConcept = conceptService.getConceptByUuid(sample.getId());
        assertTrue(sampleConcept.getSetMembers().contains(panelConcept));
        assertEquals(2, panelConcept.getSetMembers().size());
        assertTrue(panelConcept.getSetMembers().contains(conceptService.getConceptByUuid(test1.getId())));
        assertTrue(panelConcept.getSetMembers().contains(conceptService.getConceptByUuid(test2.getId())));
    }

    @org.junit.Test
    public void shouldUpdateConceptForGivenPanel() throws Exception {
        Event event = new Event("xxxx-yyyyy", "/reference-data/panel/4923d0e0-8734-11e3-baa7-0800200c9a66");
        Sample sample = new Sample("dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        Test test1 = new Test("6923d0e0-8734-11e3-baa7-0800200c9a66");
        Test test2 = new Test("7923d0e0-8734-11e3-baa7-0800200c9a66");
        HashSet<Test> tests = new HashSet<>(Arrays.asList(test1, test2));
        Panel panel = new Panel("4923d0e0-8734-11e3-baa7-0800200c9a66", "Anaemia Panel Updated", "Anaemia Panel Description updated", "AP(U)", sample, tests);
        when(httpClient.get(referenceDataUri+event.getContent(), Panel.class)).thenReturn(panel);
        assertEquals(2, conceptService.getConceptByUuid(panel.getId()).getSetMembers().size());

        panelEventWorker.process(event);

        Concept panelConcept = conceptService.getConceptByUuid(panel.getId());
        assertNotNull(panelConcept);
        assertEquals(2, panelConcept.getNames().size());
        assertEquals(panel.getName(), panelConcept.getFullySpecifiedName(Locale.ENGLISH).getName());
        assertEquals(panel.getShortName(), panelConcept.getShortNameInLocale(Locale.ENGLISH).getName());
        assertEquals(1, panelConcept.getDescriptions().size());
        assertEquals(panel.getDescription(), panelConcept.getDescription().getDescription());
        assertEquals(ConceptDatatype.N_A_UUID, panelConcept.getDatatype().getUuid());
        assertEquals(PanelEventWorker.LAB_SET, panelConcept.getConceptClass().getName());
        Concept sampleConcept = conceptService.getConceptByUuid(sample.getId());
        assertTrue(sampleConcept.getSetMembers().contains(panelConcept));
        assertEquals(2, panelConcept.getConceptSets().size());
        assertEquals(2, panelConcept.getSetMembers().size());
        assertTrue(panelConcept.getSetMembers().contains(conceptService.getConceptByUuid(test1.getId())));
        assertTrue(panelConcept.getSetMembers().contains(conceptService.getConceptByUuid(test2.getId())));
    }
}
