package org.openmrs.module.bahmniemrapi.pivottable.contract;

import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;

import java.util.HashMap;
import java.util.Map;

public class PivotRow {
    Map<String, BahmniObservation> columns = new HashMap<>();

    public void addColumn(String name, BahmniObservation bahmniObservation) {
        columns.put(name, bahmniObservation);
    }

    public BahmniObservation getValue(String key) {
        return columns.get(key);
    }

    public Map<String, BahmniObservation> getColumns() {
        return columns;
    }

}
