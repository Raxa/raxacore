package org.bahmni.module.bahmnicore.service;

import org.openmrs.Visit;

public interface BahmniVisitService {
    public Visit getLatestVisit(String patientUuid, String conceptName);
}
