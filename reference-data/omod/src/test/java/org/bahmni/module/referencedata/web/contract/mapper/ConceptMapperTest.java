package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.bahmni.module.referencedata.labconcepts.mapper.ConceptMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class ConceptMapperTest {
    private ConceptMapper conceptMapper;

    @Before
    public void setUp() throws Exception {
        Locale defaultLocale = new Locale("en", "GB");
        PowerMockito.mockStatic(Context.class);
        when(Context.getLocale()).thenReturn(defaultLocale);
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
        ArrayList<String> synonyms = new ArrayList<>();
        synonyms.add("1");
        synonyms.add("2");
        concept.setSynonyms(synonyms);

        ConceptClass conceptClassName = new ConceptClass();
        conceptClassName.setName("Finding");
        ConceptDatatype conceptDatatype = new ConceptDatatype();
        conceptDatatype.setName("N/A");
        org.openmrs.Concept mappedConcept = conceptMapper.map(concept, conceptClassName, conceptDatatype, new HashSet<ConceptAnswer>(), null);

        assertEquals(concept.getUniqueName(), mappedConcept.getFullySpecifiedName(Context.getLocale()).getName());
        assertEquals(concept.getDisplayName(), mappedConcept.getShortNames().iterator().next().getName());
        assertEquals(concept.getDescription(), mappedConcept.getDescription().getDescription());
        assertEquals(concept.getClassName(), mappedConcept.getConceptClass().getName());
        assertEquals(concept.getDataType(), mappedConcept.getDatatype().getName());
        assertEquals(2, mappedConcept.getSynonyms().size());
        for (ConceptName conceptName : mappedConcept.getSynonyms()) {
            assertTrue(conceptName.getName().equals("1") || conceptName.getName().equals("2"));
        }
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
        org.openmrs.Concept mappedConcept = conceptMapper.map(concept, conceptClassName, conceptDatatype, new HashSet<ConceptAnswer>(), null);

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
        org.openmrs.Concept mappedConcept = conceptMapper.map(concept, conceptClassName, conceptDatatype, new HashSet<ConceptAnswer>(), null);

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
        org.openmrs.Concept mappedConcept = conceptMapper.map(concept, conceptClassName, conceptDatatype, answers, null);

        assertEquals(concept.getUniqueName(), mappedConcept.getFullySpecifiedName(Context.getLocale()).getName());
        assertEquals(0, mappedConcept.getShortNames().size());
        assertNull(mappedConcept.getDescriptions());
        assertEquals(concept.getClassName(), mappedConcept.getConceptClass().getName());
        assertEquals(concept.getDataType(), mappedConcept.getDatatype().getName());
        assertEquals(concept.getAnswers().iterator().next(), mappedConcept.getAnswers().iterator().next().getAnswerConcept().getName(Context.getLocale()).getName());
    }
}