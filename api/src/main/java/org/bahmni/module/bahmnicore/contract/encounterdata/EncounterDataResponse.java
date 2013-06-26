package org.bahmni.module.bahmnicore.contract.encounterdata;

public class EncounterDataResponse {
    private String visitId;
    private String encounterId;
    private String message;

    public EncounterDataResponse(String visitId, String encounterId, String message) {
        this.visitId = visitId;
        this.encounterId = encounterId;
        this.message = message;
    }

    public String getVisitId() {
        return visitId;
    }

    public String getEncounterId() {
        return encounterId;
    }

    public String getMessage() {
        return message;
    }
}