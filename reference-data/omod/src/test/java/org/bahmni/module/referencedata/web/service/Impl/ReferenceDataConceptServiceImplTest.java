package org.bahmni.module.referencedata.web.service.Impl;

import org.bahmni.module.referencedata.labconcepts.contract.ConceptSet;
import org.bahmni.test.builder.ConceptBuilder;
import org.bahmni.module.referencedata.labconcepts.contract.Concept;
import org.bahmni.module.referencedata.labconcepts.mapper.ConceptMapper;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptReferenceTermService;
import org.bahmni.module.referencedata.labconcepts.service.ReferenceDataConceptService;
import org.bahmni.module.referencedata.labconcepts.service.impl.ReferenceDataConceptServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest({ReferenceDataConceptServiceImpl.class})
@RunWith(PowerMockRunner.class)
public class ReferenceDataConceptServiceImplTest {
    private ReferenceDataConceptService referenceDataConceptService;

    @Mock
    private ConceptService conceptService;

    @Mock
    private ReferenceDataConceptReferenceTermService referenceDataConceptReferenceTermService;

    @Mock
    private ConceptClass conceptClass;

    @Mock
    private ConceptDatatype conceptDatatype;

    @Mock
    private ConceptMapper conceptMapper;

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private org.openmrs.Concept openmrsConcept;
    private Concept concept;
    private org.openmrs.Concept answer;
    private org.openmrs.ConceptAnswer answerConcept;


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        openmrsConcept = new ConceptBuilder().build();
        concept = new Concept();
        concept.setUniqueName("unique-name");
        answer = new ConceptBuilder().build();
        answerConcept = new ConceptAnswer();

        PowerMockito.when(this.conceptMapper.map(any(Concept.class), any(ConceptClass.class), any(ConceptDatatype.class), any(HashSet.class), any(org.openmrs.Concept.class))).thenReturn(openmrsConcept);
        PowerMockito.whenNew(ConceptMapper.class).withAnyArguments().thenReturn(conceptMapper);

        referenceDataConceptService = new ReferenceDataConceptServiceImpl(conceptService, referenceDataConceptReferenceTermService);

        when(conceptService.getConceptClassByName(anyString())).thenReturn(conceptClass);
        when(referenceDataConceptReferenceTermService.getConceptReferenceTerm(anyString(), anyString())).thenReturn(new org.openmrs.ConceptReferenceTerm());
        when(conceptService.getConceptDatatypeByName(anyString())).thenReturn(conceptDatatype);
        when(conceptService.saveConcept(openmrsConcept)).thenReturn(openmrsConcept);
        when(conceptDatatype.isCoded()).thenReturn(true);
    }

    @Test
    public void shouldCreateConcept() throws Throwable {

        org.openmrs.Concept savedConcept = referenceDataConceptService.saveConcept(concept);

        verify(conceptMapper).map(concept, conceptClass, conceptDatatype, new HashSet<ConceptAnswer>(), null);
        verify(conceptService).saveConcept(openmrsConcept);
        verify(conceptService).getConceptClassByName(concept.getClassName());
        verify(conceptService).getConceptDatatypeByName(concept.getDataType());
        assertEquals(savedConcept, openmrsConcept);
    }

    @Test
    public void shouldCreateCodedConceptWithAnswers() throws Throwable {
        String answerConceptName = "answer-concept";

        List<String> answers = new ArrayList<>();
        answers.add(answerConceptName);
        concept.setDataType("Coded");
        concept.setAnswers(answers);

        Set<ConceptAnswer> openMRSAnswers = new HashSet<>();
        openMRSAnswers.add(answerConcept);

        when(conceptService.getConcept(answerConceptName)).thenReturn(answer);

        org.openmrs.Concept savedConcept = referenceDataConceptService.saveConcept(concept);

        verify(conceptMapper).map(any(Concept.class), any(ConceptClass.class), any(ConceptDatatype.class), anySet(), any(org.openmrs.Concept.class));
        verify(conceptService).getConcept(answerConceptName);
        verify(conceptService).saveConcept(openmrsConcept);
        verify(conceptService).getConceptClassByName(concept.getClassName());
        verify(conceptService).getConceptDatatypeByName(concept.getDataType());
        assertEquals(savedConcept, openmrsConcept);
    }


    @Test
    public void shouldSetAnswersInOrder() throws Throwable {
        String answerConceptName1 = "answer-concept-1";
        String answerConceptName2 = "answer-concept-2";

        List<String> answers = new ArrayList<>();
        answers.add(answerConceptName1);
        answers.add(answerConceptName2);

        concept.setDataType("Coded");
        concept.setAnswers(answers);

        HashSet<ConceptAnswer> openMRSAnswers = new HashSet<>();
        openMRSAnswers.add(answerConcept);


        when(conceptService.getConcept(answerConceptName1)).thenReturn(openmrsConcept);
        when(conceptService.getConcept(answerConceptName2)).thenReturn(openmrsConcept);

        org.openmrs.Concept savedConcept = referenceDataConceptService.saveConcept(concept);

        verify(conceptMapper).map(any(Concept.class), any(ConceptClass.class), any(ConceptDatatype.class), anySet(), any(org.openmrs.Concept.class));
        verify(conceptService).getConcept(answerConceptName1);
        verify(conceptService).getConcept(answerConceptName2);
        verify(conceptService).saveConcept(openmrsConcept);
        verify(conceptService).getConceptClassByName(concept.getClassName());
        verify(conceptService).getConceptDatatypeByName(concept.getDataType());
        assertEquals(savedConcept, openmrsConcept);
    }

    @Test
    public void shouldThrowExceptionIfConceptClassNotFound() throws Throwable {
        concept.setClassName("abc");

        when(conceptService.getConceptClassByName(concept.getClassName())).thenReturn(null);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Concept Class abc not found");
        referenceDataConceptService.saveConcept(concept);
    }

    @Test
    public void shouldThrowExceptionIfConceptDatatypeNotFound() throws Throwable {
        concept.setDataType("xyz");

        when(conceptService.getConceptDatatypeByName(concept.getDataType())).thenReturn(null);

        exception.expect(APIException.class);
        exception.expectMessage("Concept Datatype xyz not found");
        referenceDataConceptService.saveConcept(concept);
    }

    @Test
    public void shouldThrowExceptionIfConceptDatatypeAndConceptClassNotFound() throws Throwable {
        concept.setDataType("xyz");
        concept.setClassName("abc");

        when(conceptService.getConceptClassByName(concept.getClassName())).thenReturn(null);
        when(conceptService.getConceptDatatypeByName(concept.getDataType())).thenReturn(null);

        exception.expect(APIException.class);
        exception.expectMessage("Concept Class abc not found\n" +
                "Concept Datatype xyz not found\n");
        referenceDataConceptService.saveConcept(concept);
    }

    @Test
    public void shouldThrowExceptionIfTheNonCodedConceptHasAnswers() throws Throwable {
        String answerConceptName = "answer-concept";

        List<String> answers = new ArrayList<>();
        answers.add(answerConceptName);
        concept.setDataType("N/A");
        concept.setAnswers(answers);

        HashSet<ConceptAnswer> openMRSAnswers = new HashSet<>();
        openMRSAnswers.add(answerConcept);

        when(conceptDatatype.isCoded()).thenReturn(false);

        exception.expect(APIException.class);
        exception.expectMessage("Cannot create answers for concept unique-name having datatype N/A");

        referenceDataConceptService.saveConcept(concept);
    }

}