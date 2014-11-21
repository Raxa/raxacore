package org.bahmni.module.referencedata.labconcepts.model.event;

import org.bahmni.test.builder.ConceptBuilder;
import org.ict4h.atomfeed.server.service.Event;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.Drug;

import static org.junit.Assert.*;

public class DrugEventTest {

    private static final Object[] WRONG_ARGUMENTS = new Object[]{};
    private Object[] drugs;
    private Concept drugConcept;
    private Drug drug;
    private Concept notDrug;
    private Object[] notDrugs;
    private DrugEvent drugEvent;


    @Before
    public void setUp() throws Exception {
        drugConcept = new ConceptBuilder().withClass("drug").withUUID("drugConceptUuid").withClassUUID(ConceptClass.DRUG_UUID).build();
        drug = new Drug();
        drug.setConcept(drugConcept);
        drug.setUuid("drugUUID");
        drugs = new Object[]{drug};
        notDrug = new Concept();
        notDrugs = new Object[]{notDrug};
        drugEvent = new DrugEvent(ConceptServiceEventFactory.CONCEPT_URL, ConceptServiceEventFactory.DRUG, ConceptServiceEventFactory.DRUG);
    }


    @Test
    public void not_applicable_for_wrong_operation() throws Exception {
        Boolean applicable = drugEvent.isApplicable("don'tSaveDrug", WRONG_ARGUMENTS);
        assertFalse(applicable);
    }

    @Test
    public void not_applicable_for_null_operation() throws Exception {
        Boolean applicable = drugEvent.isApplicable(null, WRONG_ARGUMENTS);
        assertFalse(applicable);
    }

    @Test
    public void not_applicable_for_null_arguments() throws Exception {
        Boolean applicable = drugEvent.isApplicable("saveDrug", null);
        assertFalse(applicable);
    }

    @Test
    public void not_applicable_for_wrong_arguments() throws Exception {
        Boolean applicable = drugEvent.isApplicable("saveDrug", WRONG_ARGUMENTS);
        assertFalse(applicable);
    }

    @Test
    public void not_applicable_for_wrong_argument_type() throws Exception {
        Boolean applicable = drugEvent.isApplicable("saveDrug", notDrugs);
        assertFalse(applicable);
    }

    @Test
    public void applicable_for_right_operations_and_arguments() throws Exception {
        Boolean applicable = drugEvent.isApplicable("saveDrug", drugs);
        assertTrue(applicable);
    }

    @Test
    public void publish_event_for_drugs() throws Exception {
        Event event = drugEvent.asAtomFeedEvent(drugs);
        assertEquals(ConceptServiceEventFactory.DRUG, event.getCategory());
        assertEquals(ConceptServiceEventFactory.DRUG, event.getTitle());
        assertEquals(String.format(ConceptServiceEventFactory.CONCEPT_URL, event.getCategory(), drug.getUuid()), event.getUri().toString());
    }
}