package org.bahmni.module.admin.visit;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;

import java.util.*;

public class VisitMatcher {
    private VisitService visitService;

    public VisitMatcher(VisitService visitService) {
        this.visitService = visitService;
    }

    public Visit getMatchingVisit(Patient patient, String requestedVisitType, Date encounterDate) {
        VisitType visitType = visitService.getVisitTypes(requestedVisitType).get(0);
        List<Visit> matchingVisits = visitService.getVisits(Arrays.asList(visitType), Arrays.asList(patient), null, null, null,
                getNextDate(encounterDate), encounterDate, null, null, true, false);

        if (matchingVisits.size() > 0) {
            Visit matchingVisit = matchingVisits.get(0);
            return matchingVisit;
        } else {
            Visit newVisit = new Visit();
            newVisit.setPatient(patient);
            newVisit.setVisitType(visitType);
            newVisit.setStartDatetime(encounterDate);
            newVisit.setStopDatetime(getNextDate(encounterDate));
            newVisit.setEncounters(new HashSet<Encounter>());
            newVisit.setUuid(UUID.randomUUID().toString());
            return visitService.saveVisit(newVisit);
        }
    }

    private static Date getNextDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }


}