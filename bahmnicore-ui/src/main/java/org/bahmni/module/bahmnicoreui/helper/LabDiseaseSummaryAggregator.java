package org.bahmni.module.bahmnicoreui.helper;

import org.bahmni.module.bahmnicore.service.OrderService;
import org.bahmni.module.bahmnicoreui.contract.ConceptDetails;
import org.bahmni.module.bahmnicoreui.contract.DiseaseSummaryData;
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
    private OrderService orderService;
    private final DiseaseSummaryMapper diseaseSummaryMapper = new DiseaseSummaryMapper();


    @Autowired
    public LabDiseaseSummaryAggregator(ConceptService conceptService, LabOrderResultsService labOrderResultsService, OrderService orderService) {
        this.labOrderResultsService = labOrderResultsService;
        this.orderService = orderService;
        this.conceptHelper = new ConceptHelper(conceptService);

    }

    public DiseaseSummaryData aggregate(Patient patient, List<String> conceptNames, Integer numberOfVisits) {
        DiseaseSummaryData diseaseSummaryData =  new DiseaseSummaryData();
        List<Concept> concepts = conceptHelper.getConceptsForNames(conceptNames);
        if(!concepts.isEmpty()){
            List<LabOrderResult> labOrderResults = labOrderResultsService.getAllForConcepts(patient, conceptNames, getVisitsWithLabOrdersFor(patient,numberOfVisits));
            diseaseSummaryData.addTabularData(diseaseSummaryMapper.mapLabResults(labOrderResults));
            diseaseSummaryData.addConceptDetails(conceptHelper.getLeafConceptDetails(conceptNames));
            mapLowNormalAndHiNormal(diseaseSummaryData, labOrderResults);
        }
        return diseaseSummaryData;
    }

    private void mapLowNormalAndHiNormal(DiseaseSummaryData diseaseSummaryData, List<LabOrderResult> labOrderResults) {
        for (ConceptDetails conceptDetails : diseaseSummaryData.getConceptDetails()) {
            LabOrderResult labOrderResult = findLabOrder(conceptDetails.getName(), labOrderResults);
            if (labOrderResult!= null){
                conceptDetails.setHiNormal(labOrderResult.getMaxNormal());
                conceptDetails.setLowNormal(labOrderResult.getMinNormal());
                conceptDetails.setUnits(labOrderResult.getTestUnitOfMeasurement() != null ? labOrderResult.getTestUnitOfMeasurement() : conceptDetails.getUnits());
            }
        }

    }

    private LabOrderResult findLabOrder(String name, List<LabOrderResult> labOrderResults) {
        for (LabOrderResult labOrderResult : labOrderResults) {
            if(labOrderResult.getTestName().equals(name)){
                return labOrderResult;
            };
        }
        return null;
    }

    private List<Visit> getVisitsWithLabOrdersFor(Patient patient, Integer numberOfVisits) {
        return orderService.getVisitsWithOrders(patient, "TestOrder", true, numberOfVisits);
    }
}
