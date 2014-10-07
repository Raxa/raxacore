package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.observation.DiseaseTemplate;
import org.bahmni.module.bahmnicore.dao.PersonObsDao;
import org.bahmni.module.bahmnicore.service.DiseaseTemplateService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniObservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiseaseTemplateServiceImpl implements DiseaseTemplateService {

    private static final String ALL_DISEASE_TEMPLATES = "All Disease Templates";
    private static final String INTAKE_CONCEPT_CLASS = "Disease Intake";
    private static final String PROGRESS_CONCEPT_CLASS = "Disease Progress";

    @Autowired
    private PersonObsDao personObsDao;
    
    @Autowired
    private ConceptService conceptService;

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseTemplate> allDiseaseTemplatesFor(String patientUuid) {
        List<Concept> diseaseTemplateConcepts= getDiseaseTemplateConcepts();
        List<DiseaseTemplate> diseaseTemplates = new ArrayList<>();
        
        for (Concept diseaseTemplateConcept : diseaseTemplateConcepts) {
            DiseaseTemplate diseaseTemplate = new DiseaseTemplate(diseaseTemplateConcept.getName().getName());
            diseaseTemplates.add(diseaseTemplate);

            List<Concept> conceptSetsInDiseaseTemplates = diseaseTemplateConcept.getSetMembers();
            for (Concept conceptSet : conceptSetsInDiseaseTemplates) {
                List<Concept> rootConcepts = new ArrayList<>();
                rootConcepts.add(conceptService.getConceptByName(conceptSet.getName().getName()));

                List<BahmniObservation> observations = getLatestObsFor(patientUuid, conceptSet.getName().getName(), rootConcepts);
                diseaseTemplate.addBahmniObservationsList(observations);
            }
        }
        return diseaseTemplates;
    }

    private List<BahmniObservation> getLatestObsFor(String patientUuid, String conceptName, List<Concept> rootConcepts) {
        List<Obs> latestObsForConceptSet = personObsDao.getLatestObsForConceptSetByVisit(patientUuid, conceptName);
        return BahmniObservationMapper.map(latestObsForConceptSet, rootConcepts);
    }

    private List<Concept> getDiseaseTemplateConcepts() {
        Concept concept = conceptService.getConceptByName(ALL_DISEASE_TEMPLATES);
        return concept.getSetMembers();
    }
}
