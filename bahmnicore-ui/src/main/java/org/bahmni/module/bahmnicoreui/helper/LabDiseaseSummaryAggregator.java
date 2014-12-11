package org.bahmni.module.bahmnicoreui.helper;

import org.bahmni.module.bahmnicore.dao.OrderDao;
import org.bahmni.module.bahmnicoreui.contract.DiseaseSummaryData;
import org.bahmni.module.bahmnicoreui.helper.ConceptHelper;
import org.bahmni.module.bahmnicoreui.mapper.DiseaseSummaryMapper;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;
import org.openmrs.module.bahmniemrapi.laborder.service.LabOrderResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LabDiseaseSummaryAggregator {

    private final ConceptHelper conceptHelper;
    private LabOrderResultsService labOrderResultsService;
    private OrderDao orderDao;
    private final DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();


    @Autowired
    public LabDiseaseSummaryAggregator(ConceptService conceptService, LabOrderResultsService labOrderResultsService, OrderDao orderDao) {
        this.labOrderResultsService = labOrderResultsService;
        this.orderDao = orderDao;
        this.conceptHelper = new ConceptHelper(conceptService);

    }

    public DiseaseSummaryData aggregate(Patient patient, List<String> conceptNames, Integer numberOfVisits) {
        DiseaseSummaryData diseaseSummaryData =  new DiseaseSummaryData();
        List<Concept> concepts = conceptHelper.getConceptsForNames(conceptNames);
        if(!concepts.isEmpty()){
            List<LabOrderResult> labOrderResults = labOrderResultsService.getAllForConcepts(patient, conceptNames, getVisitsWithLabOrdersFor(patient,numberOfVisits,concepts));
            diseaseSummaryData.addTabularData(diseaseSummaryMapper.mapLabResults(labOrderResults));
            diseaseSummaryData.addConceptNames(conceptHelper.getLeafConceptNames(conceptNames));
        }
        return diseaseSummaryData;
    }

    private List<Visit> getVisitsWithLabOrdersFor(Patient patient, Integer numberOfVisits, List<Concept> concepts) {
        return orderDao.getVisitsWithOrdersForConcepts(patient, "TestOrder", true, numberOfVisits, concepts);
    }
}
