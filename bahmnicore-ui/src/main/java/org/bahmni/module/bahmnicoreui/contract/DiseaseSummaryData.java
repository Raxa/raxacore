package org.bahmni.module.bahmnicoreui.contract;

import java.util.Map;
import java.util.Set;

public class DiseaseSummaryData {

    private Map<String,Map<String, ConceptValue>> tabularData;
    private Set<String> conceptNames;

    public Map<String, Map<String, ConceptValue>> getTabularData() {
        return tabularData;
    }

    public void setTabularData(Map<String, Map<String, ConceptValue>> tabularData) {
        this.tabularData = tabularData;
    }


    public void setConceptNames(Set<String> conceptNames) {
        this.conceptNames = conceptNames;
    }

    public Set<String> getConceptNames() {
        return conceptNames;
    }
}
