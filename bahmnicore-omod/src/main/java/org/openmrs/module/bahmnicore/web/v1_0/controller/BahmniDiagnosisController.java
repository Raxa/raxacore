package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.encounter.request.BahmniDiagnosisRequest;
import org.openmrs.Patient;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.module.bahmnicore.web.v1_0.mapper.AccessionNotesMapper;
import org.openmrs.module.bahmnicore.web.v1_0.mapper.BahmniEncounterTransactionMapper;
import org.openmrs.module.bahmnicore.web.v1_0.mapper.BahmniObsMapper;
import org.openmrs.module.emrapi.diagnosis.DiagnosisService;
import org.openmrs.module.emrapi.encounter.DateMapper;
import org.openmrs.module.emrapi.encounter.DiagnosisMapper;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
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
import java.util.Date;
import java.util.List;

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
    private ObsService obsService;
    @Autowired
    private EncounterTransactionMapper encounterTransactionMapper;
    @Autowired
    private AccessionNotesMapper accessionNotesMapper;
    @Autowired
    private BahmniObsMapper bahmniObsMapper;


    @RequestMapping(method = RequestMethod.GET, value = "search")
    @ResponseBody
    public List<BahmniDiagnosisRequest> search(@RequestParam("patientUuid") String patientUuid, @RequestParam(value = "fromDate", required = false) String date) throws Exception {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        Date fromDate = new DateMapper().toDate(date);
        List<EncounterTransaction.Diagnosis> pastDiagnoses = diagnosisMapper.convert(diagnosisService.getDiagnoses(patient, fromDate));

        List<BahmniDiagnosisRequest> bahmniDiagnoses = new ArrayList<>();
        for (EncounterTransaction.Diagnosis diagnosis : pastDiagnoses) {
            BahmniDiagnosisRequest bahmniDiagnosisRequest = new BahmniEncounterTransactionMapper(obsService, encounterTransactionMapper, accessionNotesMapper, bahmniObsMapper).mapBahmniDiagnosis(diagnosis);
            if (!bahmniDiagnosisRequest.isRevised()) {
                bahmniDiagnoses.add(bahmniDiagnosisRequest);
            }
        }
        return bahmniDiagnoses;
    }
}
