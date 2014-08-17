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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiagnosisImportService {
    public static final String BAHMNI_DIAGNOSIS_CONCEPT_NAME = EmrApiConstants.CONCEPT_CODE_CODED_DIAGNOSIS;
    private HashMap<String, EncounterTransaction.Concept> cachedConcepts = new HashMap<>();

    private ConceptService conceptService;

    public DiagnosisImportService(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public List<BahmniDiagnosisRequest> getBahmniDiagnosis(EncounterRow encounterRow, DuplicateObservationsMatcher duplicateObservationsMatcher) throws ParseException {
        List<BahmniDiagnosisRequest> bahmniDiagnoses = new ArrayList<>();
        if (encounterRow.getDiagnoses() != null) {
            boolean shouldMatchDiagnosisValue = true;
            List<KeyValue> matchingDiagnosisKeyValue = duplicateObservationsMatcher.matchingObservations(getKeyValueForDiagnosis(encounterRow.getDiagnoses()), shouldMatchDiagnosisValue);

            List<String> diagnoses = encounterRow.getDiagnoses();
            for (String diagnosis : diagnoses) {
                if (shouldIgnoreDiagnosis(matchingDiagnosisKeyValue, diagnosis)) {
                    continue;
                }
                BahmniDiagnosisRequest bahmniDiagnosisRequest = createDiagnosis(encounterRow, diagnosis);
                bahmniDiagnoses.add(bahmniDiagnosisRequest);
            }
        }
        return bahmniDiagnoses;
    }

    private List<KeyValue> getKeyValueForDiagnosis(List<String> diagnoses) {
        List<KeyValue> diagnosisKeyValues = new ArrayList<>();
        for (String diagnosis : diagnoses) {
            diagnosisKeyValues.add(new KeyValue(BAHMNI_DIAGNOSIS_CONCEPT_NAME, diagnosis));
        }
        return diagnosisKeyValues;
    }

    private BahmniDiagnosisRequest createDiagnosis(EncounterRow encounterRow, String diagnosis) throws ParseException {
        EncounterTransaction.Concept diagnosisConcept = getDiagnosisConcept(diagnosis);

        BahmniDiagnosisRequest bahmniDiagnosisRequest = new BahmniDiagnosisRequest();
        bahmniDiagnosisRequest.setCodedAnswer(diagnosisConcept);
        bahmniDiagnosisRequest.setOrder(String.valueOf(Diagnosis.Order.PRIMARY));
        bahmniDiagnosisRequest.setCertainty(String.valueOf(Diagnosis.Certainty.CONFIRMED));
        bahmniDiagnosisRequest.setDiagnosisDateTime(encounterRow.getEncounterDate());
        bahmniDiagnosisRequest.setComments(ObservationImportService.FILE_IMPORT_COMMENT);
        return bahmniDiagnosisRequest;
    }

    private boolean shouldIgnoreDiagnosis(List<KeyValue> matchingDiagnosisKeyValue, String diagnosis) {
        return matchingDiagnosisKeyValue.contains(new KeyValue(BAHMNI_DIAGNOSIS_CONCEPT_NAME, diagnosis));
    }

    private EncounterTransaction.Concept getDiagnosisConcept(String diagnosis) {
        if (!cachedConcepts.containsKey(diagnosis)) {
            Concept diagnosisConcept = conceptService.getConceptByName(diagnosis);
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