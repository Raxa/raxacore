package org.bahmni.module.bahmnicore.contract.diseasetemplate;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiseaseTemplateConfig {
    private String diseaseName;
    private List<String> showOnly;

    public DiseaseTemplateConfig(String diseaseName, List<String> showOnly) {
        this.diseaseName = diseaseName;
        this.showOnly = showOnly;
    }


    public DiseaseTemplateConfig() {
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public List<String> getShowOnly() {
        return showOnly;
    }

    public void setShowOnly(List<String> showOnly) {
        this.showOnly = showOnly;
    }
}
