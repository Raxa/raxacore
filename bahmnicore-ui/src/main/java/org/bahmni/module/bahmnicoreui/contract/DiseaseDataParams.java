package org.bahmni.module.bahmnicoreui.contract;

import java.util.List;

public class DiseaseDataParams {
    public int getNumberOfVisits() {
        return numberOfVisits;
    }

    public void setNumberOfVisits(int numberOfVisits) {
        this.numberOfVisits = numberOfVisits;
    }

    private int numberOfVisits;

    public List<String> getObsConcepts() {
        return obsConcepts;
    }

    public void setObsConcepts(List<String> obsConcepts) {
        this.obsConcepts = obsConcepts;
    }

    private List<String> obsConcepts;
    private List<String> drugConcepts;
    private List<String> labConcepts;
}
