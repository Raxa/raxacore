package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.BaseIntegrationTest;
import org.bahmni.module.bahmnicore.dao.BahmniConceptDao;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BahmniConceptDaoImplIT extends BaseIntegrationTest{
    @Autowired
    private BahmniConceptDao bahmniConceptDao;

    @Autowired
    private ConceptService conceptService;
    private Concept questionConcept;

    @Before
    public void setUp() throws Exception {
        executeDataSet("sampleCodedConcept.xml");
        questionConcept = conceptService.getConcept(90);
    }

    @Test
    public void shouldReturnNonVoidedAnswersForAQuestion() {
        Collection<Concept> result = bahmniConceptDao.searchByQuestion(questionConcept, "Aneurism");

        assertThat(result.size(), is(equalTo(1)));

        Concept resultConcept = result.iterator().next();
        assertTrue(resultConcept.getId().equals(902));
    }

    @Test
    public void shouldIgnoreCaseWhenSearching() {
        Collection<Concept> result = bahmniConceptDao.searchByQuestion(questionConcept, "aNeUrIsM");

        assertThat(result.size(), is(equalTo(1)));

        Concept resultConcept = result.iterator().next();
        assertTrue(resultConcept.getId().equals(902));
    }

    @Test
    public void shouldNotReturnVoidedAnswers() throws Exception {
        Collection<Concept> result = bahmniConceptDao.searchByQuestion(questionConcept, "Porphyria");
        assertThat(result.size(), is(equalTo(0)));
    }

    @Test
    public void shouldSearchEachTermByQuestion() throws Exception {
        //Searching for "Abscess, Skin"
        Collection<Concept> result = bahmniConceptDao.searchByQuestion(questionConcept, " ab sk  ");
        assertThat(result.size(), is(equalTo(1)));
        Concept resultConcept = result.iterator().next();
        assertTrue(resultConcept.getId().equals(903));

        result = bahmniConceptDao.searchByQuestion(questionConcept, "in  ab");
        assertThat(result.size(), is(equalTo(2)));
        assertThat(result, contains(conceptService.getConcept(902), conceptService.getConcept(903)));

        result = bahmniConceptDao.searchByQuestion(questionConcept, "in  and another term that is not present");
        assertThat(result.size(), is(equalTo(0)));
    }

    @Test
    public void shouldReturnMultipleResultsIfAvailable() throws Exception {
        Collection<Concept> result = bahmniConceptDao.searchByQuestion(questionConcept, "ab");

        assertThat(result.size(), is(equalTo(2)));

        assertThat(result, contains(conceptService.getConcept(902), conceptService.getConcept(903)));
    }
}