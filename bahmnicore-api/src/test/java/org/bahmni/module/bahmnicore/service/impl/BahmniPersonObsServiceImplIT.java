package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.mapper.builder.ConceptBuilder;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniPersonObsServiceImplIT extends BaseModuleWebContextSensitiveTest {

    BahmniObsService personObsService;
    
    @Autowired
    ObsDao obsDao;

    @Before
    public void setUp() throws Exception {
        personObsService = new BahmniObsServiceImpl(obsDao);
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
        Concept bloodPressureConcept = new ConceptBuilder().withName("Blood Pressure").build();
        List<Obs> obsForConceptSet = personObsService.observationsFor("86526ed5-3c11-11de-a0ba-001e378eb67a", Arrays.asList(bloodPressureConcept), null);
        assertEquals(2, obsForConceptSet.size());

        assertEquals("Systolic", obsForConceptSet.get(1).getConcept().getName().getName());
        assertEquals((Double) 110.0, obsForConceptSet.get(1).getValueNumeric());
    }
}
