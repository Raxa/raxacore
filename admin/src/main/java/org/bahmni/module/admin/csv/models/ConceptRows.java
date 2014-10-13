package org.bahmni.module.admin.csv.models;

import java.util.ArrayList;
import java.util.List;

public class ConceptRows {
    private List<ConceptRow> conceptRows;
    private List<ConceptSetRow> conceptSetRows;

    public List<ConceptRow> getConceptRows() {
        return conceptRows == null? new ArrayList<ConceptRow>(): conceptRows;
    }

    public void setConceptRows(List<ConceptRow> conceptRows) {
        this.conceptRows = conceptRows;
    }

    public List<ConceptSetRow> getConceptSetRows() {
        return conceptSetRows == null? new ArrayList<ConceptSetRow>(): conceptSetRows;
    }

    public void setConceptSetRows(List<ConceptSetRow> conceptSetRows) {
        this.conceptSetRows = conceptSetRows;
    }
}
