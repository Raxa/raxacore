package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.web.contract.Concept;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ConceptMapperTest {
    private ConceptMapper conceptMapper;

    @Before
    public void setUp() throws Exception {
        conceptMapper = new ConceptMapper();
    }

    @Test
    public void shouldMapRequestConceptToOpenMRSConcept(){
        Concept concept = new Concept();
        concept.setUniqueName("uniqueName");
        concept.setDisplayName("displayName");
        concept.setDescription("description");
        concept.setClassName("Finding");
        concept.setDataType("N/A");

        ConceptClass conceptClassName = new ConceptClass();
        conceptClassName.setName("Finding");
        ConceptDatatype conceptDatatype = new ConceptDatatype();
        conceptDatatype.setName("N/A");
        org.openmrs.Concept mappedConcept = conceptMapper.map(concept, conceptClassName, conceptDatatype, null);

        assertEquals(concept.getUniqueName(), mappedConcept.getFullySpecifiedName(Context.getLocale()).getName());
        assertEquals(concept.getDisplayName(), mappedConcept.getShortNames().iterator().next().getName());
        assertEquals(concept.getDescription(), mappedConcept.getDescription().getDescription());
        assertEquals(concept.getClassName(), mappedConcept.getConceptClass().getName());
        assertEquals(concept.getDataType(), mappedConcept.getDatatype().getName());
    }

    @Test
    public void shouldMapConceptIfDescriptionIsNull(){
        Concept concept = new Concept();
        concept.setUniqueName("uniqueName");
        concept.setDisplayName("displayName");
        concept.setClassName("Finding");
        concept.setDataType("N/A");

        ConceptClass conceptClassName = new ConceptClass();
        conceptClassName.setName("Finding");
        ConceptDatatype conceptDatatype = new ConceptDatatype();
        conceptDatatype.setName("N/A");
        org.openmrs.Concept mappedConcept = conceptMapper.map(concept, conceptClassName, conceptDatatype, null);

        assertEquals(concept.getUniqueName(), mappedConcept.getFullySpecifiedName(Context.getLocale()).getName());
        assertEquals(concept.getDisplayName(), mappedConcept.getShortNames().iterator().next().getName());
        assertNull(mappedConcept.getDescriptions());
        assertEquals(concept.getClassName(), mappedConcept.getConceptClass().getName());
        assertEquals(concept.getDataType(), mappedConcept.getDatatype().getName());
    }

    @Test
    public void shouldMapConceptIfDisplayNameAndDescriptionIsNull(){
        Concept concept = new Concept();
        concept.setUniqueName("uniqueName");
        concept.setClassName("Finding");
        concept.setDataType("N/A");

        ConceptClass conceptClassName = new ConceptClass();
        conceptClassName.setName("Finding");
        ConceptDatatype conceptDatatype = new ConceptDatatype();
        conceptDatatype.setName("N/A");
        org.openmrs.Concept mappedConcept = conceptMapper.map(concept, conceptClassName, conceptDatatype, null);

        assertEquals(concept.getUniqueName(), mappedConcept.getFullySpecifiedName(Context.getLocale()).getName());
        assertEquals(0, mappedConcept.getShortNames().size());
        assertNull(mappedConcept.getDescriptions());
        assertEquals(concept.getClassName(), mappedConcept.getConceptClass().getName());
        assertEquals(concept.getDataType(), mappedConcept.getDatatype().getName());
    }

    @Test
    public void shouldMapCodedConceptWithAnswer(){
        Concept concept = new Concept();
        concept.setUniqueName("uniqueName");
        concept.setClassName("Finding");
        concept.setDataType("Coded");
        concept.setAnswers(new ArrayList<>(Arrays.asList("answer-concept-name")));

        ConceptClass conceptClassName = new ConceptClass();
        conceptClassName.setName("Finding");
        ConceptDatatype conceptDatatype = new ConceptDatatype();
        conceptDatatype.setName("Coded");
        Set<ConceptAnswer> answers = new HashSet<>();
        org.openmrs.Concept answerConcept = new org.openmrs.Concept();
        answerConcept.setFullySpecifiedName(new ConceptName("answer-concept-name", Context.getLocale()));
        answers.add(new ConceptAnswer(answerConcept));
        org.openmrs.Concept mappedConcept = conceptMapper.map(concept, conceptClassName, conceptDatatype, answers);

        assertEquals(concept.getUniqueName(), mappedConcept.getFullySpecifiedName(Context.getLocale()).getName());
        assertEquals(0, mappedConcept.getShortNames().size());
        assertNull(mappedConcept.getDescriptions());
        assertEquals(concept.getClassName(), mappedConcept.getConceptClass().getName());
        assertEquals(concept.getDataType(), mappedConcept.getDatatype().getName());
        assertEquals(concept.getAnswers().iterator().next(), mappedConcept.getAnswers().iterator().next().getAnswerConcept().getName(Context.getLocale()).getName());
    }
}