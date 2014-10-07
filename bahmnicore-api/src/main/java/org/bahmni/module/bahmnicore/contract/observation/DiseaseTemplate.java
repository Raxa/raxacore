package org.bahmni.module.bahmnicore.contract.observation;

import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;

import java.util.ArrayList;
import java.util.List;

public class DiseaseTemplate {
    
    private String name;
    
    private List<List<BahmniObservation>> bahmniObservations = new ArrayList<>();

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

    public List<List<BahmniObservation>> getBahmniObservations() {
        return bahmniObservations;
    }

    public void setBahmniObservations(List<List<BahmniObservation>> bahmniObservations) {
        this.bahmniObservations = bahmniObservations;
    }

    public void addBahmniObservationsList(List<BahmniObservation> bahmniObservations){
        this.bahmniObservations.add(bahmniObservations);
    }

}
