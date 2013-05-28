package org.bahmni.openerp.web.service;

import org.bahmni.openerp.web.client.OpenERPClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CustomerServiceTest {
    private CustomerService customerService;

    @Mock
    private OpenERPClient openERPClient;

    @Before
    public void setup() {
        initMocks(this);
        customerService = new CustomerService(openERPClient);
    }

    @Test
    public void shouldCreateNewCustomerIfNotExisting() throws Exception {
        String name = "Ram Singh";
        String patientId = "12345";
        String village = "Ganiyari";
        Vector searchparams = new Vector();
        searchparams.addElement(new Object[]{"ref", "=", "12345"});
        Object[] results = new Object[]{};
        when(openERPClient.search((String) any(), (Vector) any())).thenReturn(results);

        customerService.create(name, patientId, village);

        verify(openERPClient).create((String) any(), (String) any(), (String) any(), (String) any());
    }

    @Test
    public void createCustomerShouldThrowExceptionIfCustomerAlreadyExisting() throws Exception {
        String name = "Ram Singh";
        String patientId = "12345";
        Vector searchparams = new Vector();
        searchparams.addElement(new Object[]{"ref", "=", "12345"});
        Object[] results = new Object[]{new Object()};
        when(openERPClient.search((String) any(), (Vector) any())).thenReturn(results);

        try {
            customerService.create(name, patientId, null);
            assert false;
        } catch (Exception e) {
            assertEquals(true, e.getMessage().contains("Customer with id, name already exists:"));
        }
    }
}
