package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.PersonObsDao;
import org.bahmni.module.bahmnicore.service.BahmniPersonObsService;
import org.bahmni.module.bahmnicore.service.ConceptService;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniPersonObsServiceImplIT extends BaseModuleWebContextSensitiveTest {

    BahmniPersonObsService personObsService;
    @Autowired
    PersonObsDao personObsDao;

    @Autowired
    ConceptService conceptService;

    @Before
    public void setUp() throws Exception {
        personObsService = new BahmniPersonObsServiceImpl(personObsDao, conceptService);
        executeDataSet("observationsTestData.xml");
    }

    @Test
    public void shouldReturnLatestObsForEachConceptInTheConceptSet() {
        List<Obs> obsForConceptSet = personObsService.getLatest("86526ed5-3c11-11de-a0ba-001e378eb67a", Arrays.asList("Vitals"));
        assertEquals(2, obsForConceptSet.size());

        assertEquals("Weight", obsForConceptSet.get(0).getConcept().getName().getName());
        assertEquals("Pulse", obsForConceptSet.get(1).getConcept().getName().getName());
    }

    @Test
    public void return_orphaned_obs_for_patient() throws Exception {
        List<Obs> obsForConceptSet = personObsService.observationsFor("86526ed5-3c11-11de-a0ba-001e378eb67a", Arrays.asList("Blood Pressure"), null);
        assertEquals(2, obsForConceptSet.size());

        assertEquals("Systolic", obsForConceptSet.get(1).getConcept().getName().getName());
        assertEquals((Double) 110.0, obsForConceptSet.get(1).getValueNumeric());
    }
}
