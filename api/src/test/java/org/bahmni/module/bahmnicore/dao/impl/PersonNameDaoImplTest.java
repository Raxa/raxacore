package org.bahmni.module.bahmnicore.dao.impl;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class PersonNameDaoImplTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	PersonNameDaoImpl personNameDao;
	
	@Test
	public void shouldRetrievePatientListIfLastNameExists() throws Exception {
		executeDataSet("apiTestData.xml");
		String key = "familyName";
		assertEquals(2, personNameDao.getUnique(key, "singh").size());
		assertEquals(2, personNameDao.getUnique(key, "Singh").size());
		assertEquals(1, personNameDao.getUnique(key, "Banka").size());
		assertEquals(3, personNameDao.getUnique(key, "sin").size());
	}
	
	@Test
	public void shouldReturnMaxOf20Results() throws Exception {
		executeDataSet("apiTestData.xml");
		String key = "familyName";
		assertTrue(personNameDao.getUnique(key, "test").size() <= 20);
	}
}
