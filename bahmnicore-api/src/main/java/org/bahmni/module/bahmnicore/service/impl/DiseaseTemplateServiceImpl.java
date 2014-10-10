package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.diseasetemplate.DiseaseTemplate;
import org.bahmni.module.bahmnicore.contract.diseasetemplate.ObservationTemplate;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.DiseaseTemplateService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniObservationMapper;
import org.openmrs.module.emrapi.encounter.ConceptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DiseaseTemplateServiceImpl implements DiseaseTemplateService {

    private static final String ALL_DISEASE_TEMPLATES = "All Disease Templates";

    @Autowired
    private ObsDao obsDao;
    
    @Autowired
    private ConceptService conceptService;

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseTemplate> allDiseaseTemplatesFor(String patientUuid) {
        List<Concept> diseaseTemplateConcepts= getDiseaseTemplateConcepts();
        List<DiseaseTemplate> diseaseTemplates = new ArrayList<>();
        
        for (Concept diseaseTemplateConcept : diseaseTemplateConcepts) {
            DiseaseTemplate diseaseTemplate = new DiseaseTemplate(diseaseTemplateConcept.getName().getName());

            for (Concept concept : diseaseTemplateConcept.getSetMembers()) {
                List<Concept> concepts = Arrays.asList(conceptService.getConceptByName(concept.getName().getName()));
                List<BahmniObservation> observations = getLatestObsFor(patientUuid, concept.getName().getName(), concepts);
                
                ObservationTemplate observationTemplate = new ObservationTemplate();
                observationTemplate.setConcept(new ConceptMapper().map(concept));
                observationTemplate.setBahmniObservations(observations);
                
                diseaseTemplate.addObservationTemplate(observationTemplate);
            }
            diseaseTemplates.add(diseaseTemplate);
        }
        return diseaseTemplates;
    }

    private List<BahmniObservation> getLatestObsFor(String patientUuid, String conceptName, List<Concept> rootConcepts) {
        List<Obs> latestObsForConceptSet = obsDao.getLatestObsForConceptSetByVisit(patientUuid, conceptName);
        return BahmniObservationMapper.map(latestObsForConceptSet, rootConcepts);
    }

    private List<Concept> getDiseaseTemplateConcepts() {
        Concept concept = conceptService.getConceptByName(ALL_DISEASE_TEMPLATES);
        return concept.getSetMembers();
    }
}
