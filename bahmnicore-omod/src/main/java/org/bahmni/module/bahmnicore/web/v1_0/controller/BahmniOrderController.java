package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.service.BahmniOrderService;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.order.contract.BahmniOrder;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/orders")
public class BahmniOrderController extends BaseRestController {
    private ConceptService conceptService;
    private BahmniOrderService bahmniOrderService;

    @Autowired
    public BahmniOrderController(ConceptService conceptService, BahmniOrderService bahmniOrderService) {
        this.conceptService = conceptService;
        this.bahmniOrderService = bahmniOrderService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<BahmniOrder> get(@RequestParam(value = "patientUuid", required = true) String patientUUID,
                                 @RequestParam(value = "concept", required = true) List<String> rootConceptNames,
                                 @RequestParam(value = "orderTypeUuid", required = true) String orderTypeUuid,
                                 @RequestParam(value = "numberOfVisits", required = false) Integer numberOfVisits,
                                 @RequestParam(value = "obsIgnoreList", required = false) List<String> obsIgnoreList,
                                 @RequestParam(value = "includeObs", required = false, defaultValue ="true") boolean includeObs) {

        List<Concept> rootConcepts = getConcepts(rootConceptNames);
        return bahmniOrderService.getLatestObservationsAndOrdersForOrderType(patientUUID, rootConcepts, numberOfVisits, obsIgnoreList, orderTypeUuid, includeObs);
    }


    @RequestMapping(method = RequestMethod.GET, params = {"orderUuid"})
    @ResponseBody
    public List<BahmniOrder> get(@RequestParam(value = "patientUuid", required = true) String patientUUID,
                                 @RequestParam(value = "concept", required = true) List<String> rootConceptNames,
                                 @RequestParam(value = "obsIgnoreList", required = false) List<String> obsIgnoreList,
                                 @RequestParam(value = "orderUuid", required = true) String orderUuid) {

        List<Concept> rootConcepts = getConcepts(rootConceptNames);
        return bahmniOrderService.getLatestObservationsForOrder(patientUUID, rootConcepts, obsIgnoreList, orderUuid);
    }

    private List<Concept> getConcepts(List<String> rootConceptNames) {
        List<Concept> rootConcepts = new ArrayList<>();
        for (String rootConceptName : rootConceptNames) {
            rootConcepts.add(conceptService.getConceptByName(rootConceptName));
        }
        return rootConcepts;
    }
}
