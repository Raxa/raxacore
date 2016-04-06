package org.bahmni.module.admin.csv.persister;

import org.bahmni.csv.KeyValue;
import org.bahmni.csv.Messages;
import org.bahmni.module.admin.BaseIntegrationTest;
import org.bahmni.module.admin.csv.models.ConceptSetRow;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ConceptSetPersisterIT extends BaseIntegrationTest {
    
    @Autowired
    private ConceptSetPersister conceptSetPersister;

    @Autowired
    private ConceptService conceptService;

    @Before
    public void setUp() throws Exception {
        Context.authenticate("admin", "test");
        executeDataSet("conceptSetup.xml");
    }

    @Test
    public void should_fail_validation_for_no_concept_name() throws Exception {
        ConceptSetRow conceptRow = new ConceptSetRow();
        Messages persistErrorMessages = conceptSetPersister.validate(conceptRow);
        assertFalse(persistErrorMessages.isEmpty());
    }

    @Test
    public void should_fail_validation_for_no_concept_class() throws Exception {
        ConceptSetRow conceptRow = new ConceptSetRow();
        conceptRow.name = "Concept Name";
        Messages persistErrorMessages = conceptSetPersister.validate(conceptRow);
        assertFalse(persistErrorMessages.isEmpty());
    }


    @Test
    public void should_pass_validation_if_concept_name_and_concept_class_are_present() throws Exception {
        ConceptSetRow conceptRow = new ConceptSetRow();
        conceptRow.name = "concept Name";
        conceptRow.conceptClass = "concept Class";
        Messages persistErrorMessages = conceptSetPersister.validate(conceptRow);
        assertTrue(persistErrorMessages.isEmpty());
    }

    @Test
    public void should_persist_new_concept_set_with_name_and_class_input_only() throws Exception {
        ConceptSetRow conceptRow = new ConceptSetRow();
        conceptRow.name = "New concept";
        conceptRow.conceptClass = "New Class";
        Messages persistErrorMessages = conceptSetPersister.persist(conceptRow);
        assertTrue(persistErrorMessages.isEmpty());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept persistedConcept = conceptService.getConceptByName(conceptRow.name);
        assertNotNull(persistedConcept);
        assertEquals(conceptRow.name, persistedConcept.getName(Context.getLocale()).getName());
        assertEquals(conceptRow.conceptClass, persistedConcept.getConceptClass().getName());
        assertNull(persistedConcept.getDescription());
        assertEquals(0, persistedConcept.getSynonyms().size());
        assertTrue(persistedConcept.isSet());
        Context.flushSession();
        Context.closeSession();
    }


    @Test
    public void add_set_members() throws Exception {
        ConceptSetRow conceptRow = new ConceptSetRow();
        conceptRow.name = "New concept";
        conceptRow.conceptClass = "New Class";
        List<KeyValue> children = new ArrayList<>();
        children.add(new KeyValue("1", "Child1"));
        children.add(new KeyValue("2", "Child2"));
        conceptRow.children = children;
        Messages persistErrorMessages = conceptSetPersister.persist(conceptRow);
        assertTrue(persistErrorMessages.isEmpty());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept persistedConcept = conceptService.getConceptByName(conceptRow.name);
        assertNotNull(persistedConcept);
        assertEquals(conceptRow.name, persistedConcept.getName(Context.getLocale()).getName());
        assertEquals(conceptRow.conceptClass, persistedConcept.getConceptClass().getName());
        assertEquals(2, persistedConcept.getSetMembers().size());
        assertNull(persistedConcept.getDescription());
        assertEquals(0, persistedConcept.getSynonyms().size());
        assertTrue(persistedConcept.isSet());
        Context.flushSession();
        Context.closeSession();
    }

}
