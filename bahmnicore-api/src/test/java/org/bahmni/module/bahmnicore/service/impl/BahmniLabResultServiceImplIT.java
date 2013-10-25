package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.model.BahmniLabResult;
import org.bahmni.module.bahmnicore.service.BahmniLabResultService;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BahmniLabResultServiceImplIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private BahmniLabResultService bahmniLabResultService;

    @Test
    public void shouldCreateNewObservationForLabResult() throws Exception {
        executeDataSet("labOrderTestData.xml");

        Patient patient = Context.getPatientService().getPatient(1);
        Concept haemoglobin = Context.getConceptService().getConcept("Haemoglobin");
        Set<Order> orders = buildOrders(Arrays.asList(haemoglobin));
        Encounter encounter = encounterService.saveEncounter(buildEncounter(patient, orders));

        BahmniLabResult bahmniLabResult = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), haemoglobin.getUuid(), "15", "Some Alert", null);
        bahmniLabResultService.add(bahmniLabResult);

        Encounter encounterWithObs = encounterService.getEncounterByUuid(encounter.getUuid());
        ArrayList<Obs> obsList = new ArrayList<>(encounterWithObs.getObsAtTopLevel(false));
        Obs labObsGroup = obsList.get(0);
        assertEquals(labObsGroup.getConcept(), Context.getConceptService().getConcept("Laboratory"));
        assertEquals(1, labObsGroup.getGroupMembers().size());

        Obs obs = (Obs) labObsGroup.getGroupMembers().toArray()[0];
        assertEquals((Double) 15.0, obs.getValueNumeric());
        assertEquals("accessionNumber", obs.getAccessionNumber());
        assertEquals("Some Alert", obs.getComment());
        assertEquals(haemoglobin, obs.getConcept());
        assertEquals(orders.toArray()[0], obs.getOrder());
    }

    @Test
    public void shouldUpdateObservationIfObservationAlreadyExistInEncounter() throws Exception {
        executeDataSet("labOrderTestData.xml");

        Patient patient = Context.getPatientService().getPatient(1);
        Concept haemoglobin = Context.getConceptService().getConcept("Haemoglobin");
        Set<Order> orders = buildOrders(Arrays.asList(haemoglobin));
        Encounter encounter = encounterService.saveEncounter(buildEncounter(patient, orders));

        BahmniLabResult bahmniLabResult = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), haemoglobin.getUuid(), "15", "Some Alert", null);
        bahmniLabResultService.add(bahmniLabResult);

        BahmniLabResult bahmniLabResultUpdate = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), haemoglobin.getUuid(), "20", "Some Other Alert", null);
        bahmniLabResultService.add(bahmniLabResultUpdate);

        Encounter encounterWithObs = encounterService.getEncounterByUuid(encounter.getUuid());
        ArrayList<Obs> obsList = new ArrayList<>(encounterWithObs.getObsAtTopLevel(false));
        Obs labObsGroup = obsList.get(0);
        assertEquals(labObsGroup.getConcept(), Context.getConceptService().getConcept("Laboratory"));
        assertEquals(1, labObsGroup.getGroupMembers().size());

        Obs obs = (Obs) labObsGroup.getGroupMembers().toArray()[0];
        assertEquals((Double) 20.0, obs.getValueNumeric());
        assertEquals("accessionNumber", obs.getAccessionNumber());
        assertEquals("Some Other Alert", obs.getComment());
        assertEquals(haemoglobin, obs.getConcept());
        assertEquals(orders.toArray()[0], obs.getOrder());
    }

    private Encounter buildEncounter(Patient patient, Set<Order> orders) {
        Encounter enc = new Encounter();
        enc.setLocation(Context.getLocationService().getLocation(2));
        enc.setEncounterType(Context.getEncounterService().getEncounterType(2));
        enc.setEncounterDatetime(new Date());
        enc.setPatient(patient);
        enc.setOrders(orders);
        return enc;
    }

    private Set<Order> buildOrders(List<Concept> tests) {
        Set<Order> orders = new HashSet<>();
        for (Concept test : tests) {
            Order order = new Order();
            order.setConcept(test);
            orders.add(order);
        }
        return orders;
    }
}
