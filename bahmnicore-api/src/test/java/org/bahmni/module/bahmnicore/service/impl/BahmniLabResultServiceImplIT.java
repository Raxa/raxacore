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
import static org.junit.Assert.fail;

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
        Concept hbElectrophoresis = Context.getConceptService().getConcept("Hb Electrophoresis");
        Set<Order> orders = buildOrders(Arrays.asList(haemoglobin, hbElectrophoresis));
        Encounter encounter = encounterService.saveEncounter(buildEncounter(patient, orders));

        BahmniLabResult numericResult = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), haemoglobin.getUuid(), null, "15", "Some Alert", null);
        bahmniLabResultService.add(numericResult);
        BahmniLabResult codedResult = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), hbElectrophoresis.getUuid(), null, "Some coded result", null, null);
        bahmniLabResultService.add(codedResult);

        Encounter encounterWithObs = encounterService.getEncounterByUuid(encounter.getUuid());
        ArrayList<Obs> obsList = new ArrayList<>(encounterWithObs.getObsAtTopLevel(false));
        Obs labObsGroup = obsList.get(0);
        assertEquals(labObsGroup.getConcept(), Context.getConceptService().getConcept("Laboratory"));
        assertEquals(2, labObsGroup.getGroupMembers().size());

        assertLabResult(labObsGroup.getGroupMembers(), haemoglobin, "15", true);
        assertLabResult(labObsGroup.getGroupMembers(), hbElectrophoresis, "Some coded result", false);
    }

    @Test
    public void shouldUpdateObservationIfObservationAlreadyExistInEncounter() throws Exception {
        executeDataSet("labOrderTestData.xml");

        Patient patient = Context.getPatientService().getPatient(1);
        Concept haemoglobin = Context.getConceptService().getConcept("Haemoglobin");
        Set<Order> orders = buildOrders(Arrays.asList(haemoglobin));
        Encounter encounter = encounterService.saveEncounter(buildEncounter(patient, orders));

        BahmniLabResult bahmniLabResult = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), haemoglobin.getUuid(), null, "15", "Some Alert", null);
        bahmniLabResultService.add(bahmniLabResult);

        BahmniLabResult bahmniLabResultUpdate = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), haemoglobin.getUuid(), null, "20", "Some Other Alert", null);
        bahmniLabResultService.add(bahmniLabResultUpdate);

        Encounter encounterWithObs = encounterService.getEncounterByUuid(encounter.getUuid());
        Obs labObsGroup = new ArrayList<>(encounterWithObs.getObsAtTopLevel(false)).get(0);
        assertEquals(1, labObsGroup.getGroupMembers().size());

        Obs obs = (Obs) labObsGroup.getGroupMembers().toArray()[0];
        assertEquals((Double) 20.0, obs.getValueNumeric());
        assertEquals("accessionNumber", obs.getAccessionNumber());
        assertEquals("Some Other Alert", obs.getComment());
        assertEquals(haemoglobin, obs.getConcept());
        assertEquals(orders.toArray()[0], obs.getOrder());
    }

    @Test
    public void shouldSaveLabResultInsideObsGroupForPanel_WhenPanelIsOrdered() throws Exception {
        executeDataSet("labOrderTestData.xml");

        Patient patient = Context.getPatientService().getPatient(1);
        Concept bloodPanel = Context.getConceptService().getConcept("Blood Panel");
        Concept haemoglobin = Context.getConceptService().getConcept("Haemoglobin");
        Concept ESR = Context.getConceptService().getConcept("ESR");
        Set<Order> orders = buildOrders(Arrays.asList(bloodPanel));
        Encounter encounter = encounterService.saveEncounter(buildEncounter(patient, orders));

        BahmniLabResult  ESRResult = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), ESR.getUuid(), bloodPanel.getUuid(), "50", "Some Alert", null);
        bahmniLabResultService.add(ESRResult);

        BahmniLabResult hbResult = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), haemoglobin.getUuid(), bloodPanel.getUuid(), "20", "Some Other Alert", null);
        bahmniLabResultService.add(hbResult);

        BahmniLabResult updatedHbResult = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), haemoglobin.getUuid(),
                bloodPanel.getUuid(), "45", "Updated", null);
        bahmniLabResultService.add(updatedHbResult);

        Encounter encounterWithObs = encounterService.getEncounterByUuid(encounter.getUuid());
        Obs labObsGroup = new ArrayList<>(encounterWithObs.getObsAtTopLevel(false)).get(0);
        assertEquals(1, labObsGroup.getGroupMembers().size());

        Obs bloodPanelObsGroup = (Obs) labObsGroup.getGroupMembers().toArray()[0];
        assertEquals(2, bloodPanelObsGroup.getGroupMembers().size());
        assertEquals(bloodPanel, bloodPanelObsGroup.getConcept());

        assertLabResult(bloodPanelObsGroup.getGroupMembers(), haemoglobin, "45.0", true);
        assertLabResult(bloodPanelObsGroup.getGroupMembers(), ESR, "50.0", true);
    }

    @Test
    public void shouldPersistNotesAsObservation() throws Exception {
        executeDataSet("labOrderTestData.xml");

        Patient patient = Context.getPatientService().getPatient(1);
        Concept hb = Context.getConceptService().getConcept("Haemoglobin");
        Concept comment = Context.getConceptService().getConcept("COMMENTS");

        Set<Order> orders = buildOrders(Arrays.asList(hb));
        Encounter encounter = encounterService.saveEncounter(buildEncounter(patient, orders));

        BahmniLabResult result = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), hb.getUuid(), null, "15", "A", Arrays.asList("Note One"));
        bahmniLabResultService.add(result);

        BahmniLabResult updatedNotesResult = new BahmniLabResult(encounter.getUuid(), "accessionNumber", patient.getUuid(), hb.getUuid(), null, "15", "A", Arrays.asList("Note One", "Note Two"));
        bahmniLabResultService.add(updatedNotesResult);

        Encounter encounterWithObs = encounterService.getEncounterByUuid(encounter.getUuid());
        Obs labObsGroup = new ArrayList<>(encounterWithObs.getObsAtTopLevel(false)).get(0);
        assertEquals(1, labObsGroup.getGroupMembers().size());

        Obs resultObs = (Obs) labObsGroup.getGroupMembers().toArray()[0];
        assertEquals(2, resultObs.getGroupMembers().size());

        assertLabResultNote(resultObs.getGroupMembers(), comment, "Note One");
        assertLabResultNote(resultObs.getGroupMembers(), comment, "Note Two");
    }

    private void assertLabResultNote(Set<Obs> observations, Concept comment, String expectedNote) {
        ArrayList<String> notes = new ArrayList<>();
        for (Obs note : observations) {
            assertEquals(comment, note.getConcept());
            notes.add(note.getValueText());
        }

        assertTrue(notes.contains(expectedNote));
    }

    private void assertLabResult(Set<Obs> observations, Concept concept, String value, boolean isNumeric) {
        for (Obs observation : observations) {
            if(observation.getConcept().equals(concept)) {
                if(isNumeric) {
                    assertEquals((Object) Double.parseDouble(value), observation.getValueNumeric());
                } else {
                    assertEquals(value, observation.getValueText());
                }
                return;
            }
        }
        fail();
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
