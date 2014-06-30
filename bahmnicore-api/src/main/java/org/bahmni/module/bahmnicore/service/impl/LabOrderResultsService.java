package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.mapper.builder.EncounterTransactionMapperBuilder;
import org.bahmni.module.bahmnicore.model.BahmniVisit.LabOrderResult;
import org.bahmni.module.bahmnicore.model.BahmniVisit.LabOrderResults;
import org.openmrs.Encounter;
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

    @Autowired
    private EncounterTransactionMapperBuilder encounterTransactionMapperBuilder;

    @Autowired
    private EncounterService encounterService;

    public LabOrderResults getAll(Patient patient) {
        List<EncounterTransaction.TestOrder> testOrders = new ArrayList<>();
        List<EncounterTransaction.Observation> observations = new ArrayList<>();
        Map<String, Encounter> encounterTestOrderUuidMap = new HashMap<>();

        List<Encounter> encounters = encounterService.getEncounters(patient, null, null, null, null, null, null, null, null, false);
        EncounterTransactionMapper encounterTransactionMapper = encounterTransactionMapperBuilder.withOrderMapper().build();
        for (Encounter encounter : encounters) {
            EncounterTransaction encounterTransaction = encounterTransactionMapper.map(encounter, false);
            testOrders.addAll(getTestOrders(encounterTransaction, encounter, encounterTestOrderUuidMap));
            observations.addAll(encounterTransaction.getObservations());
        }

        return mapOrdersWithObs(testOrders, observations, encounterTestOrderUuidMap);
    }

    private List<EncounterTransaction.TestOrder> getTestOrders(EncounterTransaction encounterTransaction, Encounter encounter, Map<String, Encounter> encounterTestOrderUuidMap) {
        List<EncounterTransaction.TestOrder> orders = encounterTransaction.getTestOrders();
        for (EncounterTransaction.TestOrder order : orders) {
            encounterTestOrderUuidMap.put(order.getUuid(), encounter);
        }
        return orders;
    }

    private LabOrderResults mapOrdersWithObs(List<EncounterTransaction.TestOrder> testOrders, List<EncounterTransaction.Observation> observations, Map<String, Encounter> encounterTestOrderMap) {
        List<LabOrderResult> labOrderResults = new ArrayList<>();
        for (EncounterTransaction.TestOrder testOrder : testOrders) {
            EncounterTransaction.Observation obsGroup = findObsGroup(observations, testOrder);
            if(obsGroup != null) {
                labOrderResults.addAll(mapObs(obsGroup, encounterTestOrderMap));
            } else {
                labOrderResults.add(new LabOrderResult());
            }
        }
        return new LabOrderResults(labOrderResults);
    }

    private List<LabOrderResult> mapObs(EncounterTransaction.Observation obsGroup, Map<String, Encounter> encounterTestOrderMap) {
        List<LabOrderResult> labOrderResults = new ArrayList<>();
        if(isPanel(obsGroup)) {
            for (EncounterTransaction.Observation observation : obsGroup.getGroupMembers()) {
                LabOrderResult order = createLabOrderResult(observation, encounterTestOrderMap);
                order.setPanelUuid(obsGroup.getConceptUuid());
                order.setPanelName(obsGroup.getConcept().getName());
                labOrderResults.add(order);
            }
        } else {
            labOrderResults.add(createLabOrderResult(obsGroup, encounterTestOrderMap));
        }
        return labOrderResults;
    }

    private boolean isPanel(EncounterTransaction.Observation obsGroup) {
        return obsGroup.getConcept().isSet();
    }

    private LabOrderResult createLabOrderResult(EncounterTransaction.Observation observation, Map<String, Encounter> encounterTestOrderMap) {
        LabOrderResult labOrderResult = new LabOrderResult();
        Encounter encounter = encounterTestOrderMap.get(observation.getOrderUuid());
        Object resultValue = getValue(observation, observation.getConcept().getName());
        labOrderResult.setAccessionUuid(encounter.getUuid());
        labOrderResult.setAccessionDateTime(encounter.getEncounterDatetime());
        labOrderResult.setTestUuid(observation.getConceptUuid());
        labOrderResult.setTestName(observation.getConcept().getName());
        labOrderResult.setResult(resultValue != null ? resultValue.toString() : null);
        labOrderResult.setAbnormal((Boolean) getValue(observation, LAB_ABNORMAL));
        labOrderResult.setMinNormal((Double) getValue(observation, LAB_MINNORMAL));
        labOrderResult.setMaxNormal((Double) getValue(observation, LAB_MAXNORMAL));
        labOrderResult.setNotes((String) getValue(observation, LAB_NOTES));
        labOrderResult.setTestUnitOfMeasurement(observation.getConcept().getUnits());
        return labOrderResult;
    }

    private Object getValue(EncounterTransaction.Observation observation, String conceptName) {
        for (EncounterTransaction.Observation childObs : observation.getGroupMembers()) {
            if(!childObs.getGroupMembers().isEmpty()) {
                return getValue(childObs, conceptName);
            }
            if(childObs.getConcept().getName().equals(conceptName)) {
                return childObs.getValue();
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
