package org.bahmni.module.bahmnicore.contract.observation;

import java.util.ArrayList;
import java.util.List;

public class DiseaseTemplate {
    private String name;
    private List<List<ObservationData>> observations = new ArrayList<>();

    public DiseaseTemplate() {
    }

    public DiseaseTemplate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<List<ObservationData>> getObservations() {
        return observations;
    }

    public void setObservations(List<List<ObservationData>> observations) {
        this.observations = observations;
    }

    public void addObservationsList(List<ObservationData> observationDataList){
        observations.add(observationDataList);
    }

}
