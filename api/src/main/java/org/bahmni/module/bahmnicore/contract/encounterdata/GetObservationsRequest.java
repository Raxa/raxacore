package org.bahmni.module.bahmnicore.contract.encounterdata;

public class GetObservationsRequest {
    private String patientUUID;
    private String visitTypeUUID; //This can be removed when we implement location based login
    private String encounterTypeUUID;

    public String getPatientUUID() {
        return patientUUID;
    }

    public void setPatientUUID(String patientUUID) {
        this.patientUUID = patientUUID;
    }

    public String getVisitTypeUUID() {
        return visitTypeUUID;
    }

    public void setVisitTypeUUID(String visitTypeUUID) {
        this.visitTypeUUID = visitTypeUUID;
    }

    public String getEncounterTypeUUID() {
        return encounterTypeUUID;
    }

    public void setEncounterTypeUUID(String encounterTypeUUID) {
        this.encounterTypeUUID = encounterTypeUUID;
    }
}