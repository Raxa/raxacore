package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.bahmnicore.contract.encounter.data.ConceptData;
import org.bahmni.module.bahmnicore.contract.encounter.data.ObservationData;
import org.bahmni.module.bahmnicore.contract.encounter.data.TestOrderData;
import org.bahmni.module.bahmnicore.contract.encounter.request.CreateEncounterRequest;
import org.bahmni.module.bahmnicore.contract.encounter.request.GetObservationsRequest;
import org.bahmni.module.bahmnicore.contract.encounter.response.EncounterConfigResponse;
import org.bahmni.module.bahmnicore.contract.encounter.response.EncounterDataResponse;
import org.bahmni.module.bahmnicore.contract.encounter.response.EncounterObservationResponse;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/bahmniencounter")
public class BahmniEncounterController extends BaseRestController {
    @Autowired
    private VisitService visitService;
    @Autowired
    private ConceptService conceptService;
    @Autowired
    private EncounterService encounterService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private EmrEncounterService emrEncounterService;

    public BahmniEncounterController(VisitService visitService, ConceptService conceptService, EncounterService encounterService) {
        this.visitService = visitService;
        this.conceptService = conceptService;
        this.encounterService = encounterService;
    }

    public BahmniEncounterController() {
    }

    @RequestMapping(method = RequestMethod.GET, value = "config")
    @ResponseBody
    public EncounterConfigResponse getConfig(String callerContext) {
        EncounterConfigResponse encounterConfigResponse = new EncounterConfigResponse();
        List<VisitType> visitTypes = visitService.getAllVisitTypes();
        for (VisitType visitType : visitTypes) {
            encounterConfigResponse.addVisitType(visitType.getName(), visitType.getUuid());
        }
        List<EncounterType> allEncounterTypes = encounterService.getAllEncounterTypes(false);
        for (EncounterType encounterType : allEncounterTypes) {
            encounterConfigResponse.addEncounterType(encounterType.getName(), encounterType.getUuid());
        }
        Concept conceptSetConcept = conceptService.getConcept(callerContext);
        if (conceptSetConcept != null) {
            List<Concept> conceptsByConceptSet = conceptService.getConceptsByConceptSet(conceptSetConcept);
            for (Concept concept : conceptsByConceptSet) {
                ConceptData conceptData = new ConceptData(concept.getUuid(), concept.getName().getName());
                encounterConfigResponse.addConcept(concept.getName().getName(), conceptData);
            }
        }
        List<OrderType> orderTypes = orderService.getAllOrderTypes();
        for (OrderType orderType: orderTypes) {
            encounterConfigResponse.addOrderType(orderType.getName(), orderType.getUuid());
        }
        return encounterConfigResponse;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public EncounterTransaction update(@RequestBody EncounterTransaction encounterTransaction) {
        return emrEncounterService.save(encounterTransaction);
    }


}
