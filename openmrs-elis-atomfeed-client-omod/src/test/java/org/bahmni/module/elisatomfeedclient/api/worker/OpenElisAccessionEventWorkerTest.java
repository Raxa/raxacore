package org.bahmni.module.elisatomfeedclient.api.worker;

import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisAccessionBuilder;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisTestDetailBuilder;
import org.bahmni.module.elisatomfeedclient.api.domain.AccessionDiff;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;
import org.bahmni.module.elisatomfeedclient.api.mapper.AccessionMapper;
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
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.io.IOException;
import java.util.*;

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
    private AccessionMapper accessionMapper;
    @Mock
    private ElisAtomFeedProperties feedProperties;
    @Mock
    private EncounterTransactionMapper encounterTransactionMapper;
    @Mock
    private EmrEncounterService emrEncounterService;
    @Mock
    private VisitService visitService;
    @Mock
    private ConceptService conceptService;
    @Mock
    private ProviderService providerService;

    private OpenElisAccessionEventWorker accessionEventWorker;
    private String openElisUrl;
    private Event event;

    @Before
    public void setUp() {
        initMocks(this);
        accessionEventWorker = new OpenElisAccessionEventWorker(feedProperties, httpClient, encounterService, emrEncounterService, conceptService, accessionMapper, encounterTransactionMapper, visitService, providerService);
        openElisUrl = "http://localhost:8080";
        event = new Event("id", "/openelis/accession/12-34-56-78", "title", "feedUri");
        when(feedProperties.getOpenElisUri()).thenReturn(openElisUrl);
    }

    @Test
    public void shouldSaveEncounterWhenEncounterForGivenAccessionDoesNotExists() throws Exception {
        final Encounter encounter = getEncounterWithTests("test1");
        final OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().build();
        stubAccession(openElisAccession);
        EncounterTransaction encounterTransaction = new EncounterTransaction();

        // first time when it calls it should return null as there is no encounter at that point
        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(null).thenReturn(encounter);
        when(accessionMapper.mapToNewEncounter(any(OpenElisAccession.class))).thenReturn(encounter);
        when(encounterTransactionMapper.map(encounter, true)).thenReturn(encounterTransaction);


//        final EncounterTransaction[] et = {null};
//        final OngoingStubbing<EncounterTransaction> savedEncounterTransaction = when(emrEncounterService.save(any(EncounterTransaction.class))).then(new Answer<EncounterTransaction>() {
//            @Override
//            public EncounterTransaction answer(InvocationOnMock invocationOnMock) throws Throwable {
//                et[0] = new EncounterTransaction();
//                return et[0];
//            }
//        });
//
//        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenAnswer(new Answer<Encounter>() {
//            @Override
//            public Encounter answer(InvocationOnMock invocationOnMock) throws Throwable {
//                if ((et.length > 0) && et[0] != null) {
//                    return encounter;
//                } else {
//                    return null;
//                }
//
//            }
//        });

        accessionEventWorker.process(event);

        verify(emrEncounterService).save(encounterTransaction);
    }

    @Test
    public void shouldUpdateEncounterWhenAccessionHasNewOrder() throws Exception {
        Encounter previousEncounter = getEncounterWithTests("test1");
        Encounter encounterFromAccession = getEncounterWithTests("test1", "test2");
        EncounterTransaction encounterTransaction = new EncounterTransaction();
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).build();
        stubAccession(openElisAccession);
        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(previousEncounter);
        when(accessionMapper.mapToExistingEncounter(any(OpenElisAccession.class), any(AccessionDiff.class), any(Encounter.class))).thenReturn(encounterFromAccession);
        when(encounterTransactionMapper.map(encounterFromAccession, true)).thenReturn(encounterTransaction);

        accessionEventWorker.process(event);

        verify(encounterService, times(2)).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(emrEncounterService).save(encounterTransaction);
    }

    @Test
    public void shouldUpdateEncounterWhenAccessionHasRemovedOrderFromPreviousEncounter() throws Exception {
        Encounter previousEncounter = getEncounterWithTests("test1", "test2", "test3");
        Encounter encounterFromAccession = getEncounterWithTests("test1", "test2", "test3");
        EncounterTransaction encounterTransaction = new EncounterTransaction();
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisTestDetail test3 = new OpenElisTestDetailBuilder().withTestUuid("test3").withStatus("Canceled").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2, test3))).build();
        stubAccession(openElisAccession);

        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(previousEncounter);
        AccessionDiff accessionDiff = new AccessionDiff();
        accessionDiff.addRemovedTestDetails(test3);
        when(accessionMapper.mapToExistingEncounter(any(OpenElisAccession.class), any(AccessionDiff.class), any(Encounter.class))).thenReturn(encounterFromAccession);
        when(encounterTransactionMapper.map(encounterFromAccession, true)).thenReturn(encounterTransaction);

        accessionEventWorker.process(event);

        verify(encounterService, times(2)).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(accessionMapper, never()).mapToNewEncounter(any(OpenElisAccession.class));
        verify(accessionMapper).mapToExistingEncounter(any(OpenElisAccession.class), any(AccessionDiff.class), any(Encounter.class));
        verify(emrEncounterService).save(encounterTransaction);
    }

    @Test
    public void shouldNotUpdateEncounterWhenAccessionHasSameOrdersAsPreviousEncounter() throws Exception {
        Encounter previousEncounter = getEncounterWithTests("test1", "test2");
        Encounter encounterFromAccession = getEncounterWithTests("test1", "test2");
        EncounterTransaction encounterTransaction = new EncounterTransaction();
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).build();
        previousEncounter.setUuid(openElisAccession.getAccessionUuid());
        stubAccession(openElisAccession);
        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(previousEncounter);
        when(encounterTransactionMapper.map(previousEncounter, true)).thenReturn(encounterTransaction);

        accessionEventWorker.process(event);

        verify(encounterService, times(2)).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(emrEncounterService, never()).save(encounterTransaction);
    }

//    @Test
//    public void shouldCreateAnEncounterWhenTestResultDetailsHasResult() throws IOException {
//        EncounterType testResultEncounterType = new EncounterType();
//        EncounterRole encounterRole = new EncounterRole();
//        Visit visit = new Visit();
//
//        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withPanelUuid("panelUuid").withTestUuid("test1")
//                .withResult("10").withDateTime("2014-01-30T11:26:03+0530").build();
//        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withPanelUuid("panelUuid").withTestUuid("test2")
//                .withResult("10").withDateTime("2014-01-30T11:26:03+0530").build();
//        OpenElisTestDetail test3 = new OpenElisTestDetailBuilder().withTestUuid("test3").withResult("10").withDateTime("2014-01-30T11:26:03+0530").build();
//        OpenElisTestDetail test4 = new OpenElisTestDetailBuilder().withTestUuid("test4").build();
//        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2, test3, test4))).build();
//        Encounter previousEncounter = getEncounterWithTests("test1", "test2", "test3", "test4");
//        visit.addEncounter(previousEncounter);
//        Encounter resultEncounter = createEncounterWithResults(visit, testResultEncounterType, encounterRole, test3);
//        stubAccession(openElisAccession);
//        resultEncounter.addObs(createPanelObsGroup("panelUuid", test1, test2));
//        resultEncounter.addObs(createTestObs(test4));
//
//        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(previousEncounter).thenReturn(resultEncounter);
//        when(encounterService.getEncounterType("LAB_RESULT")).thenReturn(testResultEncounterType);
//        when(encounterService.getEncounterRoleByUuid(EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID)).thenReturn(encounterRole);
//
//        accessionEventWorker.process(event);
//    }

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


    @Test
    public void test() {
//        final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        System.out.println(dateFormat.format("2014-01-30T11:26:03+0530"));

        System.out.println(DateTime.parse("2014-01-30T11:26:03+0530").toDate());

    }
}
