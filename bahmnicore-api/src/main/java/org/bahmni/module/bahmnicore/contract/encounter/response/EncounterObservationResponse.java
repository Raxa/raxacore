package org.bahmni.module.bahmnicore.contract.encounter.response;

import org.bahmni.module.bahmnicore.contract.encounter.data.ObservationData;

import java.util.ArrayList;
import java.util.List;

public class EncounterObservationResponse {
    private List<ObservationData> observations = new ArrayList<ObservationData>();

    public EncounterObservationResponse(List<ObservationData> observations) {
        this.observations = observations == null ? new ArrayList<ObservationData>() : observations;
    }

    public EncounterObservationResponse() {
    }

    public List<ObservationData> getObservations() {
        return observations;
    }

    public void setObservations(List<ObservationData> observations) {
        this.observations = observations;
    }
}