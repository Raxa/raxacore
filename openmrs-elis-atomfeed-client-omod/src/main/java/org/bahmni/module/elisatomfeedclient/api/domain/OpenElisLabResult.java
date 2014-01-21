package org.bahmni.module.elisatomfeedclient.api.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OpenElisLabResult {

    private String orderId;
    private String accessionNumber;
    private String patientExternalId;
    private String patientFirstName;
    private String patientLastName;
    private String testName;
    private String testUnitOfMeasurement;
    private String testExternalId;
    private String panelExternalId;
    private String resultId;
    private Double minNormal;
    private Double maxNormal;
    private String result;
    private String alerts;
    private List<String> notes = new ArrayList<>();
    private String resultType;
    private Boolean abnormal;

}
