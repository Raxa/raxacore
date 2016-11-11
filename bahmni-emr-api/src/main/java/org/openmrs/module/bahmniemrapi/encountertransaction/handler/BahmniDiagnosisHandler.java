package org.openmrs.module.bahmniemrapi.encountertransaction.handler;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosis;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.diagnosis.helper.BahmniDiagnosisMetadata;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.encounter.postprocessor.EncounterTransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class BahmniDiagnosisHandler implements EncounterTransactionHandler {
    private BahmniDiagnosisMetadata bahmniDiagnosisMetadata;
    private ObsService obsService;
    private ConceptService conceptService;


    @Autowired
    public BahmniDiagnosisHandler(BahmniDiagnosisMetadata bahmniDiagnosisMetadata, ObsService obsService, ConceptService conceptService) {
        this.bahmniDiagnosisMetadata = bahmniDiagnosisMetadata;
        this.obsService = obsService;
        this.conceptService = conceptService;
    }

    @Override
    public void forRead(Encounter encounter, EncounterTransaction encounterTransaction) {

    }

    @Override
    public void forSave(Encounter encounter, EncounterTransaction encounterTransaction) {
        List<EncounterTransaction.Diagnosis> diagnoses = encounterTransaction.getDiagnoses();

        for (EncounterTransaction.Diagnosis diagnosis : diagnoses) {
            BahmniDiagnosisRequest bahmniDiagnosisRequest = (BahmniDiagnosisRequest) diagnosis;
            addExtraMetadata(encounter.getObsAtTopLevel(false), bahmniDiagnosisRequest);
            updateRevisedFlagOfPreviousDiagnosis(bahmniDiagnosisRequest);
        }
    }

    private void updateRevisedFlagOfPreviousDiagnosis(BahmniDiagnosisRequest bahmniDiagnosisRequest) {
        if(bahmniDiagnosisRequest.getPreviousObs() ==null){
            return;
        }
        Obs previousObs = obsService.getObsByUuid(bahmniDiagnosisRequest.getPreviousObs());
        updateRevisedConcept(previousObs, true);
        obsService.saveObs(previousObs, "Diagnosis is revised");
    }

    public void addExtraMetadata(Collection<Obs> observations, BahmniDiagnosisRequest bahmniDiagnosis) {
        Obs matchingDiagnosisObs = bahmniDiagnosisMetadata
                .findMatchingDiagnosis(observations, bahmniDiagnosis);

        updateInitialDiagnosis(matchingDiagnosisObs, bahmniDiagnosis);
        if (bahmniDiagnosisMetadata.diagnosisSchemaContainsStatus()) {
            updateStatusConcept(matchingDiagnosisObs, bahmniDiagnosis);
        }
        updateRevisedConcept(matchingDiagnosisObs, false);
    }

    void updateStatusConcept(Obs diagnosisObs, BahmniDiagnosis bahmniDiagnosis) {
        Obs obs = findOrCreateObs(diagnosisObs, bahmniDiagnosisMetadata.getBahmniDiagnosisStatusConcept());
        if (bahmniDiagnosis.getDiagnosisStatusConcept() != null) {
            Concept statusConcept = conceptService.getConcept(bahmniDiagnosis.getDiagnosisStatusConcept().getName());
            obs.setValueCoded(statusConcept);
            addToObsGroup(diagnosisObs, obs);
        }
    }


    private void updateInitialDiagnosis(Obs diagnosisObs, BahmniDiagnosisRequest bahmniDiagnosis) {
        Obs obs = findOrCreateObs(diagnosisObs, bahmniDiagnosisMetadata.getBahmniInitialDiagnosisConcept());
        String initialDiagnosisUuid = bahmniDiagnosis.getPreviousObs() == null
                ? diagnosisObs.getUuid() :
                bahmniDiagnosis.getFirstDiagnosis().getExistingObs();

        obs.setValueText(initialDiagnosisUuid);
        addToObsGroup(diagnosisObs, obs);
    }


    private void updateRevisedConcept(Obs diagnosisObs, boolean value) {
        Obs obs = findOrCreateObs(diagnosisObs, bahmniDiagnosisMetadata.getBahmniDiagnosisRevisedConcept());
        obs.setValueBoolean(value);
        addToObsGroup(diagnosisObs, obs);
    }

    private void addToObsGroup(Obs obsGroup, Obs member) {
        member.setPerson(obsGroup.getPerson());
        member.setObsDatetime(obsGroup.getObsDatetime());
        member.setLocation(obsGroup.getLocation());
        member.setEncounter(obsGroup.getEncounter());
        obsGroup.addGroupMember(member);
    }


    private Obs findOrCreateObs(Obs diagnosisObs, Concept concept) {
        for (Obs o : diagnosisObs.getGroupMembers()) {
            if (concept.equals(o.getConcept())) {
                return o;
            }
        }
        Obs obs = new Obs();
        obs.setConcept(concept);
        return obs;
    }
}
