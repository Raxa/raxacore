package org.bahmni.module.bahmnicore.contract.auditLog;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BahmniAuditLog {

    private String patientUuid;
    private String message;
    private String event;
    private String userID;
    private String module;


    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public String getEvent() {
        return event;
    }

    public String getMessage() {
        return message;
    }

    public String getModule() {
        return module;
    }
}
