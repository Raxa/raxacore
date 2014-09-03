package org.openmrs.module.bahmniemrapi.visit.contract;


import lombok.Getter;
import lombok.Setter;
import org.openmrs.Visit;

import java.util.Date;

public class VisitData {
    @Getter @Setter
    private String uuid;
    @Getter @Setter
    private Date startDateTime;

    public VisitData(Visit visit) {
        this.uuid = visit.getUuid();
        this.startDateTime = visit.getStartDatetime();
    }

}
