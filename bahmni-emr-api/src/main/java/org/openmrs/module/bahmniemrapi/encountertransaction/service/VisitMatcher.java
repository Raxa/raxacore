package org.openmrs.module.bahmniemrapi.encountertransaction.service;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;

import java.util.Date;

public interface VisitMatcher {
    public Visit getVisitFor(Patient patient, String visitTypeForNewVisit, Date orderDate, Date visitStartDate, Date visitEndDate);
    public Visit getVisitFor(Patient patient, String visitTypeForNewVisit, Date orderDate);
    public boolean hasActiveVisit(Patient patient);
    public Visit createNewVisit(Patient patient, Date date, String visitTypeForNewVisit, Date visitStartDate, Date visitEndDate);
    public VisitType getVisitTypeByName(String visitTypeName);
}
