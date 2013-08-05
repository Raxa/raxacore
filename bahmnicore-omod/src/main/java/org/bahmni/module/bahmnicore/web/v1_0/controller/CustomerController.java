package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.billing.BillingService;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/rest/v1/bahmnicore/customer")
public class CustomerController extends BaseRestController {
    private BillingService billingService;


    @Autowired
    public CustomerController(BillingService billingService) {
        this.billingService = billingService;
    }

    // TODO : Mujir - remove this method in Release 2 when OpenMRS would talk to OpenERP using atom feed events.
    // TODO : As MRS wont talk to ERP directly, we wont need this diagnostic service
    @RequestMapping(method = RequestMethod.GET, params = { "patientId"})
    @WSDoc("Returns customer ids by given patient id (Used for diagnostics)")
    @ResponseBody
	public Object[] search(@RequestParam String patientId) {
        return billingService.findCustomers(patientId);
    }
}