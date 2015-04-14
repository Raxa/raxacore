package org.bahmni.module.bahmnicore.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.contract.visit.VisitSummary;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.ObservationMapper;

import java.util.List;

public class BahmniVisitSummaryMapper {
    public VisitSummary map(Visit visit, List<Encounter> admissionAndDischargeEncounters) {
        VisitSummary visitSummary = new VisitSummary();
        visitSummary.setUuid(visit.getUuid());
        visitSummary.setStartDateTime(visit.getStartDatetime());
        visitSummary.setStopDateTime(visit.getStopDatetime());
        visitSummary.setVisitType(visit.getVisitType().getName());
        mapAdmissionAndDischargeDetails(admissionAndDischargeEncounters, visitSummary);

        return visitSummary;
    }

    private void mapAdmissionAndDischargeDetails(List<Encounter> admissionAndDischargeEncounters, VisitSummary visitSummary) {
        if (CollectionUtils.isNotEmpty(admissionAndDischargeEncounters)) {
            for (Encounter encounter : admissionAndDischargeEncounters) {
                VisitSummary.IPDDetails details = new VisitSummary.IPDDetails();
                details.setUuid(encounter.getUuid());
                details.setDate(encounter.getEncounterDatetime());
                details.setProvider(encounter.getEncounterProviders().iterator().next().getProvider().getName());
                details.setNotes(encounter.getAllObs().iterator().next().getValueText());

                if (encounter.getEncounterType().getName().equalsIgnoreCase("ADMISSION")) {
                    visitSummary.setAdmissionDetails(details);
                } else {
                    visitSummary.setDischargeDetails(details);
                }
            }
        }
    }
}
