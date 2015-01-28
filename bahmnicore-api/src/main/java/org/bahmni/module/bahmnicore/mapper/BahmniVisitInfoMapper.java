package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.contract.visit.EncounterType;
import org.bahmni.module.bahmnicore.contract.visit.VisitSummary;
import org.openmrs.Encounter;
import org.openmrs.Visit;

public class BahmniVisitInfoMapper {
    public VisitSummary map(Visit visit, Boolean isIPD) {
        VisitSummary visitSummary = new VisitSummary();
        visitSummary.setUuid(visit.getUuid());
        visitSummary.setStartDateTime(visit.getStartDatetime());
        visitSummary.setStopDateTime(visit.getStopDatetime());
        visitSummary.setIsIPD(isIPD);
        return visitSummary;
    }

}
