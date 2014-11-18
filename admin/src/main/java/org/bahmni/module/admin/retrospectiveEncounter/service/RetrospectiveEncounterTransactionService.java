package org.bahmni.module.admin.retrospectiveEncounter.service;

import org.bahmni.module.admin.retrospectiveEncounter.domain.DuplicateObservationsMatcher;
import org.bahmni.module.bahmnicore.util.VisitIdentificationHelper;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class RetrospectiveEncounterTransactionService {
    private BahmniEncounterTransactionService bahmniEncounterTransactionService;
    protected final VisitIdentificationHelper visitIdentificationHelper;

    @Autowired
    public RetrospectiveEncounterTransactionService(BahmniEncounterTransactionService bahmniEncounterTransactionService, VisitService visitService) {
        this.bahmniEncounterTransactionService = bahmniEncounterTransactionService;
        visitIdentificationHelper = new VisitIdentificationHelper(visitService);
    }

    public BahmniEncounterTransaction save(BahmniEncounterTransaction bahmniEncounterTransaction, Patient patient, Date visitStartDate, Date visitEndDate) {
        Visit matchingVisit = visitIdentificationHelper.getVisitFor(patient, bahmniEncounterTransaction.getVisitType(),
                bahmniEncounterTransaction.getEncounterDateTime(), visitStartDate, visitEndDate);

        DuplicateObservationsMatcher duplicateObservationsMatcher = new DuplicateObservationsMatcher(matchingVisit, bahmniEncounterTransaction.getEncounterType());
        List<BahmniObservation> uniqueObservations = duplicateObservationsMatcher.getUniqueBahmniObservations(bahmniEncounterTransaction.getObservations());
        List<BahmniDiagnosisRequest> uniqueDiagnoses = duplicateObservationsMatcher.getUniqueDiagnoses(bahmniEncounterTransaction.getBahmniDiagnoses());

        bahmniEncounterTransaction = updateBahmniEncounterTransaction(bahmniEncounterTransaction, matchingVisit, uniqueObservations, uniqueDiagnoses);

        return bahmniEncounterTransactionService.save(bahmniEncounterTransaction);
    }

    private BahmniEncounterTransaction updateBahmniEncounterTransaction(BahmniEncounterTransaction bahmniEncounterTransaction,
                    Visit visit, List<BahmniObservation> uniqueObservations, List<BahmniDiagnosisRequest> uniqueDiagnoses) {
        bahmniEncounterTransaction.setObservations(uniqueObservations);
        bahmniEncounterTransaction.setBahmniDiagnoses(uniqueDiagnoses);
        bahmniEncounterTransaction.setEncounterDateTime(visit.getStartDatetime());
        bahmniEncounterTransaction.setVisitUuid(visit.getUuid());

        // TODO : Mujir - this should not happen here. Just set the visitType. BahmniEncounterTransaction should handle string visitTypes.
        bahmniEncounterTransaction.setVisitTypeUuid(visit.getVisitType().getUuid());

        return bahmniEncounterTransaction;
    }
}
