package org.bahmni.module.elisatomfeedclient.api.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Date;
import java.util.Set;

@Data
public class OpenElisTestDetail {
    private String testName;
    private String testUnitOfMeasurement;
    private String testUuid;
    private String panelUuid;
    private int minNormal;
    private int maxNormal;
    private String result;
    private Set<String> notes;
    private String resultType;
    private String providerUuid;
    private Date datetime;
    private String status;
    private Boolean abnormal;

    @JsonIgnore
    public boolean isCancelled() {
        return "Cancelled".equals(status);
    }
}
