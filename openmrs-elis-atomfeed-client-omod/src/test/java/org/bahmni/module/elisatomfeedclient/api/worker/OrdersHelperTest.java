package org.bahmni.module.elisatomfeedclient.api.worker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class OrdersHelperTest {
    private OrdersHelper orderHelper;
    @Mock
    private OrderService orderService;
    @Mock
    private ConceptService conceptService;
    @Mock
    private EncounterService encounterService;
    @Mock
    private AdministrationService administrationService;

    @Before
    public void setUp() {
        initMocks(this);
        PowerMockito.mockStatic(Context.class);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getLocale()).thenReturn(Locale.UK);
        when(orderService.getAllOrderTypes()).thenReturn(createOrderTypes());
        orderHelper = new OrdersHelper(orderService, conceptService, encounterService);

    }

    private List<OrderType> createOrderTypes() {
        return Arrays.asList(new OrderType("Lab Order", "Good Order"));
    }

    public void setConceptMock(String conceptName){
        when(conceptService.getConcept(anyString())).thenReturn(createConcept(conceptName));
    }

    @Test
    public void shouldCreateOrderOfTypeIn() throws Exception {
        Encounter encounter = getEncounter();
        String conceptName = "Order Concept Name";
        setConceptMock(conceptName);
        Order order = orderHelper.createOrderInEncounter(encounter, "Lab Order", conceptName);
        order.setUuid("OrderUUID");
        assertEquals("Lab Order", order.getOrderType().getName());
        assertEquals(conceptName, order.getConcept().getName().getName());
        assertEquals((Integer) 1, order.getPatient().getId());
        assertEquals(encounter.getUuid(), order.getAccessionNumber());
    }

    @Test
    public void shouldNotGetOrderByWrongConceptName() throws Exception {
        Order order = orderHelper.getOrderByConceptName(getEncounter(), "ramesh");
        assertNull(order);
    }

    @Test
    public void shouldGetOrderByConceptName() throws Exception {
        Order order = orderHelper.getOrderByConceptName(getEncounter(), "Concept1");
        assertEquals("Concept1", order.getConcept().getName().getName());
    }

    public Encounter getEncounter() {
        Encounter encounter = new Encounter(2);
        encounter.setPatient(new Patient(1));
        encounter.setUuid("encounterUUID");
        encounter.setOrders(createOrders(new OrderUUIDAndConceptName("10", "Concept1"),
                new OrderUUIDAndConceptName("20", "Concept2"), new OrderUUIDAndConceptName("30", "Concept3")));
        return encounter;
    }

    private Set<Order> createOrders(OrderUUIDAndConceptName... orderUuidsAndConceptNames) {
        HashSet<Order> orders = new HashSet<Order>();
        for (OrderUUIDAndConceptName orderUuidAndConceptName : orderUuidsAndConceptNames) {
            Order order = new Order();
            order.setUuid(orderUuidAndConceptName.orderUuid);
            order.setConcept(createConcept(orderUuidAndConceptName.conceptName));
            orders.add(order);
        }
        return orders;
    }

    private Concept createConcept(String conceptName) {
        Concept concept = new Concept();
        ConceptName name= new ConceptName(conceptName, Locale.getDefault());
       // name.setName(conceptName);
        concept.addName(name);
        return concept;
    }

    class OrderUUIDAndConceptName {
        public String orderUuid;
        public String conceptName;

        OrderUUIDAndConceptName(String orderUuid, String conceptName) {
            this.orderUuid = orderUuid;
            this.conceptName = conceptName;
        }
    }


}
