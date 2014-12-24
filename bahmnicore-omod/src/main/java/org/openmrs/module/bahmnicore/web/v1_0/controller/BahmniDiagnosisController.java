package org.openmrs.module.bahmnicore.web.v1_0.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniDiagnosisMapper;
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

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/diagnosis")
public class BahmniDiagnosisController extends BaseRestController {

    @Autowired
    private PatientService patientService;
    @Autowired
    private DiagnosisService diagnosisService;
    @Autowired
    private DiagnosisMapper diagnosisMapper;
    @Autowired
    private BahmniDiagnosisMapper bahmniDiagnosisMapper;


    @RequestMapping(method = RequestMethod.GET, value = "search")
    @ResponseBody
    public List<BahmniDiagnosisRequest> search(@RequestParam("patientUuid") String patientUuid, @RequestParam(value = "fromDate", required = false) String date) throws Exception {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        Date fromDate = new DateMapper().toDate(date);
        List<EncounterTransaction.Diagnosis> pastDiagnoses = diagnosisMapper.convert(diagnosisService.getDiagnoses(patient, fromDate));

        List<BahmniDiagnosisRequest> bahmniDiagnoses = new ArrayList<>();
        List<BahmniDiagnosisRequest> mappedBahmniDiagnoses = bahmniDiagnosisMapper.map(pastDiagnoses);
        for (BahmniDiagnosisRequest mappedBahmniDiagnose : mappedBahmniDiagnoses) {
            if (!mappedBahmniDiagnose.isRevised()) {
                bahmniDiagnoses.add(mappedBahmniDiagnose);
            }
        }
        return bahmniDiagnoses;
    }
}
