package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.encounter.data.ConceptData;
import org.bahmni.module.bahmnicore.contract.encounter.response.EncounterConfigResponse;
import org.joda.time.DateTime;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniEncounterTransactionMapper;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.module.bahmniemrapi.encountertransaction.utils.DateUtil;
import org.openmrs.module.emrapi.encounter.ActiveEncounterParameters;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterSearchParameters;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/bahmniencounter")
public class BahmniEncounterController extends BaseRestController {
    private AdministrationService adminService;
    private VisitService visitService;
    private ConceptService conceptService;
    private EncounterService encounterService;
    private OrderService orderService;
    private EmrEncounterService emrEncounterService;
    private EncounterTransactionMapper encounterTransactionMapper;
    private BahmniEncounterTransactionService bahmniEncounterTransactionService;
    private BahmniEncounterTransactionMapper bahmniEncounterTransactionMapper;

    public BahmniEncounterController() {
    }

    @Autowired
    public BahmniEncounterController(VisitService visitService, ConceptService conceptService,
                                     EncounterService encounterService, OrderService orderService,
                                     EmrEncounterService emrEncounterService, EncounterTransactionMapper encounterTransactionMapper,
                                     BahmniEncounterTransactionService bahmniEncounterTransactionService,
                                     BahmniEncounterTransactionMapper bahmniEncounterTransactionMapper, @Qualifier("adminService") AdministrationService administrationService) {
        this.visitService = visitService;
        this.conceptService = conceptService;
        this.encounterService = encounterService;
        this.orderService = orderService;
        this.emrEncounterService = emrEncounterService;
        this.encounterTransactionMapper = encounterTransactionMapper;
        this.bahmniEncounterTransactionService = bahmniEncounterTransactionService;
        this.bahmniEncounterTransactionMapper = bahmniEncounterTransactionMapper;
        this.adminService = administrationService;

    }

    @RequestMapping(method = RequestMethod.GET, value = "config")
    @ResponseBody
    public EncounterConfigResponse getConfig(String callerContext) {
        EncounterConfigResponse encounterConfigResponse = new EncounterConfigResponse();
        List<VisitType> visitTypes = visitService.getAllVisitTypes();
        for (VisitType visitType : visitTypes) {
            if (!visitType.isRetired()) {
                encounterConfigResponse.addVisitType(visitType.getName(), visitType.getUuid());
            }
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
        List<OrderType> orderTypes = orderService.getOrderTypes(true);
        for (OrderType orderType : orderTypes) {
            encounterConfigResponse.addOrderType(orderType.getName(), orderType.getUuid());
        }
        return encounterConfigResponse;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{uuid}")
    @ResponseBody
    public BahmniEncounterTransaction get(@PathVariable("uuid") String uuid, Boolean includeAll) {
        EncounterTransaction encounterTransaction = emrEncounterService.getEncounterTransaction(uuid, includeAll);
        return bahmniEncounterTransactionMapper.map(encounterTransaction, includeAll);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/active")
    @ResponseBody
    public BahmniEncounterTransaction getActive(ActiveEncounterParameters activeEncounterParameters) {
        EncounterTransaction activeEncounter = emrEncounterService.getActiveEncounter(activeEncounterParameters);
        return bahmniEncounterTransactionMapper.map(activeEncounter, activeEncounterParameters.getIncludeAll());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/find")
    @ResponseBody
    public List<BahmniEncounterTransaction> find(@RequestBody EncounterSearchParameters encounterSearchParameters) {
        List<BahmniEncounterTransaction> bahmniEncounterTransactions = new ArrayList<>();

        List<EncounterTransaction> encounterTransactions = null;
        try {
            encounterTransactions = bahmniEncounterTransactionService.find(encounterSearchParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (encounterTransactions != null && encounterTransactions.size() > 0) {
            for (EncounterTransaction encounterTransaction : encounterTransactions) {
                bahmniEncounterTransactions.add(bahmniEncounterTransactionMapper.map(encounterTransaction, encounterSearchParameters.getIncludeAll()));
            }
        } else {
            bahmniEncounterTransactions.add(bahmniEncounterTransactionMapper.map(new EncounterTransaction(), false));
        }

        return bahmniEncounterTransactions;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public BahmniEncounterTransaction update(@RequestBody BahmniEncounterTransaction bahmniEncounterTransaction) {
        setUuidsForObservations(bahmniEncounterTransaction.getObservations());
        setAutoExpireDateForTestOrders(bahmniEncounterTransaction.getTestOrders());
        return bahmniEncounterTransactionService.save(bahmniEncounterTransaction);
    }

    private void setAutoExpireDateForTestOrders(List<EncounterTransaction.TestOrder> testOrders) {
        String configuredSessionDuration = adminService.getGlobalProperty("bahmni.encountersession.duration");
        int encounterSessionDuration = configuredSessionDuration != null ? Integer.parseInt(configuredSessionDuration) : 60;

        for (EncounterTransaction.TestOrder testOrder : testOrders) {
            testOrder.setAutoExpireDate(DateTime.now().plusMinutes(encounterSessionDuration).toDate());
        }
    }

    public BahmniEncounterTransaction get(String encounterUuid) {
        Encounter encounter = encounterService.getEncounterByUuid(encounterUuid);
        boolean includeAll = false;
        EncounterTransaction encounterTransaction = encounterTransactionMapper.map(encounter, includeAll);
        return bahmniEncounterTransactionMapper.map(encounterTransaction, includeAll);
    }

    private void setUuidsForObservations(Collection<BahmniObservation> bahmniObservations) {
        for (BahmniObservation bahmniObservation : bahmniObservations) {
            if (org.apache.commons.lang3.StringUtils.isBlank(bahmniObservation.getUuid())) {
                bahmniObservation.setUuid(UUID.randomUUID().toString());
            }
        }
    }
}
