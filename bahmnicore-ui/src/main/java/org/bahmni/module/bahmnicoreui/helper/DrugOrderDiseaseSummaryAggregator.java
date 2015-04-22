package org.bahmni.module.bahmnicoreui.helper;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.bahmnicore.service.OrderService;
import org.bahmni.module.bahmnicoreui.contract.DiseaseDataParams;
import org.bahmni.module.bahmnicoreui.contract.DiseaseSummaryData;
import org.bahmni.module.bahmnicoreui.mapper.DiseaseSummaryDrugOrderMapper;
import org.bahmni.module.bahmnimetadata.helper.ConceptHelper;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DrugOrderDiseaseSummaryAggregator {

    private final VisitService visitService;
    private BahmniDrugOrderService drugOrderService;
    private ConceptHelper conceptHelper;
    private OrderService orderService;
    private final DiseaseSummaryDrugOrderMapper diseaseSummaryDrugOrderMapper = new DiseaseSummaryDrugOrderMapper();

    @Autowired
    public DrugOrderDiseaseSummaryAggregator(ConceptHelper conceptHelper, VisitService visitService, BahmniDrugOrderService drugOrderService, OrderService orderService) {
        this.visitService = visitService;
        this.drugOrderService = drugOrderService;
        this.orderService = orderService;
        this.conceptHelper = conceptHelper;
    }

    public DiseaseSummaryData aggregate(Patient patient, DiseaseDataParams diseaseDataParams) {
        DiseaseSummaryData diseaseSummaryData = new DiseaseSummaryData();
        List<Concept> concepts = conceptHelper.getConceptsForNames(diseaseDataParams.getDrugConcepts());
        if (!concepts.isEmpty()) {
            List<DrugOrder> drugOrders = drugOrderService.getPrescribedDrugOrdersForConcepts(patient, true, getVisits(patient, diseaseDataParams), concepts);
            diseaseSummaryData.addTabularData(diseaseSummaryDrugOrderMapper.map(drugOrders, diseaseDataParams.getGroupBy()));
            diseaseSummaryData.addConceptDetails(conceptHelper.getConceptDetails(diseaseDataParams.getDrugConcepts()));
        }
        return diseaseSummaryData;
    }

    private List<Visit> getVisits(Patient patient, final DiseaseDataParams diseaseDataParams) {
        if (StringUtils.isBlank(diseaseDataParams.getVisitUuid())) {
            return orderService.getVisitsWithOrders(patient, "DrugOrder", true, diseaseDataParams.getNumberOfVisits());
        }
        return new ArrayList() {{
            add(visitService.getVisitByUuid(diseaseDataParams.getVisitUuid()));
        }};
    }
}
