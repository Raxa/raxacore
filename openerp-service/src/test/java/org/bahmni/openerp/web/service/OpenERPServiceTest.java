package org.bahmni.openerp.web.service;

import org.bahmni.openerp.web.OpenERPException;
import org.bahmni.openerp.web.service.domain.Customer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
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
    public void shouldCreateCustomer() throws Exception {
        String name = "name";
        String patientId = "12344";

        openERPService.createCustomer(name, patientId, null);

        verify(customerService).create(new Customer(name,patientId,null));
    }

    @Test
    public void shouldUpdatePatientBalanceForExistingPatients() throws Exception {
        String patientId = "12345";
        float balance = 56;

        openERPService.updateCustomerBalance(patientId, balance);

        verify(customerAccountService).updateCustomerReceivables(patientId, balance);
    }

    @Test
    public void shouldThrowExceptionWhencreationOfCustomerFails() throws Exception {
        String expectedMessage = "Failed to execute Exception";
        doThrow(new OpenERPException(expectedMessage)).when(customerService).create(new Customer("name", "12345", "Ganiyari"));

        try {
            openERPService.createCustomer("name", "12345", "Ganiyari");
            fail("Should have thrown an exception");
        } catch (Exception ex) {
            assertEquals(expectedMessage, ex.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenUpdationOfCustomerWithBalanceFails() throws Exception {
        String expectedMessage = "Failed to execute Exception";
        doThrow(new OpenERPException(expectedMessage)).when(customerAccountService).updateCustomerReceivables(anyString(),anyDouble());
        try {
            openERPService.updateCustomerBalance("name", 12345);
            fail("Should have thrown an exception");
        } catch (Exception ex) {
            assertEquals(expectedMessage, ex.getMessage());
        }
    }
}
