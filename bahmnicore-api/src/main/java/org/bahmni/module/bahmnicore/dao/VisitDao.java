package org.bahmni.module.bahmnicore.dao;

import org.openmrs.Visit;

public interface VisitDao {
    public Visit getLatestVisit(String patientUuid, String conceptName);

    Visit getVisitSummary(String visitUuid);

    boolean hasAdmissionEncounter(String visitUuid);
}
