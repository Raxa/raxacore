package org.bahmni.module.admin.observation;

import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.bahmni.module.admin.encounter.DuplicateObservationsMatcher;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.encounter.exception.ConceptNotFoundException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DiagnosisImportService {
    private HashMap<String, EncounterTransaction.Concept> cachedConcepts = new HashMap<>();

    private ConceptService conceptService;

    public DiagnosisImportService(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public List<BahmniDiagnosisRequest> getBahmniDiagnosis(EncounterRow encounterRow, Date visitStartDatetime, DuplicateObservationsMatcher duplicateObservationsMatcher) throws ParseException {
        List<BahmniDiagnosisRequest> bahmniDiagnoses = new ArrayList<>();
        if (encounterRow.getDiagnoses() != null) {
            boolean shouldMatchDiagnosisValue = true;
            List<KeyValue> uniqueDiagnoses = duplicateObservationsMatcher.getUniqueObsRows(getKeyValueForDiagnosis(encounterRow.getDiagnoses()), shouldMatchDiagnosisValue);

            for (KeyValue uniqueDiagnosisKeyValue : uniqueDiagnoses) {
                BahmniDiagnosisRequest bahmniDiagnosisRequest = createDiagnosis(visitStartDatetime, uniqueDiagnosisKeyValue.getValue());
                bahmniDiagnoses.add(bahmniDiagnosisRequest);
            }
        }
        return bahmniDiagnoses;
    }

    private List<KeyValue> getKeyValueForDiagnosis(List<String> diagnoses) {
        List<KeyValue> diagnosisKeyValues = new ArrayList<>();
        for (String diagnosis : diagnoses) {
            diagnosisKeyValues.add(new KeyValue(EmrApiConstants.CONCEPT_CODE_CODED_DIAGNOSIS, diagnosis));
        }
        return diagnosisKeyValues;
    }

    private BahmniDiagnosisRequest createDiagnosis(Date visitStartDatetime, String diagnosis) throws ParseException {
        EncounterTransaction.Concept diagnosisConcept = getDiagnosisConcept(diagnosis);

        BahmniDiagnosisRequest bahmniDiagnosisRequest = new BahmniDiagnosisRequest();
        bahmniDiagnosisRequest.setCodedAnswer(diagnosisConcept);
        bahmniDiagnosisRequest.setOrder(String.valueOf(Diagnosis.Order.PRIMARY));
        bahmniDiagnosisRequest.setCertainty(String.valueOf(Diagnosis.Certainty.CONFIRMED));
        bahmniDiagnosisRequest.setDiagnosisDateTime(visitStartDatetime);
        bahmniDiagnosisRequest.setComments(ObservationImportService.FILE_IMPORT_COMMENT);
        return bahmniDiagnosisRequest;
    }

    private EncounterTransaction.Concept getDiagnosisConcept(String diagnosis) {
        if (!cachedConcepts.containsKey(diagnosis)) {
            Concept diagnosisConcept = conceptService.getConceptByName(diagnosis);
            if(diagnosisConcept == null){
                throw new ConceptNotFoundException("Concept '"+ diagnosis +"' not found");
            }
            cachedConcepts.put(diagnosis, getEncounterTransactionConcept(diagnosisConcept));
        }
        return cachedConcepts.get(diagnosis);
    }

    private static EncounterTransaction.Concept getEncounterTransactionConcept(Concept diagnosisConcept) {
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept();
        concept.setUuid(diagnosisConcept.getUuid());
        return concept;
    }

}