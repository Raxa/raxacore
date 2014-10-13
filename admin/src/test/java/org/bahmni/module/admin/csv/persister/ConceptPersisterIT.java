package org.bahmni.module.admin.csv.persister;

import org.bahmni.csv.KeyValue;
import org.bahmni.csv.RowResult;
import org.bahmni.module.admin.csv.models.ConceptRow;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class ConceptPersisterIT extends BaseModuleWebContextSensitiveTest {
    public static final String SAME_AS = "SAME-AS";
    @Autowired
    private ConceptPersister conceptPersister;

    @Autowired
    private ConceptService conceptService;
    private UserContext userContext;

    @Before
    public void setUp() throws Exception {
        Context.authenticate("admin", "test");
        executeDataSet("conceptSetup.xml");
        userContext = Context.getUserContext();
        conceptPersister.init(userContext);
    }

    @Test
    public void should_fail_validation_for_no_concept_name() throws Exception {
        ConceptRow conceptRow = new ConceptRow();
        RowResult<ConceptRow> conceptRowResult = conceptPersister.validate(conceptRow);
        assertFalse(conceptRowResult.getErrorMessage().isEmpty());
    }

    @Test
    public void should_fail_validation_for_no_concept_class() throws Exception {
        ConceptRow conceptRow = new ConceptRow();
        conceptRow.name = "Concept Name";
        RowResult<ConceptRow> conceptRowResult = conceptPersister.validate(conceptRow);
        assertFalse(conceptRowResult.getErrorMessage().isEmpty());
    }


    @Test
    public void should_pass_validation_if_concept_name_and_concept_class_are_present() throws Exception {
        ConceptRow conceptRow = new ConceptRow();
        conceptRow.name = "concept Name";
        conceptRow.conceptClass = "concept Class";
        RowResult<ConceptRow> conceptRowResult = conceptPersister.validate(conceptRow);
        assertTrue(conceptRowResult.getErrorMessage().isEmpty());
    }

    @Test
    public void should_persist_new_concept_with_name_and_class_input_only() throws Exception {
        ConceptRow conceptRow = new ConceptRow();
        conceptRow.name = "New concept";
        conceptRow.conceptClass = "New Class";
        RowResult<ConceptRow> conceptRowResult = conceptPersister.persist(conceptRow);
        assertNull(conceptRowResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept persistedConcept = conceptService.getConceptByName(conceptRow.name);
        assertNotNull(persistedConcept);
        assertEquals(conceptRow.name, persistedConcept.getName(Context.getLocale()).getName());
        assertEquals(conceptRow.conceptClass, persistedConcept.getConceptClass().getName());
        assertNull(persistedConcept.getDescription());
        assertEquals(0, persistedConcept.getSynonyms().size());
        Context.flushSession();
        Context.closeSession();
    }

    @Test
    public void should_persist_new_concept_with_name_and_class_and_datatype_description_shortname_synonyms_input_only() throws Exception {
        ConceptRow conceptRow = new ConceptRow();
        conceptRow.name = "New Concept";
        conceptRow.description = "New Description";
        conceptRow.conceptClass = "New Class";
        conceptRow.dataType = "Numeric";
        conceptRow.shortName = "NConcept";
        List<KeyValue> synonyms = new ArrayList<>();
        synonyms.add(new KeyValue("1", "Synonym1"));
        synonyms.add(new KeyValue("2", "Synonym2"));
        conceptRow.synonyms = synonyms;
        RowResult<ConceptRow> conceptRowResult = conceptPersister.persist(conceptRow);
        assertNull(conceptRowResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept persistedConcept = conceptService.getConceptByName(conceptRow.name);
        assertNotNull(persistedConcept);
        assertEquals(conceptRow.name, persistedConcept.getName(Context.getLocale()).getName());
        assertEquals(conceptRow.description, persistedConcept.getDescription(Context.getLocale()).getDescription());
        assertEquals(conceptRow.conceptClass, persistedConcept.getConceptClass().getName());
        assertEquals(ConceptDatatype.NUMERIC_UUID, persistedConcept.getDatatype().getUuid());
        assertEquals(conceptRow.shortName, persistedConcept.getShortestName(Context.getLocale(), false).getName());
        assertEquals(2, persistedConcept.getSynonyms().size());
        for (ConceptName conceptName : persistedConcept.getSynonyms(Context.getLocale())) {
            assertTrue(conceptName.getName().equals("Synonym1") || conceptName.getName().equals("Synonym2"));
        }
        Context.flushSession();
        Context.closeSession();
    }

    @Test
    public void should_persist_new_concept_with_answers() throws Exception {
        ConceptRow conceptRow = new ConceptRow();
        conceptRow.name = "New Concept";
        conceptRow.description = "New Description";
        conceptRow.conceptClass = "New Class";
        conceptRow.dataType = "Coded";
        conceptRow.shortName = "NConcept";
        List<KeyValue> synonyms = new ArrayList<>();
        synonyms.add(new KeyValue("1", "Synonym1"));
        synonyms.add(new KeyValue("2", "Synonym2"));
        conceptRow.synonyms = synonyms;
        List<KeyValue> answers = new ArrayList<>();
        answers.add(new KeyValue("1", "Answer1"));
        answers.add(new KeyValue("2", "Answer2"));
        conceptRow.answers = answers;
        RowResult<ConceptRow> conceptRowResult = conceptPersister.persist(conceptRow);
        assertNull(conceptRowResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept persistedConcept = conceptService.getConceptByName(conceptRow.name);
        assertNotNull(persistedConcept);
        assertEquals(conceptRow.name, persistedConcept.getName(Context.getLocale()).getName());
        assertEquals(conceptRow.description, persistedConcept.getDescription(Context.getLocale()).getDescription());
        assertEquals(conceptRow.conceptClass, persistedConcept.getConceptClass().getName());
        assertEquals(ConceptDatatype.CODED_UUID, persistedConcept.getDatatype().getUuid());
        assertEquals(conceptRow.shortName, persistedConcept.getShortestName(Context.getLocale(), false).getName());
        assertEquals(2, persistedConcept.getSynonyms().size());
        assertEquals(2, persistedConcept.getAnswers().size());
        for (ConceptName conceptName : persistedConcept.getSynonyms(Context.getLocale())) {
            assertTrue(conceptName.getName().equals("Synonym1") || conceptName.getName().equals("Synonym2"));
        }
        for (ConceptAnswer conceptAnswer : persistedConcept.getAnswers()) {
            assertTrue(conceptAnswer.getAnswerConcept().getName(Context.getLocale()).getName().equals("Answer1")
                    || conceptAnswer.getAnswerConcept().getName(Context.getLocale()).getName().equals("Answer2"));
        }
        Context.flushSession();
        Context.closeSession();
    }

    @Test
    public void should_set_concept_reference_terms() throws Exception {
        ConceptRow conceptRow = new ConceptRow();
        conceptRow.name = "New Concept";
        conceptRow.description = "New Description";
        conceptRow.conceptClass = "New Class";
        conceptRow.dataType = "Coded";
        conceptRow.shortName = "NConcept";
        conceptRow.referenceTermSource = "org.openmrs.module.emrapi";
        conceptRow.referenceTermCode = "New Code";
        conceptRow.referenceTermRelationship = SAME_AS;
        List<KeyValue> synonyms = new ArrayList<>();
        synonyms.add(new KeyValue("1", "Synonym1"));
        synonyms.add(new KeyValue("2", "Synonym2"));
        conceptRow.synonyms = synonyms;
        List<KeyValue> answers = new ArrayList<>();
        answers.add(new KeyValue("1", "Answer1"));
        answers.add(new KeyValue("2", "Answer2"));
        conceptRow.answers = answers;
        RowResult<ConceptRow> conceptRowResult = conceptPersister.persist(conceptRow);
        assertNull(conceptRowResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept persistedConcept = conceptService.getConceptByName(conceptRow.name);
        assertNotNull(persistedConcept);
        assertEquals(conceptRow.name, persistedConcept.getName(Context.getLocale()).getName());
        assertEquals(conceptRow.description, persistedConcept.getDescription(Context.getLocale()).getDescription());
        assertEquals(conceptRow.conceptClass, persistedConcept.getConceptClass().getName());
        assertEquals(ConceptDatatype.CODED_UUID, persistedConcept.getDatatype().getUuid());
        assertEquals(conceptRow.shortName, persistedConcept.getShortestName(Context.getLocale(), false).getName());
        assertEquals(2, persistedConcept.getSynonyms().size());
        assertEquals(2, persistedConcept.getAnswers().size());
        for (ConceptName conceptName : persistedConcept.getSynonyms(Context.getLocale())) {
            assertTrue(conceptName.getName().equals("Synonym1") || conceptName.getName().equals("Synonym2"));
        }
        for (ConceptAnswer conceptAnswer : persistedConcept.getAnswers()) {
            assertTrue(conceptAnswer.getAnswerConcept().getName(Context.getLocale()).getName().equals("Answer1")
                    || conceptAnswer.getAnswerConcept().getName(Context.getLocale()).getName().equals("Answer2"));
        }
        ArrayList<ConceptMap> conceptMaps = new ArrayList<>(persistedConcept.getConceptMappings());
        ConceptMap conceptMap = conceptMaps.get(0);
        assertEquals(persistedConcept, conceptMap.getConcept());
        assertEquals(conceptRow.referenceTermCode, conceptMap.getConceptReferenceTerm().getCode());
        assertEquals(conceptRow.referenceTermRelationship.toLowerCase(), conceptMap.getConceptMapType().toString());
        assertEquals(conceptRow.referenceTermSource, conceptMap.getConceptReferenceTerm().getConceptSource().getName());
        Context.flushSession();
        Context.closeSession();

    }

    @Test
    public void should_update_details_on_existing_concepts() throws Exception {
        ConceptRow conceptRow = new ConceptRow();
        conceptRow.name = "Existing Concept";
        conceptRow.conceptClass = "New Class";
        conceptRow.description = "Some Description";
        List<KeyValue> synonyms = new ArrayList<>();
        synonyms.add(new KeyValue("1", "Synonym1"));
        synonyms.add(new KeyValue("2", "Synonym2"));
        conceptRow.synonyms = synonyms;
        conceptRow.shortName = "NConcept";
        RowResult<ConceptRow> conceptRowResult = conceptPersister.persist(conceptRow);
        assertNull(conceptRowResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept persistedConcept = conceptService.getConceptByName(conceptRow.name);
        assertNotNull(persistedConcept);
        assertEquals(conceptRow.description, persistedConcept.getDescription(Context.getLocale()).getDescription());
        assertEquals(conceptRow.conceptClass, persistedConcept.getConceptClass().getName());
        assertEquals(conceptRow.shortName, persistedConcept.getShortestName(Context.getLocale(), false).getName());
        assertEquals(2, persistedConcept.getSynonyms().size());
        assertEquals(0, persistedConcept.getAnswers().size());
        for (ConceptName conceptName : persistedConcept.getSynonyms(Context.getLocale())) {
            assertTrue(conceptName.getName().equals("Synonym1") || conceptName.getName().equals("Synonym2"));
        }
        Context.flushSession();
        Context.closeSession();
    }

    @Test
    public void should_create_new_mapping_for_existing_concept() throws Exception {
        ConceptRow conceptRow = new ConceptRow();
        conceptRow.name = "Existing Concept";
        conceptRow.conceptClass = "New Class";
        conceptRow.description = "Some Description";
        conceptRow.referenceTermSource = "org.openmrs.module.emrapi";
        conceptRow.referenceTermCode = "New Code";
        conceptRow.referenceTermRelationship = SAME_AS;

        List<KeyValue> synonyms = new ArrayList<>();
        synonyms.add(new KeyValue("1", "Synonym1"));
        synonyms.add(new KeyValue("2", "Synonym2"));
        conceptRow.synonyms = synonyms;
        conceptRow.shortName = "NConcept";
        RowResult<ConceptRow> conceptRowResult = conceptPersister.persist(conceptRow);
        assertNull(conceptRowResult.getErrorMessage());
        Context.openSession();
        Context.authenticate("admin", "test");
        Concept persistedConcept = conceptService.getConceptByName(conceptRow.name);
        assertNotNull(persistedConcept);
        assertEquals(conceptRow.description, persistedConcept.getDescription(Context.getLocale()).getDescription());
        assertEquals(conceptRow.conceptClass, persistedConcept.getConceptClass().getName());
        assertEquals(conceptRow.shortName, persistedConcept.getShortestName(Context.getLocale(), false).getName());
        assertEquals(2, persistedConcept.getSynonyms().size());
        assertEquals(0, persistedConcept.getAnswers().size());
        ArrayList<ConceptMap> conceptMaps = new ArrayList<>(persistedConcept.getConceptMappings());
        ConceptMap conceptMap = conceptMaps.get(0);
        assertEquals(persistedConcept, conceptMap.getConcept());
        assertEquals(conceptRow.referenceTermCode, conceptMap.getConceptReferenceTerm().getCode());
        assertEquals(conceptRow.referenceTermRelationship.toLowerCase(), conceptMap.getConceptMapType().toString());
        assertEquals(conceptRow.referenceTermSource, conceptMap.getConceptReferenceTerm().getConceptSource().getName());

        for (ConceptName conceptName : persistedConcept.getSynonyms(Context.getLocale())) {
            assertTrue(conceptName.getName().equals("Synonym1") || conceptName.getName().equals("Synonym2"));
        }
        Context.flushSession();
        Context.closeSession();
    }
}
