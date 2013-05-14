package org.bahmni.openerp.web.service;

import org.apache.log4j.Logger;
import org.bahmni.openerp.web.OpenERPException;
import org.bahmni.openerp.web.client.OpenERPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Vector;

@Service
public class CustomerAccountService {
    OpenERPClient openERPClient;
    private static Logger logger = Logger.getLogger(CustomerAccountService.class);


    @Autowired
    public CustomerAccountService(OpenERPClient client) {
        this.openERPClient = client;
    }

    public void updateCustomerReceivables(String patientId, double amount) {
        boolean success = tryUpdateReceivables(patientId, amount);
        if (!success){
            logger.info("Retrying ");
            success = tryUpdateReceivables(patientId, amount);
        }
        if(!success)
            throw new OpenERPException(String.format("[%s] : Account Receivable update failed after 2 retries for amount of %s", patientId, amount));
    }

    public boolean tryUpdateReceivables(String patientId, double amount){
        Object args1[] = {"partner_id", patientId};
        Object args2[] = {"amount", amount};
        Vector params = new Vector();
        params.addElement(args1);
        params.addElement(args2);

        try {
            openERPClient.updateCustomerReceivables("account.receivables", params);
            return true ;
        } catch (Exception exception) {
            logger.error(String.format("[%s] : Account Receivable update failed for amount of %s", patientId, amount) + exception);
            return false;
        }


    }
}
