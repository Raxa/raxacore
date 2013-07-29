package org.bahmni.openerp.web.service;

import org.bahmni.openerp.web.client.OpenERPClient;
import org.bahmni.openerp.web.request.OpenERPRequest;
import org.bahmni.openerp.web.request.builder.OpenERPRequestTestHelper;
import org.bahmni.openerp.web.request.builder.Parameter;
import org.bahmni.openerp.web.request.mapper.OpenERPParameterMapper;
import org.bahmni.openerp.web.service.domain.Customer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
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

    @Mock
    private OpenERPParameterMapper parameterMapper;
    private OpenERPRequestTestHelper openERPRequestTestHelper;

    @Before
    public void setup() {
        initMocks(this);
        customerService = new CustomerService(openERPClient, parameterMapper);
        openERPRequestTestHelper = new OpenERPRequestTestHelper();
    }

    @Test
    public void shouldCreateNewCustomerIfNotExisting() throws Exception {
        String name = "Ram Singh";
        String patientId = "12345";
        String village = "Ganiyari";
        Customer customer = new Customer(name,patientId,village);
        Vector searchparams = new Vector();
        searchparams.addElement(new Object[]{"ref", "=", "12345"});
        Object[] results = new Object[]{};
        when(openERPClient.search((String) any(), (Vector) any())).thenReturn(results);

        List<Parameter> parameters = openERPRequestTestHelper.createCustomerRequest(name,patientId,village);
        OpenERPRequest request = new OpenERPRequest("res_partner", "execute", parameters);

        when(parameterMapper.mapCustomerParams(customer,"create")).thenReturn(request);

        customerService.create(customer);

        verify(openERPClient).execute(request);
    }

    @Test
    public void createCustomerShouldThrowExceptionIfCustomerAlreadyExisting() throws Exception {
        String name = "Ram Singh";
        String patientId = "12345";
        Customer customer = new Customer(name,patientId,"");
        Vector searchparams = new Vector();
        searchparams.addElement(new Object[]{"ref", "=", "12345"});
        Object[] results = new Object[]{new Object()};
        when(openERPClient.search((String) any(), (Vector) any())).thenReturn(results);

        try {
            customerService.create(customer);
            assert false;
        } catch (Exception e) {
            assertEquals(true, e.getMessage().contains("Customer with id, name already exists:"));
        }
    }
}
