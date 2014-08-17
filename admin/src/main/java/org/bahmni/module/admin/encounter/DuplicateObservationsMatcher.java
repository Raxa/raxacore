package org.bahmni.module.admin.encounter;

import org.bahmni.csv.KeyValue;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DuplicateObservationsMatcher {
    private Visit visit;
    private EncounterType requestedEncounterType;

    public DuplicateObservationsMatcher(Visit visit, EncounterType requestedEncounterType) {
        this.visit = visit;
        this.requestedEncounterType = requestedEncounterType;
    }

    public List<KeyValue> matchingObservations(List<KeyValue> obsRows) {
        return matchingObservations(obsRows, false);
    }

    public List<KeyValue> matchingObservations(List<KeyValue> obsRows, boolean shouldMatchValue) {
        if (obsRows == null || obsRows.isEmpty())
            return new ArrayList<>();

        EncounterMatcher encounterMatcher = new EncounterMatcher(visit);
        List<Encounter> matchingEncounters = encounterMatcher.getMatchingEncounters(requestedEncounterType);

        List<KeyValue> matchingObservations = new ArrayList<>();
        for (Encounter matchingEncounter : matchingEncounters) {
            Set<Obs> leafObservations = matchingEncounter.getObs();
            for (Obs leafObservation : leafObservations) {
                KeyValue matchingObsRow = matchingObsRow(leafObservation, obsRows, shouldMatchValue);
                if (matchingObsRow != null) {
                    matchingObservations.add(matchingObsRow);
                }
            }
        }

        return matchingObservations;
    }

    private KeyValue matchingObsRow(Obs obs, List<KeyValue> obsRows, boolean shouldMatchValue) {
        for (KeyValue obsRow : obsRows) {
            if (doesConceptNameMatch(obs, obsRow)) {
                if (!shouldMatchValue || doesObsValueMatch(obs, obsRow)) return obsRow;
            }
        }
        return null;
    }

    private boolean doesConceptNameMatch(Obs obs, KeyValue obsRow) {
        return obsRow.getKey().equalsIgnoreCase(obs.getConcept().getName().getName());
    }

    private boolean doesObsValueMatch(Obs obs, KeyValue obsRow) {
        return obsRow.getValue().equalsIgnoreCase(obs.getValueAsString(Context.getLocale()));
    }
}
