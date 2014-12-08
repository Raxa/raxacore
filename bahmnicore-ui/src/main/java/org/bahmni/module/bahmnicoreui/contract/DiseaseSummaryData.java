package org.bahmni.module.bahmnicoreui.contract;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DiseaseSummaryData {

    private Map<String,Map<String, ConceptValue>> tabularData = new LinkedHashMap<>();
    private Set<String> conceptNames;

    public Map<String, Map<String, ConceptValue>> getTabularData() {
        return tabularData;
    }

    public void setTabularData(Map<String, Map<String, ConceptValue>> tabularData) {
        this.tabularData = tabularData;
    }

    public void addTabularData(Map<String, Map<String, ConceptValue>> newTable){
        for (String visitDate : newTable.keySet()) {
            Map<String, ConceptValue> valuesForVisit = getValuesForVisit(visitDate);//tabularData.get(visitDate);
            valuesForVisit.putAll(newTable.get(visitDate));
        }
    }

    private Map<String, ConceptValue> getValuesForVisit(String visitDate) {
        Map<String, ConceptValue> valuesForVisit = tabularData.get(visitDate);
        if( valuesForVisit == null){
            valuesForVisit = new LinkedHashMap<>();
            tabularData.put(visitDate,valuesForVisit);
        }
        return valuesForVisit;
    }

    public void setConceptNames(Set<String> conceptNames) {
        this.conceptNames = conceptNames;
    }

    public Set<String> getConceptNames() {
        return conceptNames;
    }
}
