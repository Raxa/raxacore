package org.bahmni.module.bahmnicore.dao.impl;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class OrderDaoImplIT  extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private OrderDaoImpl orderDao;

    @Test
    public void getPrescribedDrugOrders_ShouldNotGetDiscontinueOrders() throws Exception {
        executeDataSet("patientWithDiscontinuedOrders.xml");
        Patient patient = Context.getPatientService().getPatient(1001);

        List<DrugOrder> drugOrdersInLastVisit = orderDao.getPrescribedDrugOrders(patient, true, null);

        assertThat(drugOrdersInLastVisit.size(), is(3));
        assertThat(getOrderIds(drugOrdersInLastVisit), hasItems(15, 16, 18));
    }

    @Test
    public void getPrescribedDrugOrders_ShouldGetRevisedOrdersAloneIfRevisionIsInSameEncounter() throws Exception {
        executeDataSet("patientWithOrderRevisedInSameEncounter.xml");
        Patient patient = Context.getPatientService().getPatient(1001);

        List<DrugOrder> drugOrdersInLastVisit = orderDao.getPrescribedDrugOrders(patient, true, null);

        assertThat(drugOrdersInLastVisit.size(), is(1));
        assertThat(getOrderIds(drugOrdersInLastVisit), hasItems(16));
    }

    @Test
    public void getPrescribedDrugOrders_ShouldGetBothRevisedOrdersAndPreviousOrderIfRevisionIsInDifferentEncounter() throws Exception {
        executeDataSet("patientWithOrderRevisedInDifferentEncounter.xml");
        Patient patient = Context.getPatientService().getPatient(1001);

        List<DrugOrder> drugOrdersInLastVisit = orderDao.getPrescribedDrugOrders(patient, true, null);

        assertThat(drugOrdersInLastVisit.size(), is(2));
        assertThat(getOrderIds(drugOrdersInLastVisit), hasItems(15, 16));
    }

    @Test
    public void getPrescribedDrugOrders_ShouldFetchAllPrescribedDrugOrdersInPastVisits() throws Exception {
        executeDataSet("patientWithOrders.xml");
        Patient patient = Context.getPatientService().getPatient(1001);

        List<DrugOrder> drugOrdersInLastVisit = orderDao.getPrescribedDrugOrders(patient, false, 1);
        assertThat(drugOrdersInLastVisit.size(), is(equalTo(1)));
        assertThat(getOrderIds(drugOrdersInLastVisit), hasItems(17));

        List<DrugOrder> drugOrdersInLastTwoVisit = orderDao.getPrescribedDrugOrders(patient, false, 2);
        assertThat(drugOrdersInLastTwoVisit.size(), is(equalTo(3)));
        assertThat(getOrderIds(drugOrdersInLastTwoVisit), hasItems(15, 16, 17));

        List<DrugOrder> drugOrders = orderDao.getPrescribedDrugOrders(patient, false, null);
        assertThat(drugOrders.size(), is(equalTo(3)));
        assertThat(getOrderIds(drugOrders), hasItems(15, 16, 17));
    }

    @Test
    public void getPrescribedDrugOrders_shouldFetchAllPrescribedDrugOrdersIncludingActiveVisit() throws Exception {
        executeDataSet("patientWithOrders.xml");
        Patient patient = Context.getPatientService().getPatient(1001);

        List<DrugOrder> drugOrders = orderDao.getPrescribedDrugOrders(patient, true, null);
        assertThat(drugOrders.size(), is(equalTo(4)));
        assertThat(getOrderIds(drugOrders), hasItems(15, 16, 17, 19));


        drugOrders = orderDao.getPrescribedDrugOrders(patient, null, null);
        assertThat(drugOrders.size(), is(equalTo(3)));
        assertThat(getOrderIds(drugOrders), hasItems(15, 16, 17));
    }

    @Test
    public void getVisitsWithOrders_ShouldFetchVisitsWithGivenOrderType() throws Exception {
        executeDataSet("patientWithOrders.xml");
        Patient patient = Context.getPatientService().getPatient(1001);

        List<Visit> visits = orderDao.getVisitsWithOrders(patient, "TestOrder", true, 1);

        assertThat(visits.size(), is(equalTo(1)));
        assertThat(visits.get(0).getId(), is(equalTo(5)));
    }

    private List<Integer> getOrderIds(List<DrugOrder> drugOrders) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (DrugOrder drugOrder : drugOrders) {
            ids.add(drugOrder.getOrderId());
        }
        return ids;
    }
}
