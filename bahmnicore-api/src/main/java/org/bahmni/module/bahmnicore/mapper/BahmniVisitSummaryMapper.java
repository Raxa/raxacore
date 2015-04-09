package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.contract.visit.EncounterType;
import org.bahmni.module.bahmnicore.contract.visit.VisitSummary;
import org.openmrs.Visit;

public class BahmniVisitSummaryMapper {
    public VisitSummary map(Visit visit, Boolean hasAdmissionEncounter) {
        VisitSummary visitSummary = new VisitSummary();
        visitSummary.setUuid(visit.getUuid());
        visitSummary.setStartDateTime(visit.getStartDatetime());
        visitSummary.setStopDateTime(visit.getStopDatetime());
        visitSummary.setVisitType(visit.getVisitType().getName());
        visitSummary.setHasBeenAdmitted(hasAdmissionEncounter);
        return visitSummary;
    }

}
