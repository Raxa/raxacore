package org.bahmni.openerp.web.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext-Test.xml"})
public class OpenERPServiceIT extends TestCase {

    @Autowired
    OpenERPService openerpService;

    @Test
    public void shouldCreateFindAndDeleteCustomer() throws Exception {
        setUp();

        String name= "Dhara Singh";
        String patientId ="1226789845";
        String village ="Ganiyari";
        openerpService.createCustomer(name, patientId, village);

       openerpService.deleteCustomer(patientId);
    }

//    @Test
//    public void shouldupdateCustomer() throws Exception {
//        setUp();
//
//        String name= "Raman Singh";
//        String patientId ="12245";
//        customerService.updateCustomerReceivables(6,22f);
//    }

}
