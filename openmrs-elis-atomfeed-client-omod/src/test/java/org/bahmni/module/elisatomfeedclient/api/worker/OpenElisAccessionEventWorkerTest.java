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

    private OpenElisAccessionEventWorker accessionEventWorker;
    private String openElisUrl;
    private Event event;

    @Before
    public void setUp() {
        initMocks(this);
        accessionEventWorker = new OpenElisAccessionEventWorker(feedProperties, httpClient, encounterService, accessionMapper);
        openElisUrl = "http://localhost";
        event = new Event("id", "/openelis/accession/12-34-56-78", "title", "feedUri");
        when(feedProperties.getOpenElisUri()).thenReturn(openElisUrl);
    }

    @Test
    public void shouldSaveEncounterWhenEncounterForGivenAccessionDoesNotExists() throws Exception {
        Encounter encounter = getEncounterWithTests("test1");
        stubAccession(new OpenElisAccessionBuilder().build());
        when(accessionMapper.mapToNewEncounter(any(OpenElisAccession.class))).thenReturn(encounter);

        accessionEventWorker.process(event);

        verify(encounterService).saveEncounter(encounter);
    }

    @Test
    public void shouldUpdateEncounterWhenAccessionHasNewOrder() throws Exception {
        Encounter previousEncounter = getEncounterWithTests("test1");
        Encounter encounterFromAccession = getEncounterWithTests("test1", "test2");
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).build();
        stubAccession(openElisAccession);
        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(previousEncounter);
        when(accessionMapper.mapToExistingEncounter(any(OpenElisAccession.class), any(AccessionDiff.class), any(Encounter.class))).thenReturn(encounterFromAccession);

        accessionEventWorker.process(event);

        verify(encounterService).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(encounterService).saveEncounter(encounterFromAccession);
    }

    @Test
    public void shouldUpdateEncounterWhenAccessionHasRemovedOrderFromPreviousEncounter() throws Exception {
        Encounter previousEncounter = getEncounterWithTests("test1", "test2", "test3");
        Encounter encounterFromAccession = getEncounterWithTests("test1", "test2", "test3");
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisTestDetail test3 = new OpenElisTestDetailBuilder().withTestUuid("test3").withStatus("Cancelled").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2, test3))).build();
        stubAccession(openElisAccession);
        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(previousEncounter);
        when(accessionMapper.mapToExistingEncounter(any(OpenElisAccession.class), any(AccessionDiff.class), any(Encounter.class))).thenReturn(encounterFromAccession);

        accessionEventWorker.process(event);

        verify(encounterService).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(encounterService).saveEncounter(encounterFromAccession);
    }

    @Test
    public void shouldNotUpdateEncounterWhenAccessionHasSameOrdersAsPreviousEncounter() throws Exception {
        Encounter previousEncounter = getEncounterWithTests("test1", "test2");
        Encounter encounterFromAccession = getEncounterWithTests("test1", "test2");
        OpenElisTestDetail test1 = new OpenElisTestDetailBuilder().withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(new HashSet<>(Arrays.asList(test1, test2))).build();
        previousEncounter.setUuid(openElisAccession.getAccessionUuid());
        stubAccession(openElisAccession);
        when(encounterService.getEncounterByUuid(openElisAccession.getAccessionUuid())).thenReturn(previousEncounter);
        when(accessionMapper.mapToNewEncounter(any(OpenElisAccession.class))).thenReturn(encounterFromAccession);

        accessionEventWorker.process(event);

        verify(encounterService).getEncounterByUuid(openElisAccession.getAccessionUuid());
        verify(encounterService, never()).saveEncounter(encounterFromAccession);
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
        String json = getJson(accession);
        when(httpClient.get(URI.create(openElisUrl + event.getContent()))).thenReturn(json);
    }

    private String getJson(OpenElisAccession openElisAccession) throws IOException {
        return objectMapper.writeValueAsString(openElisAccession);
    }
}
