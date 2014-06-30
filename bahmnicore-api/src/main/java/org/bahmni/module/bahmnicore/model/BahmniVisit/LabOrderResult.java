package org.bahmni.module.bahmnicore.model.BahmniVisit;

import lombok.Data;

import java.util.Date;

@Data
public class LabOrderResult {
    private String accessionUuid;
    private Date accessionDateTime;
    private String accessionNotes;
    private String testName;
    private String testUnitOfMeasurement;
    private String testUuid;
    private String panelUuid;
    private String panelName;
    private Double minNormal;
    private Double maxNormal;
    private String result;
    private String notes;
    private Boolean abnormal;
    private String providerUuid;

    public LabOrderResult() {
    }

    public LabOrderResult(String testName, String testUnitOfMeasurement, Double minNormal, Double maxNormal, Date accessionDateTime, String result, Boolean abnormal) {
        this.testName = testName;
        this.testUnitOfMeasurement = testUnitOfMeasurement;
        this.minNormal = minNormal;
        this.maxNormal = maxNormal;
        this.accessionDateTime = accessionDateTime;
        this.result = result;
        this.abnormal = abnormal;
    }
}
