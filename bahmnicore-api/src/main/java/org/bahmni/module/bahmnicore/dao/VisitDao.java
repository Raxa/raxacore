package org.bahmni.module.bahmnicore.dao;

import org.openmrs.Patient;
import org.openmrs.Visit;

import java.util.List;

public interface VisitDao {
    public Visit getLatestVisit(String patientUuid, String conceptName);

    Visit getVisitSummary(String visitUuid);

    boolean hasAdmissionEncounter(String visitUuid);

    List<Visit> getVisitsByPatient(Patient patient, int numberOfVisits);
}
