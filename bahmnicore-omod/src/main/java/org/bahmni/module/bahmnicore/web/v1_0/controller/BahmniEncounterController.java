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
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    private PatientService patientService;
    @Autowired
    private ConceptService conceptService;
    @Autowired
    private EncounterService encounterService;
    @Autowired
    private ObsService obsService;
    @Autowired
    private OrderService orderService;

    public BahmniEncounterController(VisitService visitService, PatientService patientService, ConceptService conceptService, EncounterService encounterService,
                                     ObsService obsService) {
        this.visitService = visitService;
        this.patientService = patientService;
        this.conceptService = conceptService;
        this.encounterService = encounterService;
        this.obsService = obsService;
    }

    public BahmniEncounterController() {
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public EncounterObservationResponse get(GetObservationsRequest getObservationsRequest) {
        Visit visit = getActiveVisit(getObservationsRequest.getPatientUUID());
        ArrayList<ObservationData> observations = new ArrayList<ObservationData>();
        if (visit == null) return new EncounterObservationResponse(observations);

        Encounter encounter = getMatchingEncounter(visit, getObservationsRequest.getEncounterTypeUUID());
        if (encounter == null) return new EncounterObservationResponse(observations);

        Set<Obs> allObs = encounter.getAllObs();
        for (Obs obs : allObs) {
            Concept concept = obs.getConcept();
            ConceptDatatype datatype = concept.getDatatype();
            Object value = datatype.isNumeric() ? obs.getValueNumeric() : obs.getValueAsString(Locale.getDefault());
            observations.add(new ObservationData(concept.getUuid(), concept.getName().getName(), value));
        }
        return new EncounterObservationResponse(observations);
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
                ConceptData conceptData = new ConceptData(concept.getUuid());
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
    public EncounterDataResponse create(@RequestBody CreateEncounterRequest createEncounterRequest)
            throws Exception {
        Patient patient = patientService.getPatientByUuid(createEncounterRequest.getPatientUUID());
        Visit visit = getActiveVisit(createEncounterRequest.getPatientUUID());
        Date encounterDatetime = new Date();
        if (visit == null) {
            visit = new Visit();
            visit.setPatient(patient);
            visit.setVisitType(visitService.getVisitTypeByUuid(createEncounterRequest.getVisitTypeUUID()));
            visit.setStartDatetime(encounterDatetime);
            visit.setEncounters(new HashSet<Encounter>());
            visit.setUuid(UUID.randomUUID().toString());
        }
        Encounter encounter = getMatchingEncounter(visit, createEncounterRequest.getEncounterTypeUUID());
        if (encounter == null) {
            encounter = new Encounter();
            encounter.setPatient(patient);
            encounter.setEncounterType(encounterService.getEncounterTypeByUuid(createEncounterRequest.getEncounterTypeUUID()));
            encounter.setEncounterDatetime(encounterDatetime);
            encounter.setUuid(UUID.randomUUID().toString());
            encounter.setObs(new HashSet<Obs>());
            //should use addEncounter method here, which seems to be have been added later
            visit.getEncounters().add(encounter);
        }
        addOrOverwriteObservations(createEncounterRequest, encounter, patient, encounterDatetime);
        addOrOverwriteOrders(createEncounterRequest, encounter);
        visitService.saveVisit(visit);
        return new EncounterDataResponse(visit.getUuid(), encounter.getUuid(), "");
    }

    private void addOrOverwriteOrders(CreateEncounterRequest createEncounterRequest, Encounter encounter) {
        for (TestOrderData testOrderData : createEncounterRequest.getTestOrders()) {
            if(hasOrderByConceptUuid(encounter, testOrderData.getConceptUUID())) continue;
            Order order = new TestOrder();
            order.setConcept(conceptService.getConceptByUuid(testOrderData.getConceptUUID()));
            encounter.addOrder(order);
        }
    }

    private boolean hasOrderByConceptUuid(Encounter encounter, String conceptUuid) {
        for (Order order: encounter.getOrders()){
            if(order.getConcept().getUuid().equals(conceptUuid))
                return true;
        }
        return false;
    }

    private void addOrOverwriteObservations(CreateEncounterRequest createEncounterRequest, Encounter encounter, Patient patient, Date encounterDateTime) throws ParseException {
        Set<Obs> existingObservations = encounter.getAllObs();
        for (ObservationData observationData : createEncounterRequest.getObservations()) {
            Obs observation = getMatchingObservation(existingObservations, observationData.getConceptUUID());
            Object value = observationData.getValue();
            boolean observationValueSpecified = value != null && StringUtils.isNotEmpty(value.toString());
            if (observation == null && observationValueSpecified) {
                observation = new Obs();
                Concept concept = conceptService.getConceptByUuid(observationData.getConceptUUID());
                observation.setConcept(concept);
                observation.setUuid(UUID.randomUUID().toString());
                observation.setPerson(patient);
                observation.setEncounter(encounter);
                encounter.addObs(observation);
            }
            if (observation != null && observationValueSpecified) {
                setObservationValue(observationData, observation);
                observation.setObsDatetime(encounterDateTime);
            }
            if (observation != null && !observationValueSpecified) {
                encounter.removeObs(observation);
                obsService.purgeObs(observation);
            }
        }
    }

    private void setObservationValue(ObservationData observationData, Obs observation) throws ParseException {
        if (observation.getConcept().getDatatype().isNumeric()) {
            observation.setValueNumeric(Double.parseDouble(observationData.getValue().toString()));
        } else {
            observation.setValueAsString((String) observationData.getValue());
        }
    }

    private Obs getMatchingObservation(Set<Obs> existingObservations, String conceptUUID) {
        for (Obs obs : existingObservations) {
            if (StringUtils.equals(obs.getConcept().getUuid(), conceptUUID)) return obs;
        }
        return null;
    }

    private Encounter getMatchingEncounter(Visit visit, String encounterTypeUUID) {
        Set<Encounter> encounters = visit.getEncounters();
        for (Encounter encounter : encounters) {
            if (StringUtils.equals(encounter.getEncounterType().getUuid(), encounterTypeUUID)) {
                return encounter;
            }
        }
        return null;
    }

    private Visit getActiveVisit(String patientUuid) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        List<Visit> activeVisitsByPatient = visitService.getActiveVisitsByPatient(patient);
        return activeVisitsByPatient.isEmpty()? null : activeVisitsByPatient.get(0);
    }
}
