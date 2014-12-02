package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.diseasetemplate.DiseaseTemplate;
import org.bahmni.module.bahmnicore.contract.diseasetemplate.DiseaseTemplatesConfig;

import java.util.ArrayList;
import java.util.List;

public interface DiseaseTemplateService {

    public List<DiseaseTemplate> allDiseaseTemplatesFor(DiseaseTemplatesConfig diseaseTemplatesConfig);

    public DiseaseTemplate diseaseTemplateFor(String patientUUID, String diseaseName);
}
