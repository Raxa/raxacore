package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.drugorder.*;
import org.bahmni.module.bahmnicore.contract.observation.*;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.api.context.*;
import org.openmrs.module.webservices.rest.web.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/drugorder")
public class DrugOrderConfigController {

    private final String GP_DOSING_INSTRUCTIONS_CONCEPT_UUID = "order.dosingInstructionsConceptUuid";
    private OrderService orderService;

    @Autowired
    public DrugOrderConfigController(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "config")
    @ResponseBody
    public DrugOrderConfigResponse getConfig() {
        DrugOrderConfigResponse response = new DrugOrderConfigResponse();
        response.setFrequencies(getFrequencies());
        response.setRoutes(mapConcepts(orderService.getDrugRoutes()));
        response.setDoseUnits(mapConcepts(orderService.getDrugDosingUnits()));
        response.setDurationUnits(mapConcepts(orderService.getDurationUnits()));
        response.setDispensingUnits(mapConcepts(orderService.getDrugDispensingUnits()));
        response.setDosingInstructions(mapConcepts(getSetMembersOfConceptSetFromGP(GP_DOSING_INSTRUCTIONS_CONCEPT_UUID)));
        return response;
    }

    private List<Concept> getSetMembersOfConceptSetFromGP(String globalProperty) {
        String conceptUuid = Context.getAdministrationService().getGlobalProperty(globalProperty);
        Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
        if (concept != null && concept.isSet()) {
            return concept.getSetMembers();
        }
        return Collections.emptyList();
    }

    private List<ConceptData> mapConcepts(List<Concept> drugDosingUnits) {
        List<ConceptData> listOfDoseUnits = new ArrayList<>();
        for (Concept drugDosingUnit : drugDosingUnits) {
            listOfDoseUnits.add(new ConceptData(drugDosingUnit));
        }
        return listOfDoseUnits;
    }

    private List<OrderFrequencyData> getFrequencies() {
        List<OrderFrequencyData> listOfFrequencyData = new ArrayList<>();
        for (OrderFrequency orderFrequency : orderService.getOrderFrequencies(false)) {
            listOfFrequencyData.add(new OrderFrequencyData(orderFrequency));
        }
        return listOfFrequencyData;
    }


}
