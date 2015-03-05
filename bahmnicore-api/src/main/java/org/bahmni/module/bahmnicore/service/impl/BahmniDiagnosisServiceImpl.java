package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.BahmniDiagnosisService;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.module.bahmniemrapi.diagnosis.helper.BahmniDiagnosisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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
    public void delete(String diagnosisEncounterUuid, String visitDiagnosesObservationUuid) {
        Encounter encounterByUuid = encounterService.getEncounterByUuid(diagnosisEncounterUuid);

        // void initial diagnosis and its children
        String initialVisitDiagnosisUuid = null;
        Obs visitDiagnosisObs = obsService.getObsByUuid(visitDiagnosesObservationUuid);
        for (Obs obs : visitDiagnosisObs.getGroupMembers()) {
            if (obs.getConcept().getName().getName().equals(BahmniDiagnosisHelper.BAHMNI_INITIAL_DIAGNOSIS)) {
                initialVisitDiagnosisUuid = obs.getValueText();
                break;
            }
        }

        // get encounter for this obs initialVisitDiagnosisUuid
            voidOtherDiagnosisWithSameInitialDiagnosis(initialVisitDiagnosisUuid, visitDiagnosisObs);

//        }

        // void modified diagnosis obs and its children
//        voidDiagnosis(visitDiagnosesObservationUuid);
    }

    private void voidOtherDiagnosisWithSameInitialDiagnosis(String initialVisitDiagnosisUuid, Obs visitDiagnosisObs) {
        //find observations for this patient and concept
        List<Obs> observations = obsService.getObservationsByPersonAndConcept(visitDiagnosisObs.getPerson(), visitDiagnosisObs.getConcept());

        for (Obs observation : observations) {
            for (Obs obs : observation.getGroupMembers()) {
                if (obs.getConcept().getName().getName().equals(BahmniDiagnosisHelper.BAHMNI_INITIAL_DIAGNOSIS)) {
                    if (initialVisitDiagnosisUuid.equals(obs.getValueText())) {
                        voidDiagnosis(obs.getUuid(), obs.getEncounter());
                        break;
                    }
                }
            }
        }
    }

    private void voidDiagnosis(String visitDiagnosesObservationUuid, Encounter encounter) {
        Obs obsByUuid = obsService.getObsByUuid(visitDiagnosesObservationUuid);
        voidObsAndItsChildren(obsByUuid);
//        obsService.saveObs(obsByUuid, "freason");
        encounterService.saveEncounter(encounter);

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
