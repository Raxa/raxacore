package org.bahmni.module.bahmnicore.contract.auditLog;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditLogResponse {
    private Integer auditLogId;
    private Date dateCreated;
    private String eventType;
    private String patientId;
    private String userId;
    private String message;

    public AuditLogResponse(Integer auditLogId, Date dateCreated, String eventType, String patientId, String userId, String message) {
        this.auditLogId = auditLogId;
        this.dateCreated = dateCreated;
        this.eventType = eventType;
        this.patientId = patientId;
        this.userId = userId;
        this.message = message;
    }

    public AuditLogResponse() {
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Integer getAuditLogId() {
        return auditLogId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getPatientId() {
        return patientId;
    }
}
