package org.bahmni.module.admin.encounter;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Visit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class EncounterMatcher {
    private Visit visit;

    public EncounterMatcher(Visit visit) {
        this.visit = visit;
    }

    public List<Encounter> getMatchingEncounters(EncounterType requestedEncounterType) {
        List<Encounter> matchingEncounters = new ArrayList<>();
        Set<Encounter> encounters = visit.getEncounters();
        for (Encounter anEncounter : encounters) {
            if (anEncounter.getEncounterType().equals(requestedEncounterType)) {
                matchingEncounters.add(anEncounter);
            }
        }
        return matchingEncounters;
    }
}
