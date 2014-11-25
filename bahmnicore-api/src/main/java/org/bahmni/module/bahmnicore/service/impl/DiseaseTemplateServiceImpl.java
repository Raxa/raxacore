package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.diseasetemplate.DiseaseTemplate;
import org.bahmni.module.bahmnicore.contract.diseasetemplate.ObservationTemplate;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.dao.VisitDao;
import org.bahmni.module.bahmnicore.mapper.ObservationTemplateMapper;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.bahmni.module.bahmnicore.service.BahmniVisitService;
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

    private BahmniObsService bahmniObsService;

    private BahmniVisitService bahmniVisitService;

    private ConceptService conceptService;

    private ConceptMapper conceptMapper;

    private ObservationTemplateMapper observationTemplateMapper;

    @Autowired
    public DiseaseTemplateServiceImpl(BahmniObsService bahmniObsService, BahmniVisitService bahmniVisitService, ConceptService conceptService) {
        this.bahmniObsService = bahmniObsService;
        this.bahmniVisitService = bahmniVisitService;
        this.conceptService = conceptService;
        this.conceptMapper = new ConceptMapper();
        this.observationTemplateMapper = new ObservationTemplateMapper(conceptMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseTemplate> allDiseaseTemplatesFor(String patientUuid) {
        List<Concept> diseaseTemplateConcepts = getDiseaseTemplateConcepts();
        List<DiseaseTemplate> diseaseTemplates = new ArrayList<>();

        for (Concept diseaseTemplateConcept : diseaseTemplateConcepts) {
            diseaseTemplates.add(getDiseaseTemplate(patientUuid, diseaseTemplateConcept));
        }

        return diseaseTemplates;
    }

    @Override
    @Transactional(readOnly = true)
    public DiseaseTemplate diseaseTemplateFor(String patientUUID, String diseaseName) {
        Concept diseaseTemplateConcept = conceptService.getConceptByName(diseaseName);
        DiseaseTemplate diseaseTemplate = new DiseaseTemplate(conceptMapper.map(diseaseTemplateConcept));
        List<Concept> observationTemplateConcepts = diseaseTemplateConcept.getSetMembers();
        for (Concept concept : observationTemplateConcepts) {
            List<BahmniObservation> observations = bahmniObsService.observationsFor(patientUUID, Arrays.asList(concept), null);
            List<ObservationTemplate> observationTemplates = observationTemplateMapper.map(observations, concept);
            diseaseTemplate.addObservationTemplates(observationTemplates);
        }

        return diseaseTemplate;
    }

    private DiseaseTemplate getDiseaseTemplate(String patientUuid, Concept diseaseTemplateConcept) {
        DiseaseTemplate diseaseTemplate = new DiseaseTemplate(conceptMapper.map(diseaseTemplateConcept));

        for (Concept concept : diseaseTemplateConcept.getSetMembers()) {
            Visit latestVisit = bahmniVisitService.getLatestVisit(patientUuid, concept.getName().getName());
            if (latestVisit != null) {
                diseaseTemplate.addObservationTemplate(getObservationTemplate(patientUuid, concept, latestVisit));
            }
        }

        return diseaseTemplate;
    }

    private ObservationTemplate getObservationTemplate(String patientUuid, Concept concept, Visit latestVisit) {
        List<BahmniObservation> observations = getLatestObsFor(patientUuid, concept.getName(Context.getLocale()).getName(), Arrays.asList(concept), latestVisit.getVisitId());
        ObservationTemplate observationTemplate = new ObservationTemplate();
        observationTemplate.setVisitStartDate(latestVisit.getStartDatetime());
        observationTemplate.setConcept(conceptMapper.map(concept));
        observationTemplate.setBahmniObservations(observations);
        return observationTemplate;
    }

    private List<BahmniObservation> getLatestObsFor(String patientUuid, String conceptName, List<Concept> rootConcepts, Integer visitId) {
        List<Obs> latestObsForConceptSet = bahmniObsService.getLatestObsForConceptSetByVisit(patientUuid, conceptName, visitId);
        return BahmniObservationMapper.map(latestObsForConceptSet, rootConcepts);
    }

    private List<Concept> getDiseaseTemplateConcepts() {
        Concept concept = conceptService.getConceptByName(DiseaseTemplate.ALL_DISEASE_TEMPLATES);
        return concept.getSetMembers();
    }
}
