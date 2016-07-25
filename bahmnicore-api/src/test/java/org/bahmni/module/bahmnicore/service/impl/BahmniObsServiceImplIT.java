package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.BaseIntegrationTest;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.bahmni.test.builder.ConceptBuilder;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BahmniObsServiceImplIT extends BaseIntegrationTest {

    @Autowired
    BahmniObsService bahmniObsService;
    @Autowired
    private ConceptService conceptService;
    @Autowired
    private VisitService visitService;

    @Autowired
    ObsDao obsDao;

    @Before
    public void setUp() throws Exception {
        executeDataSet("diagnosisMetadata.xml");
        executeDataSet("dispositionMetadata.xml");
        executeDataSet("observationsTestData.xml");
        executeDataSet("patientProgramTestData.xml");
    }

    @Test
    public void shouldReturnLatestObsForEachConcept() {
        Concept vitalsConcept = conceptService.getConceptByName("Vitals");
        Collection<BahmniObservation> bahmniObservations = bahmniObsService.getLatest("86526ed5-3c11-11de-a0ba-001e378eb67a",
                Arrays.asList(vitalsConcept), 3, null, false, null);
        BahmniObservation vitalObservation = bahmniObservations.iterator().next();
        Collection<BahmniObservation> vitalsGroupMembers = vitalObservation.getGroupMembers();
        assertEquals(2, vitalsGroupMembers.size());
        Iterator<BahmniObservation> observationIterator = vitalsGroupMembers.iterator();

        BahmniObservation weight = observationIterator.next();
        BahmniObservation pulse = observationIterator.next();
        assertEquals("Pulse", pulse.getConcept().getName());
        assertEquals("Weight", weight.getConcept().getName());
    }

    @Test
    public void shouldReturnLatestObsForEachConceptForSpecifiedNumberOfVisits() {
        Concept sittingConcept = conceptService.getConceptByName("Vitals");
        //Latest limited by last two visits.
        Collection<BahmniObservation> bahmniObservations = bahmniObsService.getLatest("86526ed5-3c11-11de-a0ba-001e378eb67a",
                Arrays.asList(sittingConcept), 2, null, false, null);
        assertEquals(0, bahmniObservations.size());
        bahmniObservations = bahmniObsService.getLatest("86526ed5-3c11-11de-a0ba-001e378eb67a", Arrays.asList(sittingConcept), 3, null, false, null);
        assertEquals(1, bahmniObservations.size());

        BahmniObservation sittingObservation = bahmniObservations.iterator().next();
        assertEquals("Vitals", sittingObservation.getConcept().getName());
    }

    @Test
    public void shouldReturnLatestObsForEachConceptForSpecifiedVisitUuid() {
        Concept sittingConcept = conceptService.getConceptByName("Sitting");
        Visit visit = visitService.getVisitByUuid("e10186d8-1c8e-11e4-bb80-f18add123456");

        Collection<BahmniObservation> latestObsByVisit = bahmniObsService.getLatestObsByVisit(visit, Arrays.asList(sittingConcept), null, false);
        assertEquals(1, latestObsByVisit.size());
        BahmniObservation sittingObservation = latestObsByVisit.iterator().next();
        assertEquals("1.5", sittingObservation.getValueAsString());
    }

    @Test
    public void shouldReturnEmptyListIfTheVisitDoesnotHaveData() throws Exception {
        Concept sittingConcept = conceptService.getConceptByName("Sitting");
        Visit visit = visitService.getVisitByUuid("e10186d8-1c8e-11e4-bb80-f1badd123456");

        Collection<BahmniObservation> latestObsByVisit = bahmniObsService.getLatestObsByVisit(visit, Arrays.asList(sittingConcept), null, false);

        assertEquals(0, latestObsByVisit.size());
    }

    @Test
    public void shouldReturnLatestObsFromAllEncountersInVisit() {
        Concept concept = conceptService.getConcept("100");
        Visit visit = visitService.getVisitByUuid("e10186d8-1c8e-11e4-bb80-f18add123456");
        Collection<BahmniObservation> latestObsByVisit = bahmniObsService.getLatestObsByVisit(visit, Arrays.asList(concept), null, false);

        assertEquals(1, latestObsByVisit.size());
        BahmniObservation obs = latestObsByVisit.iterator().next();
        assertEquals("100.0", obs.getValueAsString());

    }

    @Test
    public void return_orphaned_obs_for_patient() throws Exception {
        Concept bloodPressureConcept = new ConceptBuilder().withName("Blood Pressure").build();
        Collection<BahmniObservation> obsForConceptSet = bahmniObsService.observationsFor("86526ed5-3c11-11de-a0ba-001e378eb67a",
                Arrays.asList(bloodPressureConcept), null, null, false, null, null, null);
        assertEquals(1, obsForConceptSet.size());
        Collection<BahmniObservation> bloodPressureMembers = obsForConceptSet.iterator().next().getGroupMembers();
        Iterator<BahmniObservation> bloodPressureMembersIterator = bloodPressureMembers.iterator();
        assertEquals(2, bloodPressureMembers.size());
        Collection<BahmniObservation> systolicMembers = bloodPressureMembersIterator.next().getGroupMembers();
        Collection<BahmniObservation> diastolicMembers = bloodPressureMembersIterator.next().getGroupMembers();
        assertEquals(2, systolicMembers.size());
        assertEquals(2, diastolicMembers.size());
    }

    @Test
    public void shouldReturnObsForAllConceptForGivenVisit() {
        List<BahmniObservation> bahmniObservations = (List<BahmniObservation>) bahmniObsService.getObservationForVisit("ad41fb41-a41a-4ad6-8835-2f59099acf5b", null, null, false, null);
        assertEquals(2, bahmniObservations.size());
        assertEquals(2, bahmniObservations.get(0).getGroupMembers().size());
        assertEquals(1, bahmniObservations.get(1).getGroupMembers().size());
    }

    @Test
    public void shouldReturnObsForGivenConceptForGivenVisitWithoutTakingObservationNamesCaseIntoAccount() {
        Collection<BahmniObservation> bahmniObservations =
                bahmniObsService.getObservationForVisit("ad41fb41-a41a-4ad6-8835-2f59099acf5b", Arrays.asList("SYSTOlic", "Diastolic"), null, false, null);
        assertEquals(2, bahmniObservations.size());
    }

    @Test
    public void shouldRetrieveObsForEncounter() throws Exception {
        ArrayList<String> conceptNames = new ArrayList<>();
        conceptNames.add("Systolic");

        Collection<BahmniObservation> observations = bahmniObsService.getObservationsForEncounter("bb0af6767-707a-4629-9850-f15206e63ab0", conceptNames);

        assertEquals(1, observations.size());
        assertEquals("6d8f507a-fb89-11e3-bb80-f18addb6f9bd", observations.iterator().next().getUuid());
    }

    @Test
    public void shouldRetrieveObsForPatientProgram() throws Exception {
        ArrayList<String> conceptNames = new ArrayList<>();
        conceptNames.add("conceptABC");

        Collection<BahmniObservation> observations = bahmniObsService.getObservationsForPatientProgram("dfdfoifo-dkcd-475d-b939-6d82327f36a3", conceptNames);

        assertEquals(1, observations.size());
        assertEquals("6d8f507a-fb899-11e3-bb80-996addb6f9we", observations.iterator().next().getUuid());

    }

    @Test
    public void shouldRetrieveEmptyObsListWhenPatientProgramUuidDoesNotExist() throws Exception {
        ArrayList<String> conceptNames = new ArrayList<>();
        conceptNames.add("conceptABC");

        Collection<BahmniObservation> observations = bahmniObsService.getObservationsForPatientProgram("patientProgramUuid", conceptNames);

        assertEquals(0, observations.size());
    }

    @Test
    public void shouldRetrieveEmptyObsIfPatientProgramDoesNotHaveAnyEncounters() throws Exception {
        ArrayList<String> conceptNames = new ArrayList<>();
        conceptNames.add("conceptABC");

        Collection<BahmniObservation> observations = bahmniObsService.getObservationsForPatientProgram("df0foifo-dkcd-475d-b939-6d82327f36a3", conceptNames);

        assertEquals(0, observations.size());
    }

    @Test
    public void shouldRetrieveBahmniObservationByObservationUuid() throws Exception {
        BahmniObservation bahmniObservation = bahmniObsService.getBahmniObservationByUuid("633dc076-1c8f-11e4-bkk0-f18addb6fmtb");

        assertNotNull("BahmniObservation should not be null", bahmniObservation);
        assertEquals("633dc076-1c8f-11e4-bkk0-f18addb6fmtb", bahmniObservation.getUuid());
    }
}
