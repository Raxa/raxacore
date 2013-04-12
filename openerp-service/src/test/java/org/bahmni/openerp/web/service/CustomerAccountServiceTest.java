package org.bahmni.openerp.web.service;

import org.bahmni.openerp.web.client.OpenERPClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Vector;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CustomerAccountServiceTest {
    @Mock
    OpenERPClient openERPClient;

    @Before
    public void setUp()  {
        initMocks(this);
    }


    @Test
    public void shouldUpdateCustomerReceivables() throws Exception {
        String name = "Ram Singh";
        String patientId = "12345";
        double amount = 27.0;

        Object args1[]={"patientId","12345"};
        Object args2[]={"amount",amount};
        Vector params = new Vector();
        params.addElement(args1);
        params.addElement(args2);

        Object[] results = new Object[]{};

        CustomerAccountService customerAccountService = new CustomerAccountService(openERPClient);
        customerAccountService.updateCustomerReceivables(patientId,amount);

        verify(openERPClient).updateCustomerReceivables((String) any(),(Vector) any());
    }

}
