package org.bahmni.module.bahmnicore.service;

import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.List;

public interface VisitSummaryService {
    public List<EncounterTransaction> getVisitSummary(String visitUUID, Boolean includeAll);
}

