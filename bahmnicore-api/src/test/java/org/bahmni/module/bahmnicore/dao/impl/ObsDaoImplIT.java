package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml", "classpath:webModuleApplicationContext.xml"}, inheritLocations = true)
public class ObsDaoImplIT extends BaseContextSensitiveTest {
    
    @Autowired
    ObsDao obsDao;

    Map<Integer, Integer> conceptToObsMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        executeDataSet("obsTestData.xml");
        conceptToObsMap.put(9012, 10);
        conceptToObsMap.put(9021, 11);
        conceptToObsMap.put(9011, 4);
        conceptToObsMap.put(9022, 6);
    }

    @Test
    public void shouldGetLatestObsForConceptSetByVisit() {
        List<Obs> obsList = obsDao.getLatestObsForConceptSetByVisit("86526ed5-3c11-11de-a0ba-001e378eb67a", "Breast Cancer Intake", 901);
        assertEquals(3, obsList.size());
        for (Obs obs : obsList) {
            assertEquals("for concept : " + obs.getConcept().getName().getName(), latestObsForConcept(obs.getConcept().getId()), obs.getId());
        }
    }

    private Integer latestObsForConcept(Integer id) {
        return conceptToObsMap.get(id);
    }
}