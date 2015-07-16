package org.bahmni.module.elisatomfeedclient.api.mapper;

import org.bahmni.module.elisatomfeedclient.api.ElisAtomFeedProperties;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisAccessionBuilder;
import org.bahmni.module.elisatomfeedclient.api.builder.OpenElisTestDetailBuilder;
import org.bahmni.module.elisatomfeedclient.api.domain.AccessionDiff;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class AccessionHelperTest {
    @Mock
    EncounterService encounterService;
    @Mock
    PatientService patientService;
    @Mock
    VisitService visitService;
    @Mock
    ConceptService conceptService;
    @Mock
    private ElisAtomFeedProperties feedProperties;
    @Mock
    private UserService userService;
    @Mock
    private ProviderService providerService;
    @Mock
    private OrderService orderService;

    private AccessionHelper accessionHelper;
    private static final String VISIT_START_DATE = "2014-01-15 15:25:43+0530";
    private static final String ENCOUNTER_START_DATE = "2014-01-17T17:25:43Z";
    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ssZ";
    private SimpleDateFormat simpleDateFormat;


    @Before
    public void setUp() {
        initMocks(this);
        accessionHelper = new AccessionHelper(encounterService, patientService, visitService, conceptService, userService, providerService, orderService, feedProperties);
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
    }

    @Test
    public void shouldMapToNewEncounter() throws ParseException {
        OpenElisTestDetail panel1 = new OpenElisTestDetailBuilder().withPanelUuid("panel1").withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        HashSet<OpenElisTestDetail> testDetails = new HashSet<>();

        testDetails.add(panel1);
        testDetails.add(test2);
        Patient patient = new Patient();
        List<Visit> visits = createVisits(1);
        User provider = new User();

        when(patientService.getPatientByUuid(any(String.class))).thenReturn(patient);
        when(feedProperties.getEncounterTypeClinical()).thenReturn("OPD");
        when(encounterService.getEncounterType("Consultation")).thenReturn(new EncounterType());
        when(conceptService.getConceptByUuid("panel1")).thenReturn(getConceptByUuid("panel1"));
        when(conceptService.getConceptByUuid("test2")).thenReturn(getConceptByUuid("test2"));
        when(visitService.getVisits(anyCollection(), anyCollection(), anyCollection(), anyCollection(), any(Date.class), any(Date.class), any(Date.class), any(Date.class), anyMap(), anyBoolean(), anyBoolean())).thenReturn(visits);
        when(userService.getUserByUsername(anyString())).thenReturn(provider);
        when(providerService.getProvidersByPerson(any(Person.class))).thenReturn(Arrays.asList(new Provider()));
        when(encounterService.getEncounterRoleByUuid(EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID)).thenReturn(new EncounterRole());
        when(orderService.getOrderTypes(true)).thenReturn(Arrays.asList(getOrderType()));
        PowerMockito.mockStatic(Context.class);
        when(Context.getAuthenticatedUser()).thenReturn(provider);

        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(testDetails).build();
        openElisAccession.setDateTime(ENCOUNTER_START_DATE);
        Encounter encounter = accessionHelper.mapToNewEncounter(openElisAccession, "LAB_RESULTS");

        Set<Order> orders = encounter.getOrders();
        Assert.assertEquals(2, orders.size());
        verify(conceptService, never()).getConceptByUuid("test1");

        for (Order order : orders) {
            Assert.assertTrue(order.getConcept().getUuid().equals("panel1") || order.getConcept().getUuid().equals("test2"));
        }
    }

    @Test
    public void shouldFindProperVisitAndMapToNewEncounter() throws ParseException {
        OpenElisTestDetail panel1 = new OpenElisTestDetailBuilder().withPanelUuid("panel1").withTestUuid("test1").build();
        OpenElisTestDetail test2 = new OpenElisTestDetailBuilder().withTestUuid("test2").build();
        HashSet<OpenElisTestDetail> testDetails = new HashSet<>();
        testDetails.add(panel1);
        testDetails.add(test2);
        Patient patient = new Patient();
        List<Visit> visits = createVisits(3);
        User provider = new User();

        when(patientService.getPatientByUuid(any(String.class))).thenReturn(patient);
        when(feedProperties.getEncounterTypeClinical()).thenReturn("Consultation");
        when(encounterService.getEncounterType("Consultation")).thenReturn(new EncounterType());
        when(conceptService.getConceptByUuid("panel1")).thenReturn(getConceptByUuid("panel1"));
        when(conceptService.getConceptByUuid("test2")).thenReturn(getConceptByUuid("test2"));
        when(visitService.getVisits(anyCollection(), anyCollection(), anyCollection(), anyCollection(), any(Date.class), any(Date.class), any(Date.class), any(Date.class), anyMap(), anyBoolean(), anyBoolean())).thenReturn(visits);
        when(userService.getUserByUsername(anyString())).thenReturn(provider);
        when(providerService.getProvidersByPerson(any(Person.class))).thenReturn(Arrays.asList(new Provider()));
        when(encounterService.getEncounterRoleByUuid(EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID)).thenReturn(new EncounterRole());
        when(orderService.getOrderTypes(true)).thenReturn(Arrays.asList(getOrderType()));
        PowerMockito.mockStatic(Context.class);
        when(Context.getAuthenticatedUser()).thenReturn(provider);

        OpenElisAccession openElisAccession = new OpenElisAccessionBuilder().withTestDetails(testDetails).build();
        openElisAccession.setDateTime(ENCOUNTER_START_DATE);
        Encounter encounter = accessionHelper.mapToNewEncounter(openElisAccession, "LAB_RESULTS");

        Date startDatetime = encounter.getVisit().getStartDatetime();
        Assert.assertTrue("Encounter should be before or after visit start", encounter.getEncounterDatetime().compareTo(startDatetime) >= 0);
        Set<Order> orders = encounter.getOrders();
        Assert.assertEquals(2, orders.size());
        verify(conceptService, never()).getConceptByUuid("test1");

        for (Order order : orders) {
            Assert.assertTrue(order.getConcept().getUuid().equals("panel1") || order.getConcept().getUuid().equals("test2"));
        }
    }

    @Test
    public void shouldMapNewOrdersToExistingEncounter() {
        Encounter previousEncounter = new Encounter();
        Order panel = getOrderWithConceptUuid("panel");
        Order test = getOrderWithConceptUuid("test");
        HashSet<Order> orders = new HashSet<>();
        orders.add(panel);
        orders.add(test);
        previousEncounter.setOrders(orders);
        when(userService.getUserByUsername(anyString())).thenReturn(new User());
        when(providerService.getProvidersByPerson(any(Person.class))).thenReturn(Arrays.asList(new Provider()));

        AccessionDiff diff = new AccessionDiff();
        diff.addAddedTestDetail(new OpenElisTestDetailBuilder().withTestUuid("test2").build());
        diff.addAddedTestDetail(new OpenElisTestDetailBuilder().withTestUuid("panel1").build());

        Encounter encounter = accessionHelper.addOrVoidOrderDifferences(new OpenElisAccessionBuilder().build(), diff, previousEncounter);

        Assert.assertEquals(4, encounter.getOrders().size());
    }

    @Test
    public void shouldMapDeletedOrdersToExistingEncounter() {
        Encounter previousEncounter = new Encounter();
        Order panel = getOrderWithConceptUuid("panel1");
        Order test = getOrderWithConceptUuid("test2");
        HashSet<Order> orders = new HashSet<>();
        orders.add(panel);
        orders.add(test);
        previousEncounter.setOrders(orders);

        AccessionDiff diff = new AccessionDiff();
        diff.addRemovedTestDetails(new OpenElisTestDetailBuilder().withTestUuid("test2").withStatus("Cancelled").build());

        Encounter encounter = accessionHelper.addOrVoidOrderDifferences(new OpenElisAccessionBuilder().build(), diff, previousEncounter);

        Set<Order> result = encounter.getOrders();
        Assert.assertEquals(2, result.size());
        for (Order order : result) {
            if (order.getConcept().getUuid().equals("test2")) {
                Assert.assertTrue(order.getVoided());
            }
        }
    }

    private Order getOrderWithConceptUuid(String conceptUuid) {
        Order order = new Order();
        Concept concept = new Concept();
        concept.setUuid(conceptUuid);
        order.setConcept(concept);
        return order;
    }

    private Concept getConceptByUuid(String uuid) {
        Concept concept = new Concept();
        concept.setUuid(uuid);
        return concept;
    }

    private List<Visit> createVisits(int i) throws ParseException {
        List<Visit> visits = new ArrayList<>();

        for(int j = 0;j<i;j++){
            Calendar datetime = Calendar.getInstance();
            datetime.setTime(simpleDateFormat.parse(VISIT_START_DATE));
            Visit visit = new Visit();
            datetime.add(Calendar.DAY_OF_MONTH, j);
            visit.setStartDatetime(datetime.getTime());
            datetime.add(Calendar.DAY_OF_MONTH, 1);
            visit.setStopDatetime(datetime.getTime());
            visits.add(visit);
        }
        return visits;
    }

    private OrderType getOrderType() {
        OrderType orderType = new OrderType();
        orderType.setName("Order");
        return orderType;
    }
}
