package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/calculateDose")
public class DoseCalculatorController extends BaseRestController {

    private DoseCalculatorService doseCaluclatorService;

    @Autowired
    public DoseCalculatorController(DoseCalculatorService doseCaluclatorService) {
        this.doseCaluclatorService = doseCaluclatorService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Double calculateDose(@RequestParam(value = "patientUuid") String patientUuid,
                                @RequestParam(value = "baseDose") Double baseDose,
                                String doseUnits) throws Exception {
        return doseCaluclatorService.calculateDose(patientUuid, baseDose, doseUnits);
    }
}
