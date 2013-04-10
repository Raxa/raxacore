package org.bahmni.openerp.web.service;


import org.apache.log4j.Logger;
import org.bahmni.module.billing.BillingService;
import org.bahmni.openerp.web.client.OpenERPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Vector;

@Service
public class OpenERPService implements BillingService {
    OpenERPClient openERPClient;
    private static Logger logger = Logger.getLogger(OpenERPService.class);

    @Autowired
    public OpenERPService(OpenERPClient client){
        this.openERPClient = client;
    }

    public void tryCreateCustomer(String name, String patientId){
        try {
            createCustomer(name, patientId);
        } catch (Exception ex) {
            logger.error(String.format("[%s, %s] : Failed to create customer in openERP", patientId, name), ex);
        }
    }

    public void createCustomer(String name, String patientId) throws Exception {
        if (noCustomersFound(findCustomerWithPatientReference(patientId))) {
            openERPClient.create("res.partner", name, patientId);
        } else
            raiseDuplicateException(patientId);
    }

    private Object[] findCustomerWithPatientReference(String patientId) throws Exception {
        Object args[]={"ref","=",patientId};
        Vector params = new Vector();
        params.addElement(args);
        return (Object[])openERPClient.search("res.partner", params);
    }

    public void deleteCustomerWithPatientReference(String patientId) throws Exception {
        Object[] customerIds = findCustomerWithPatientReference(patientId);
        Vector params = new Vector();
        params.addElement(customerIds[0]);
        openERPClient.delete("res.partner", params);
    }

    private boolean noCustomersFound(Object[] customers) {
        return customers.length == 0;
    }

    private void raiseDuplicateException(String patientId) throws Exception {
        throw new Exception(String.format("Customer with id %s already exists", patientId));
    }
}

