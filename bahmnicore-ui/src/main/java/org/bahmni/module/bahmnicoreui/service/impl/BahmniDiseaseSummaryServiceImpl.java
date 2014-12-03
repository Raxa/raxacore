package org.bahmni.module.bahmnicoreui.service.impl;

import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.bahmni.module.bahmnicoreui.contract.ConceptValue;
import org.bahmni.module.bahmnicoreui.contract.DiseaseDataParams;
import org.bahmni.module.bahmnicoreui.mapper.DiseaseSummaryMapper;
import org.bahmni.module.bahmnicoreui.service.BahmniDiseaseSummaryService;
import org.openmrs.Concept;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openmrs.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Service
public class BahmniDiseaseSummaryServiceImpl implements BahmniDiseaseSummaryService {

    private BahmniObsService bahmniObsService;

    private ConceptService conceptService;

    private DiseaseSummaryMapper diseaseSummaryMapper;

    @Autowired
    public BahmniDiseaseSummaryServiceImpl(BahmniObsService bahmniObsService, ConceptService conceptService) {
        this.bahmniObsService = bahmniObsService;
        this.conceptService = conceptService;
        this.diseaseSummaryMapper = new DiseaseSummaryMapper();
    }

    @Override
    public Map<String, Map<String, ConceptValue>> getDiseaseSummary(String patientUuid, DiseaseDataParams queryParams) {
        Collection<Concept> concepts = new ArrayList<>();
        if(queryParams.getObsConcepts() == null){
            throw new RuntimeException("ObsConcept list is null: atleast one concept name should be specified for getting observations of related concept");
        }
        for (String conceptName : queryParams.getObsConcepts()) {
            concepts.add(conceptService.getConceptByName(conceptName));
        }
        List<BahmniObservation> bahmniObservations = bahmniObsService.observationsFor(patientUuid, concepts, queryParams.getNumberOfVisits());
        return  diseaseSummaryMapper.mapObservations(bahmniObservations);
    }

}
