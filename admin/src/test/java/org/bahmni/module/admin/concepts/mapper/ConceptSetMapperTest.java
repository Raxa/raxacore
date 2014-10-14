package org.bahmni.module.admin.concepts.mapper;

import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.ConceptRow;
import org.bahmni.module.admin.csv.models.ConceptRows;
import org.bahmni.module.admin.csv.models.ConceptSetRow;
import org.bahmni.module.bahmnicore.mapper.builder.ConceptBuilder;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ConceptSetMapperTest {

    private ConceptSetMapper conceptSetMapper;
    private ArrayList<KeyValue> children;

    @Before
    public void setUp() throws Exception {
        conceptSetMapper = new ConceptSetMapper();
        children = new ArrayList<>();
        children.add(new KeyValue("1", "child1"));
        children.add(new KeyValue("2", "child2"));
    }

    @Test
    public void map_concept_set_row_to_concept_set_dto() throws Exception {
        ConceptSetRow conceptSetRow = new ConceptSetRow();
        conceptSetRow.name = "UniqueName";
        conceptSetRow.shortName = "shortName";
        conceptSetRow.conceptClass = "ConvSet";
        conceptSetRow.children = children;

        ConceptSet conceptSet = conceptSetMapper.map(conceptSetRow);
        assertEquals(2, conceptSet.getChildren().size());
        assertEquals(conceptSetRow.name, conceptSet.getUniqueName());
        assertEquals(conceptSetRow.shortName, conceptSet.getDisplayName());
        assertEquals(conceptSetRow.conceptClass, conceptSet.getClassName());
    }

    @Test
    public void null_if_no_shortName() throws Exception {
        ConceptSetRow conceptSetRow = new ConceptSetRow();
        conceptSetRow.name = "Some";
        conceptSetRow.shortName = " ";
        ConceptSet map = conceptSetMapper.map(conceptSetRow);
        assertNull(map.getDisplayName());
        assertEquals(conceptSetRow.name, map.getUniqueName());
    }

    @Test
    public void shouldNotMapEmptyChildren() throws Exception {
        ConceptSetRow conceptSetRow = new ConceptSetRow();
        conceptSetRow.name = "UniqueName";
        conceptSetRow.shortName = "shortName";
        conceptSetRow.conceptClass = "ConvSet";
        conceptSetRow.children = children;
        conceptSetRow.children.add(new KeyValue("3", ""));

        ConceptSet conceptSet = conceptSetMapper.map(conceptSetRow);
        assertEquals(2, conceptSet.getChildren().size());
        assertEquals(conceptSetRow.name, conceptSet.getUniqueName());
        assertEquals(conceptSetRow.shortName, conceptSet.getDisplayName());
        assertEquals(conceptSetRow.conceptClass, conceptSet.getClassName());

    }

    @Test
    public void map_concept_reference_term_to_concept_set_dto() throws Exception {
        ConceptSetRow conceptSetRow = new ConceptSetRow();
        conceptSetRow.name = "UniqueName";
        conceptSetRow.shortName = "shortName";
        conceptSetRow.conceptClass = "ConvSet";
        conceptSetRow.children = children;
        conceptSetRow.referenceTermCode = "code";
        conceptSetRow.referenceTermRelationship = "rel";
        conceptSetRow.referenceTermSource = "source";


        ConceptSet conceptSet = conceptSetMapper.map(conceptSetRow);
        assertEquals(2, conceptSet.getChildren().size());
        assertEquals(conceptSetRow.name, conceptSet.getUniqueName());
        assertEquals(conceptSetRow.shortName, conceptSet.getDisplayName());
        assertEquals(conceptSetRow.conceptClass, conceptSet.getClassName());
        assertEquals("code", conceptSet.getConceptReferenceTerm().getReferenceTermCode());
        assertEquals("rel", conceptSet.getConceptReferenceTerm().getReferenceTermRelationship());
        assertEquals("source", conceptSet.getConceptReferenceTerm().getReferenceTermSource());
    }

    @Test
    public void get_concept_list_in_order_from_concept_set() throws Exception {
        org.openmrs.Concept child1 = new ConceptBuilder().withName("Child1").withDescription("Description").withClass("Some").withDataType("N/A").withShortName("short").build();
        org.openmrs.Concept child2 = new ConceptBuilder().withName("Child2").withDescription("Description").withClass("Some").withDataType("N/A").withShortName("short").build();
        org.openmrs.Concept child3 = new ConceptBuilder().withName("Child3").withDescription("Description").withClass("Some").withDataType("N/A").withShortName("short").build();
        org.openmrs.Concept child4 = new ConceptBuilder().withName("Child4").withDescription("Description").withClass("Some").withDataType("N/A").withShortName("short").build();
        org.openmrs.Concept conceptSet = new ConceptBuilder().withName("Parent Concept").withClass("Misc").withDataType("N/A").withShortName("Shortn").withUUID("Parent").withSetMember(child1).withSetMember(child2).withSetMember(child3).withSetMember(child4).build();
        List<ConceptRow> conceptRows =  conceptSetMapper.mapAll(conceptSet).getConceptRows();
        assertEquals(4, conceptRows.size());
        assertEquals("Child1", conceptRows.get(0).name);
        assertEquals("Child2", conceptRows.get(1).name);
        assertEquals("Child3", conceptRows.get(2).name);
        assertEquals("Child4", conceptRows.get(3).name);
        assertEquals("Description", conceptRows.get(0).description);
        assertEquals("Some", conceptRows.get(0).conceptClass);
        assertEquals("N/A", conceptRows.get(0).getDataType());
        assertEquals(0, conceptRows.get(0).getSynonyms().size());
        assertEquals(0, conceptRows.get(1).getSynonyms().size());
        assertEquals(0, conceptRows.get(2).getSynonyms().size());
        assertEquals(0, conceptRows.get(3).getSynonyms().size());
    }

    @Test
    public void get_list_of_concept_with_concept_answers() throws Exception {
        org.openmrs.Concept answer1 = new ConceptBuilder().withName("Answer1").withDataType("N/A").withClass("Misc").withShortName("ShortName3").withUUID("answer1").build();
        org.openmrs.Concept child1 = new ConceptBuilder().withName("Child1").withDataType("N/A").withClass("Misc").withShortName("ShortName1").withUUID("child1").withAnswer(answer1).build();
        org.openmrs.Concept child2 = new ConceptBuilder().withName("Child2").withDataType("N/A").withClass("Misc").withShortName("ShortName2").withUUID("child2").build();
        org.openmrs.Concept conceptSet = new ConceptBuilder().withName("Parent Concept").withClass("Misc").withDataType("N/A").withShortName("Shortn").withUUID("Parent").withSetMember(child1).withSetMember(child2).build();
        ConceptRows conceptRows = conceptSetMapper.mapAll(conceptSet);
        List<ConceptRow> conceptList = conceptRows.getConceptRows();
        ConceptRow answer = conceptList.get(0);
        ConceptRow child1Row = conceptList.get(1);
        ConceptRow child2Row = conceptList.get(2);
        assertEquals(3, conceptList.size());
        assertEquals("Answer1", answer.name);
        assertEquals("Child1", child1Row.name);
        assertEquals("Child2", child2Row.name);
    }
    @Test
    public void get_list_of_concept_with_concept_sets() throws Exception {
        org.openmrs.Concept answer1 = new ConceptBuilder().withName("Answer1").withDataType("N/A").withClass("Misc").withShortName("ShortName3").withUUID("answer1").build();
        org.openmrs.Concept child1 = new ConceptBuilder().withName("Child1").withDataType("N/A").withClass("Misc").withShortName("ShortName1").withUUID("child1").withAnswer(answer1).build();
        org.openmrs.Concept child2 = new ConceptBuilder().withName("Child2").withDataType("N/A").withClass("Misc").withShortName("ShortName2").withUUID("child2").build();
        org.openmrs.Concept child3 = new ConceptBuilder().withName("Child3").withDataType("N/A").withClass("Misc").withShortName("ShortName3").withUUID("child3").build();
        org.openmrs.Concept set1 = new ConceptBuilder().withName("Sub Parent").withDataType("N/A").withClass("Misc").withShortName("SubP").withUUID("subp").withSetMember(child3).build();
        set1.setSet(true);
        org.openmrs.Concept conceptSet = new ConceptBuilder().withName("Parent Concept").withClass("Misc").withDataType("N/A").withShortName("Shortn").withUUID("Parent").withSetMember(child1).withSetMember(child2).withSetMember(set1).build();
        conceptSet.setSet(true);
        ConceptRows conceptRows = conceptSetMapper.mapAll(conceptSet);
        List<ConceptRow> conceptList = conceptRows.getConceptRows();
        List<ConceptSetRow> conceptSetList = conceptRows.getConceptSetRows();
        ConceptRow answer = conceptList.get(0);
        ConceptRow child1Row = conceptList.get(1);
        ConceptRow child2Row = conceptList.get(2);
        assertEquals(4, conceptList.size());
        assertEquals("Answer1", answer.name);
        assertEquals("Child1", child1Row.name);
        assertEquals("Child2", child2Row.name);
        assertEquals(2, conceptSetList.size());
        ConceptSetRow conceptSetRow1 = conceptSetList.get(0);
        ConceptSetRow conceptSetRow2 = conceptSetList.get(1);
        assertEquals("Parent Concept", conceptSetRow2.name);
        assertEquals("Sub Parent", conceptSetRow1.name);
    }
}