package org.bahmni.module.bahmnicoreui.contract;

import java.util.List;

public class DiseaseDataParams {

    private int numberOfVisits;
    private List<String> obsConcepts;
    private List<String> drugConcepts;
    private List<String> labConcepts;

    public int getNumberOfVisits() {
        return numberOfVisits;
    }

    public void setNumberOfVisits(int numberOfVisits) {
        this.numberOfVisits = numberOfVisits;
    }

    public List<String> getObsConcepts() {
        return obsConcepts;
    }

    public void setObsConcepts(List<String> obsConcepts) {
        this.obsConcepts = obsConcepts;
    }

    public List<String> getLabConcepts() {
        return labConcepts;
    }

    public void setLabConcepts(List<String> labConcepts) {
        this.labConcepts = labConcepts;
    }

    public List<String> getDrugConcepts() {
        return drugConcepts;
    }

    public void setDrugConcepts(List<String> drugConcepts) {
        this.drugConcepts = drugConcepts;
    }


}
