package org.bahmni.module.referencedata.labconcepts.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;

import java.util.HashSet;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ReferenceDataConceptReferenceTermServiceImplTest {

    @InjectMocks
    private ReferenceDataConceptReferenceTermServiceImpl referenceDataConceptReferenceTermService = new ReferenceDataConceptReferenceTermServiceImpl();

    @Mock
    private ConceptService conceptService;

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_throw_exception_if_concept_reference_source_not_found() throws Exception {
        when(conceptService.getConceptSourceByName(anyString())).thenReturn(null);
        ConceptReferenceTerm conceptReferenceTerm = new ConceptReferenceTerm();
        conceptReferenceTerm.setConceptSource(new ConceptSource());
        when(conceptService.getConceptReferenceTermByCode(anyString(), any(ConceptSource.class))).thenReturn(conceptReferenceTerm);
        exception.expect(APIException.class);
        exception.expectMessage("Concept reference source not found");
        referenceDataConceptReferenceTermService.getConceptReferenceTerm("some", "some");
    }

    @Test
    public void should_throw_exception_if_concept_reference_term_not_found() throws Exception {
        when(conceptService.getConceptSourceByName(anyString())).thenReturn(new ConceptSource());
        when(conceptService.getConceptReferenceTermByCode(anyString(), any(ConceptSource.class))).thenReturn(null);
        exception.expect(APIException.class);
        exception.expectMessage("Concept reference term code not found");
        referenceDataConceptReferenceTermService.getConceptReferenceTerm("some", "some");
    }

    @Test
    public void should_throw_exception_if_concept_reference_term_not_mapped_to_source() throws Exception {
        ConceptSource source = new ConceptSource(1);
        ConceptSource termSource = new ConceptSource(2);
        source.setUuid("source");
        when(conceptService.getConceptSourceByName(anyString())).thenReturn(source);
        termSource.setUuid("termSource");
        ConceptReferenceTerm conceptReferenceTerm = new ConceptReferenceTerm();
        conceptReferenceTerm.setConceptSource(termSource);
        when(conceptService.getConceptReferenceTermByCode(anyString(), any(ConceptSource.class))).thenReturn(conceptReferenceTerm);
        exception.expect(APIException.class);
        exception.expectMessage("Concept reference term not mapped to the given source");
        referenceDataConceptReferenceTermService.getConceptReferenceTerm("some", "some");
    }
}