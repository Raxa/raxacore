package org.openmrs.module.bahmnicore.web.v1_0.controller;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
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
    @Autowired
    private EmrApiProperties emrApiProperties;
    @Autowired
    private ObsService obsService;
    @Autowired
    private VisitService visitService;
    private static final Log log = LogFactory.getLog(DiagnosisService.class);


    @RequestMapping(method = RequestMethod.GET, value = "search")
    @ResponseBody
    public List<BahmniDiagnosisRequest> search(@RequestParam("patientUuid") String patientUuid, @RequestParam(value = "fromDate", required = false) String date, String visitUuid) throws Exception {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        Date fromDate = new DateMapper().toDate(date);
        List<EncounterTransaction.Diagnosis> pastDiagnoses = null;
        if (visitUuid != null) {
            pastDiagnoses = diagnosisMapper.convert(getDiagnoses(patient, visitUuid));

        } else {
            pastDiagnoses = diagnosisMapper.convert(diagnosisService.getDiagnoses(patient, fromDate));
        }

        List<BahmniDiagnosisRequest> bahmniDiagnoses = new ArrayList<>();
        List<BahmniDiagnosisRequest> mappedBahmniDiagnoses = bahmniDiagnosisMapper.map(pastDiagnoses);
        for (BahmniDiagnosisRequest mappedBahmniDiagnose : mappedBahmniDiagnoses) {
            if (!mappedBahmniDiagnose.isRevised()) {
                bahmniDiagnoses.add(mappedBahmniDiagnose);
            }
        }
        return bahmniDiagnoses;
    }

//    TODO : This fix was added 3 hours before finalizing candidate build for the release.
//    TODO : This is copy/pasted code from emrapi and needs to be pushed there at some future point in time
    public List<Diagnosis> getDiagnoses(Patient patient, String visitUuid) {
        List<Diagnosis> diagnoses = new ArrayList<Diagnosis>();

        DiagnosisMetadata diagnosisMetadata = emrApiProperties.getDiagnosisMetadata();

        Visit visit = visitService.getVisitByUuid(visitUuid);
        List<Obs> observations = obsService.getObservations(Arrays.asList((Person) patient), new ArrayList<Encounter>(visit.getEncounters()), Arrays.asList(diagnosisMetadata.getDiagnosisSetConcept()),
                null, null, null, Arrays.asList("obsDatetime"),
                null, null, null, null, false);

        for (Obs obs : observations) {
            Diagnosis diagnosis;
            try {
                diagnosis = diagnosisMetadata.toDiagnosis(obs);
            } catch (Exception ex) {
                log.warn("Error trying to interpret " + obs + " as a diagnosis");
                if (log.isDebugEnabled()) {
                    log.debug("Detailed error", ex);
                }
                continue;
            }

            Collection<Concept> nonDiagnosisConcepts = emrApiProperties.getSuppressedDiagnosisConcepts();
            Collection<Concept> nonDiagnosisConceptSets = emrApiProperties.getNonDiagnosisConceptSets();

            Set<Concept> filter = new HashSet<Concept>();
            filter.addAll(nonDiagnosisConcepts);
            for (Concept conceptSet : nonDiagnosisConceptSets) {
                filter.addAll(conceptSet.getSetMembers());
            }

            if (!filter.contains(diagnosis.getDiagnosis().getCodedAnswer())) {
                diagnoses.add(diagnosis);
            }
        }

        return diagnoses;
    }


}
