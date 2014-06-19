package org.bahmni.module.bahmnicore.dao.impl;

import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class OrderDaoImplIT  extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private OrderDaoImpl orderDao;


    @Test
    public void shouldRetrieveActiveOrdersForAPatient() throws Exception {
        executeDataSet("patientWithOrders.xml");
        Patient patient = Context.getPatientService().getPatient(1);

        List<DrugOrder> activeOrders = orderDao.getActiveDrugOrders(patient);

        assertThat(activeOrders.size(), is(equalTo(4)));
        List<String> instructions = getInstructions(activeOrders);
        assertThat(instructions, hasItem("non-expiring"));
        assertThat(instructions, hasItem("another-non-expiring"));
        assertThat(instructions, hasItem("expire-date in future"));
        assertThat(instructions, hasItem("drug in active visit"));
    }

    @Test
    public void shouldFetchAllPrescribedDrugOrdersInPastVisits() throws Exception {
        executeDataSet("patientWithOrders.xml");
        Patient patient = Context.getPatientService().getPatient(1);

        List<DrugOrder> drugOrdersInLastVisit = orderDao.getPrescribedDrugOrders(patient, false, 1);
        assertThat(drugOrdersInLastVisit.size(), is(equalTo(2)));

        List<DrugOrder> drugOrdersInLastTwoVisit = orderDao.getPrescribedDrugOrders(patient, false, 2);
        assertThat(drugOrdersInLastTwoVisit.size(), is(equalTo(4)));

        List<DrugOrder> drugOrders = orderDao.getPrescribedDrugOrders(patient, false, null);
        assertThat(drugOrders.size(), is(equalTo(4)));
    }

    @Test
    public void shouldFetchAllPrescribedDrugOrdersIncludingActiveVisit() throws Exception {
        executeDataSet("patientWithOrders.xml");
        Patient patient = Context.getPatientService().getPatient(1);

        List<DrugOrder> drugOrders = orderDao.getPrescribedDrugOrders(patient, true, null);
        assertThat(drugOrders.size(), is(equalTo(5)));

        drugOrders = orderDao.getPrescribedDrugOrders(patient, null, null);
        assertThat(drugOrders.size(), is(equalTo(4)));
    }

    private List<String> getInstructions(List<DrugOrder> activeOrders) {
        ArrayList<String> instructions = new ArrayList<String>();
        for (Order order: activeOrders) {
            instructions.add(order.getInstructions());
        }
        return instructions;
    }
}
