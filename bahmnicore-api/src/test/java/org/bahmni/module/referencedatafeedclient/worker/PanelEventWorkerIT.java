package org.bahmni.module.referencedatafeedclient.worker;

import org.bahmni.module.referencedatafeedclient.ReferenceDataFeedProperties;
import org.bahmni.module.referencedatafeedclient.domain.Panel;
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

import javax.validation.constraints.AssertFalse;
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
        panelEventWorker = new PanelEventWorker(httpClient, referenceDataFeedProperties, conceptService, referenceDataConceptService, new EventWorkerUtility(conceptService));
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
        Panel panel = new Panel("59474920-8734-11e3-baa7-0800200c9a66", "Routine Blood", "Routine Blood Description", "RB", true, sample, tests);
        when(httpClient.get(referenceDataUri + event.getContent(), Panel.class)).thenReturn(panel);

        panelEventWorker.process(event);

        Concept panelConcept = conceptService.getConceptByUuid(panel.getId());
        assertNotNull(panelConcept);
        assertEquals(2, panelConcept.getNames().size());
        assertEquals(panel.getName(), panelConcept.getFullySpecifiedName(Locale.ENGLISH).getName());
        assertEquals(panel.getShortName(), panelConcept.getShortNameInLocale(Locale.ENGLISH).getName());
        assertEquals(1, panelConcept.getDescriptions().size());
        assertEquals(panel.getDescription(), panelConcept.getDescription().getDescription());
        assertEquals(false, panelConcept.isRetired());
        assertEquals(ConceptDatatype.N_A_UUID, panelConcept.getDatatype().getUuid());
        assertEquals(PanelEventWorker.LAB_SET, panelConcept.getConceptClass().getName());
        assertEquals(true, panelConcept.isSet());
        Concept sampleConcept = conceptService.getConceptByUuid(sample.getId());
        assertTrue(sampleConcept.getSetMembers().contains(panelConcept));
        assertEquals(2, panelConcept.getSetMembers().size());
        assertTrue(panelConcept.getSetMembers().contains(conceptService.getConceptByUuid(test1.getId())));
        assertTrue(panelConcept.getSetMembers().contains(conceptService.getConceptByUuid(test2.getId())));
    }

    @org.junit.Test
    public void updating_sample_for_panel_moves_the_panel_from_oldsample_to_newsample() throws Exception {
        Sample oldBloodSample = new Sample("dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        HashSet<Test> routineBloodTests = new HashSet<>(Arrays.asList(new Test("5923d0e0-8734-11e3-baa7-0800200c9a66"), new Test("7923d0e0-8734-11e3-baa7-0800200c9a66")));
        Panel routineBloodPanel = new Panel("59474920-8734-11e3-baa7-0800200c9a66", "Routine Blood", "Routine Blood Description", "RB", true, oldBloodSample, routineBloodTests);
        Event routineBloodPanelCreationEvent = new Event("xxxx-yyyyy-1", "/reference-data/panel/59474920-8734-11e3-baa7-0800200c9a66");
        when(httpClient.get(referenceDataUri + routineBloodPanelCreationEvent.getContent(), Panel.class)).thenReturn(routineBloodPanel);
        panelEventWorker.process(routineBloodPanelCreationEvent);

        Sample newUrineSample = new Sample("788ac8c0-8716-11e3-baa7-0800200c9a66");
        routineBloodPanel.setSample(newUrineSample);
        Event routineBloodPanelUpdatedEvent = new Event("xxxx-yyyyy-2", "/reference-data/panel/59474920-8734-11e3-baa7-0800200c9a66");
        when(httpClient.get(referenceDataUri + routineBloodPanelUpdatedEvent.getContent(), Panel.class)).thenReturn(routineBloodPanel);
        panelEventWorker.process(routineBloodPanelUpdatedEvent);

        Concept bloodSampleConcept = conceptService.getConceptByUuid(oldBloodSample.getId());
        Concept routineBloodPanelConcept = conceptService.getConceptByUuid(routineBloodPanel.getId());
        assertFalse("Older sample should not contain the panel", bloodSampleConcept.getSetMembers().contains(routineBloodPanelConcept));

        Concept newUrineConcept = conceptService.getConceptByUuid(newUrineSample.getId());
        assertTrue("New Sample should contain the panel", newUrineConcept.getSetMembers().contains(routineBloodPanelConcept));
    }

    @org.junit.Test
    public void retire_the_panel_when_isActive_is_false() throws Exception {
        String fileContents = new FileReader("inActivePanelEventFeedData.json").readFile();
        Panel panel = ObjectMapperRepository.objectMapper.readValue(fileContents, Panel.class);
        Assert.assertFalse("panel is not active", panel.getIsActive());

        Event panelEvent = new Event("xxxx-yyyyy-2", "/reference-data/panel/dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        when(httpClient.get(referenceDataUri + panelEvent.getContent(), Panel.class)).thenReturn(panel);
        panelEventWorker.process(panelEvent);

        Concept panelConcept = conceptService.getConceptByUuid(panel.getId());
        Concept sampleConcept = conceptService.getConceptByUuid(panel.getSample().getId());

        Assert.assertFalse(sampleConcept.getSetMembers().contains(panelConcept));
    }

    @org.junit.Test
    public void not_retire_the_panel_when_isActive_is_true() throws Exception {
        String fileContents = new FileReader("activePanelEventFeedData.json").readFile();
        Panel panel = ObjectMapperRepository.objectMapper.readValue(fileContents, Panel.class);
        Assert.assertTrue("panel is not active", panel.getIsActive());

        Event updatedSampleEvent = new Event("xxxx-yyyyy-2", "/reference-data/panel/dc8ac8c0-8716-11e3-baa7-0800200c9a66");
        when(httpClient.get(referenceDataUri + updatedSampleEvent.getContent(), Panel.class)).thenReturn(panel);
        panelEventWorker.process(updatedSampleEvent);

        Concept panelConcept = conceptService.getConcept(panel.getName());
        Assert.assertNotNull(panelConcept);

        Concept sampleConcept = conceptService.getConceptByUuid(panel.getSample().getId());
        Assert.assertTrue(sampleConcept.getSetMembers().contains(panelConcept));
    }

}
