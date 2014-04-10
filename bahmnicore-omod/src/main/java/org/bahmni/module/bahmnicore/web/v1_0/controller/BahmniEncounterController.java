package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.bahmnicore.BahmniCoreException;
import org.bahmni.module.bahmnicore.contract.encounter.data.ConceptData;
import org.bahmni.module.bahmnicore.contract.encounter.request.BahmniDiagnosis;
import org.bahmni.module.bahmnicore.contract.encounter.request.BahmniDiagnosisRequest;
import org.bahmni.module.bahmnicore.contract.encounter.request.BahmniEncounterTransaction;
import org.bahmni.module.bahmnicore.contract.encounter.response.EncounterConfigResponse;
import org.bahmni.module.bahmnicore.web.v1_0.InvalidInputException;
import org.bahmni.module.bahmnicore.web.v1_0.mapper.BahmniDiagnosisHelper;
import org.bahmni.module.bahmnicore.web.v1_0.mapper.BahmniEncounterTransactionMapper;
import org.bahmni.module.bahmnicore.web.v1_0.mapper.EncounterTransactionDiagnosisMapper;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.EncounterSearchParameters;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private ObsService obsService;
    @Autowired
    private EncounterTransactionMapper encounterTransactionMapper;

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
        for (OrderType orderType : orderTypes) {
            encounterConfigResponse.addOrderType(orderType.getName(), orderType.getUuid());
        }
        return encounterConfigResponse;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<BahmniEncounterTransaction> find(EncounterSearchParameters encounterSearchParameters) {
        checkForValidInput(encounterSearchParameters);
        List<EncounterTransaction> encounterTransactions = emrEncounterService.find(encounterSearchParameters);
        List<BahmniEncounterTransaction> bahmniEncounterTransactions = new ArrayList<>();
        for (EncounterTransaction encounterTransaction : encounterTransactions) {
            bahmniEncounterTransactions.add(new BahmniEncounterTransactionMapper(obsService, encounterTransactionMapper).map(encounterTransaction));
        }
        return bahmniEncounterTransactions;
    }

    private void checkForValidInput(EncounterSearchParameters encounterSearchParameters) {
        String visitUuid = encounterSearchParameters.getVisitUuid();
        if (StringUtils.isBlank(visitUuid))
            throw new InvalidInputException("Visit UUID cannot be empty.");

        String encounterDate = encounterSearchParameters.getEncounterDate();
        if (StringUtils.isNotBlank(encounterDate)){
            try {
                new SimpleDateFormat("yyyy-MM-dd").parse(encounterDate);
            } catch (ParseException e) {
                throw new InvalidInputException("Date format needs to be 'yyyy-MM-dd'. Incorrect Date:" + encounterDate + ".", e);
            }
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public BahmniEncounterTransaction update(@RequestBody BahmniEncounterTransaction bahmniEncounterTransaction) {
        //Reconstruct the encounter transaction as understood by emr-api and save
        new EncounterTransactionDiagnosisMapper().populateDiagnosis(bahmniEncounterTransaction);
        EncounterTransaction encounterTransaction = emrEncounterService.save(bahmniEncounterTransaction);

        //Get the saved encounter transaction from emr-api
        String encounterUuid = encounterTransaction.getEncounterUuid();
        Encounter currentEncounter = encounterService.getEncounterByUuid(encounterUuid);
        EncounterTransaction updatedEncounterTransaction = encounterTransactionMapper.map(currentEncounter, true);

        //Update the diagnosis information with Meta Data managed by Bahmni
        BahmniDiagnosisHelper bahmniDiagnosisHelper = new BahmniDiagnosisHelper(obsService, conceptService);
        for (BahmniDiagnosisRequest bahmniDiagnosis : bahmniEncounterTransaction.getBahmniDiagnoses()) {
            EncounterTransaction.Diagnosis diagnosis = getMatchingEncounterTransactionDiagnosis(bahmniDiagnosis, updatedEncounterTransaction.getDiagnoses());
            bahmniDiagnosisHelper.updateDiagnosisMetaData(bahmniDiagnosis, diagnosis, currentEncounter);
        }
        encounterService.saveEncounter(currentEncounter);

        // Void the previous diagnosis if required
        for (BahmniDiagnosisRequest bahmniDiagnosis : bahmniEncounterTransaction.getBahmniDiagnoses()) {
            String previousDiagnosisObs = bahmniDiagnosis.getPreviousObs();
            if (previousDiagnosisObs == null) continue;

            Obs diagnosisObs = obsService.getObsByUuid(previousDiagnosisObs);
            Encounter encounterForDiagnosis = encounterService.getEncounterByUuid(diagnosisObs.getEncounter().getUuid());
            if (!encounterForDiagnosis.equals(currentEncounter)) {
                bahmniDiagnosisHelper.markAsRevised(encounterForDiagnosis, diagnosisObs.getUuid());
                encounterService.saveEncounter(encounterForDiagnosis);
            }
        }
        return new BahmniEncounterTransactionMapper(obsService, encounterTransactionMapper).map(updatedEncounterTransaction);
    }

    private EncounterTransaction.Diagnosis getMatchingEncounterTransactionDiagnosis(BahmniDiagnosis bahmniDiagnosis, List<EncounterTransaction.Diagnosis> encounterTransactionDiagnoses) {
        for (EncounterTransaction.Diagnosis diagnosis : encounterTransactionDiagnoses) {
            if (bahmniDiagnosis.isSame(diagnosis)) {
                return diagnosis;
            }
        }
        throw new BahmniCoreException("Error fetching the saved diagnosis for  " + bahmniDiagnosis.getCodedAnswer().getName());
    }

    public BahmniEncounterTransaction get(String encounterUuid) {
        Encounter encounter = encounterService.getEncounterByUuid(encounterUuid);
        EncounterTransaction encounterTransaction = encounterTransactionMapper.map(encounter, true);
        return new BahmniEncounterTransactionMapper(obsService, encounterTransactionMapper).map(encounterTransaction);
    }
}
