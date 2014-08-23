package org.bahmni.module.admin.encounter;

import org.bahmni.module.admin.csv.models.EncounterRow;
import org.bahmni.module.admin.observation.DiagnosisMapper;
import org.bahmni.module.admin.observation.ObservationMapper;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.text.ParseException;
import java.util.List;

public class BahmniEncounterTransactionImportService {

    private EncounterService encounterService;
    private final ObservationMapper observationService;
    private final DiagnosisMapper diagnosisService;

    public BahmniEncounterTransactionImportService(EncounterService encounterService,
                                                   ObservationMapper observationService, DiagnosisMapper diagnosisService) {
        this.encounterService = encounterService;
        this.observationService = observationService;
        this.diagnosisService = diagnosisService;
    }

    public BahmniEncounterTransaction getBahmniEncounterTransaction(EncounterRow encounterRow, Patient patient) throws ParseException {
        EncounterType requestedEncounterType = encounterService.getEncounterType(encounterRow.encounterType);
        if (requestedEncounterType == null) {
            throw new RuntimeException("Encounter type:'" + encounterRow.encounterType + "' not found.");
        }

        List<EncounterTransaction.Observation> allObservations = observationService.getObservations(encounterRow);
        List<BahmniDiagnosisRequest> allDiagnosis = diagnosisService.getBahmniDiagnosis(encounterRow);

        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();
        bahmniEncounterTransaction.setPatientUuid(patient.getUuid());
        bahmniEncounterTransaction.setBahmniDiagnoses(allDiagnosis);
        bahmniEncounterTransaction.setObservations(allObservations);
        bahmniEncounterTransaction.setEncounterDateTime(encounterRow.getEncounterDate());
        bahmniEncounterTransaction.setEncounterType(encounterRow.encounterType);
        bahmniEncounterTransaction.setVisitType(encounterRow.visitType);

        return bahmniEncounterTransaction;
    }

}
