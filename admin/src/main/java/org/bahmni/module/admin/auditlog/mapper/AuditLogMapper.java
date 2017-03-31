package org.bahmni.module.admin.auditlog.mapper;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditLogMapper {
    private Integer auditLogId;
    private Date dateCreated;
    private String eventType;
    private String patientId;
    private String userId;
    private String message;

    public AuditLogMapper(Integer auditLogId, Date dateCreated, String eventType, String patientId, String userId, String message) {
        this.auditLogId = auditLogId;
        this.dateCreated = dateCreated;
        this.eventType = eventType;
        this.patientId = patientId;
        this.userId = userId;
        this.message = message;
    }

    public AuditLogMapper() {
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
