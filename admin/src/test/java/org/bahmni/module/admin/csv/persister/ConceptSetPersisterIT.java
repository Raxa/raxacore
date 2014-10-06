package org.bahmni.module.admin.csv.persister;

import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.ConceptRow;
import org.bahmni.module.admin.csv.models.ConceptSetRow;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class ConceptSetPersisterIT extends BaseModuleWebContextSensitiveTest {
    public static final String SAME_AS = "SAME-AS";
    @Autowired
    private ConceptSetPersister conceptSetPersister;

    @Autowired
    private ConceptService conceptService;
    private UserContext userContext;

    @Before
    public void setUp() throws Exception {
        Context.authenticate("admin", "test");
        executeDataSet("conceptSetup.xml");
        userContext = Context.getUserContext();
        conceptSetPersister.init(userContext);
    }

    @Test
    public void should_fail_validation_for_no_concept_name() throws Exception {
        ConceptSetRow conceptRow = new ConceptSetRow();
        RowResult<ConceptSetRow> conceptRowResult = conceptSetPersister.validate(conceptRow);
        assertFalse(conceptRowResult.getErrorMessage().isEmpty());
    }

    @Test
    public void should_fail_validation_for_no_concept_class() throws Exception {
        ConceptSetRow conceptRow = new ConceptSetRow();
        conceptRow.name = "Concept Name";
        RowResult<ConceptSetRow> conceptRowResult = conceptSetPersister.validate(conceptRow);
        assertFalse(conceptRowResult.getErrorMessage().isEmpty());
    }


    @Test
    public void should_pass_validation_if_concept_name_and_concept_class_are_present() throws Exception {
        ConceptSetRow conceptRow = new ConceptSetRow();
        conceptRow.name = "concept Name";
        conceptRow.conceptClass = "concept Class";
        RowResult<ConceptSetRow> conceptRowResult = conceptSetPersister.validate(conceptRow);
        assertTrue(conceptRowResult.getErrorMessage().isEmpty());
    }

    @Test @Ignore
    public void should_persist_new_concept_set_with_name_and_class_input_only() throws Exception {
        ConceptSetRow conceptRow = new ConceptSetRow();
        conceptRow.name = "New concept";
        conceptRow.conceptClass = "New Class";
        RowResult<ConceptSetRow> conceptRowResult = conceptSetPersister.persist(conceptRow);
        assertNull(conceptRowResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept persistedConcept = conceptService.getConceptByName(conceptRow.name);
        assertNotNull(persistedConcept);
        assertEquals(conceptRow.name, persistedConcept.getName(Context.getLocale()).getName());
        assertEquals(conceptRow.conceptClass, persistedConcept.getConceptClass().getName());
        assertNull(persistedConcept.getDescriptions());
        assertEquals(0, persistedConcept.getSynonyms().size());
        assertTrue(persistedConcept.isSet());
        Context.flushSession();
        Context.closeSession();
    }

}
