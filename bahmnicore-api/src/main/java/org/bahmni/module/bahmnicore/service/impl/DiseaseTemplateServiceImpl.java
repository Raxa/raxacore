package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.diseasetemplate.DiseaseTemplate;
import org.bahmni.module.bahmnicore.contract.diseasetemplate.ObservationTemplate;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.dao.VisitDao;
import org.bahmni.module.bahmnicore.mapper.ObservationTemplateMapper;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.bahmni.module.bahmnicore.service.DiseaseTemplateService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
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
    private BahmniObsService bahmniObsService;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private VisitDao visitDao;

    private ConceptMapper conceptMapper = new ConceptMapper();

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseTemplate> allDiseaseTemplatesFor(String patientUuid) {
        List<Concept> diseaseTemplateConcepts = getDiseaseTemplateConcepts();
        List<DiseaseTemplate> diseaseTemplates = new ArrayList<>();

        for (Concept diseaseTemplateConcept : diseaseTemplateConcepts) {
            DiseaseTemplate diseaseTemplate = new DiseaseTemplate(conceptMapper.map(diseaseTemplateConcept));

            for (Concept concept : diseaseTemplateConcept.getSetMembers()) {
                Visit latestVisit = visitDao.getLatestVisit(patientUuid, concept.getName().getName());
                if (latestVisit != null) {
                    List<BahmniObservation> observations = getLatestObsFor(patientUuid, concept.getName().getName(), Arrays.asList(concept), latestVisit.getVisitId());
                    ObservationTemplate observationTemplate = new ObservationTemplate();
                    observationTemplate.setVisitStartDate(latestVisit.getStartDatetime());
                    observationTemplate.setConcept(conceptMapper.map(concept));
                    observationTemplate.setBahmniObservations(observations);

                    diseaseTemplate.addObservationTemplate(observationTemplate);
                }
            }
            diseaseTemplates.add(diseaseTemplate);
        }
        return diseaseTemplates;
    }

    @Override
    public DiseaseTemplate diseaseTemplateFor(String patientUUID, String diseaseName) {
        Concept diseaseTemplateConcept = conceptService.getConceptByName(diseaseName);
        DiseaseTemplate diseaseTemplate = new DiseaseTemplate(conceptMapper.map(diseaseTemplateConcept));
        ObservationTemplateMapper observationTemplateMapper = new ObservationTemplateMapper(new ConceptMapper());
        List<Concept> observationTemplates = diseaseTemplateConcept.getSetMembers();
        for (Concept concept : observationTemplates) {
            List<Concept> setMembers = new ArrayList<>();
            if(concept.isSet()){
                setMembers = concept.getSetMembers();
            } else{
                setMembers.add(concept);
            }
            List<BahmniObservation> observations = bahmniObsService.observationsFor(patientUUID, setMembers, null);
            List<ObservationTemplate> observationTemplateList = observationTemplateMapper.map(observations, concept);
            diseaseTemplate.addObservationTemplates(observationTemplateList);
        }

        return diseaseTemplate;
    }

    private List<BahmniObservation> getLatestObsFor(String patientUuid, String conceptName, List<Concept> rootConcepts, Integer visitId) {
        List<Obs> latestObsForConceptSet = obsDao.getLatestObsForConceptSetByVisit(patientUuid, conceptName, visitId);
        return BahmniObservationMapper.map(latestObsForConceptSet, rootConcepts);
    }

    private List<Concept> getDiseaseTemplateConcepts() {
        Concept concept = conceptService.getConceptByName(ALL_DISEASE_TEMPLATES);
        return concept.getSetMembers();
    }
}
