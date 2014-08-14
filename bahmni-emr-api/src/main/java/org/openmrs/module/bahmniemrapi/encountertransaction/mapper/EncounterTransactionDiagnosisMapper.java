package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosis;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EncounterTransactionDiagnosisMapper {
    public void populateDiagnosis(BahmniEncounterTransaction bahmniEncounterTransaction) {
        List<EncounterTransaction.Diagnosis> diagnoses = new ArrayList<>();
        for (BahmniDiagnosis bahmniDiagnosis : bahmniEncounterTransaction.getBahmniDiagnoses()) {
            // TODO: (Mihir, Mujir) Move to EMR-API
            bahmniDiagnosis.setDiagnosisDateTime( bahmniDiagnosis.getDiagnosisDateTime() != null ? bahmniDiagnosis.getDiagnosisDateTime() : new Date());
            diagnoses.add(bahmniDiagnosis);
        }
        bahmniEncounterTransaction.setDiagnoses(diagnoses);
    }
}
