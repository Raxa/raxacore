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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.TestOrder;
import org.openmrs.api.EncounterService;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
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
    private AccessionMapper accessionMapper;
    @Mock
    private ElisAtomFeedProperties feedProperties;
    @Mock
    private EncounterTransactionMapper encounterTransactionMapper;
    @Mock
    private EmrEncounterService emrEncounterService;
    private OpenElisAccessionEventWorker accessionEventWorker;
    private String openElisUrl;
    private Event event;

    @Before
    public void setUp() {
        initMocks(this);
        accessionEventWorker = new OpenElisAccessionEventWorker(feedProperties, httpClient, encounterService, emrEncounterService, accessionMapper, encounterTransactionMapper);
        openElisUrl = "http://localhost";
        event = new Event("id", "/openelis/accession/12-34-56-78", "title", "feedUri");
        when(feedProperties.getOpenElisUri()).thenReturn(openElisUrl);
    }

    @Test
    public void shouldSaveEncounterWhenEncounterForGivenAccessionDoesNotExists() throws Exception {
        Encounter encounter = getEncounterWithTests("test1");
        stubAccession(new OpenElisAccessionBuilder().build());
        EncounterTransaction encounterTransaction = new EncounterTransaction();

        when(accessionMapper.mapToNewEncounter(any(OpenElisAccession.class))).thenReturn(encounter);
        when(encounterTransactionMapper.map(encounter, true)).thenReturn(encounterTransaction);

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

        verify(encounterService).getEncounterByUuid(openElisAccession.getAccessionUuid());
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

        verify(encounterService).getEncounterByUuid(openElisAccession.getAccessionUuid());
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

        verify(encounterService).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(emrEncounterService, never()).save(encounterTransaction);
    }

    private Encounter getEncounterWithTests(String... testUuids) {
        Encounter encounter = new Encounter();
        for (String testUuid : testUuids) {
            TestOrder order = new TestOrder();
            Concept concept = new Concept();
            concept.setUuid(testUuid);
            order.setConcept(concept);
            encounter.addOrder(order);
        }
        return encounter;
    }

    private void stubAccession(OpenElisAccession accession) throws IOException {
        when(httpClient.get(openElisUrl + event.getContent(), OpenElisAccession.class)).thenReturn(accession);
    }
}
