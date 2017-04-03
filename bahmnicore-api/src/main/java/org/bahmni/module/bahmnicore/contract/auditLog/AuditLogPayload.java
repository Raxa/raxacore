package org.bahmni.module.bahmnicore.contract.auditLog;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditLogPayload {

    private String patientUuid;
    private String eventType;
    private String message;

    public AuditLogPayload(){
        super();
    }

    public AuditLogPayload(String patientUuid, String message, String eventType) {
        this.patientUuid = patientUuid;
        this.eventType = eventType;
        this.message = message;
    }

    public String getPatientUuid() {
        return patientUuid;
    }


    public String getEventType() {
        return eventType;
    }


    public String getMessage() {
        return message;
    }
}
