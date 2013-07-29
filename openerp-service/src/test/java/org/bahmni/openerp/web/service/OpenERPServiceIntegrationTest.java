package org.bahmni.openerp.web.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext-Test.xml"})
public class OpenERPServiceIntegrationTest extends TestCase {

    @Autowired
    OpenERPService openerpService;

    @Test
    public void shouldCreateFindAndDeleteCustomer() throws Exception {
        setUp();

        String name= "Mario Areias";
        String patientId ="122678984333";
        String village ="Ganiyari";
        openerpService.createCustomer(name, patientId, village);

        assertEquals(openerpService.findCustomers(patientId).length, 1);

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
