package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.diseasetemplate.DiseaseTemplate;

import java.util.List;

public interface DiseaseTemplateService {

    List<DiseaseTemplate> allDiseaseTemplatesFor(String patientUuid);

    DiseaseTemplate diseaseTemplateFor(String patientUUID, String diseaseName);
}
