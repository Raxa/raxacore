package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.module.bahmnicore.BaseIntegrationTest;
import org.bahmni.module.bahmnicore.contract.drugorder.DrugOrderConfigResponse;
import org.bahmni.module.bahmnicore.model.BahmniFeedDrugOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class BahmniDrugOrderServiceImplIT extends BaseIntegrationTest {

    public static final String TEST_VISIT_TYPE = "TEST VISIT TYPE";
    @Autowired
    private BahmniDrugOrderServiceImpl bahmniDrugOrderService;
    @Autowired
    private VisitService visitService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private EncounterService encounterService;
    @Autowired
    private ProviderService providerService;
    private DateFormat dateOnly;

    @Before
    public void setUp() throws Exception {
        dateOnly = new SimpleDateFormat("dd.MM.yyyy");
        executeDataSet("drugOrdersTestData.xml");
        executeDataSet("visitAttributeDataSet.xml");
    }





    @Test
    public void shouldReturnOrderAttributeConceptNamesWithGetConfig() throws ParseException {
        DrugOrderConfigResponse config = bahmniDrugOrderService.getConfig();
        List<EncounterTransaction.Concept> orderAttributes = config.getOrderAttributes();

        assertEquals(2,orderAttributes.size());
        assertEquals("dispensed",orderAttributes.get(0).getName());
        assertEquals("administered",orderAttributes.get(1).getName());
    }

    @Test
    public void shouldReturnDiscontinuedOrderMap() throws Exception {
        executeDataSet("patientWithStoppedOrders.xml");
        DrugOrder newFirstOrder = (DrugOrder) Context.getOrderService().getOrder(15);
        DrugOrder revisedFirstOrder = (DrugOrder) Context.getOrderService().getOrder(16);
        DrugOrder newSecondOrder = (DrugOrder) Context.getOrderService().getOrder(18);
        DrugOrder discontinuedFirstOrder = (DrugOrder) Context.getOrderService().getOrder(17);
        DrugOrder discontinuedSecondOrder = (DrugOrder) Context.getOrderService().getOrder(19);

        List<DrugOrder> drugOrdersList=Arrays.asList(newFirstOrder, revisedFirstOrder, newSecondOrder);
        Map<String, DrugOrder> discontinuedOrderMap = bahmniDrugOrderService.getDiscontinuedDrugOrders(drugOrdersList);
        assertEquals(discontinuedOrderMap.get("2").getUuid(), discontinuedFirstOrder.getUuid());
        assertEquals(discontinuedOrderMap.get("4").getUuid(), discontinuedSecondOrder.getUuid());
        assertNull(discontinuedOrderMap.get("1"));

    }

    @Test
    public void shouldReturnEmptyDiscontinuedOrderMapWhenThereAreNoActiveDrugOrders() throws Exception {
        List<DrugOrder> drugOrdersList=new ArrayList<>();
        Map<String, DrugOrder> discontinuedOrderMap = bahmniDrugOrderService.getDiscontinuedDrugOrders(drugOrdersList);
        Assert.assertNotNull(discontinuedOrderMap);
        assertEquals(0, discontinuedOrderMap.size());

    }




    private Visit createVisitForDate(Patient patient, Encounter encounter, Date orderDate, boolean isActive, Date stopDatetime) {
        VisitType regularVisitType = visitService.getVisitType(4);
        Visit visit = new Visit(patient, regularVisitType, orderDate);
        if(encounter != null)
            visit.addEncounter(encounter);
        if (!isActive)
            visit.setStopDatetime(stopDatetime);
        Location location = Context.getLocationService().getLocation(1);
        visit.setLocation(location);

        return visitService.saveVisit(visit);
    }

}
