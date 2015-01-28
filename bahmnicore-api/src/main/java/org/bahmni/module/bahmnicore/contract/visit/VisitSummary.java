package org.bahmni.module.bahmnicore.contract.visit;

import java.util.Date;

public class VisitSummary {
    private String uuid;
    private Date startDateTime;
    private Date stopDateTime;
    private Boolean isIPD;

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getStopDateTime() {
        return stopDateTime;
    }

    public void setStopDateTime(Date stopDateTime) {
        this.stopDateTime = stopDateTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getIsIPD() {
        return isIPD;
    }

    public void setIsIPD(Boolean isIPD) {
        this.isIPD = isIPD;
    }
}
