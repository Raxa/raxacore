package org.bahmni.module.bahmnicore.model;


import lombok.Getter;
import lombok.Setter;
import org.bahmni.module.bahmnicore.util.CustomDateSerializer;
import org.openmrs.Visit;

import java.util.Date;

public class VisitData {
    @Getter @Setter
    private String uuid;
    @Setter
    private Date startDateTime;

    public VisitData(Visit visit) {
        this.uuid = visit.getUuid();
        this.startDateTime = visit.getStartDatetime();
    }

    public String getStartDateTime() {
        return CustomDateSerializer.serializeDate(startDateTime);
    }
}
