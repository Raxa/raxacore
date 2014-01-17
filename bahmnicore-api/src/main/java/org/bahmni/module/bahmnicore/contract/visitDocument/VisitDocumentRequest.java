package org.bahmni.module.bahmnicore.contract.visitDocument;

import lombok.Data;
import org.bahmni.module.bahmnicore.model.Document;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class VisitDocumentRequest {
    String patientUuid;
    String visitTypeUuid;
    Date visitStartDate;
    Date visitEndDate;
    String encounterTypeUuid;
    Date encounterDateTime;
    List<Document> documents = new ArrayList<>();
    private String providerUuid;

    public VisitDocumentRequest() {
    }

    public VisitDocumentRequest(String patientUUID, String visitTypeUUID, Date visitStartDate, Date visitEndDate, String encounterTypeUUID, Date encounterDateTime, List<Document> documents, String providerUuid) {
        this.patientUuid = patientUUID;
        this.visitTypeUuid = visitTypeUUID;
        this.visitStartDate = visitStartDate;
        this.visitEndDate = visitEndDate;
        this.encounterTypeUuid = encounterTypeUUID;
        this.encounterDateTime = encounterDateTime;
        this.providerUuid = providerUuid;
        this.documents = documents;
    }

}
