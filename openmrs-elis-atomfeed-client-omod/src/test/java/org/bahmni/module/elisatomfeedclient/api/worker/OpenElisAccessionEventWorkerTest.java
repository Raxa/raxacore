package org.bahmni.module.elisatomfeedclient.api.worker;

import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisAccessionBuilder;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisTestDetailBuilder;
import org.bahmni.module.elisatomfeedclient.api.domain.AccessionDiff;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionHelper;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.TestOrder;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.impl.BahmniVisitAttributeSaveCommandImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenElisAccessionEventWorkerTest {
    @Mock
    private HttpClient httpClient;
    @Mock
    private EncounterService encounterService;
    @Mock
    private AccessionHelper accessionMapper;
    @Mock
    private ElisAtomFeedProperties feedProperties;
    @Mock
    private ConceptService conceptService;
    @Mock
    private ProviderService providerService;
    @Mock
    private BahmniVisitAttributeSaveCommandImpl bahmniVisitAttributeSaveCommand;

    private OpenElisAccessionEventWorker accessionEventWorker;
    private String openElisUrl;
    private Event event;
    @Mock
    private OrderService orderService;
    @Mock
    private VisitService visitService;

    @Before
    public void setUp() {
        initMocks(this);
        accessionEventWorker = new OpenElisAccessionEventWorker(feedProperties, httpClient, encounterService,
                conceptService, accessionMapper, providerService, bahmniVisitAttributeSaveCommand);
        openElisUrl = "http://localhost:8080";
        event = new Event("id", "/openelis/accession/12-34-56-78", "title", "feedUri", new Date());
        when(feedProperties.getOpenElisUri()).thenReturn(openElisUrl);
    }

    @Test
    public void shouldSaveEncounterWhenEncounterForGivenAccessionDoesNotExists() throws Exception {
        final Encounter encounter = getEncounterWithTests("test1");
        final Visit visit = new Visit();
        visit.setId(1);
        encounter.setVisit(visit);
        visit.setEncounters(new HashSet<>(Arrays.asList(encounter)));
        final OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().build();
        stubAccession(openElisAccession);

        // first time when it calls it should return null as there is no encounter at that point
        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(null).thenReturn(encounter);
        when(accessionMapper.mapToNewEncounter(any(OpenElisAccession.class), any(String.class))).thenReturn(encounter);
        when(accessionMapper.findOrInitializeVisit(any(Patient.class), any(Date.class), any(String.class))).thenReturn(visit);
        when(encounterService.saveEncounter(encounter)).thenReturn(encounter);
        accessionEventWorker.process(event);

        verify(encounterService).saveEncounter(encounter);
        verify(bahmniVisitAttributeSaveCommand).save(encounter);
    }

    @Test
    public void shouldUpdateEncounterWhenAccessionHasNewOrder() throws Exception {
        Encounter previousEncounter = getEncounterWithTests("test1");
        Encounter encounterFromAccession = getEncounterWithTests("test1", "test2");
        final Visit visit = new Visit();
        visit.setId(1);
        previousEncounter.setVisit(visit);
        visit.setEncounters(new HashSet<>(Arrays.asList(previousEncounter)));
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).build();
        stubAccession(openElisAccession);
        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(previousEncounter);
        when(accessionMapper.addOrVoidOrderDifferences(any(OpenElisAccession.class), any(AccessionDiff.class), any(Encounter.class))).thenReturn(encounterFromAccession);
        when(accessionMapper.findOrInitializeVisit(any(Patient.class), any(Date.class), any(String.class))).thenReturn(visit);
        when(encounterService.saveEncounter(previousEncounter)).thenReturn(previousEncounter);
        accessionEventWorker.process(event);

        verify(encounterService, times(2)).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(encounterService).saveEncounter(previousEncounter);
        verify(bahmniVisitAttributeSaveCommand).save(previousEncounter);
    }

    @Test
    public void shouldUpdateEncounterWhenAccessionHasRemovedOrderFromPreviousEncounter() throws Exception {
        Encounter previousEncounter = getEncounterWithTests("test1", "test2", "test3");
        Encounter encounterFromAccession = getEncounterWithTests("test1", "test2", "test3");
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisTestDetail test3 = new OpenElisTestDetailBuilder().withTestUuid("test3").withStatus("Canceled").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2, test3))).build();
        stubAccession(openElisAccession);

        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(previousEncounter);
        AccessionDiff accessionDiff = new AccessionDiff();
        accessionDiff.addRemovedTestDetails(test3);
        when(accessionMapper.addOrVoidOrderDifferences(any(OpenElisAccession.class), any(AccessionDiff.class), any(Encounter.class))).thenReturn(encounterFromAccession);
        final Visit visit = new Visit();
        visit.setId(1);
        previousEncounter.setVisit(visit);
        visit.setEncounters(new HashSet<>(Arrays.asList(previousEncounter)));
        when(accessionMapper.findOrInitializeVisit(any(Patient.class), any(Date.class), any(String.class))).thenReturn(visit);
        when(encounterService.saveEncounter(previousEncounter)).thenReturn(previousEncounter);

        accessionEventWorker.process(event);

        verify(encounterService, times(2)).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(accessionMapper, never()).mapToNewEncounter(any(OpenElisAccession.class), any(String.class));
        verify(accessionMapper).addOrVoidOrderDifferences(any(OpenElisAccession.class), any(AccessionDiff.class), any(Encounter.class));
        verify(encounterService).saveEncounter(previousEncounter);
        verify(bahmniVisitAttributeSaveCommand).save(previousEncounter);
    }

    @Test
    public void shouldNotUpdateEncounterWhenAccessionHasSameOrdersAsPreviousEncounter() throws Exception {
        Encounter previousEncounter = getEncounterWithTests("test1", "test2");
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).build();
        previousEncounter.setUuid(openElisAccession.getAccessionUuid());
        stubAccession(openElisAccession);
        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(previousEncounter);
        final Visit visit = new Visit();
        visit.setId(1);
        previousEncounter.setVisit(visit);
        visit.setEncounters(new HashSet<>(Arrays.asList(previousEncounter)));
        when(accessionMapper.findOrInitializeVisit(any(Patient.class), any(Date.class), any(String.class))).thenReturn(visit);

        accessionEventWorker.process(event);

        verify(encounterService, times(2)).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(encounterService, never()).saveEncounter(previousEncounter);
    }

    private Encounter getEncounterWithTests(String... testUuids) {
        Encounter encounter = new Encounter();
        for (String testUuid : testUuids) {
            TestOrder order = new TestOrder();
            Concept concept = new Concept();
            concept.setUuid(testUuid);
            order.setConcept(concept);
            encounter.addOrder(order);
            encounter.setEncounterType(new EncounterType());
        }
        return encounter;
    }

    private void stubAccession(OpenElisAccession accession) throws IOException {
        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(accession);
    }
}
