package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class ObsDaoIT extends BaseModuleWebContextSensitiveTest {
	@Autowired
    ObsDao obsDao;

    @Before
    public void setUp() throws Exception {
        executeDataSet("apiTestData.xml");
    }

    @Test
	public void shouldRetrievePatientObs() throws Exception {
        List<Obs> obsByPerson = obsDao.getNumericObsByPerson("86526ed5-3c11-11de-a0ba-001e378eb67a");
        assertEquals(5, obsByPerson.size());
	}

    @Test
    public void retrieve_all_observations_when_no_visit_ids_are_specified() throws Exception {
        List<Obs> allObs = obsDao.getObsFor("86526ed5-3c11-11de-a0ba-001e378eb67a", Arrays.asList("Blood Pressure"), null, null, false, null);

        assertEquals(1, allObs.size());

        Obs parent_obs = allObs.get(0);
        List<Obs> groupMembers = new ArrayList<>(parent_obs.getGroupMembers());
        assertEquals(2, groupMembers.size());
        assertEquals("Blood Pressure", parent_obs.getConcept().getName().getName());

        Obs childObs1 = groupMembers.get(0);
        Obs childObs2 = groupMembers.get(1);
        List<Obs> childGroupMembers1 = new ArrayList<>(childObs1.getGroupMembers());
        List<Obs> childGroupMembers2 = new ArrayList<>(childObs2.getGroupMembers());
        assertEquals("Systolic Data", childObs1.getConcept().getName().getName());
        assertEquals("Diastolic Data", childObs2.getConcept().getName().getName());

        assertEquals("Systolic", childGroupMembers1.get(0).getConcept().getName().getName());
        assertEquals("Diastolic", childGroupMembers2.get(0).getConcept().getName().getName());

        assertEquals(120.0, childGroupMembers1.get(0).getValueNumeric());
        assertEquals(100.0, childGroupMembers2.get(0).getValueNumeric());

        assertEquals("Systolic Abnormal", childGroupMembers1.get(1).getConcept().getName().getName());
        assertEquals("Diastolic Abnormal", childGroupMembers2.get(1).getConcept().getName().getName());

        assertEquals("False", childGroupMembers1.get(1).getValueCoded().getName().getName());
        assertEquals("True", childGroupMembers2.get(1).getValueCoded().getName().getName());
    }

    @Test
    public void retrieve_only_orphaned_observation() throws Exception {
        List<Obs> allObs = obsDao.getObsFor("341b4e41-790c-484f-b6ed-71dc8da222db", Arrays.asList("Diastolic"), null, null, false, null);

        assertEquals(1, allObs.size());
        assertEquals("Diastolic", allObs.get(0).getConcept().getName().getName());
        assertEquals(125.0, allObs.get(0).getValueNumeric());
    }

    @Test
	public void shouldRetrieveNumericalConceptsForPatient() throws Exception {
		assertEquals(5, obsDao.getNumericConceptsForPerson("86526ed5-3c11-11de-a0ba-001e378eb67a").size());
	}

    @Test
    public void do_not_fetch_voided_observations() throws Exception {
        List<Obs> allObs = obsDao.getObsFor("86526ed5-3c11-11de-a0ba-001e378eb67a", Arrays.asList("Blood Pressure"), null, null, false, null);
        assertEquals(1, allObs.size());
    }
}
