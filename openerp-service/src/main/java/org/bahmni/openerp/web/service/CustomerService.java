package org.bahmni.openerp.web.service;

import org.bahmni.openerp.web.OpenERPException;
import org.bahmni.openerp.web.client.OpenERPClient;
import org.bahmni.openerp.web.request.OpenERPRequest;
import org.bahmni.openerp.web.request.mapper.OpenERPParameterMapper;
import org.bahmni.openerp.web.service.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Vector;

@Service
public class CustomerService {
    private OpenERPClient openERPClient;
    private OpenERPParameterMapper parameterMapper;

    @Autowired
    public CustomerService(OpenERPClient openERPClient) {
        this.openERPClient = openERPClient;
        this.parameterMapper = new OpenERPParameterMapper();
    }

    CustomerService(OpenERPClient openERPClient,OpenERPParameterMapper parameterMapper) {
        this.openERPClient = openERPClient;
        this.parameterMapper = parameterMapper;
    }

    public void create(Customer customer) {
        if (noCustomersFound(findCustomersWithPatientReference(customer.getRef()))) {
            OpenERPRequest request = parameterMapper.mapCustomerParams(customer, "create");
            String response = openERPClient.execute(request);
        } else
            throw new OpenERPException(String.format("Customer with id, name already exists: %s, %s ", customer.getRef(), customer.getName()));
    }

    public void deleteCustomerWithPatientReference(String patientId) {
        Object[] customerIds = findCustomersWithPatientReference(patientId);
        Vector params = new Vector();
        params.addElement(customerIds[0]);
        openERPClient.delete("res.partner", params);
    }

    public Object[] findCustomersWithPatientReference(String patientId) {
        Object args[] = {"ref", "=", patientId};
        Vector params = new Vector();
        params.addElement(args);
        return (Object[]) openERPClient.search("res.partner", params);
    }

    private boolean noCustomersFound(Object[] customers) {
        return customers.length == 0;
    }
}
