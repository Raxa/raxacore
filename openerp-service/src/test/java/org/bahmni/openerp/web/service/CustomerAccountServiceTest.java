package org.bahmni.openerp.web.service;

import org.bahmni.openerp.web.OpenERPException;
import org.bahmni.openerp.web.client.OpenERPClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CustomerAccountServiceTest {
    @Mock
    OpenERPClient openERPClient;
    private CustomerAccountService customerAccountService;

    @Before
    public void setUp()  {
        initMocks(this);
        customerAccountService = new CustomerAccountService(openERPClient);
    }

    @Test
    public void shouldUpdateCustomerReceivables() throws Exception {
        String patientId = "12345";
        double amount = 27.0;

        Object args1[]={"patientId","12345"};
        Object args2[]={"amount",amount};
        Vector params = new Vector();
        params.addElement(args1);
        params.addElement(args2);

        customerAccountService.updateCustomerReceivables(patientId,amount);

        verify(openERPClient).updateCustomerReceivables((String) any(),(Vector) any());
    }

    @Test
    public void shouldThrowExceptionIfUpdationFails() {
        String patientId = "12345";
        double amount = 27.0;
        doThrow(new OpenERPException("message")).when(openERPClient).updateCustomerReceivables(anyString(), any(Vector.class));

        try {
            customerAccountService.updateCustomerReceivables(patientId, amount);
            assert false;
        } catch (Exception e) {
        }
    }
}
