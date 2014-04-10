package org.bahmni.module.bahmnicore.web.v1_0.mapper;

import org.bahmni.module.bahmnicore.contract.encounter.request.BahmniDiagnosis;
import org.bahmni.module.bahmnicore.contract.encounter.request.BahmniEncounterTransaction;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.ArrayList;
import java.util.List;

public class EncounterTransactionDiagnosisMapper {
    public void populateDiagnosis(BahmniEncounterTransaction bahmniEncounterTransaction) {
        List<EncounterTransaction.Diagnosis> diagnoses = new ArrayList<>();
        for (BahmniDiagnosis bahmniDiagnosis : bahmniEncounterTransaction.getBahmniDiagnoses()) {
            diagnoses.add(bahmniDiagnosis);
        }
        bahmniEncounterTransaction.setDiagnoses(diagnoses);
    }
}
