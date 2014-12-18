package org.bahmni.module.bahmnicoreui.helper;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.bahmnicoreui.contract.DiseaseSummaryData;
import org.bahmni.module.bahmnicoreui.helper.ConceptHelper;
import org.bahmni.module.bahmnicoreui.mapper.DiseaseSummaryMapper;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class DrugOrderDiseaseSummaryAggregator {

    private BahmniDrugOrderService drugOrderService;
    private ConceptHelper conceptHelper;
    private final DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();
    private Logger logger = Logger.getLogger(DrugOrderDiseaseSummaryAggregator.class);

    @Autowired
    public DrugOrderDiseaseSummaryAggregator(ConceptService conceptService, BahmniDrugOrderService drugOrderService) {
        this.drugOrderService = drugOrderService;
        this.conceptHelper = new ConceptHelper(conceptService);
    }

    public DiseaseSummaryData aggregate(Patient patient, List<String> conceptNames, Integer numberOfVisits) {
        DiseaseSummaryData diseaseSummaryData =  new DiseaseSummaryData();
        List<Concept> concepts = conceptHelper.getConceptsForNames(conceptNames);
        if(!concepts.isEmpty()){
            List<DrugOrder> drugOrders = drugOrderService.getPrescribedDrugOrdersForConcepts(patient, true, numberOfVisits, concepts);
            try {
                diseaseSummaryData.addTabularData(diseaseSummaryMapper.mapDrugOrders(drugOrders));
            } catch (IOException e) {
                logger.error("Could not parse dosing instructions",e);
                throw new RuntimeException("Could not parse dosing instructions",e);
            }
            diseaseSummaryData.addConceptNames(conceptNamesAsSet(conceptNames));
        }
        return diseaseSummaryData;
    }

    private Set<String> conceptNamesAsSet(List<String> conceptNames) {
        if(conceptNames == null || conceptNames.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        return new LinkedHashSet<>(conceptNames);
    }
}
