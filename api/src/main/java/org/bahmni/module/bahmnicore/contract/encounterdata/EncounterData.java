package org.bahmni.module.bahmnicore.contract.encounterdata;

import java.util.List;

public class EncounterData {
    private String patientUUID;
    private String visitTypeUUID; //This can be removed when we implement location based login
    private String encounterTypeUUID;

    private List<ObservationData> observations;

    public EncounterData() {
    }

    public EncounterData(String patientUUID, String visitTypeUUID, String encounterTypeUUID, List<ObservationData> observations) {
        this.patientUUID = patientUUID;
        this.visitTypeUUID = visitTypeUUID;
        this.encounterTypeUUID = encounterTypeUUID;
        this.observations = observations;
    }

    public String getPatientUUID() {
        return patientUUID;
    }

    public String getEncounterTypeUUID() {
        return encounterTypeUUID;
    }

    public String getVisitTypeUUID() {
        return visitTypeUUID;
    }

    public void setPatientUUID(String patientUUID) {
        this.patientUUID = patientUUID;
    }

    public void setVisitTypeUUID(String visitTypeUUID) {
        this.visitTypeUUID = visitTypeUUID;
    }

    public void setEncounterTypeUUID(String encounterTypeUUID) {
        this.encounterTypeUUID = encounterTypeUUID;
    }

    public void setObservations(List<ObservationData> observations) {
        this.observations = observations;
    }

    public List<ObservationData> getObservations() {
        return observations;
    }
}