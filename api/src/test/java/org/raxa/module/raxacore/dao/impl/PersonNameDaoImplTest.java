package org.raxa.module.raxacore.dao.impl;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class PersonNameDaoImplTest extends BaseModuleContextSensitiveTest {
	
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
}
