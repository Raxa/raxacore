package org.bahmni.module.bahmnicoreui.service.impl;

import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.bahmni.module.bahmnicoreui.contract.DiseaseDataParams;
import org.bahmni.module.bahmnicoreui.contract.DiseaseSummaryData;
import org.bahmni.module.bahmnicoreui.mapper.DiseaseSummaryMapper;
import org.bahmni.module.bahmnicoreui.service.BahmniDiseaseSummaryService;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniObservationMapper;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;
import org.openmrs.module.bahmniemrapi.laborder.service.LabOrderResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class BahmniDiseaseSummaryServiceImpl implements BahmniDiseaseSummaryService {

    private PatientService patientService;
    private BahmniObsService bahmniObsService;
    private LabOrderResultsService labOrderResultsService;

    private ConceptService conceptService;

    private DiseaseSummaryMapper diseaseSummaryMapper;
    private BahmniDrugOrderService drugOrderService;

    @Autowired
    public BahmniDiseaseSummaryServiceImpl(PatientService patientService, BahmniObsService bahmniObsService, LabOrderResultsService labOrderResultsService, ConceptService conceptService, BahmniDrugOrderService drugOrderService) {
        this.patientService = patientService;
        this.bahmniObsService = bahmniObsService;
        this.labOrderResultsService = labOrderResultsService;
        this.conceptService = conceptService;
        this.drugOrderService = drugOrderService;
        this.diseaseSummaryMapper = new DiseaseSummaryMapper();
    }

    @Override
    @Transactional(readOnly = true)
    public DiseaseSummaryData getDiseaseSummary(String patientUuid, DiseaseDataParams queryParams) {
        DiseaseSummaryData diseaseSummaryData = new DiseaseSummaryData();
        Collection<Concept> concepts = new ArrayList<>();
        for (String conceptName : conceptNamesAsSet(queryParams.getObsConcepts())) {
            concepts.add(conceptService.getConceptByName(conceptName));
        }
        List<Concept> drugConcepts = new ArrayList<>();
        if(queryParams.getDrugConcepts() != null){
            for (String conceptName : queryParams.getDrugConcepts()) {
                drugConcepts.add(conceptService.getConceptByName(conceptName.replaceAll("%20", " ")));
            }
        }

        Patient patient = patientService.getPatientByUuid(patientUuid);
        List<BahmniObservation> bahmniObservations = null;
        if(!concepts.isEmpty()){
            bahmniObservations = bahmniObsService.observationsFor(patientUuid, concepts, queryParams.getNumberOfVisits());
        }
        List<LabOrderResult> labOrderResults = labOrderResultsService.getAllForConcepts(patient, queryParams.getLabConcepts(), null);
        List<DrugOrder> drugOrders = drugOrderService.getPrescribedDrugOrdersForConcepts(patient, true, null, drugConcepts);

        diseaseSummaryData.addTabularData(diseaseSummaryMapper.mapObservations(bahmniObservations));
        diseaseSummaryData.addTabularData(diseaseSummaryMapper.mapLabResults(labOrderResults));
        diseaseSummaryData.addTabularData(diseaseSummaryMapper.mapDrugOrders(drugOrders));

        diseaseSummaryData.addConceptNames(getLeafConceptNames(queryParams.getObsConcepts()));
        diseaseSummaryData.addConceptNames(getLeafConceptNames(queryParams.getLabConcepts()));
        diseaseSummaryData.addConceptNames(conceptNamesAsSet(queryParams.getDrugConcepts()));
        return diseaseSummaryData;
    }

    private Set<String> conceptNamesAsSet(List<String> conceptNames) {
        if(conceptNames == null || conceptNames.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        return new LinkedHashSet<>(conceptNames);
    }

    private Set<String> getLeafConceptNames(List<String> obsConcepts) {
        if(obsConcepts != null && !obsConcepts.isEmpty()){
            Set<String> leafConcepts = new LinkedHashSet<>();
            for (String conceptName : obsConcepts) {
                Concept concept = conceptService.getConceptByName(conceptName);
                addLeafConcepts(concept, null, leafConcepts);
            }
            return leafConcepts;
        }
        return Collections.EMPTY_SET;
    }

    private void addLeafConcepts(Concept rootConcept, Concept parentConcept, Collection<String> leafConcepts) {
        if(rootConcept.isSet()){
            for (Concept setMember : rootConcept.getSetMembers()) {
                addLeafConcepts(setMember,rootConcept,leafConcepts);
            }
        }
        else if(!shouldBeExcluded(rootConcept)){
            Concept conceptToAdd = rootConcept;
            if(parentConcept != null){
                if(BahmniObservationMapper.CONCEPT_DETAILS_CONCEPT_CLASS.equals(parentConcept.getConceptClass().getName())){
                    conceptToAdd = parentConcept;
                }
            }
            String fullName = getConceptName(conceptToAdd, ConceptNameType.FULLY_SPECIFIED);
            String shortName = getConceptName(conceptToAdd, ConceptNameType.SHORT);
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
        return BahmniObservationMapper.ABNORMAL_CONCEPT_CLASS.equals(rootConcept.getConceptClass().getName()) ||
                BahmniObservationMapper.DURATION_CONCEPT_CLASS.equals(rootConcept.getConceptClass().getName());
    }

}
