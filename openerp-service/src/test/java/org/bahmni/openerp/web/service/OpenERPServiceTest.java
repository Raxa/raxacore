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


public class OpenERPServiceTest {
    @Mock
    OpenERPClient openERPClient;

    @Before
    public void setUp()  {
        initMocks(this);
    }

    @Test
    public void ShouldCreateNewCustomerIfNotExisting() throws Exception {
        String name = "Ram Singh";
        String patientId = "12345";

        Object args[]={"ref","=","12345"};
        Vector searchparams = new Vector();
        searchparams.addElement(args);

        Object[] results = new Object[]{};

        when(openERPClient.search((String)any(), (Vector)any())).thenReturn(results);

        OpenERPService openERPService = new OpenERPService(openERPClient);
        openERPService.createCustomer(name,patientId);

       verify(openERPClient).create((String) any(),(String) any(), (String) any());
    }

    @Test
    public void ShouldThrowExceptionIfCustomerAlreadyExisting() throws Exception {
        String name = "Ram Singh";
        String patientId = "12345";

        Object args[]={"ref","=","12345"};
        Vector searchparams = new Vector();
        searchparams.addElement(args);

        Object[] results = new Object[]{new Object()};

        when(openERPClient.search((String)any(), (Vector)any())).thenReturn(results);

        OpenERPService openERPService = new OpenERPService(openERPClient);
        try{
          openERPService.createCustomer(name, patientId);
          assert(false);
        }catch(Exception e){
            assert(true);
            assertEquals("Customer with "+patientId+" already exists",e.getMessage());
        }
    }
}
