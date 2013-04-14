package org.bahmni.openerp.web.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


public class OpenERPServiceTest {

    private OpenERPService openERPService;
    @Mock
    CustomerAccountService customerAccountService;
    @Mock
    private CustomerService customerService;

    @Before
    public void setUp()  {
        initMocks(this);
        openERPService = new OpenERPService(customerService, customerAccountService);
    }

    @Test
    public void shouldCreateCustomer() {
        String name = "name";
        String patientId = "12344";

        openERPService.createCustomer(name, patientId);

        verify(customerService).create(name, patientId);
    }

    @Test
    public void shouldUpdatePatientBalanceForExistingPatients() {
        String patientId = "12345";
        float balance = 56;

        openERPService.updateCustomerBalance(patientId, balance);

        verify(customerAccountService).updateCustomerReceivables(patientId, balance);
    }
}
