package org.bahmni.module.elisatomfeedclient.api.worker;

import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisAccessionBuilder;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisTestDetailBuilder;
import org.bahmni.module.elisatomfeedclient.api.client.impl.HealthCenterFilterRule;
import org.bahmni.module.elisatomfeedclient.api.domain.AccessionDiff;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionHelper;
import org.bahmni.webclients.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.ict4h.atomfeed.client.domain.Event;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenElisAccessionEventWorkerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
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
    private HealthCenterFilterRule healthCenterFilterRule;

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
                conceptService, accessionMapper, providerService, orderService, visitService, healthCenterFilterRule);
        openElisUrl = "http://localhost:8080";
        event = new Event("id", "/openelis/accession/12-34-56-78", "title", "feedUri");
        when(feedProperties.getOpenElisUri()).thenReturn(openElisUrl);
        when(healthCenterFilterRule.passesWith("GAN")).thenReturn(true);
        when(healthCenterFilterRule.passesWith("ANC")).thenReturn(false);
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

        accessionEventWorker.process(event);

        verify(encounterService).saveEncounter(encounter);
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

        accessionEventWorker.process(event);

        verify(encounterService, times(2)).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(encounterService).saveEncounter(previousEncounter);
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

        accessionEventWorker.process(event);

        verify(encounterService, times(2)).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(accessionMapper, never()).mapToNewEncounter(any(OpenElisAccession.class), any(String.class));
        verify(accessionMapper).addOrVoidOrderDifferences(any(OpenElisAccession.class), any(AccessionDiff.class), any(Encounter.class));
        verify(encounterService).saveEncounter(previousEncounter);
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


    @Test
    public void shouldNotProcessPatientsThatDoNotGoThroughTheFilter() throws Exception {
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withPatientIdentifier("ANC12345").build();
        stubAccession(openElisAccession);

        accessionEventWorker.process(event);
    }

    private Encounter createEncounterWithResults(Visit visit, EncounterType labEncounterType, EncounterRole encounterRole, OpenElisTestDetail test1) {
        Encounter encounter = new Encounter();
        Obs obs = createTestObs(test1);
        encounter.addObs(obs);
        encounter.setEncounterType(labEncounterType);
        visit.addEncounter(encounter);
        return encounter;
    }

    private Obs createTestObs(OpenElisTestDetail test1) {
        Concept concept = new Concept();
        concept.setUuid(test1.getTestUuid());
        Obs obs = new Obs();
        obs.setConcept(concept);
        obs.setValueText(test1.getResult());
        obs.setObsDatetime(DateTime.parse(test1.getDateTime()).toDate());
        return obs;
    }

    private Obs createPanelObsGroup(String panelUuid, OpenElisTestDetail... test) {
        Obs parentObs = new Obs();
        Concept concept = new Concept();
        concept.setUuid(panelUuid);
        parentObs.setConcept(concept);

        for (OpenElisTestDetail openElisTestDetail : test) {
            Obs testObs = createTestObs(openElisTestDetail);
            parentObs.addGroupMember(testObs);
        }
        return parentObs;
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
