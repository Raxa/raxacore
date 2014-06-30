package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.model.BahmniVisit.LabOrderResults;
import org.bahmni.module.bahmnicore.service.impl.LabOrderResultsService;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/labOrderResults")
public class BahmniLabOrderResultController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private LabOrderResultsService labOrderResultsService;


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public LabOrderResults getForPatient(@RequestParam(value = "patientUuid", required = true) String patientUuid) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        return labOrderResultsService.getAll(patient);
    }
}
