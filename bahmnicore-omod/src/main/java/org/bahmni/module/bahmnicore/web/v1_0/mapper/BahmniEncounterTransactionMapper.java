package org.bahmni.module.bahmnicore.web.v1_0.mapper;

import org.bahmni.module.bahmnicore.contract.encounter.request.BahmniDiagnosisRequest;
import org.bahmni.module.bahmnicore.contract.encounter.request.BahmniEncounterTransaction;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.ArrayList;
import java.util.List;

public class BahmniEncounterTransactionMapper {
    private ObsService obsService;
    private EncounterTransactionMapper encounterTransactionMapper;

    public BahmniEncounterTransactionMapper(ObsService obsService, EncounterTransactionMapper encounterTransactionMapper) {
        this.obsService = obsService;
        this.encounterTransactionMapper = encounterTransactionMapper;
    }

    public BahmniEncounterTransaction map(EncounterTransaction encounterTransaction) {
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction(encounterTransaction);
        List<BahmniDiagnosisRequest> bahmniDiagnoses = new ArrayList<>();
        for (EncounterTransaction.Diagnosis diagnosis : encounterTransaction.getDiagnoses()) {
            bahmniDiagnoses.add(mapBahmniDiagnosis(diagnosis));
        }
        bahmniEncounterTransaction.setBahmniDiagnoses(bahmniDiagnoses);
        return bahmniEncounterTransaction;
    }

    public BahmniDiagnosisRequest mapBahmniDiagnosis(EncounterTransaction.Diagnosis diagnosis) {
        BahmniDiagnosisRequest bahmniDiagnosis = mapBasicDiagnosis(diagnosis);
        bahmniDiagnosis.setExistingObs(diagnosis.getExistingObs());

        Obs diagnosisObsGroup = obsService.getObsByUuid(diagnosis.getExistingObs());
        Obs statusObs = findObs(diagnosisObsGroup, BahmniDiagnosisHelper.BAHMNI_DIAGNOSIS_STATUS);
        Concept statusConcept = statusObs.getValueCoded();
        if (statusConcept != null) {
            bahmniDiagnosis.setDiagnosisStatusConcept(new EncounterTransaction.Concept(statusConcept.getUuid(), statusConcept.getName().getName()));
        }

        String initialDiagnosisObsGroupUuid = findObs(diagnosisObsGroup, BahmniDiagnosisHelper.BAHMNI_INITIAL_DIAGNOSIS).getValueText();
        if (!initialDiagnosisObsGroupUuid.equals(diagnosisObsGroup.getUuid())) {
            Obs initialDiagnosisObsGroup = obsService.getObsByUuid(initialDiagnosisObsGroupUuid);
            EncounterTransaction encounterTransactionWithInitialDiagnosis = encounterTransactionMapper.map(initialDiagnosisObsGroup.getEncounter(), true);
            EncounterTransaction.Diagnosis initialDiagnosis = findInitialDiagnosis(encounterTransactionWithInitialDiagnosis, initialDiagnosisObsGroup);
            bahmniDiagnosis.setFirstDiagnosis(mapBahmniDiagnosis(initialDiagnosis));
        }

        Obs revisedObs = findObs(diagnosisObsGroup, BahmniDiagnosisHelper.BAHMNI_DIAGNOSIS_REVISED);
        bahmniDiagnosis.setRevised(revisedObs.getValueAsBoolean());

        bahmniDiagnosis.setEncounterUuid(diagnosisObsGroup.getEncounter().getUuid());
        return bahmniDiagnosis;
    }

    private BahmniDiagnosisRequest mapBasicDiagnosis(EncounterTransaction.Diagnosis diagnosis) {
        BahmniDiagnosisRequest bahmniDiagnosis = new BahmniDiagnosisRequest();
        bahmniDiagnosis.setCertainty(diagnosis.getCertainty());
        bahmniDiagnosis.setCodedAnswer(diagnosis.getCodedAnswer());
        bahmniDiagnosis.setFreeTextAnswer(diagnosis.getFreeTextAnswer());
        bahmniDiagnosis.setOrder(diagnosis.getOrder());
        bahmniDiagnosis.setExistingObs(diagnosis.getExistingObs());
        bahmniDiagnosis.setDiagnosisDateTime(diagnosis.getDiagnosisDateTime());
        bahmniDiagnosis.setProviders(diagnosis.getProviders());
        return bahmniDiagnosis;
    }

    private Obs findObs(Obs diagnosisObs, String conceptName) {
        for (Obs o : diagnosisObs.getGroupMembers()) {
            if (o.getConcept().hasName(conceptName, null)) {
                return o;
            }
        }
        throw new AssertionError(String.format("Diagnosis found without meta-data for %s, diagnosisObsUUID: %s", conceptName, diagnosisObs.getUuid()));
    }

    private EncounterTransaction.Diagnosis findInitialDiagnosis(EncounterTransaction encounterTransactionWithInitialDiagnosis, Obs initialDiagnosisObs) {
        for (EncounterTransaction.Diagnosis diagnosis : encounterTransactionWithInitialDiagnosis.getDiagnoses()) {
            if (diagnosis.getExistingObs().equals(initialDiagnosisObs.getUuid()))
                return diagnosis;
        }
        throw new AssertionError(String.format("Initial Diagnosis not found for: %s", initialDiagnosisObs.getUuid()));
    }


}
