package org.bahmni.module.bahmnicoreui.service.impl;

import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.bahmni.module.bahmnicoreui.contract.ConceptValue;
import org.bahmni.module.bahmnicoreui.contract.DiseaseDataParams;
import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniDiseaseSummaryServiceImplIT extends BaseModuleContextSensitiveTest {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private BahmniObsService bahmniObsService;

    private BahmniDiseaseSummaryServiceImpl bahmniDiseaseSummaryData;

    @org.junit.Before
    public void setUp() throws Exception {
        bahmniDiseaseSummaryData = new BahmniDiseaseSummaryServiceImpl(bahmniObsService, conceptService);
        executeDataSet("observationsTestData.xml");
    }

    @Test
    public void shouldReturnObsForGivenConceptsAndNoOfVisits(){
        DiseaseDataParams diseaseDataParams = new DiseaseDataParams();
        diseaseDataParams.setNumberOfVisits(3);
        ArrayList<String> obsConcepts = new ArrayList<String>(){{
            add("Blood Pressure");
            add("Weight");
        }};
        diseaseDataParams.setObsConcepts(obsConcepts);
        Map<String, Map<String, ConceptValue>> obsTable = bahmniDiseaseSummaryData.getDiseaseSummary("86526ed5-3c11-11de-a0ba-001e378eb67a", diseaseDataParams);

        assertNotNull(obsTable);
        assertEquals(2, obsTable.size());

        Map<String, ConceptValue> obsForVisit = obsTable.get("2008-09-18");
        assertEquals(1, obsForVisit.size());
        assertEquals("110.0", obsForVisit.get("Weight").getValue());

        obsForVisit = obsTable.get("2008-08-18");
        assertEquals(2, obsForVisit.size());
        assertEquals("120.0", obsForVisit.get("Systolic").getValue());
        assertTrue(obsForVisit.get("Systolic").getAbnormal());
        assertEquals("40.0", obsForVisit.get("Diastolic").getValue());
        assertTrue(obsForVisit.get("Diastolic").getAbnormal());

    }


}