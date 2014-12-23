package org.openmrs.module.bahmniemrapi.laborder.service;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResults;
import org.openmrs.module.bahmniemrapi.laborder.mapper.LabOrderResultMapper;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class LabOrderResultsServiceImpl implements LabOrderResultsService {
    public static final String LAB_ABNORMAL = "LAB_ABNORMAL";
    public static final String LAB_MINNORMAL = "LAB_MINNORMAL";
    public static final String LAB_MAXNORMAL = "LAB_MAXNORMAL";
    public static final String LAB_NOTES = "LAB_NOTES";
    private static final String REFERRED_OUT = "REFERRED_OUT";
    public static final String LAB_REPORT = "LAB_REPORT";

    @Autowired
    private EncounterTransactionMapper encounterTransactionMapper;

    @Autowired
    private EncounterService encounterService;

    @Override
    public LabOrderResults getAll(Patient patient, List<Visit> visits) {
        List<EncounterTransaction.TestOrder> testOrders = new ArrayList<>();
        List<EncounterTransaction.Observation> observations = new ArrayList<>();
        Map<String, Encounter> encounterTestOrderUuidMap = new HashMap<>();
        Map<String, Encounter> encounterObservationMap = new HashMap<>();

        List<Encounter> encounters = encounterService.getEncounters(patient, null, null, null, null, null, null, null, visits, false);
        for (Encounter encounter : encounters) {
            EncounterTransaction encounterTransaction = encounterTransactionMapper.map(encounter, false);
            testOrders.addAll(getTestOrders(encounterTransaction, encounter, encounterTestOrderUuidMap));
            List<EncounterTransaction.Observation> nonVoidedObservations = filterVoided(encounterTransaction.getObservations());
            observations.addAll(nonVoidedObservations);
            mapObservationsWithEncounter(nonVoidedObservations, encounter, encounterObservationMap);
        }

        return new LabOrderResults(mapOrdersWithObs(testOrders, observations, encounterTestOrderUuidMap, encounterObservationMap));
    }

    @Override
    public List<LabOrderResult> getAllForConcepts(Patient patient, Collection<String> concepts, List<Visit> visits){
        if (concepts != null && !concepts.isEmpty()) {

            List<EncounterTransaction.TestOrder> testOrders = new ArrayList<>();
            List<EncounterTransaction.Observation> observations = new ArrayList<>();
            Map<String, Encounter> encounterTestOrderUuidMap = new HashMap<>();
            Map<String, Encounter> encounterObservationMap = new HashMap<>();

            List<Encounter> encounters = encounterService.getEncounters(patient, null, null, null, null, null, null, null, visits, false);
            for (Encounter encounter : encounters) {
                EncounterTransaction encounterTransaction = encounterTransactionMapper.map(encounter, false);
                testOrders.addAll(getTestOrdersForConcepts(encounterTransaction, encounter, encounterTestOrderUuidMap, concepts));
                List<EncounterTransaction.Observation> nonVoidedObservations = filterVoided(encounterTransaction.getObservations());
                observations.addAll(nonVoidedObservations);
                mapObservationsWithEncounter(nonVoidedObservations, encounter, encounterObservationMap);
            }
            return mapOrdersWithObs(testOrders, observations, encounterTestOrderUuidMap, encounterObservationMap);
        }
        return Collections.EMPTY_LIST;
    }

    private List<EncounterTransaction.TestOrder> getTestOrdersForConcepts(EncounterTransaction encounterTransaction, Encounter encounter, Map<String, Encounter> encounterTestOrderUuidMap, Collection<String> concepts) {
        List<EncounterTransaction.TestOrder> orders = new ArrayList<>();
        for (EncounterTransaction.TestOrder order : encounterTransaction.getTestOrders()) {
            if(!order.isVoided() && concepts.contains(order.getConcept().getName())){
                encounterTestOrderUuidMap.put(order.getUuid(), encounter);
                orders.add(order);
            }
        }
        return orders;
    }


    private List<EncounterTransaction.Observation> filterVoided(List<EncounterTransaction.Observation> observations) {
        List<EncounterTransaction.Observation> nonVoidedObservations = new ArrayList<>();
        for (EncounterTransaction.Observation observation : observations) {
            if(!observation.getVoided()){
                nonVoidedObservations.add(observation);
            }
        }
        return nonVoidedObservations;
    }

    private List<EncounterTransaction.TestOrder> getTestOrders(EncounterTransaction encounterTransaction, Encounter encounter, Map<String, Encounter> encounterTestOrderUuidMap) {
        List<EncounterTransaction.TestOrder> orders = new ArrayList<>();
        for (EncounterTransaction.TestOrder order : encounterTransaction.getTestOrders()) {
            if(!order.isVoided()){
                encounterTestOrderUuidMap.put(order.getUuid(), encounter);
                orders.add(order);
            }
        }
        return orders;
    }

    private void mapObservationsWithEncounter(List<EncounterTransaction.Observation> observations, Encounter encounter, Map<String, Encounter> encounterObservationMap) {
        for (EncounterTransaction.Observation observation : observations) {
            encounterObservationMap.put(observation.getUuid(), encounter);
            if(observation.getGroupMembers().size() > 0) {
                mapObservationsWithEncounter(observation.getGroupMembers(), encounter, encounterObservationMap);
            }
        }
    }

    private List<LabOrderResult> mapOrdersWithObs(List<EncounterTransaction.TestOrder> testOrders, List<EncounterTransaction.Observation> observations, Map<String, Encounter> encounterTestOrderMap, Map<String, Encounter> encounterObservationMap) {
        List<LabOrderResult> labOrderResults = new ArrayList<>();
        for (EncounterTransaction.TestOrder testOrder : testOrders) {
            List<EncounterTransaction.Observation> obsGroups = findObsGroup(observations, testOrder);
            if(!obsGroups.isEmpty()) {
                for (EncounterTransaction.Observation obsGroup : obsGroups) {
                    labOrderResults.addAll(mapObs(obsGroup, encounterTestOrderMap, encounterObservationMap));
                }
            } else {
                EncounterTransaction.Concept orderConcept = testOrder.getConcept();
                Encounter orderEncounter = encounterTestOrderMap.get(testOrder.getUuid());
                LabOrderResult labOrderResult = new LabOrderResult(orderEncounter.getUuid(), orderEncounter.getEncounterDatetime(), orderConcept.getName(), orderConcept.getUnits(), null, null, null, null, false, null);
                labOrderResult.setVisitStartTime(orderEncounter.getVisit().getStartDatetime());
                labOrderResults.add(labOrderResult);
            }
        }
        return labOrderResults;
    }

    private List<LabOrderResult> mapObs(EncounterTransaction.Observation obsGroup, Map<String, Encounter> encounterTestOrderMap, Map<String, Encounter> encounterObservationMap) {
        List<LabOrderResult> labOrderResults = new ArrayList<>();
        if(isPanel(obsGroup)) {
            for (EncounterTransaction.Observation observation : obsGroup.getGroupMembers()) {
                LabOrderResult order = createLabOrderResult(observation, encounterTestOrderMap, encounterObservationMap);
                order.setPanelUuid(obsGroup.getConceptUuid());
                order.setPanelName(obsGroup.getConcept().getName());
                labOrderResults.add(order);
            }
        } else {
            labOrderResults.add(createLabOrderResult(obsGroup, encounterTestOrderMap, encounterObservationMap));
        }
        return labOrderResults;
    }

    private boolean isPanel(EncounterTransaction.Observation obsGroup) {
        return obsGroup.getConcept().isSet();
    }

    private LabOrderResult createLabOrderResult(EncounterTransaction.Observation observation, Map<String, Encounter> encounterTestOrderMap, Map<String, Encounter> encounterObservationMap) {
        LabOrderResult labOrderResult = new LabOrderResult();
        Encounter orderEncounter = encounterTestOrderMap.get(observation.getOrderUuid());
        Object resultValue = getValue(observation, observation.getConcept().getName());
        String notes = (String) getValue(observation, LAB_NOTES);
        String uploadedFileName = (String) getValue(observation, LAB_REPORT);
        labOrderResult.setAccessionUuid(orderEncounter.getUuid());
        labOrderResult.setAccessionDateTime(orderEncounter.getEncounterDatetime());
        labOrderResult.setProvider(getProviderName(observation, encounterObservationMap));
        labOrderResult.setResultDateTime(observation.getObservationDateTime());
        labOrderResult.setTestUuid(observation.getConceptUuid());
        labOrderResult.setTestName(observation.getConcept().getName());
        labOrderResult.setResult(resultValue != null ? resultValue.toString() : null);
        labOrderResult.setAbnormal((Boolean) getValue(observation, LAB_ABNORMAL));
        labOrderResult.setMinNormal((Double) getValue(observation, LAB_MINNORMAL));
        labOrderResult.setMaxNormal((Double) getValue(observation, LAB_MAXNORMAL));
        labOrderResult.setNotes(notes != null && notes.trim().length() > 1 ? notes.trim() : null);
        labOrderResult.setReferredOut(getLeafObservation(observation, REFERRED_OUT) != null);
        labOrderResult.setTestUnitOfMeasurement(observation.getConcept().getUnits());
        labOrderResult.setUploadedFileName(uploadedFileName != null && uploadedFileName.trim().length() > 0 ? uploadedFileName.trim() : null);
        labOrderResult.setVisitStartTime(orderEncounter.getVisit().getStartDatetime());
        return labOrderResult;
    }

    private String getProviderName(EncounterTransaction.Observation observation, Map<String, Encounter> encounterObservationMap) {
        Encounter obsEncounter = encounterObservationMap.get(observation.getUuid());
        ArrayList<EncounterProvider> encounterProviders = new ArrayList<>(obsEncounter.getEncounterProviders());
        return encounterProviders.size() > 0 ? encounterProviders.get(0).getProvider().getName() : null;
    }

    private Object getValue(EncounterTransaction.Observation observation, String conceptName) {
        EncounterTransaction.Observation leafObservation = getLeafObservation(observation, conceptName);
        return leafObservation != null ? leafObservation.getValue() : null;
    }

    private EncounterTransaction.Observation getLeafObservation(EncounterTransaction.Observation observation, String conceptName) {
        for (EncounterTransaction.Observation childObs : observation.getGroupMembers()) {
            if(!childObs.getGroupMembers().isEmpty()) {
                return getLeafObservation(childObs, conceptName);
            }
            if(childObs.getConcept().getName().equalsIgnoreCase(conceptName)) {
                return childObs;
            }
        }
        return null;
    }

    private List<EncounterTransaction.Observation> findObsGroup(List<EncounterTransaction.Observation> observations, EncounterTransaction.TestOrder testOrder) {
        List<EncounterTransaction.Observation> obsGroups = new ArrayList<>();
        for (EncounterTransaction.Observation observation : observations) {
            if(observation.getOrderUuid() != null && observation.getOrderUuid().equals(testOrder.getUuid())) {
                obsGroups.add(observation);
            }
        }
        return obsGroups;
    }
}
