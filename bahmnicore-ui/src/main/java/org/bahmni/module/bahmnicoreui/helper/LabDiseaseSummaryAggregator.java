package org.bahmni.module.bahmnicoreui.helper;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmnicore.service.OrderService;
import org.bahmni.module.bahmnicoreui.contract.DiseaseDataParams;
import org.bahmni.module.bahmnicoreui.contract.DiseaseSummaryData;
import org.bahmni.module.bahmnicoreui.mapper.DiseaseSummaryLabMapper;
import org.bahmni.module.referencedata.contract.ConceptDetails;
import org.bahmni.module.referencedata.helper.ConceptHelper;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;
import org.openmrs.module.bahmniemrapi.laborder.service.LabOrderResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LabDiseaseSummaryAggregator {

    private final ConceptHelper conceptHelper;
    private LabOrderResultsService labOrderResultsService;
    private OrderService orderService;
    private VisitService visitService;
    private final DiseaseSummaryLabMapper diseaseSummaryLabMapper = new DiseaseSummaryLabMapper();


    @Autowired
    public LabDiseaseSummaryAggregator(ConceptHelper conceptHelper, LabOrderResultsService labOrderResultsService, OrderService orderService, VisitService visitService) {
        this.labOrderResultsService = labOrderResultsService;
        this.orderService = orderService;
        this.visitService = visitService;
        this.conceptHelper = conceptHelper;
    }

    public DiseaseSummaryData aggregate(Patient patient, DiseaseDataParams diseaseDataParams) {
        DiseaseSummaryData diseaseSummaryData =  new DiseaseSummaryData();
        List<Concept> concepts = conceptHelper.getConceptsForNames(diseaseDataParams.getLabConcepts());
        if(!concepts.isEmpty()){
            List<LabOrderResult> labOrderResults = labOrderResultsService.getAllForConcepts(patient, diseaseDataParams.getLabConcepts(), getVisits(patient, diseaseDataParams));
            diseaseSummaryData.addTabularData(diseaseSummaryLabMapper.map(labOrderResults, diseaseDataParams.getGroupBy()));
            diseaseSummaryData.addConceptDetails(conceptHelper.getLeafConceptDetails(diseaseDataParams.getLabConcepts(), false));
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

    private List<Visit> getVisits(Patient patient, final DiseaseDataParams diseaseDataParams) {
        if(StringUtils.isBlank(diseaseDataParams.getVisitUuid())){
            return orderService.getVisitsWithOrders(patient, "Order", true, diseaseDataParams.getNumberOfVisits());
        }
        return new ArrayList(){{
            add(visitService.getVisitByUuid(diseaseDataParams.getVisitUuid()))  ;
        }};
    }
}
