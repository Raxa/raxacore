package org.bahmni.module.admin.retrospectiveEncounter.service;

import org.bahmni.module.admin.retrospectiveEncounter.domain.DuplicateObservationsMatcher;
import org.bahmni.module.bahmnicore.util.VisitIdentificationHelper;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.List;

public class RetrospectiveEncounterTransactionService {
    private BahmniEncounterTransactionService bahmniEncounterTransactionService;
    protected final VisitIdentificationHelper visitIdentificationHelper;

    public RetrospectiveEncounterTransactionService(BahmniEncounterTransactionService bahmniEncounterTransactionService, VisitService visitService) {
        this.bahmniEncounterTransactionService = bahmniEncounterTransactionService;
        visitIdentificationHelper = new VisitIdentificationHelper(visitService);
    }

    public BahmniEncounterTransaction save(BahmniEncounterTransaction bahmniEncounterTransaction, Patient patient) {
        Visit matchingVisit = visitIdentificationHelper.getVisitFor(patient, bahmniEncounterTransaction.getVisitType(),
                bahmniEncounterTransaction.getEncounterDateTime());

        DuplicateObservationsMatcher duplicateObservationsMatcher = new DuplicateObservationsMatcher(matchingVisit, bahmniEncounterTransaction.getEncounterType());

        List<EncounterTransaction.Observation> observations = bahmniEncounterTransaction.getObservations();
        bahmniEncounterTransaction.setObservations(duplicateObservationsMatcher.getUniqueObservations(observations));

        List<BahmniDiagnosisRequest> bahmniDiagnoses = bahmniEncounterTransaction.getBahmniDiagnoses();
        bahmniEncounterTransaction.setBahmniDiagnoses(duplicateObservationsMatcher.getUniqueDiagnoses(bahmniDiagnoses));

        // TODO : Mujir - this should not happen here. Just set the visitType. BahmniEncounterTransaction should handle string visitTypes.
        bahmniEncounterTransaction.setVisitTypeUuid(matchingVisit.getVisitType().getUuid());

        bahmniEncounterTransaction.setEncounterDateTime(matchingVisit.getStartDatetime());
        bahmniEncounterTransaction.setVisitUuid(matchingVisit.getUuid());

        return bahmniEncounterTransactionService.save(bahmniEncounterTransaction);
    }
}
