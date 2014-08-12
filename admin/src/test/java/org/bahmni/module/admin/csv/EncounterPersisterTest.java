package org.bahmni.module.admin.csv;

import org.bahmni.csv.KeyValue;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class EncounterPersisterTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private EncounterPersister encounterPersister;

    @Autowired
    private EncounterService encounterService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("dataSetup.xml");
    }

    @Test
    public void should_fail_validation_for_encounter_type_not_found() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertEquals("Encounter Type null not found", validationResult.getErrorMessage());
    }

    @Test
    public void should_fail_validation_for_visit_type_not_found() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertEquals("Visit Type null not found", validationResult.getErrorMessage());
    }

    @Test
    public void should_pass_validation_for_correct_entries() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200000";
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        assertNull(validationResult.getErrorMessage());
    }

    @Test
    public void should_pass_validation_and_persist_encounters_for_patient() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200000";
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        RowResult<EncounterRow> persistenceResult = encounterPersister.persist(encounterRow);
        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(encounterRow.patientIdentifier);
        Context.flushSession();
        Context.closeSession();
        Encounter encounter = encounters.get(0);
        assertNull(validationResult.getErrorMessage());
        assertNull(persistenceResult.getErrorMessage());
        assertEquals(1, encounters.size());
        assertEquals("Anad", encounter.getPatient().getGivenName());
        assertEquals("Kewat", encounter.getPatient().getFamilyName());
        assertEquals("OPD", encounter.getVisit().getVisitType().getName());
        assertEquals("OPD", encounter.getEncounterType().getName());
    }

    @Test
    @Ignore
    public void should_pass_validation_and_persist_encounter_and_observations_for_patient() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200000";
        encounterRow.obsRows = new ArrayList<>();
        KeyValue weight = new KeyValue("WEIGHT", "150");
        encounterRow.obsRows.add(weight);
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        RowResult<EncounterRow> persistenceResult = encounterPersister.persist(encounterRow);
        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(encounterRow.patientIdentifier);
        Context.flushSession();
        Context.closeSession();
        Encounter encounter = encounters.get(0);
        assertNull(validationResult.getErrorMessage());
        assertNull(persistenceResult.getErrorMessage());
        assertEquals(1, encounters.size());
        assertEquals("Anad", encounter.getPatient().getGivenName());
        assertEquals("Kewat", encounter.getPatient().getFamilyName());
        assertEquals("OPD", encounter.getVisit().getVisitType().getName());
        assertEquals("OPD", encounter.getEncounterType().getName());
        assertEquals(1, encounter.getAllObs().size());
    }


}