package org.bahmni.module.admin.observation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "adminDiagnosisMapper")
public class DiagnosisMapper {

    private final ConceptCache conceptCache;

    @Autowired
    public DiagnosisMapper(ConceptService conceptService) {
        this.conceptCache = new ConceptCache(conceptService);
    }

    public List<BahmniDiagnosisRequest> getBahmniDiagnosis(EncounterRow encounterRow) throws ParseException {
        List<BahmniDiagnosisRequest> bahmniDiagnoses = new ArrayList<>();
        if (encounterRow.hasDiagnoses()) {
            Date encounterDate = encounterRow.getEncounterDate();
            for (KeyValue uniqueDiagnosisKeyValue : encounterRow.diagnosesRows) {
                String diagnosis = uniqueDiagnosisKeyValue.getValue();
                if (StringUtils.isNotBlank(diagnosis)) {
                    BahmniDiagnosisRequest bahmniDiagnosisRequest = createDiagnosis(encounterDate, diagnosis);
                    bahmniDiagnoses.add(bahmniDiagnosisRequest);
                }
            }
        }
        return bahmniDiagnoses;
    }

    private BahmniDiagnosisRequest createDiagnosis(Date encounterDate, String diagnosis) throws ParseException {
        Concept obsConcept = conceptCache.getConcept(diagnosis);
        EncounterTransaction.Concept diagnosisConcept = new EncounterTransaction.Concept(obsConcept.getUuid(), obsConcept.getName().getName());

        BahmniDiagnosisRequest bahmniDiagnosisRequest = new BahmniDiagnosisRequest();
        bahmniDiagnosisRequest.setCodedAnswer(diagnosisConcept);
        bahmniDiagnosisRequest.setOrder(String.valueOf(Diagnosis.Order.PRIMARY));
        bahmniDiagnosisRequest.setCertainty(String.valueOf(Diagnosis.Certainty.CONFIRMED));
        bahmniDiagnosisRequest.setDiagnosisDateTime(encounterDate);
        return bahmniDiagnosisRequest;
    }
}