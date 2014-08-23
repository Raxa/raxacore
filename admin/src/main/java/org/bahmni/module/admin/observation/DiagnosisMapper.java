package org.bahmni.module.admin.observation;

import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DiagnosisMapper extends ObservationMapper {
    private HashMap<String, EncounterTransaction.Concept> cachedConcepts = new HashMap<>();

    public DiagnosisMapper(ConceptService conceptService) {
        super(conceptService);
    }

    public List<BahmniDiagnosisRequest> getBahmniDiagnosis(EncounterRow encounterRow) throws ParseException {
        List<BahmniDiagnosisRequest> bahmniDiagnoses = new ArrayList<>();
        if (encounterRow.hasDiagnoses()) {
            Date encounterDate = encounterRow.getEncounterDate();
            for (KeyValue uniqueDiagnosisKeyValue : encounterRow.diagnosesRows) {
                BahmniDiagnosisRequest bahmniDiagnosisRequest = createDiagnosis(encounterDate, uniqueDiagnosisKeyValue.getValue());
                bahmniDiagnoses.add(bahmniDiagnosisRequest);
            }
        }
        return bahmniDiagnoses;
    }

    private BahmniDiagnosisRequest createDiagnosis(Date encounterDate, String diagnosis) throws ParseException {
        EncounterTransaction.Concept diagnosisConcept = getDiagnosisConcept(diagnosis);

        BahmniDiagnosisRequest bahmniDiagnosisRequest = new BahmniDiagnosisRequest();
        bahmniDiagnosisRequest.setCodedAnswer(diagnosisConcept);
        bahmniDiagnosisRequest.setOrder(String.valueOf(Diagnosis.Order.PRIMARY));
        bahmniDiagnosisRequest.setCertainty(String.valueOf(Diagnosis.Certainty.CONFIRMED));
        bahmniDiagnosisRequest.setDiagnosisDateTime(encounterDate);
        bahmniDiagnosisRequest.setComments(ObservationMapper.FILE_IMPORT_COMMENT);
        return bahmniDiagnosisRequest;
    }

    private EncounterTransaction.Concept getDiagnosisConcept(String diagnosis) {
        if (!cachedConcepts.containsKey(diagnosis)) {
            cachedConcepts.put(diagnosis, getConcept(diagnosis));
        }
        return cachedConcepts.get(diagnosis);
    }
}