package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.service.DoseCalculatorService;
import org.bahmni.module.bahmnicore.service.impl.Dose;
import org.bahmni.module.bahmnicore.service.impl.Dose.CalculatedDoseUnit;
import org.openmrs.api.APIException;
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
    public Dose calculateDose(@RequestParam(value = "patientUuid") String patientUuid,
                              @RequestParam(value = "baseDose") Double baseDose,
                              @RequestParam(value = "doseUnit") String stringDoseUnit) throws Exception {
        CalculatedDoseUnit calculatedDoseUnit = Dose.CalculatedDoseUnit.getConstant(stringDoseUnit);
        if(null== calculatedDoseUnit){
            String errMessage = "Dose Calculator not found for given doseUnits (" + stringDoseUnit + ").";
            throw new APIException(errMessage);
        }
        return doseCaluclatorService.calculateDose(patientUuid, baseDose, calculatedDoseUnit);
    }
}
