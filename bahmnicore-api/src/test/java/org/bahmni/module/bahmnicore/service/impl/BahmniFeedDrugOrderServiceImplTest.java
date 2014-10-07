package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.PatientDao;
import org.bahmni.module.bahmnicore.dao.impl.PatientDaoImpl;
import org.bahmni.module.bahmnicore.model.BahmniFeedDrugOrder;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BahmniFeedDrugOrderServiceImplTest {
    public static final String TEST_VISIT_TYPE = "TEST VISIT TYPE";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void throw_patient_not_found_exception_for_empty_customerId() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Patient Id is null or empty. PatientId=''");

        Date orderDate = new Date();
        BahmniFeedDrugOrder calpol = new BahmniFeedDrugOrder("3e4933ff-7799-11e3-a96a-0800271c1b75", 2.0, 10, 20.0, "mg");
        List<BahmniFeedDrugOrder> drugOrders = Arrays.asList(calpol);

        BahmniDrugOrderService bahmniDrugOrderService = new BahmniDrugOrderServiceImpl(null, null, null, null, null, null, new PatientDaoImpl(null), null, null);
        bahmniDrugOrderService.add("", orderDate, drugOrders, "System", TEST_VISIT_TYPE);
    }

    @Test
    public void throw_patient_not_found_exception_for_null_customerId() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Patient Id is null or empty. PatientId='null'");

        Date orderDate = new Date();
        BahmniFeedDrugOrder calpol = new BahmniFeedDrugOrder("3e4933ff-7799-11e3-a96a-0800271c1b75", 2.0, 10, 20.0, "mg");
        List<BahmniFeedDrugOrder> drugOrders = Arrays.asList(calpol);

        BahmniDrugOrderService bahmniDrugOrderService = new BahmniDrugOrderServiceImpl(null, null, null, null, null, null, new PatientDaoImpl(null), null, null);
        bahmniDrugOrderService.add(null, orderDate, drugOrders, "System", TEST_VISIT_TYPE);
    }

    @Test
    public void throw_patient_not_found_exception_for_non_existent_customerId() {
        String patientId = "12345";

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Patient Id is null or empty. PatientId='12345'");

        Date orderDate = new Date();
        BahmniFeedDrugOrder calpol = new BahmniFeedDrugOrder("3e4933ff-7799-11e3-a96a-0800271c1b75", 2.0, 10, 20.0, "mg");
        List<BahmniFeedDrugOrder> drugOrders = Arrays.asList(calpol);

        PatientDao patientDao = mock(PatientDao.class);
        when(patientDao.getPatient(patientId)).thenReturn(null);

        BahmniDrugOrderServiceImpl bahmniDrugOrderService = new BahmniDrugOrderServiceImpl(null, null, null, null, null, null, patientDao, null, null);
        bahmniDrugOrderService.add(patientId, orderDate, drugOrders, "System", TEST_VISIT_TYPE);
    }

}
