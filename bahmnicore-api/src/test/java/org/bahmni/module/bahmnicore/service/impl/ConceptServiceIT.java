package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.observation.ConceptDefinition;
import org.bahmni.module.bahmnicore.service.ConceptService;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class ConceptServiceIT extends BaseModuleWebContextSensitiveTest {
    @Autowired
    private ConceptService conceptService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("conceptSetApiData.xml");
    }

    @Test
    public void getSortWeightFor_returns_sortweight_for_flattened_child_concept() throws Exception {
        String conceptNameInAnyCase = "BLOOd Pressure";
        ConceptDefinition conceptDefinition = conceptService.conceptsFor(new String[]{conceptNameInAnyCase, "non_existent_concept"});
        assertEquals(4, conceptDefinition.size());

        Concept childConcept = new Concept();
        childConcept.setPreferredName(new ConceptName("DIASTOLic", Context.getLocale()));

        int childSortWeight = conceptDefinition.getSortWeightFor(childConcept);
        assertEquals(3, childSortWeight);
    }

    @Test
    public void return_negative_sortweight_for_concept_that_does_not_exist() {
        String conceptNameInAnyCase = "BLOOd Pressure";
        ConceptDefinition conceptDefinition = conceptService.conceptsFor(new String[]{conceptNameInAnyCase});

        Concept childConcept = new Concept();
        childConcept.setPreferredName(new ConceptName("non_existent_concept", Context.getLocale()));

        int childSortWeight = conceptDefinition.getSortWeightFor(childConcept);
        assertEquals(-1, childSortWeight);
    }

    @Test
    public void do_not_fetch_voided_concepts() throws Exception {
        ConceptDefinition conceptDefinition = conceptService.conceptsFor(new String[]{"Blood Pressure voided node"});
        assertEquals(0, conceptDefinition.size());
    }
}