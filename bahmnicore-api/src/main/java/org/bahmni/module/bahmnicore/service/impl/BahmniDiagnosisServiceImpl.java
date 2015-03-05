package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.BahmniDiagnosisService;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;

import static org.openmrs.module.bahmniemrapi.diagnosis.helper.BahmniDiagnosisHelper.BAHMNI_INITIAL_DIAGNOSIS;

@Component
public class BahmniDiagnosisServiceImpl implements BahmniDiagnosisService {
    private EncounterService encounterService;
    private ObsService obsService;

    @Autowired
    public BahmniDiagnosisServiceImpl(EncounterService encounterService, ObsService obsService) {
        this.encounterService = encounterService;
        this.obsService = obsService;
    }

    @Override
    public void delete(String visitDiagnosesObservationUuid) {
        Obs visitDiagnosisObs = obsService.getObsByUuid(visitDiagnosesObservationUuid);
        String initialVisitDiagnosisUuid = findInitialDiagnosisUuid(visitDiagnosisObs);
        voidAllDiagnosisWithSameInitialDiagnosis(initialVisitDiagnosisUuid, visitDiagnosisObs);
    }

    private String findInitialDiagnosisUuid(Obs visitDiagnosisObs) {
        for (Obs obs : visitDiagnosisObs.getGroupMembers()) {
            if (obs.getConcept().getName().getName().equals(BAHMNI_INITIAL_DIAGNOSIS)) {
                return obs.getValueText();
            }
        }
        return null;
    }

    private void voidAllDiagnosisWithSameInitialDiagnosis(String initialVisitDiagnosisUuid, Obs visitDiagnosisObs) {
        //find observations for this patient and concept
        List<Obs> observations = obsService.getObservationsByPersonAndConcept(visitDiagnosisObs.getPerson(), visitDiagnosisObs.getConcept());
        for (Obs observation : observations) {
            for (Obs obs : observation.getGroupMembers()) {
                if (initialVisitDiagnosisUuid.equals(obs.getValueText())) {
                    voidDiagnosis(observation);
                    break;
                }
            }
        }
    }

    private void voidDiagnosis(Obs observation) {
        voidObsAndItsChildren(observation);
        encounterService.saveEncounter(observation.getEncounter());
    }

    private void voidObsAndItsChildren(Obs obs) {
        obs.setVoided(true);
        if (obs.getGroupMembers() == null)
            return;
        for (Obs childObs : obs.getGroupMembers()) {
            voidObsAndItsChildren(childObs);
        }
    }
}
