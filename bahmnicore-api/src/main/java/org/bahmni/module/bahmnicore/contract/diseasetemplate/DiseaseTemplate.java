package org.bahmni.module.bahmnicore.contract.diseasetemplate;

import java.util.ArrayList;
import java.util.List;

public class DiseaseTemplate {
    
    private String name;
    private List<ObservationTemplate> observationTemplates = new ArrayList<>();
    
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

    public List<ObservationTemplate> getObservationTemplates() {
        return observationTemplates;
    }

    public void setObservationTemplates(List<ObservationTemplate> observationTemplates) {
        this.observationTemplates = observationTemplates;
    }

    public void addObservationTemplate(ObservationTemplate observationTemplate) {
        this.observationTemplates.add(observationTemplate);
    }

    public void addObservationTemplates(List<ObservationTemplate> observationTemplates) {
        this.observationTemplates.addAll(observationTemplates);
    }
}
