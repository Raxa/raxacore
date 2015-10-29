package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.BahmniConceptDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.encounter.ConceptMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BahmniConceptServiceImplTest {

    public static final String QUESTION = "question";
    public static final String SEARCH_QUERY = "q";
    @Mock
    private BahmniConceptDao bahmniConceptDao;
    @Mock
    private ConceptService conceptService;
    private BahmniConceptServiceImpl bahmniConceptService;

    @Before
    public void setUp() {
        initMocks(this);
        bahmniConceptService = new BahmniConceptServiceImpl(conceptService, bahmniConceptDao);
    }

    @Test
    public void searchByQuestionShouldDelegateToConceptDaoToSearchConcepts() {
        Concept questionConcept = new Concept();
        when(conceptService.getConceptByName(QUESTION)).thenReturn(questionConcept);
        Concept resultConcept = new Concept();
        when(bahmniConceptDao.searchByQuestion(questionConcept, SEARCH_QUERY)).thenReturn(Arrays.asList(resultConcept));

        Collection<Concept> concepts = bahmniConceptService.searchByQuestion(QUESTION, SEARCH_QUERY);
        assertThat(concepts.size(), is(equalTo(1)));
        assertThat(concepts.iterator().next().getUuid(), is(equalTo(resultConcept.getUuid())));
    }

}