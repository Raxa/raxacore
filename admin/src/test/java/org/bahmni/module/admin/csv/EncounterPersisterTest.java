package org.bahmni.module.admin.csv;

import org.bahmni.csv.KeyValue;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.EncounterRow;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class EncounterPersisterTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private EncounterPersister encounterPersister;

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private VisitService visitService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("baseMetaData.xml");
        executeDataSet("diagnosisMetaData.xml");
        executeDataSet("dispositionMetaData.xml");
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
        assertEquals("WEIGHT", encounter.getAllObs().iterator().next().getConcept().getName().getName());
        assertEquals("150.0", encounter.getAllObs().iterator().next().getValueAsString(Context.getLocale()));
    }

    @Test
    public void should_roll_back_transaction_once_persistence_fails_for_one_resource() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200000";
        encounterRow.obsRows = new ArrayList<>();
        KeyValue weight = new KeyValue("WEIGHT", "150");
        encounterRow.obsRows.add(weight);
        encounterPersister.validate(encounterRow);
        encounterRow.encounterType = "O1PD";
        encounterPersister.persist(encounterRow);
        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(encounterRow.patientIdentifier);
        List<Visit> visits = visitService.getVisitsByPatient(new Patient(1));
        Context.closeSession();
        assertEquals(0, visits.size());
        assertEquals(0, encounters.size());
    }

    @Test
    public void should_validate_and_persist_diagnosis() throws Exception {
        EncounterRow encounterRow = new EncounterRow();
        encounterRow.encounterType = "OPD";
        encounterRow.visitType = "OPD";
        encounterRow.patientIdentifier = "GAN200000";
        encounterRow.obsRows = new ArrayList<>();
        encounterRow.diagnosesRows = new ArrayList<>();
        KeyValue weight = new KeyValue("WEIGHT", "150");
        KeyValue diabetes = new KeyValue("Diagnosis1", "Diabetes");
        encounterRow.obsRows.add(weight);
        encounterRow.diagnosesRows.add(diabetes);
        RowResult<EncounterRow> validationResult = encounterPersister.validate(encounterRow);
        RowResult<EncounterRow> persistenceResult = encounterPersister.persist(encounterRow);
        Context.openSession();
        Context.authenticate("admin", "test");
        List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(encounterRow.patientIdentifier);
        Context.closeSession();
        Encounter encounter = encounters.get(0);
        assertNull(validationResult.getErrorMessage());
        assertNull(persistenceResult.getErrorMessage());
        assertEquals(1, encounters.size());
        assertEquals("Anad", encounter.getPatient().getGivenName());
        assertEquals("Kewat", encounter.getPatient().getFamilyName());
        assertEquals("OPD", encounter.getVisit().getVisitType().getName());
        assertEquals("OPD", encounter.getEncounterType().getName());
        ArrayList<Obs> allObs = new ArrayList<>();
        allObs.addAll(encounter.getAllObs());
        assertEquals(2, allObs.size());
        int weightIndex = 0;
        int diagnosisIndex = 0;
        if (allObs.get(0).getGroupMembers() == null || allObs.get(0).getGroupMembers().size() == 0) {
            diagnosisIndex = 1;
        } else {
            weightIndex = 1;
        }
        Obs weightObs = allObs.get(weightIndex);
        Obs diagnosisObs = allObs.get(diagnosisIndex);
        assertEquals("WEIGHT", weightObs.getConcept().getName().getName());
        assertEquals("150.0", weightObs.getValueAsString(Context.getLocale()));
        assertEquals("Diagnosis Concept Set", diagnosisObs.getConcept().getName().getName());
        List<String> obsConceptNames = new ArrayList<>();
        for (Obs obs : diagnosisObs.getGroupMembers()) {
            obsConceptNames.add(obs.getConcept().getName().getName());
        }
        assertTrue(obsConceptNames.contains("Diabetes"));
        assertTrue(obsConceptNames.contains("Diagnosis Certainty"));
        assertTrue(obsConceptNames.contains("Diagnosis Order"));
        assertTrue(obsConceptNames.contains("Bahmni Diagnosis Status"));
        assertTrue(obsConceptNames.contains("Bahmni Diagnosis Revised"));
        assertTrue(obsConceptNames.contains("Bahmni Initial Diagnosis"));
    }


}