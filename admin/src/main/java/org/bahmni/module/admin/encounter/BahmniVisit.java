package org.bahmni.module.admin.encounter;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Visit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class BahmniVisit {
    private Visit visit;

    public BahmniVisit(Visit visit) {
        this.visit = visit;
    }

    public List<Obs> obsFor(EncounterType requestedEncounterType) {
        List<Obs> allObs = new ArrayList<>();
        for (Encounter anEncounter : visit.getEncounters()) {
            if (anEncounter.getEncounterType().equals(requestedEncounterType)) {
                Set<Obs> obs = anEncounter.getObs();
                allObs.addAll(obs);
            }
        }
        return allObs;
    }

}
