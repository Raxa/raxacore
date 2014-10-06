package org.bahmni.module.admin.concepts.mapper;

import org.bahmni.csv.KeyValue;
import org.bahmni.module.admin.csv.models.ConceptSetRow;
import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

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
}