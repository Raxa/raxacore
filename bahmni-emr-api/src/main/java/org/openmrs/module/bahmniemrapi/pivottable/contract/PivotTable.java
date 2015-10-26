package org.openmrs.module.bahmniemrapi.pivottable.contract;

import java.util.*;

public class PivotTable {
    private Set<String> headers = new LinkedHashSet<>();
    private List<PivotRow> rows = new ArrayList<>();

    public Set<String> getHeaders() {
        return headers;
    }

    public void setHeaders(Set<String> headers) {
        this.headers = headers;
    }

    public List<PivotRow> getRows() {
        return rows;
    }

    public void setRows(List<PivotRow> rows) {
        this.rows = rows;
    }
}