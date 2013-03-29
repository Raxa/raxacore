package org.raxa.module.raxacore.dao.impl;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.dao.NameListDao;

import static junit.framework.Assert.assertEquals;

public class NameListDaoImplTest extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldRetrievePatientListIfLastNameExists() throws Exception {
		executeDataSet("apiTestData.xml");
		NameListDao dao = (NameListDao) applicationContext.getBean("patientListDao");
		assertEquals(2, dao.getLastNames("singh").size());
		assertEquals(2, dao.getLastNames("Singh").size());
		assertEquals(1, dao.getLastNames("Banka").size());
		assertEquals(3, dao.getLastNames("sin").size());
	}
}
