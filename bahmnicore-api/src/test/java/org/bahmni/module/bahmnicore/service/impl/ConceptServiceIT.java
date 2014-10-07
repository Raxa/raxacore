package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.contract.observation.ConceptData;
import org.bahmni.module.bahmnicore.contract.observation.ConceptDefinition;
import org.bahmni.module.bahmnicore.service.ConceptService;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
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
    public void do_not_fetch_voided_concepts() throws Exception {
        ConceptDefinition conceptDefinition = conceptService.conceptsFor(Arrays.asList("Blood Pressure voided node"));
        assertEquals(0, conceptDefinition.size());
    }

    @Test
    public void return_all_leaf_nodes_in_a_group() throws Exception {
        String conceptNameInAnyCase = "Chief Complaint Data";
        ConceptDefinition conceptDefinition = conceptService.conceptsFor(Arrays.asList(conceptNameInAnyCase, "non_existent_concept"));
        assertEquals(2, conceptDefinition.size());

        List<ConceptData> chiefComplaintDataChildrenConcepts = conceptDefinition.getConcepts();
        assertEquals("Coded Complaint", chiefComplaintDataChildrenConcepts.get(0).getName());
        assertEquals("Non Coded Complaint", chiefComplaintDataChildrenConcepts.get(1).getName());
    }
}