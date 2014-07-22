package org.openmrs.module.bahmniemrapi.laborder.contract;

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
    private String provider;
    private Boolean referredOut;
    private Date resultDateTime;

    public LabOrderResult() {
    }

    public LabOrderResult(String accessionUuid, Date accessionDateTime, String testName, String testUnitOfMeasurement, Double minNormal, Double maxNormal, String result, Boolean abnormal, Boolean referredOut) {
        this.accessionUuid = accessionUuid;
        this.testName = testName;
        this.testUnitOfMeasurement = testUnitOfMeasurement;
        this.minNormal = minNormal;
        this.maxNormal = maxNormal;
        this.accessionDateTime = accessionDateTime;
        this.result = result;
        this.abnormal = abnormal;
        this.referredOut = referredOut;
    }
}
