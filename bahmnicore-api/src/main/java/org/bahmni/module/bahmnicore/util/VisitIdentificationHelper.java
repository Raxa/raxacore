package org.bahmni.module.bahmnicore.util;


import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VisitIdentificationHelper {
    private VisitService visitService;

    public VisitIdentificationHelper(VisitService visitService) {
        this.visitService = visitService;
    }

    public Visit getVisitFor(Patient patient, Date orderDate, String visitType) {
//        Visit applicableVisit = getVisitForPatientWithinDates(patient, orderDate);
//        if (applicableVisit != null)
//            return applicableVisit;
        List<Visit> visits = visitService.getVisits(null, Arrays.asList(patient), null, null, null, getNextDate(orderDate), orderDate, null, null, true, false);
        if (visits != null && !visits.isEmpty()) {
            Visit visit = visits.get(0);
            if (visit.getStartDatetime().after(orderDate)) {
                visit.setStartDatetime(orderDate);
            }
            return visit;
        }
        return createNewLabVisit(patient, orderDate, visitType);
    }

    private Visit createNewLabVisit(Patient patient, Date date, String visitType) {
        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setStartDatetime(date);
        if(!DateUtils.isSameDay(date, new Date())) {
            visit.setStopDatetime(new DateTime(date).toDateMidnight().toDateTime().plusDays(1).minusSeconds(1).toDate());
        }
        visit.setVisitType(getVisitTypeByName(visitType));
        return visit;
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

    private VisitType getVisitTypeByName(String visitTypeName) {
        List<VisitType> visitTypes = visitService.getVisitTypes(visitTypeName);
        return visitTypes.isEmpty() ? null : visitTypes.get(0);
    }

}
