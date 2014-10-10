package org.bahmni.module.bahmnicore.dao;

import org.openmrs.Visit;

public interface VisitDao {
    public Visit getLatestVisit(String patientUuid, String conceptName);
}
