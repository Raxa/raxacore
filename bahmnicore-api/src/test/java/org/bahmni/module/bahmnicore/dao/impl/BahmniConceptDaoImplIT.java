package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.BaseIntegrationTest;
import org.bahmni.module.bahmnicore.dao.BahmniConceptDao;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BahmniConceptDaoImplIT extends BaseIntegrationTest{
    @Autowired
    private BahmniConceptDao bahmniConceptDao;

    @Autowired
    private ConceptService conceptService;


    @Test
    public void shouldSearchByQuestion() {
        Concept questionConcept = conceptService.getConceptByName("CIVIL STATUS");
        Collection<Concept> result = bahmniConceptDao.searchByQuestion(questionConcept, "SIN");
        assertThat(result.size(), is(equalTo(1)));
    }

    @Override
    public Properties getRuntimeProperties() {
        Properties runtimeProperties1 = super.getRuntimeProperties();
        runtimeProperties1.setProperty("show_sql", "true");
        return runtimeProperties1;
    }
}