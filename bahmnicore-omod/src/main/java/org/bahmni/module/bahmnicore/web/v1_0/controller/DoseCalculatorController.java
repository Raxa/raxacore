package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.openmrs.api.APIException;
import org.openmrs.module.rulesengine.domain.Dose;
import org.openmrs.module.rulesengine.rule.BSABasedDoseRule;
import org.openmrs.module.rulesengine.rule.WeightBasedDoseRule;
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
                              @RequestParam(value = "doseUnit") String doseUnit) throws Exception {
        if("mg/kg".equals(doseUnit)){
            return WeightBasedDoseRule.calculateDose(patientUuid, baseDose);
        }
        if("mg/m2".equals(doseUnit)){
            return BSABasedDoseRule.calculateDose(patientUuid, baseDose);
        }
        String errMessage = "Dosing Rule not found for given doseUnits (" + doseUnit + ").";
        throw new APIException(errMessage);
    }
}
