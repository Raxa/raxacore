package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.bahmnicore.web.v1_0.controller.BahmniDrugOrderController;
import org.openmrs.module.bahmniemrapi.drugorder.contract.BahmniDrugOrder;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniDrugOrderControllerIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private BahmniDrugOrderController bahmniDrugOrderController;

    @Test
    public void shouldReturnVisitWisePrescribedAndOtherActiveOrdersInOrderOfStartDate() throws Exception {
        executeDataSet("prescribedAndActiveDrugOrdersForVisits.xml");
        Map<String, Collection<BahmniDrugOrder>> drugOrders = bahmniDrugOrderController.getVisitWisePrescribedAndOtherActiveOrders("1a246ed5-3c11-11de-a0ba-001ed98eb67a", 1, true);
        assertEquals(2, drugOrders.keySet().size());

        assertEquals(1, drugOrders.get("c809162f-dc55-4814-be3f-33d23c8abc1d").size());
        assertEquals("92c1bdef-72d4-49d9-8a1f-804892f44acf", drugOrders.get("c809162f-dc55-4814-be3f-33d23c8abc1d").iterator().next().getUuid());

        assertEquals(1, drugOrders.get("otherActiveOrders").size());
        assertEquals("92c1bdef-72d4-77d9-8a1f-80411ac77abe", drugOrders.get("otherActiveOrders").iterator().next().getUuid());
    }

    @Test
    public void shouldReturnVisitWisePrescribedWithoutOtherActiveOrdersInOrderOfStartDate() throws Exception {
        executeDataSet("prescribedAndActiveDrugOrdersForVisits.xml");
        Map<String, Collection<BahmniDrugOrder>> drugOrders = bahmniDrugOrderController.getVisitWisePrescribedAndOtherActiveOrders("1a246ed5-3c11-11de-a0ba-001ed98eb67a", 2, true);
        assertEquals(3, drugOrders.keySet().size());

        assertEquals(1, drugOrders.get("c809162f-dc55-4814-be3f-33d23c8abc1d").size());
        Collection<BahmniDrugOrder> bahmniDrugOrders1 = drugOrders.get("c809162f-dc55-4814-be3f-33d23c8abc1d");
        assertEquals("92c1bdef-72d4-49d9-8a1f-804892f44acf", bahmniDrugOrders1.iterator().next().getUuid());

        assertEquals(3, drugOrders.get("d798916f-210d-4c4e-8978-67dd1a969f31").size());
        Iterator<BahmniDrugOrder> drugOrderIterator = drugOrders.get("d798916f-210d-4c4e-8978-67dd1a969f31").iterator();
        assertEquals("92c1bdef-72d4-77d9-8a1f-80411ac77abe", drugOrderIterator.next().getUuid());
        assertEquals("92c1bdef-72d4-88d9-8a1f-804892f66abf", drugOrderIterator.next().getUuid());
        assertEquals("92c1bdef-72d4-77d9-8a1f-80411ac66abe", drugOrderIterator.next().getUuid());

        assertEquals(0, drugOrders.get("otherActiveOrders").size());

        drugOrders = bahmniDrugOrderController.getVisitWisePrescribedAndOtherActiveOrders("1a246ed5-3c11-11de-a0ba-001ed98eb67a", 2, false);
        assertEquals(2, drugOrders.keySet().size());
        assertNull(drugOrders.get("otherActiveOrders"));

    }

    @Test
    public void shouldReturnDrugOrdersForSpecifiedNumberOfVisits() throws Exception {
        executeDataSet("drugOrdersForVisits.xml");
        List<BahmniDrugOrder> prescribedDrugOrders = bahmniDrugOrderController.getPrescribedDrugOrders("86526ed5-3c11-11de-a0ba-001ed98eb67a", true, 2);
        assertEquals(4, prescribedDrugOrders.size());

        BahmniDrugOrder drugOrder1 = prescribedDrugOrders.get(0);
        assertEquals("d798916f-210d-4c4e-8978-467d1a969f31", drugOrder1.getVisit().getUuid());
        EncounterTransaction.DosingInstructions dosingInstructions1 = drugOrder1.getDosingInstructions();
        assertEquals("{\"dose\": \"1.5\", \"doseUnits\": \"Tablet\"}", dosingInstructions1.getAdministrationInstructions());
        assertEquals(15, drugOrder1.getDuration(), 0);
        assertEquals("Triomune-30", drugOrder1.getDrug().getName());
        assertEquals("2011-10-24 00:00:00.0", drugOrder1.getEffectiveStartDate().toString());
        assertEquals("2011-11-08 00:00:00.0", drugOrder1.getEffectiveStopDate().toString());

        BahmniDrugOrder drugOrder2 = prescribedDrugOrders.get(1);
        assertEquals("d798916f-210d-4c4e-8978-467d1a969f31", drugOrder2.getVisit().getUuid());
        EncounterTransaction.DosingInstructions dosingInstructions2 = drugOrder2.getDosingInstructions();
        assertEquals(4.5, dosingInstructions2.getDose(), 0);
        assertEquals("Before meals", drugOrder2.getInstructions());
        assertEquals("Take while sleeping", drugOrder2.getCommentToFulfiller());
        assertEquals("1/day x 7 days/week", dosingInstructions2.getFrequency());
        assertEquals("UNKNOWN", dosingInstructions2.getRoute());
        assertEquals(6, drugOrder2.getDuration(), 0);
        assertEquals("Paracetamol 250 mg", drugOrder2.getDrug().getName());
        assertEquals("2011-10-22 00:00:00.0", drugOrder2.getEffectiveStartDate().toString());
        assertEquals("2011-10-30 00:00:00.0", drugOrder2.getEffectiveStopDate().toString());

        BahmniDrugOrder drugOrder3 = prescribedDrugOrders.get(2);
        assertEquals("adf4fb41-a41a-4ad6-8835-2f59889acf5a", drugOrder3.getVisit().getUuid());
        EncounterTransaction.DosingInstructions dosingInstructions3 = drugOrder3.getDosingInstructions();
        assertEquals("{\"dose\": \"5.0\", \"doseUnits\": \"Tablet\"}", dosingInstructions3.getAdministrationInstructions());
        assertEquals("tab (s)", drugOrder3.getDrug().getForm());
        assertEquals(6, drugOrder3.getDuration(), 0);
        assertEquals("Triomune-30", drugOrder3.getDrug().getName());
        assertEquals("2005-09-23 08:00:00.0", drugOrder3.getEffectiveStartDate().toString());
        assertEquals("2005-09-30 00:00:00.0", drugOrder3.getEffectiveStopDate().toString());
        assertEquals(1, drugOrder3.getOrderAttributes().size());
        assertEquals("dispensed", drugOrder3.getOrderAttributes().get(0).getName());
        assertEquals("true", drugOrder3.getOrderAttributes().get(0).getValue());
        assertEquals("be48cdcb-6666-47e3-9f2e-2635032f3a9a", drugOrder3.getOrderAttributes().get(0).getObsUuid());
        assertNotNull(drugOrder3.getOrderAttributes().get(0).getEncounterUuid());

        BahmniDrugOrder drugOrder4 = prescribedDrugOrders.get(3);
        assertEquals("adf4fb41-a41a-4ad6-8835-2f59889acf5a", drugOrder4.getVisit().getUuid());
        EncounterTransaction.DosingInstructions dosingInstructions4 = drugOrder4.getDosingInstructions();
        assertEquals("{\"dose\": \"2.5\", \"doseUnits\": \"Tablet\"}", dosingInstructions4.getAdministrationInstructions());
        assertEquals(6, drugOrder4.getDuration(), 0);
        assertEquals("Triomune-40", drugOrder4.getDrug().getName());
        assertEquals("2005-09-23 00:00:00.0", drugOrder4.getEffectiveStartDate().toString());
        assertEquals("2005-09-29 00:00:00.0", drugOrder4.getEffectiveStopDate().toString());
    }

}
