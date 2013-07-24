package org.bahmni.module.bahmnicore.web.v1_0.controller;

import junit.framework.Assert;
import org.bahmni.module.bahmnicore.contract.encounter.data.TestOrderData;
import org.bahmni.module.bahmnicore.contract.encounter.request.CreateEncounterRequest;
import org.bahmni.module.bahmnicore.contract.encounter.data.ObservationData;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BahmniEncounterControllerTest {
    @Mock
    private VisitService visitService;
    @Mock
    private PatientService patientService;
    @Mock
    private ConceptService conceptService;
    @Mock
    private EncounterService encounterService;
    @Mock
    private ObsService obsService;

    @Test
    public void shouldCreateNewEncounter() throws Exception {
        String patientUUID = "1";
        String encounterTypeUUID = "3";
        String orderTypeUUID = "3";
        String conceptHeightUUID = "conceptHeightUUID";
        String conceptRegFeeUUID = "conceptRegFeeUUID";
        String concepWeightUUID = "concepWeightUUID";
        String conceptSerumTestUUID = "conceptSerumTestUUID";
        String conceptHaemoglobinPanelUUID = "conceptHaemoglobinPanelUUID";

        //Existing data
        Patient patient = new Patient();
        Visit visit = new Visit();
        visit.setStartDatetime(new LocalDateTime(DateMidnight.now()).plusSeconds(10).toDate());

        HashSet<Obs> obses = new HashSet<Obs>();
        addObservation(obses, conceptHeightUUID, 120);
        Obs regFeeObs = addObservation(obses, conceptRegFeeUUID, 10);
        Concept weightConcept = createConcept(concepWeightUUID);
        Concept serumTestConcept = createConcept(conceptSerumTestUUID);
        Concept haemoglobinPanelConcept = createConcept(conceptHaemoglobinPanelUUID);

        HashSet<Encounter> encounters = new HashSet<Encounter>();
        Encounter encounter = createEncounter(encounterTypeUUID, obses);
        encounters.add(encounter);
        visit.setEncounters(encounters);
        //end

        initMocks(this);
        when(patientService.getPatientByUuid(patientUUID)).thenReturn(patient);
        when(visitService.getActiveVisitsByPatient(patient)).thenReturn(Arrays.asList(visit));
        when(conceptService.getConceptByUuid(concepWeightUUID)).thenReturn(weightConcept);
        when(conceptService.getConceptByUuid(conceptSerumTestUUID)).thenReturn(serumTestConcept);
        when(conceptService.getConceptByUuid(conceptHaemoglobinPanelUUID)).thenReturn(haemoglobinPanelConcept);

        BahmniEncounterController bahmniEncounterController = new BahmniEncounterController(visitService, patientService, conceptService, encounterService, obsService);
        ObservationData heightObservationData = new ObservationData(conceptHeightUUID, "HEIGHT", null);
        ObservationData weightObservationData = new ObservationData(concepWeightUUID, "WEIGHT", 50);
        ObservationData regFeeObservationData = new ObservationData(conceptRegFeeUUID, "REG FEE", 5);
        TestOrderData serumTestOrderData = new TestOrderData(conceptSerumTestUUID);
        TestOrderData haemoglobinPanelOrderData = new TestOrderData(conceptHaemoglobinPanelUUID);
        List<ObservationData> observations = Arrays.asList(heightObservationData, weightObservationData, regFeeObservationData);
        List<TestOrderData> orders = Arrays.asList(serumTestOrderData, haemoglobinPanelOrderData);
        CreateEncounterRequest createEncounterRequest = new CreateEncounterRequest(patientUUID, "2", encounterTypeUUID, observations, orders);
        bahmniEncounterController.create(createEncounterRequest);

        Assert.assertEquals(5.0, regFeeObs.getValueNumeric());
        Assert.assertEquals(2, encounter.getAllObs().size());
        Assert.assertEquals(2, encounter.getOrders().size());
        verify(obsService).purgeObs(any(Obs.class));
        verify(conceptService).getConceptByUuid(conceptSerumTestUUID);
        verify(conceptService).getConceptByUuid(conceptHaemoglobinPanelUUID);
    }

    private Encounter createEncounter(String encounterTypeUUID, HashSet<Obs> obses) {
        Encounter encounter = new Encounter();
        encounter.setUuid("encounterUUID");
        encounter.setObs(obses);
        EncounterType encounterType = new EncounterType();
        encounterType.setUuid(encounterTypeUUID);
        encounter.setEncounterType(encounterType);
        return encounter;
    }

    private Obs addObservation(HashSet<Obs> obses, String conceptUUID, double value) {
        Obs obs = new Obs();
        Concept concept = createConcept(conceptUUID);
        obs.setConcept(concept);
        obs.setValueNumeric(value);
        obses.add(obs);
        return obs;
    }

    private Concept createConcept(String conceptUUID) {
        Concept concept = new Concept();
        ConceptDatatype conceptDatatype = new ConceptDatatype();
        conceptDatatype.setUuid(ConceptDatatype.NUMERIC_UUID);
        concept.setDatatype(conceptDatatype);
        concept.setUuid(conceptUUID);
        return concept;
    }
}