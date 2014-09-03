package org.openmrs.module.bahmniemrapi.encountertransaction.impl;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.service.BahmniEncounterTransactionService;
import org.openmrs.module.bahmniemrapi.obsrelation.contract.ObsRelationship;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniEncounterTransactionServiceImplIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    BahmniEncounterTransactionService bahmniEncounterTransactionService;
    @Autowired
    private VisitService visitService;
    @Autowired
    private EncounterService encounterService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("diagnosisMetadata.xml");
        executeDataSet("dispositionMetadata.xml");
        executeDataSet("obsRelationshipDataset.xml");
    }

    @Test
    public void shouldSaveBahmniEncounterTransactionWithBahmniObservationsWithGivenUuid(){
        Date obsDate = new Date();
        String obsUuid = UUID.randomUUID().toString();
        String visitUuid = "4e663d66-6b78-11e0-93c3-18a905e044dc";
        String patientUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
        Visit visit = visitService.getVisitByUuid(visitUuid);

        BahmniObservation bahmniObservation = createBahmniObservation(obsUuid, "obs-value", createConcept("96408258-000b-424e-af1a-403919332938", "FAVORITE FOOD, NON-CODED"), obsDate, null);
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();
        bahmniEncounterTransaction.addBahmniObservation(bahmniObservation);

        bahmniEncounterTransaction.setEncounterTypeUuid("07000be2-26b6-4cce-8b40-866d8435b613");
        bahmniEncounterTransaction.setPatientUuid(patientUuid);

        bahmniEncounterTransaction.setVisitUuid(visit.getUuid());
        BahmniEncounterTransaction encounterTransaction = bahmniEncounterTransactionService.save(bahmniEncounterTransaction);

        assertNotNull(encounterTransaction);
        assertEquals(1, encounterTransaction.getBahmniObservations().size());
        assertEquals("obs-value",encounterTransaction.getBahmniObservations().get(0).getValue());
        assertEquals(obsUuid, encounterTransaction.getBahmniObservations().get(0).getUuid());

    }

    @Test
    public void shouldSaveObsRelationShipWhenBothObservationsAreInSameEncounter(){
        Date obsDate = new Date();
        String srcObsUuid = UUID.randomUUID().toString();
        String targetObsUuid = UUID.randomUUID().toString();
        String visitUuid = "4e663d66-6b78-11e0-93c3-18a905e044dc";
        String patientUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";

        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();
        bahmniEncounterTransaction.setEncounterTypeUuid("07000be2-26b6-4cce-8b40-866d8435b613");
        bahmniEncounterTransaction.setPatientUuid(patientUuid);
        bahmniEncounterTransaction.setVisitUuid(visitUuid);

        EncounterTransaction.Concept srcConcept = createConcept("96408258-000b-424e-af1a-403919332938", "FAVORITE FOOD, NON-CODED");
        EncounterTransaction.Concept targetConcept = createConcept("c607c80f-1ea9-4da3-bb88-6276ce8868dd", "WEIGHT (KG)");
        BahmniObservation targetObs = createBahmniObservation(targetObsUuid, 150.0, targetConcept, obsDate, null);
        BahmniObservation srcObs = createBahmniObservation(srcObsUuid, "src-value", srcConcept, obsDate, targetObs);
        bahmniEncounterTransaction.addBahmniObservation(srcObs);
        bahmniEncounterTransaction.addBahmniObservation(targetObs);

        BahmniEncounterTransaction encounterTransaction = bahmniEncounterTransactionService.save(bahmniEncounterTransaction);

        assertEquals(2, encounterTransaction.getBahmniObservations().size());
        BahmniObservation savedSrcObs = encounterTransaction.getBahmniObservations().get(1);
        assertEquals(srcObs.getValue(), savedSrcObs.getValue());
        assertEquals(srcObsUuid, savedSrcObs.getUuid());
        assertEquals(srcConcept.getUuid(), savedSrcObs.getConceptUuid());

        assertEquals(targetObs.getValue(), savedSrcObs.getTargetObsRelation().getTargetObs().getValue());
        assertEquals(targetObsUuid, savedSrcObs.getTargetObsRelation().getTargetObs().getUuid());
        assertEquals(targetConcept.getUuid(), savedSrcObs.getTargetObsRelation().getTargetObs().getConceptUuid());
    }

    @Test
    public void shouldSaveObsRelationShipWhenBothObservationsAreInDifferentEncounter(){

    }


    private EncounterTransaction.Concept createConcept(String conceptUuid, String conceptName) {
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept();
        concept.setUuid(conceptUuid);
        concept.setName(conceptName);
        return concept;
    }

    private BahmniObservation createBahmniObservation(String uuid, String value, EncounterTransaction.Concept concept, Date obsDate, BahmniObservation bahmniObservation) {
        BahmniObservation bahmniObservation1 = new BahmniObservation();
        bahmniObservation1.setUuid(uuid);
        bahmniObservation1.setValue(value);
        bahmniObservation1.setConcept(concept);
        bahmniObservation1.setComment("comment");
        bahmniObservation1.setObservationDateTime(obsDate);
        bahmniObservation1.setTargetObsRelation(new ObsRelationship(bahmniObservation,null,"qualified-by"));
//        bahmniObservation1.setOrderUuid("order-uuid");
        return bahmniObservation1;
    }

    private BahmniObservation createBahmniObservation(String uuid, double value, EncounterTransaction.Concept concept, Date obsDate, BahmniObservation bahmniObservation) {
        BahmniObservation bahmniObservation1 = new BahmniObservation();
        bahmniObservation1.setUuid(uuid);
        bahmniObservation1.setValue(value);
        bahmniObservation1.setConcept(concept);
        bahmniObservation1.setComment("comment");
        bahmniObservation1.setObservationDateTime(obsDate);
        bahmniObservation1.setTargetObsRelation(new ObsRelationship(bahmniObservation,null,"qualified-by"));
//        bahmniObservation1.setOrderUuid("order-uuid");
        return bahmniObservation1;
    }
}
