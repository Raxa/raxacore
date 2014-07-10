package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.mapper.builder.EncounterTransactionMapperBuilder;
import org.bahmni.module.bahmnicore.model.BahmniVisit.LabOrderResult;
import org.bahmni.module.bahmnicore.model.BahmniVisit.LabOrderResults;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.module.emrapi.encounter.EncounterTransactionMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class LabOrderResultsService {
    public static final String LAB_RESULT = "LAB_RESULT";
    public static final String LAB_ABNORMAL = "LAB_ABNORMAL";
    public static final String LAB_MINNORMAL = "LAB_MINNORMAL";
    public static final String LAB_MAXNORMAL = "LAB_MAXNORMAL";
    public static final String LAB_NOTES = "LAB_NOTES";
    private static final String REFERRED_OUT = "REFERRED_OUT";

    @Autowired
    private EncounterTransactionMapperBuilder encounterTransactionMapperBuilder;

    @Autowired
    private EncounterService encounterService;

    public LabOrderResults getAll(Patient patient) {
        List<EncounterTransaction.TestOrder> testOrders = new ArrayList<>();
        List<EncounterTransaction.Observation> observations = new ArrayList<>();
        Map<String, Encounter> encounterTestOrderUuidMap = new HashMap<>();
        Map<String, Encounter> encounterObservationMap = new HashMap<>();

        List<Encounter> encounters = encounterService.getEncounters(patient, null, null, null, null, null, null, null, null, false);
        EncounterTransactionMapper encounterTransactionMapper = encounterTransactionMapperBuilder.withOrderMapper().build();
        for (Encounter encounter : encounters) {
            EncounterTransaction encounterTransaction = encounterTransactionMapper.map(encounter, false);
            testOrders.addAll(getTestOrders(encounterTransaction, encounter, encounterTestOrderUuidMap));
            observations.addAll(encounterTransaction.getObservations());
            mapObservationsWithEncounter(encounterTransaction.getObservations(), encounter, encounterObservationMap);
        }

        return mapOrdersWithObs(testOrders, observations, encounterTestOrderUuidMap, encounterObservationMap);
    }

    private List<EncounterTransaction.TestOrder> getTestOrders(EncounterTransaction encounterTransaction, Encounter encounter, Map<String, Encounter> encounterTestOrderUuidMap) {
        List<EncounterTransaction.TestOrder> orders = encounterTransaction.getTestOrders();
        for (EncounterTransaction.TestOrder order : orders) {
            encounterTestOrderUuidMap.put(order.getUuid(), encounter);
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

    private LabOrderResults mapOrdersWithObs(List<EncounterTransaction.TestOrder> testOrders, List<EncounterTransaction.Observation> observations, Map<String, Encounter> encounterTestOrderMap, Map<String, Encounter> encounterObservationMap) {
        List<LabOrderResult> labOrderResults = new ArrayList<>();
        for (EncounterTransaction.TestOrder testOrder : testOrders) {
            EncounterTransaction.Observation obsGroup = findObsGroup(observations, testOrder);
            if(obsGroup != null) {
                labOrderResults.addAll(mapObs(obsGroup, encounterTestOrderMap, encounterObservationMap));
            } else {
                EncounterTransaction.Concept orderConcept = testOrder.getConcept();
                Encounter orderEncounter = encounterTestOrderMap.get(testOrder.getUuid());
                labOrderResults.add(new LabOrderResult(orderEncounter.getUuid(), orderEncounter.getEncounterDatetime(), orderConcept.getName(), orderConcept.getUnits(), null, null, null, null, false));
            }
        }
        return new LabOrderResults(labOrderResults);
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
        labOrderResult.setNotes((String) getValue(observation, LAB_NOTES));
        labOrderResult.setReferredOut(getLeafObservation(observation, REFERRED_OUT) != null);
        labOrderResult.setTestUnitOfMeasurement(observation.getConcept().getUnits());
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
            if(childObs.getConcept().getName().equals(conceptName)) {
                return childObs;
            }
        }
        return null;
    }

    private EncounterTransaction.Observation findObsGroup(List<EncounterTransaction.Observation> observations, EncounterTransaction.TestOrder testOrder) {
        for (EncounterTransaction.Observation observation : observations) {
            if(observation.getOrderUuid() != null && observation.getOrderUuid().equals(testOrder.getUuid())) {
                return observation;
            }
        }
        return null;
    }
}
