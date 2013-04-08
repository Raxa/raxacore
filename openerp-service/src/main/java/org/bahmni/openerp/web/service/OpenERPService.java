package org.bahmni.openerp.web.service;


import org.apache.log4j.Logger;
import org.bahmni.module.billing.BillingService;
import org.bahmni.openerp.web.client.OpenERPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Vector;

@Service
public class OpenERPService implements BillingService {
    public @Value("${host}") String host;
    public @Value("${port}") int port;
    public @Value("${database}") String database;
    public @Value("${user}") String user;
    public @Value("${password}") String password;


    OpenERPClient openERPClient;
    private static Logger logger =Logger.getLogger("OpenERPService") ;

    @Autowired
    public OpenERPService(OpenERPClient client){
        this.openERPClient = client;
    }

    public void createCustomer(String name, String patientId) throws Exception {
        if(noCustomersFound(findCustomerWithPatientReference(patientId))){
            openERPClient.create("res.partner",name, patientId);
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
        logger.error("Customer with "+patientId+" already exists");
        throw new Exception("Customer with "+patientId+" already exists");
    }
}

