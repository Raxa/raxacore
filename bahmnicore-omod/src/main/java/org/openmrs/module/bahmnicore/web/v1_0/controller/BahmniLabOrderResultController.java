package org.openmrs.module.bahmnicore.web.v1_0.controller;

import java.util.List;
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

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/labOrderResults")
public class BahmniLabOrderResultController {
    private PatientService patientService;
    private OrderDao orderDao;
    private LabOrderResultsService labOrderResultsService;

    @Autowired
    public BahmniLabOrderResultController(PatientService patientService,
                                          OrderDao orderDao,
                                          LabOrderResultsService labOrderResultsService) {
        this.patientService = patientService;
        this.orderDao = orderDao;
        this.labOrderResultsService = labOrderResultsService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"visitUuids"})
    @ResponseBody
    public LabOrderResults getForVisitUuids(
            @RequestParam(value = "visitUuids", required = true) String[] visitUuids) {
        List<Visit> visits = orderDao.getVisitsForUUids(visitUuids);
        return labOrderResultsService.getAll(patientFrom(visits), visits);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"patientUuid", "numberOfVisits"})
    @ResponseBody
    public LabOrderResults getForPatient(
            @RequestParam(value = "patientUuid", required = true) String patientUuid,
            @RequestParam(value = "numberOfVisits", required = true) Integer numberOfVisits) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        List<Visit> visits = null;
        if (numberOfVisits != null) {
            visits = orderDao.getVisitsWithOrders(patient, "TestOrder", true, numberOfVisits);
        }
        return labOrderResultsService.getAll(patient, visits);
    }

    private Patient patientFrom(List<Visit> visits) {
        return visits.get(0).getPatient();
    }
}
