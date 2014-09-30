package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.openmrs.Obs;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.ObservationMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.ArrayList;
import java.util.List;

public class BahmniObservationMapper {

    public static List<BahmniObservation> map(List<Obs> obsList) {
        List<BahmniObservation> bahmniObservations = new ArrayList<>();
        for (Obs obs : obsList) {
            bahmniObservations.add(new BahmniObservation(new ObservationMapper().map(obs), true));
        }
        return bahmniObservations;
    }

    public static List<BahmniObservation> toBahmniObsFromETObs(List<EncounterTransaction.Observation> allObservations) {
        List<BahmniObservation> bahmniObservations = new ArrayList<>();
        for (EncounterTransaction.Observation observation : allObservations) {
            bahmniObservations.add(new BahmniObservation(observation));
        }
        return bahmniObservations;
    }
    
}
