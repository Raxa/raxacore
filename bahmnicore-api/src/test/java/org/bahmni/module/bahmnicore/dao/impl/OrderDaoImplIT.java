package org.bahmni.module.bahmnicore.dao.impl;

import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Visit;
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
    public void shouldFetchAllPrescribedDrugOrdersInPastVisits() throws Exception {
        executeDataSet("patientWithOrders.xml");
        Patient patient = Context.getPatientService().getPatient(1);

        List<DrugOrder> drugOrdersInLastVisit = orderDao.getPrescribedDrugOrders(patient, false, 1);
        assertThat(drugOrdersInLastVisit.size(), is(equalTo(1)));

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

    @Test
    public void shouldFetchVisitsWithGivenOrderType() throws Exception {
        executeDataSet("patientWithOrders.xml");
        Patient patient = Context.getPatientService().getPatient(1);

        List<Visit> visits = orderDao.getVisitsWithOrders(patient, "TestOrder", true, 1);

        assertThat(visits.size(), is(equalTo(1)));
        assertThat(visits.get(0).getId(), is(equalTo(5)));
    }
}
