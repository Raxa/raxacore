package org.bahmni.module.bahmnicore.service.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.module.bahmnicore.model.BahmniDrugOrder;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class BahmniDrugOrderServiceImplIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private BahmniDrugOrderServiceImpl bahmniDrugOrderService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private PatientService patientService;


    @Test
    public void shouldCreateNewEncounterAndAddDrugOrdersWhenActiveVisitExists() throws Exception {
        executeDataSet("drugOrdersTestData.xml");
        Date orderDate = new Date();
        BahmniDrugOrder calpol = new BahmniDrugOrder("3e4933ff-7799-11e3-a96a-0800271c1b75", 2.0, 10, 20.0, "mg");
        BahmniDrugOrder cetrizine = new BahmniDrugOrder("f5bf0aa6-7855-11e3-bd53-328f386b70f0", 3.0, 5, 21.0, "mg");
        BahmniDrugOrder cetzine = new BahmniDrugOrder("f5bf0aa6-7855-11e3-bd53-328f386b70fa", 0.0, 0, 10.0, "mg");
        List<BahmniDrugOrder> drugOrders = Arrays.asList(calpol, cetrizine, cetzine);

        Patient patient = patientService.getPatient(1);
        Visit activeVisit = createActiveVisit(patient);
        assertNull(activeVisit.getEncounters());

        bahmniDrugOrderService.add("GAN200000", orderDate, drugOrders);

        Visit visit = visitService.getVisit(activeVisit.getId());
        Encounter encounter = (Encounter) visit.getEncounters().toArray()[0];
        EncounterProvider encounterProvider = (EncounterProvider) encounter.getEncounterProviders().toArray()[0];
        assertEquals("System", encounterProvider.getProvider().getName());
        assertEquals("Unknown", encounterProvider.getEncounterRole().getName());
        assertEquals("OPD", encounter.getEncounterType().getName());
        assertEquals(3, encounter.getOrders().size());

        assertDrugOrder(encounter.getOrders(), "Calpol", orderDate, calpol.getDosage(), calpol.getNumberOfDays());
        assertDrugOrder(encounter.getOrders(), "Cetirizine", orderDate, cetrizine.getDosage(), cetrizine.getNumberOfDays());
        assertDrugOrder(encounter.getOrders(), "Cetzine", orderDate, cetzine.getDosage(), cetzine.getNumberOfDays());
    }

    private Visit createActiveVisit(Patient patient) {
        VisitType regularVisitType = visitService.getVisitType(4);
        return visitService.saveVisit(new Visit(patient, regularVisitType, new Date()));
    }

    private void assertDrugOrder(Set<Order> orders, String drugName, Date orderDate, Double dosage, int numberOfDays) {
        for (Order order : orders) {
            DrugOrder drugOrder = (DrugOrder) order;
            if (drugOrder.getDrug().getName().equals(drugName)){
                assertEquals(dosage, drugOrder.getDose());
                assertEquals(orderDate, drugOrder.getStartDate());
                assertEquals(DateUtils.addDays(orderDate, numberOfDays), drugOrder.getAutoExpireDate());
                return;
            }
        }
        fail("No Drug Order found for drug name : " + drugName);
    }
}
