package org.bahmni.module.bahmnicoreui.service.impl;

import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.bahmni.module.bahmnicoreui.contract.DiseaseDataParams;
import org.bahmni.module.bahmnicoreui.contract.DiseaseSummaryData;
import org.bahmni.module.bahmnicoreui.mapper.DiseaseSummaryMapper;
import org.bahmni.module.bahmnicoreui.service.BahmniDiseaseSummaryService;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniObservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    @Transactional(readOnly = true)
    public DiseaseSummaryData getDiseaseSummary(String patientUuid, DiseaseDataParams queryParams) {
        DiseaseSummaryData diseaseSummaryData = new DiseaseSummaryData();
        Collection<Concept> concepts = new ArrayList<>();
            if(queryParams.getObsConcepts() == null){
            throw new RuntimeException("ObsConcept list is null: atleast one concept name should be specified for getting observations of related concept");
        }
        for (String conceptName : queryParams.getObsConcepts()) {
            concepts.add(conceptService.getConceptByName(conceptName));
        }
        List<BahmniObservation> bahmniObservations = bahmniObsService.observationsFor(patientUuid, concepts, queryParams.getNumberOfVisits());
        diseaseSummaryData.setTabularData(diseaseSummaryMapper.mapObservations(bahmniObservations));
        diseaseSummaryData.setConceptNames(getLeafConceptNames(queryParams.getObsConcepts()));
        return diseaseSummaryData;
    }

    private Set<String> getLeafConceptNames(List<String> obsConcepts) {
        Set<String> leafConcepts = new HashSet<>();
        for (String conceptName : obsConcepts) {
            Concept concept = conceptService.getConceptByName(conceptName);
            addLeafConcepts(concept,leafConcepts);
        }
        return leafConcepts;
    }

    private void addLeafConcepts(Concept rootConcept, Collection<String> leafConcepts) {
        if(rootConcept.isSet()){
            for (Concept setMember : rootConcept.getSetMembers()) {
                addLeafConcepts(setMember,leafConcepts);
            };
        }
        else if(!shouldBeExcluded(rootConcept)){
            String fullName = getConceptName(rootConcept, ConceptNameType.FULLY_SPECIFIED);
            String shortName = getConceptName(rootConcept, ConceptNameType.SHORT);
            leafConcepts.add(shortName==null?fullName:shortName);
        }
    }

    private String getConceptName(Concept rootConcept, ConceptNameType conceptNameType) {
        String conceptName = null;
        ConceptName name = rootConcept.getName(Context.getLocale(), conceptNameType, null);
        if(name != null){
            conceptName  = name.getName();
        }
        return conceptName;
    }

    private boolean shouldBeExcluded(Concept rootConcept) {
        if(BahmniObservationMapper.ABNORMAL_CONCEPT_CLASS.equals(rootConcept.getConceptClass().getName()) ||
                BahmniObservationMapper.DURATION_CONCEPT_CLASS.equals(rootConcept.getConceptClass().getName())){
            return true;
        }
        return false;
    }

}
