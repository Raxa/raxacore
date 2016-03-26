package org.openmrs.module.bahmniemrapi.drugogram.contract;

import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class TreatmentRegimen {
    private Set<EncounterTransaction.Concept> headers = new LinkedHashSet<>();
    private SortedSet<RegimenRow> rows = new TreeSet<>(new RegimenRow.RegimenComparator());

    public Set<EncounterTransaction.Concept> getHeaders() {
        return headers;
    }

    public void setHeaders(Set<EncounterTransaction.Concept> headers) {
        this.headers = headers;
    }

    public SortedSet<RegimenRow> getRows() {
        return rows;
    }

    public void setRows(SortedSet<RegimenRow> rows) {
        this.rows.addAll(rows);
    }
}
