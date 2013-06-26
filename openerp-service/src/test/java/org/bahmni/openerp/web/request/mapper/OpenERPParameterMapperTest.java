package org.bahmni.openerp.web.request.mapper;


import org.bahmni.openerp.web.request.OpenERPRequest;
import org.bahmni.openerp.web.request.builder.Parameter;
import org.bahmni.openerp.web.service.domain.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;

public class OpenERPParameterMapperTest {

    private OpenERPParameterMapper mapper;

    @Before
    public void setUp(){
        mapper = new OpenERPParameterMapper();
    }


    @Test
    public void shouldConvertCustomerToParameterList(){
        String name = "Ram Singh";
        String patientId ="GAN12345";
        String village="Ganiyari";
        Customer customer = new Customer(name,patientId,village);

        List<Parameter> expectedParams = asList(createParameter("name", name, "string"),
                createParameter("ref", patientId, "string"),
                createParameter("village", village, "string"));

        OpenERPRequest expectedRequest = new OpenERPRequest("res.partner", "execute", expectedParams);

        OpenERPRequest request = mapper.mapCustomerParams(customer, "execute");
        Assert.assertEquals(expectedRequest, request);

    }

    private Parameter createParameter(String name, String value, String type) {
        return new Parameter(name, value, type);
    }

}
