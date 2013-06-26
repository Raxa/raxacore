package org.bahmni.module.bahmnicore.contract.encounterdata;

import java.util.ArrayList;
import java.util.List;

public class EncounterObservations {
    private List<ObservationData> observations = new ArrayList<ObservationData>();

    public EncounterObservations(List<ObservationData> observations) {
        this.observations = observations == null ? new ArrayList<ObservationData>() : observations;
    }

    public EncounterObservations() {
    }

    public List<ObservationData> getObservations() {
        return observations;
    }

    public void setObservations(List<ObservationData> observations) {
        this.observations = observations;
    }
}