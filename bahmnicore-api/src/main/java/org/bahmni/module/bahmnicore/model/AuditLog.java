package org.bahmni.module.bahmnicore.model;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.User;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.io.Serializable;
import java.util.Date;

public class AuditLog implements Serializable {

    private Integer auditLogId;
    private User user;
    private Patient patient;
    private String eventType;
    private String message;
    private Date dateCreated;
    private String uuid;

    public Integer getAuditLogId() {
        return auditLogId;
    }

    public void setAuditLogId(Integer auditLogId) {
        this.auditLogId = auditLogId;
    }


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User provider) {
        this.user = provider;
    }

    public SimpleObject map() {
        PatientIdentifier patientIdentifier = patient != null ? patient.getPatientIdentifier() : null;
        String identifier = patientIdentifier != null ? patientIdentifier.getIdentifier() : null;
        SimpleObject response = new SimpleObject();
        response.add("patientId", identifier);
        response.add("auditLogId", auditLogId);
        response.add("dateCreated", dateCreated);
        response.add("eventType", eventType);
        response.add("userId", user.getUsername());
        response.add("message", message);
        return response;
    }
}
