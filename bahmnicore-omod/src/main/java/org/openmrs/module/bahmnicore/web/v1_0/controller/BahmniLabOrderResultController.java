package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.dao.OrderDao;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResults;
import org.openmrs.module.bahmniemrapi.laborder.service.LabOrderResultsService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/labOrderResults")
public class BahmniLabOrderResultController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private LabOrderResultsService labOrderResultsService;


    @RequestMapping(method = RequestMethod.GET, params = {"patientUuid"})
    @ResponseBody
    public LabOrderResults getForPatient(
            @RequestParam(value = "patientUuid", required = true) String patientUuid,
            @RequestParam(value = "numberOfVisits", required = false) Integer numberOfVisits) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        List<Visit> visits = null;
        if(numberOfVisits != null) {
            visits = orderDao.getVisitsWithOrders(patient, "TestOrder", true, numberOfVisits);
        }
        return labOrderResultsService.getAll(patient, visits);
    }
}
