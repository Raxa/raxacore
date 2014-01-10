package org.bahmni.module.bahmnicore.model;

import org.openmrs.module.webservices.rest.SimpleObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class VisitDocumentUpload {
    String patientUUID;
    String visitTypeUUID;
    Date visitStartDate;
    Date visitEndDate;
    String encounterTypeUUID;
    Date encounterDateTime;
    List<Document> documents = new ArrayList<>();

    public VisitDocumentUpload(String patientUUID, String visitTypeUUID, Date visitStartDate, Date visitEndDate, String encounterTypeUUID, Date encounterDateTime, List<Document> documents) {
        this.patientUUID = patientUUID;
        this.visitTypeUUID = visitTypeUUID;
        this.visitStartDate = visitStartDate;
        this.visitEndDate = visitEndDate;
        this.encounterTypeUUID = encounterTypeUUID;
        this.encounterDateTime = encounterDateTime;
        this.documents = documents;
    }

    public VisitDocumentUpload(SimpleObject post) throws ParseException {
        SimpleObjectExtractor extractor = new SimpleObjectExtractor(post);
        patientUUID = extractor.extract("patientUUID");
        visitTypeUUID = extractor.extract("visitTypeUUID");
        visitStartDate = new SimpleDateFormat("dd-MM-yyyy").parse(extractor.<String>extract("visitStartDate"));
        visitEndDate = new SimpleDateFormat("dd-MM-yyyy").parse(extractor.<String>extract("visitEndDate"));
        encounterTypeUUID = extractor.extract("encounterTypeUUID");
        encounterDateTime = new SimpleDateFormat("dd-MM-yyyy").parse(extractor.<String>extract("encounterDateTime"));
        List<LinkedHashMap> documentsList = extractor.extract("documents");
        for (LinkedHashMap document : documentsList) {
            documents.add(new Document(document));
        }
     }

    public String getPatientUUID() {
        return patientUUID;
    }

    public void setPatientUUID(String patientUUID) {
        this.patientUUID = patientUUID;
    }

    public String getEncounterTypeUUID() {
        return encounterTypeUUID;
    }

    public void setEncounterTypeUUID(String encounterTypeUUID) {
        this.encounterTypeUUID = encounterTypeUUID;
    }

    public Date getEncounterDateTime() {
        return encounterDateTime;
    }

    public void setEncounterDateTime(Date encounterDateTime) {
        this.encounterDateTime = encounterDateTime;
    }

    public String getVisitTypeUUID() {
        return visitTypeUUID;
    }

    public void setVisitTypeUUID(String visitTypeUUID) {
        this.visitTypeUUID = visitTypeUUID;
    }

    public Date getVisitStartDate() {
        return visitStartDate;
    }

    public void setVisitStartDate(Date visitStartDate) {
        this.visitStartDate = visitStartDate;
    }

    public Date getVisitEndDate() {
        return visitEndDate;
    }

    public void setVisitEndDate(Date visitEndDate) {
        this.visitEndDate = visitEndDate;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
