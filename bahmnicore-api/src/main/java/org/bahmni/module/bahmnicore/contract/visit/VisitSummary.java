package org.bahmni.module.bahmnicore.contract.visit;

import java.util.Date;

public class VisitSummary {
    private String uuid;
    private Date startDateTime;
    private Date stopDateTime;
    private Boolean hasBeenAdmitted;
    private String visitType;

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

    public Boolean getHasBeenAdmitted() {
        return hasBeenAdmitted;
    }

    public void setHasBeenAdmitted(Boolean hasAdmitted) {
        this.hasBeenAdmitted = hasAdmitted;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }
}
