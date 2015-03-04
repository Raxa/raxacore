package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.service.BahmniDiagnosisService;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.module.bahmniemrapi.diagnosis.helper.BahmniDiagnosisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            }
        }

        // get encounter for this obs initialVisitDiagnosisUuid
        if (initialVisitDiagnosisUuid != null && !initialVisitDiagnosisUuid.equals(visitDiagnosesObservationUuid)) {
            Obs obsByUuid = obsService.getObsByUuid(initialVisitDiagnosisUuid);
            Encounter initialEncounter = obsByUuid.getEncounter();
            voidDiagnosis(initialVisitDiagnosisUuid, initialEncounter);
        }

        // void modified diagnosis obs and its children
        voidDiagnosis(visitDiagnosesObservationUuid, encounterByUuid);
    }

    private void voidDiagnosis(String visitDiagnosesObservationUuid, Encounter encounterByUuid) {
        for (Obs obs : encounterByUuid.getAllObs()) {
            if (obs.getUuid().equals(visitDiagnosesObservationUuid)) {
                voidObsAndItsChildren(obs);
            }
        }

        encounterService.saveEncounter(encounterByUuid);
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
