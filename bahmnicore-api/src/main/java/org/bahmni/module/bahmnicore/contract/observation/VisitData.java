package org.bahmni.module.bahmnicore.contract.observation;


import org.openmrs.Visit;

import java.util.Date;

public class VisitData {
    private String uuid;
    private Date startDateTime;

    public VisitData(Visit visit) {
        this.uuid = visit.getUuid();
        this.startDateTime = visit.getStartDatetime();
    }

    public VisitData() {
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
