package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.service.BahmniDiagnosisService;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Visit;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniDiagnosisMapper;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.diagnosis.DiagnosisMetadata;
import org.openmrs.module.emrapi.diagnosis.DiagnosisService;
import org.openmrs.module.emrapi.encounter.DateMapper;
import org.openmrs.module.emrapi.encounter.DiagnosisMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/diagnosis")
public class BahmniDiagnosisController extends BaseRestController {

    @Autowired
    private BahmniDiagnosisService bahmniDiagnosisService;

    @RequestMapping(method = RequestMethod.GET, value = "search")
    @ResponseBody
    public List<BahmniDiagnosisRequest> search(@RequestParam("patientUuid") String patientUuid, @RequestParam(value = "fromDate", required = false) String date, String visitUuid) throws Exception {
        if(visitUuid!=null){
            return bahmniDiagnosisService.getBahmniDiagnosisByPatientAndVisit(patientUuid,visitUuid);
        }else{
            return bahmniDiagnosisService.getBahmniDiagnosisByPatientAndDate(patientUuid,date);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "delete")
    @ResponseBody
    public boolean delete(@RequestParam(value = "obsUuid", required = true) String obsUuid) throws Exception {
        bahmniDiagnosisService.delete(obsUuid);
        return true;
    }
}
