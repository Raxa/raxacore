package org.bahmni.openerp.web.request.mapper;


import org.bahmni.openerp.web.request.OpenERPRequest;
import org.bahmni.openerp.web.request.builder.Parameter;
import org.bahmni.openerp.web.service.domain.Customer;

import java.util.Arrays;
import java.util.List;

public class OpenERPParameterMapper {

    public static final String OPENERP_CUSTOMER_NAME = "name";
    public static final String OPENERP_CUSTOMER_REF = "ref";
    public static final String OPENERP_CUSTOMER_VILLAGE = "village";
    public static final String CUSTOMER_RESOURCE = "res.partner";

    public OpenERPRequest mapCustomerParams(Customer customer, String operation) {
        Parameter name = new Parameter(OPENERP_CUSTOMER_NAME,customer.getName(),"string") ;
        Parameter ref = new Parameter(OPENERP_CUSTOMER_REF,customer.getRef(),"string") ;
        Parameter village = new Parameter(OPENERP_CUSTOMER_VILLAGE,customer.getVillage(),"string") ;

        List<Parameter> parameters = Arrays.asList(name, ref, village);
        String resource = CUSTOMER_RESOURCE;
        OpenERPRequest request = new OpenERPRequest(resource, operation,parameters);
        return request;
    }
}
