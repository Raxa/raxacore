package org.raxa.module.raxacore.dao.impl;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.model.ResultList;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class PersonAttributeDaoImplTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	PersonAttributeDaoImpl personAttributeDao;
	
	@Test
	public void shouldRetrieveUniqueCasteList() throws Exception {
		executeDataSet("apiTestData.xml");
		
		ResultList result = personAttributeDao.getUnique("caste", "caste");
		assertEquals(2, result.size());
		
		result = personAttributeDao.getUnique("caste", "some");
		assertEquals(1, result.size());
	}
}
