package org.bahmni.module.admin.encounter;

import org.bahmni.csv.KeyValue;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;

public class DuplicateObservationsMatcher {
    private BahmniVisit visit;
    private EncounterType requestedEncounterType;

    public DuplicateObservationsMatcher(BahmniVisit visit, EncounterType requestedEncounterType) {
        this.visit = visit;
        this.requestedEncounterType = requestedEncounterType;
    }

    public List<KeyValue> getUniqueObsRows(List<KeyValue> obsRows, boolean shouldMatchValue) {
        List<Obs> allObs = visit.obsFor(requestedEncounterType);

        List<KeyValue> uniqueObsRows = new ArrayList<>();
        for (KeyValue obsRow : obsRows) {
            if (isUnique(allObs, obsRow, shouldMatchValue)) {
                uniqueObsRows.add(obsRow);
            }
        }
        return uniqueObsRows;
    }

    private boolean isUnique(List<Obs> allObs, KeyValue obsRow, boolean shouldMatchValue) {
        for (Obs anObs : allObs) {
            if (doesConceptNameMatch(anObs, obsRow) &&
                    (!shouldMatchValue || doesObsValueMatch(anObs, obsRow)))
                return false;

        }
        return true;
    }

    private boolean doesConceptNameMatch(Obs obs, KeyValue obsRow) {
        return obsRow.getKey().equalsIgnoreCase(obs.getConcept().getName().getName());
    }

    private boolean doesObsValueMatch(Obs obs, KeyValue obsRow) {
        return obsRow.getValue().equalsIgnoreCase(obs.getValueAsString(Context.getLocale()));
    }
}
