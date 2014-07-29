package org.bahmni.module.bahmnicore.contract.drugorder;


import org.bahmni.module.bahmnicore.contract.observation.*;

import java.util.*;

public class DrugOrderConfigResponse {
    private List<ConceptData> doseUnits;
    private List<ConceptData> routes;
    private List<ConceptData> durationUnits;
    private List<ConceptData> dispensingUnits;
    private List<ConceptData> dosingInstructions;
    public List<OrderFrequencyData> getFrequencies() {
        return frequencies;
    }

    public void setFrequencies(List<OrderFrequencyData> frequencies) {
        this.frequencies = frequencies;
    }

    private List<OrderFrequencyData> frequencies = new ArrayList<>();

    public void setDoseUnits(List<ConceptData> doseUnits) {
        this.doseUnits = doseUnits;
    }

    public List<ConceptData> getDoseUnits() {
        return doseUnits;
    }

    public void setRoutes(List<ConceptData> routes) {
        this.routes = routes;
    }

    public List<ConceptData> getRoutes() {
        return routes;
    }

    public void setDurationUnits(List<ConceptData> durationUnits) {
        this.durationUnits = durationUnits;
    }

    public List<ConceptData> getDurationUnits() {
        return durationUnits;
    }

    public void setDispensingUnits(List<ConceptData> quantityUnits) {
        this.dispensingUnits = quantityUnits;
    }

    public List<ConceptData> getDispensingUnits() {
        return dispensingUnits;
    }

    public void setDosingInstructions(List<ConceptData> dosingInstructions) {
        this.dosingInstructions = dosingInstructions;
    }

    public List<ConceptData> getDosingInstructions() {
        return dosingInstructions;
    }
}
