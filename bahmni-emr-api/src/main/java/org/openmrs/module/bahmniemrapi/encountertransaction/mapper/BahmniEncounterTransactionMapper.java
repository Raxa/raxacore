package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.openmrs.module.bahmniemrapi.accessionnote.mapper.AccessionNotesMapper;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BahmniEncounterTransactionMapper {
    private EncounterTransactionObsMapper encounterTransactionObsMapper;
    private AccessionNotesMapper validationNotesMapper;
    private BahmniDiagnosisMapper bahmniDiagnosisMapper;
    private ObsRelationshipMapper obsRelationshipMapper;

    @Autowired
    public BahmniEncounterTransactionMapper(AccessionNotesMapper validationNotesMapper, EncounterTransactionObsMapper encounterTransactionObsMapper, BahmniDiagnosisMapper bahmniDiagnosisMapper, ObsRelationshipMapper obsRelationshipMapper) {
        this.encounterTransactionObsMapper = encounterTransactionObsMapper;
        this.validationNotesMapper = validationNotesMapper;
        this.bahmniDiagnosisMapper = bahmniDiagnosisMapper;
        this.obsRelationshipMapper = obsRelationshipMapper;
    }

    public BahmniEncounterTransaction map(EncounterTransaction encounterTransaction) {
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction(encounterTransaction);
        List<BahmniDiagnosisRequest> bahmniDiagnoses = bahmniDiagnosisMapper.map(encounterTransaction.getDiagnoses());
        bahmniEncounterTransaction.setBahmniDiagnoses(bahmniDiagnoses);
        bahmniEncounterTransaction.setAccessionNotes(validationNotesMapper.map(encounterTransaction));
        List<EncounterTransaction.Observation> etObservations = encounterTransactionObsMapper.map(encounterTransaction);
        List<BahmniObservation> bahmniObservations = BahmniObservation.toBahmniObsFromETObs(etObservations);
        bahmniEncounterTransaction.setBahmniObservations(obsRelationshipMapper.map(bahmniObservations,encounterTransaction.getEncounterUuid()));
        return bahmniEncounterTransaction;
    }
}
