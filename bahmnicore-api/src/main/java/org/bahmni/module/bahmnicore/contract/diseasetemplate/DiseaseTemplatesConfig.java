package org.bahmni.module.bahmnicore.contract.diseasetemplate;

import java.util.List;

public class DiseaseTemplatesConfig {
    private List<DiseaseTemplateConfig> diseaseTemplateConfigList;
    private String patientUuid;

    public DiseaseTemplatesConfig() {
    }

    public List<DiseaseTemplateConfig> getDiseaseTemplateConfigList() {
        return diseaseTemplateConfigList;
    }

    public void setDiseaseTemplateConfigList(List<DiseaseTemplateConfig> diseaseTemplateConfigList) {
        this.diseaseTemplateConfigList = diseaseTemplateConfigList;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }
}
