package org.bahmni.module.bahmnicore.dao.impl;

import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class PersonObsDaoImplIT extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	PersonObsDaoImpl personObsDao;
	
	@Test
	public void shouldRetrievePatientObs() throws Exception {
		executeDataSet("apiTestData.xml");
		assertEquals(3, personObsDao.getObsByPerson("86526ed5-3c11-11de-a0ba-001e378eb67a").size());
	}

    @Test
	public void shouldRetrieveNumericalConceptsForPatient() throws Exception {
		executeDataSet("apiTestData.xml");
		assertEquals(3, personObsDao.getNumericConceptsForPerson("86526ed5-3c11-11de-a0ba-001e378eb67a").size());
	}
}
