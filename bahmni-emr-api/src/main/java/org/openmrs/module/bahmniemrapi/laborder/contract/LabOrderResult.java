package org.openmrs.module.bahmniemrapi.laborder.contract;

import lombok.Data;
import org.openmrs.module.bahmniemrapi.accessionnote.contract.AccessionNote;

import java.util.Date;
import java.util.List;

@Data
public class LabOrderResult {
    private String orderUuid;
    private String action;
    private String accessionUuid;
    private Date accessionDateTime;
    private Date visitStartTime;
    private List<AccessionNote> accessionNotes;
    private String testName;
    private String testUnitOfMeasurement;
    private String testUuid;
    private String panelUuid;
    private String panelName;
    private Double minNormal;
    private Double maxNormal;
    private String resultUuid;
    private String result;
    private String notes;
    private Boolean abnormal;
    private String provider;
    private Boolean referredOut;
    private Date resultDateTime;
    private String uploadedFileName;

    public LabOrderResult() {
    }

    public LabOrderResult(String orderUuid, String action, String accessionUuid, Date accessionDateTime, String testName, String testUnitOfMeasurement, Double minNormal, Double maxNormal, String result, Boolean abnormal, Boolean referredOut, String uploadedFileName, List<AccessionNote> accessionNotes) {
        this.orderUuid = orderUuid;
        this.action = action;
        this.accessionUuid = accessionUuid;
        this.testName = testName;
        this.testUnitOfMeasurement = testUnitOfMeasurement;
        this.minNormal = minNormal;
        this.maxNormal = maxNormal;
        this.accessionDateTime = accessionDateTime;
        this.result = result;
        this.abnormal = abnormal;
        this.referredOut = referredOut;
        this.uploadedFileName = uploadedFileName;
        this.accessionNotes = accessionNotes;
    }
}
