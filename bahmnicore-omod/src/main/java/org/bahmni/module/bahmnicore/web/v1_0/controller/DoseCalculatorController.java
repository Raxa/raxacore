package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.rulesengine.contract.Dose;
import org.openmrs.module.rulesengine.service.DoseRuleService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/calculateDose")
public class DoseCalculatorController extends BaseRestController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Dose calculateDose(@RequestParam(value = "patientUuid") String patientUuid,
                              @RequestParam(value = "baseDose") Double baseDose,
                              @RequestParam(value = "doseUnit") String stringDoseUnit) throws Exception {
        DoseRuleService doseRuleService = Context.getService(DoseRuleService.class);
        return doseRuleService.calculateDose(patientUuid, baseDose, stringDoseUnit);
    }
}
