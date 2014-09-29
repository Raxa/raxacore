package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.observation.DiseaseTemplate;

import java.util.List;

public interface DiseaseTemplateService {

    List<DiseaseTemplate> allDiseaseTemplatesFor(String patientUuid);
}
