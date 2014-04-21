package org.bahmni.module.elisatomfeedclient.api.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;

import java.util.Date;

@Data
public class OpenElisTestDetail {
    private String testName;
    private String testUnitOfMeasurement;
    private String testUuid;
    private String panelUuid;
    private Double minNormal;
    private Double maxNormal;
    private String result;
    private String notes;
    private String resultType;
    private String providerUuid;
    private String dateTime;
    private String status;
    private Boolean abnormal;

    @JsonIgnore
    public boolean isCancelled() {
        return "Canceled".equals(status);
    }

    public Date fetchDate() {
        return  dateTime == null ? null : DateTime.parse(dateTime).toDate();
    }

    @JsonIgnore
    public boolean isReferredOut() {
        return status != null && (status.equalsIgnoreCase("referred out") || status.equalsIgnoreCase("Finalized RO"));
    }
}
