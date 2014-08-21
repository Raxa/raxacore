package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.model.BahmniDrugOrder;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.bahmnicore.web.v1_0.controller.BahmniDrugOrderController;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniDrugOrderControllerIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private BahmniDrugOrderController bahmniDrugOrderController;

    @Before
    public void setUp() throws Exception {
        executeDataSet("drugOrdersForVisits.xml");
    }


    @Test
    public void shouldReturnDrugOrdersForSpecifiedNumberOfVisits(){
        List<BahmniDrugOrder> prescribedDrugOrders = bahmniDrugOrderController.getPrescribedDrugOrders("86526ed5-3c11-11de-a0ba-001ed98eb67a", true, 3);
        assertEquals(4,prescribedDrugOrders.size());

        BahmniDrugOrder drugOrder1 = prescribedDrugOrders.get(0);
        assertEquals("d798916f-210d-4c4e-8978-467d1a969f31", drugOrder1.getVisit().getUuid());
        assertEquals(1.5,drugOrder1.getDose(),0);
        assertEquals(15,drugOrder1.getDuration(), 0);
        assertEquals("Triomune-30",drugOrder1.getDrugName());
        assertEquals("2011-10-24T00:00:00.000+0530",drugOrder1.getEffectiveStartDate());
        assertEquals("2011-11-08T00:00:00.000+0530", drugOrder1.getEffectiveStopDate());

        BahmniDrugOrder drugOrder2 = prescribedDrugOrders.get(1);
        assertEquals("d798916f-210d-4c4e-8978-467d1a969f31", drugOrder2.getVisit().getUuid());
        assertEquals(4.5,drugOrder2.getDose(),0);
        assertEquals("Before meals",drugOrder2.getDosingInstructions().getInstructions());
        assertEquals("Take while sleeping",drugOrder2.getDosingInstructions().getNotes());
        assertEquals("1/day x 7 days/week",drugOrder2.getFrequency());
        assertEquals("UNKNOWN",drugOrder2.getRoute());
        assertEquals(6,drugOrder2.getDuration(), 0);
        assertEquals("Paracetamol 250 mg",drugOrder2.getDrugName());
        assertEquals("2011-10-22T00:00:00.000+0530",drugOrder2.getEffectiveStartDate());
        assertEquals("2011-10-30T00:00:00.000+0530", drugOrder2.getEffectiveStopDate());

        BahmniDrugOrder drugOrder3 = prescribedDrugOrders.get(2);
        assertEquals("adf4fb41-a41a-4ad6-8835-2f59889acf5a", drugOrder3.getVisit().getUuid());
        assertEquals(5.0,drugOrder3.getDose(),0);
        assertEquals("Tablet",drugOrder3.getDoseUnits());
        assertEquals("tab (s)",drugOrder3.getDrugForm());
        assertEquals(2,drugOrder3.getDuration(), 0);
        assertEquals("Triomune-30",drugOrder3.getDrugName());
        assertEquals("2005-09-23T08:00:00.000+0530",drugOrder3.getEffectiveStartDate());
        assertEquals("2005-09-30T00:00:00.000+0530", drugOrder3.getEffectiveStopDate());

        BahmniDrugOrder drugOrder4 = prescribedDrugOrders.get(3);
        assertEquals("adf4fb41-a41a-4ad6-8835-2f59889acf5a", drugOrder4.getVisit().getUuid());
        assertEquals(2.5,drugOrder4.getDose(),0);
        assertEquals(4,drugOrder4.getDuration(), 0);
        assertEquals("Triomune-40",drugOrder4.getDrugName());
        assertEquals("2005-09-23T00:00:00.000+0530",drugOrder4.getEffectiveStartDate());
        assertEquals("2005-09-29T00:00:00.000+0530", drugOrder4.getEffectiveStopDate());


    }
}
