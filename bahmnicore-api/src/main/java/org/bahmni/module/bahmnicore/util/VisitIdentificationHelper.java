package org.bahmni.module.bahmnicore.util;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;

import java.util.*;

public class VisitIdentificationHelper {
    private VisitService visitService;

    public VisitIdentificationHelper(VisitService visitService) {
        this.visitService = visitService;
    }

    public Visit getVisitFor(Patient patient, String visitTypeForNewVisit, Date orderDate) {
        Date nextDate = getNextDate(orderDate);
        List<Visit> visits = visitService.getVisits(null, Arrays.asList(patient), null, null, null, nextDate, orderDate, null, null, true, false);
        if (matchingVisitsFound(visits)) {
            Visit matchingVisit = getVisit(orderDate, visits);
            return stretchVisits(orderDate, matchingVisit);
        }
        return createNewVisit(patient, orderDate, visitTypeForNewVisit);
    }

    private boolean matchingVisitsFound(List<Visit> visits) {
        return visits != null && !visits.isEmpty();
    }

    private Visit stretchVisits(Date orderDate, Visit matchingVisit) {
        if (matchingVisit.getStartDatetime().after(orderDate)) {
            matchingVisit.setStartDatetime(orderDate);
        }
        if (matchingVisit.getStopDatetime() != null && matchingVisit.getStopDatetime().before(orderDate)) {
            matchingVisit.setStopDatetime(orderDate);
        }
        return matchingVisit;
    }

    private Visit getVisit(Date orderDate, List<Visit> visits) {
        if (visits.size() > 1) {
            return getVisitMatchingOrderDate(orderDate, visits);
        } else {
            return visits.get(0);
        }
    }

    private Visit getVisitMatchingOrderDate(Date orderDate, List<Visit> visits) {
        for (Visit visit : visits) {
            if ( (orderDate.equals(visit.getStartDatetime()) || visit.getStartDatetime().before(orderDate)) &&
                    (orderDate.equals(visit.getStopDatetime()) || visit.getStopDatetime().after(orderDate)) )
                return visit;
        }
        return null;
    }

    private Visit createNewVisit(Patient patient, Date date, String visitTypeForNewVisit) {
        VisitType visitTypeByName = getVisitTypeByName(visitTypeForNewVisit);
        if (visitTypeByName == null) {
            throw new RuntimeException("Visit type:'" + visitTypeForNewVisit + "' not found.");
        }

        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setVisitType(visitTypeByName);
        visit.setStartDatetime(date);
        if (!DateUtils.isSameDay(date, new Date())) {
            visit.setStopDatetime(new DateTime(date).toDateMidnight().toDateTime().plusDays(1).minusSeconds(1).toDate());
        }
        visit.setEncounters(new HashSet<Encounter>());
        return visitService.saveVisit(visit);
    }

    private VisitType getVisitTypeByName(String visitTypeName) {
        List<VisitType> visitTypes = visitService.getVisitTypes(visitTypeName);
        return visitTypes.isEmpty() ? null : visitTypes.get(0);
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