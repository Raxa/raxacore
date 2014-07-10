package org.bahmni.module.bahmnicore.dao.impl;

import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class PersonObsDaoImplIT extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	PersonObsDaoImpl personObsDao;

	@Test
	public void shouldRetrievePatientObs() throws Exception {
		executeDataSet("apiTestData.xml");
		assertEquals(3, personObsDao.getObsByPerson("86526ed5-3c11-11de-a0ba-001e378eb67a").size());
	}

    @Test
    public void retrieve_all_observations_when_no_visit_ids_are_specified() throws Exception {
        executeDataSet("apiTestData.xml");
        List<Obs> allObs = personObsDao.getObsFor("86526ed5-3c11-11de-a0ba-001e378eb67a", "Blood Pressure", null);
        assertEquals(1, allObs.size());
        Obs parent_obs = allObs.get(0);
        ArrayList<Obs> groupMembers = new ArrayList<Obs>(parent_obs.getGroupMembers());
        assertEquals(2, groupMembers.size());
        assertEquals("Blood Pressure", parent_obs.getConcept().getName().getName());
        Obs childObs1 = groupMembers.get(0);
        Obs childObs2 = groupMembers.get(1);
        ArrayList<Obs> childGroupMembers1 = new ArrayList<Obs>(childObs1.getGroupMembers());
        ArrayList<Obs> childGroupMembers2 = new ArrayList<Obs>(childObs2.getGroupMembers());
        assertEquals("Systolic Data", childObs1.getConcept().getName().getName());
        assertEquals("Diastolic Data", childObs2.getConcept().getName().getName());

        assertEquals("Systolic", childGroupMembers1.get(0).getConcept().getName().getName());
        assertEquals("Diastolic", childGroupMembers2.get(0).getConcept().getName().getName());

        assertEquals(120, childGroupMembers1.get(0).getValueNumeric());
        assertEquals(100, childGroupMembers2.get(1).getValueNumeric());

        assertEquals("Systolic Abnormal", childGroupMembers1.get(1).getConcept().getName().getName());
        assertEquals("Diastolic Abnormal", childGroupMembers2.get(1).getConcept().getName().getName());

        assertEquals("False", childGroupMembers1.get(0).getValueCoded().getName().getName());
        assertEquals("True", childGroupMembers2.get(1).getValueCoded().getName().getName());
    }

    @Test
	public void shouldRetrieveNumericalConceptsForPatient() throws Exception {
		executeDataSet("apiTestData.xml");
		assertEquals(3, personObsDao.getNumericConceptsForPerson("86526ed5-3c11-11de-a0ba-001e378eb67a").size());
	}
}
