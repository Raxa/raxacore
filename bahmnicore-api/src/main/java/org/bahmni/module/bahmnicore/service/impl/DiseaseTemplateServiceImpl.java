package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.observation.ConceptDefinition;
import org.bahmni.module.bahmnicore.contract.observation.DiseaseTemplate;
import org.bahmni.module.bahmnicore.contract.observation.ObservationData;
import org.bahmni.module.bahmnicore.dao.PersonObsDao;
import org.bahmni.module.bahmnicore.mapper.BahmniObservationsMapper;
import org.bahmni.module.bahmnicore.service.DiseaseTemplateService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DiseaseTemplateServiceImpl implements DiseaseTemplateService {

    private static final String ALL_DISEASE_TEMPLATES = "All Disease Templates";
    private static final String INTAKE_CONCEPT_CLASS = "Disease Intake";
    private static final String PROGRESS_CONCEPT_CLASS = "Disease Progress";

    @Autowired
    private PersonObsDao personObsDao;
    
    @Autowired
    private ConceptService conceptService;
    
    @Autowired
    private RestService restService;

    @Autowired
    private org.bahmni.module.bahmnicore.service.ConceptService bahmniConceptService;


    @Override
    public List<DiseaseTemplate> allDiseaseTemplatesFor(String patientUuid) {
        List<Concept> diseaseTemplateConcepts= getDiseaseTemplateConcepts();
        List<DiseaseTemplate> diseaseTemplates = new ArrayList<>();
        
        for (Concept diseaseTemplateConcept : diseaseTemplateConcepts) {
            DiseaseTemplate diseaseTemplate = new DiseaseTemplate(diseaseTemplateConcept.getName().getName());
            diseaseTemplates.add(diseaseTemplate);

            List<Concept> conceptSetsInDiseaseTemplates = diseaseTemplateConcept.getSetMembers();
            for (Concept conceptSet : conceptSetsInDiseaseTemplates) {
                ConceptDefinition conceptDefinition = bahmniConceptService.conceptsFor(Arrays.asList(conceptSet.getName().getName()));
                List<ObservationData> observations = getLatestObsfor(patientUuid, conceptSet.getName().getName(),conceptDefinition);
                diseaseTemplate.addObservationsList(observations);
            }
        }
        return diseaseTemplates;
    }

    private List<ObservationData> getLatestObsfor(String patientUuid, String conceptName, ConceptDefinition conceptDefinition) {
        List<Obs> latestObsForConceptSet = personObsDao.getLatestObsForConceptSetByVisit(patientUuid, conceptName);
        List<ObservationData> observations = new BahmniObservationsMapper(restService, conceptDefinition).mapNonVoidedObservations(latestObsForConceptSet);
        return observations;
    }

    private List<Concept> getDiseaseTemplateConcepts() {
        Concept concept = conceptService.getConceptByName(ALL_DISEASE_TEMPLATES);
        return concept.getSetMembers();
    }
}
