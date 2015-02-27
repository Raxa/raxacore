package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.module.bahmniemrapi.accessionnote.mapper.AccessionNotesMapper;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.parameters.AdditionalBahmniObservationFields;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BahmniEncounterTransactionMapper {
    private AccessionNotesMapper accessionNotesMapper;
    private BahmniDiagnosisMapper bahmniDiagnosisMapper;
    private ObsRelationshipMapper obsRelationshipMapper;
    private PatientService patientService;
    private EncounterService encounterService;
    private ETObsToBahmniObsMapper fromETObsToBahmniObs;

    @Autowired
    public BahmniEncounterTransactionMapper(AccessionNotesMapper accessionNotesMapper,
                                            BahmniDiagnosisMapper bahmniDiagnosisMapper,
                                            ObsRelationshipMapper obsRelationshipMapper,
                                            PatientService patientService,
                                            EncounterService encounterService,
                                            ETObsToBahmniObsMapper fromETObsToBahmniObs) {
        this.accessionNotesMapper = accessionNotesMapper;
        this.bahmniDiagnosisMapper = bahmniDiagnosisMapper;
        this.obsRelationshipMapper = obsRelationshipMapper;
        this.patientService = patientService;
        this.encounterService = encounterService;
        this.fromETObsToBahmniObs = fromETObsToBahmniObs;
    }

    public BahmniEncounterTransaction map(EncounterTransaction encounterTransaction) {
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction(encounterTransaction);
        List<BahmniDiagnosisRequest> bahmniDiagnoses = bahmniDiagnosisMapper.map(encounterTransaction.getDiagnoses());
        bahmniEncounterTransaction.setBahmniDiagnoses(bahmniDiagnoses);
        bahmniEncounterTransaction.setAccessionNotes(accessionNotesMapper.map(encounterTransaction));
        AdditionalBahmniObservationFields additionalBahmniObservationFields = new AdditionalBahmniObservationFields(encounterTransaction.getEncounterUuid(), encounterTransaction.getEncounterDateTime(), null,null);
        additionalBahmniObservationFields.setProviders(encounterTransaction.getProviders());
        List<BahmniObservation> bahmniObservations = fromETObsToBahmniObs.create(encounterTransaction.getObservations(), additionalBahmniObservationFields);
        bahmniEncounterTransaction.setObservations(obsRelationshipMapper.map(bahmniObservations, encounterTransaction.getEncounterUuid()));
        addPatientIdentifier(bahmniEncounterTransaction, encounterTransaction);
        addEncounterType(encounterTransaction, bahmniEncounterTransaction);
        return bahmniEncounterTransaction;
    }

    private void addEncounterType(EncounterTransaction encounterTransaction, BahmniEncounterTransaction bahmniEncounterTransaction) {
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(encounterTransaction.getEncounterTypeUuid());
        if (encounterType != null) {
            bahmniEncounterTransaction.setEncounterType(encounterType.getName());
        }
    }

    private void addPatientIdentifier(BahmniEncounterTransaction bahmniEncounterTransaction, EncounterTransaction encounterTransaction) {
        Patient patient = patientService.getPatientByUuid(encounterTransaction.getPatientUuid());
        if (patient != null) {
            PatientIdentifier patientIdentifier = patient.getPatientIdentifier();
            bahmniEncounterTransaction.setPatientId(patientIdentifier.getIdentifier());
        }
    }
}
