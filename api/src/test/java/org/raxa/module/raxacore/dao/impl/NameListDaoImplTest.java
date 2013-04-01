package org.raxa.module.raxacore.dao.impl;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class NameListDaoImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	NameListDaoImpl nameListDao;

	@Test
	public void shouldRetrievePatientListIfLastNameExists() throws Exception {
		executeDataSet("apiTestData.xml");
		assertEquals(2, nameListDao.getLastNames("singh").size());
		assertEquals(2, nameListDao.getLastNames("Singh").size());
		assertEquals(1, nameListDao.getLastNames("Banka").size());
		assertEquals(3, nameListDao.getLastNames("sin").size());
	}
}
