package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.model.ResultList;
import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class PersonAttributeDaoImplTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	PersonAttributeDaoImpl personAttributeDao;
	
	@Test
	public void shouldRetrieveUniqueCasteList() throws Exception {
        assertEquals(0, personAttributeDao.getUnique("caste", "caste").size());

        executeDataSet("apiTestData.xml");
		
		ResultList result = personAttributeDao.getUnique("caste", "caste");
		assertEquals(2, result.size());
	}
	
	@Test
	public void shouldRetrieveOnly20Results() throws Exception {
		executeDataSet("apiTestData.xml");
		
		ResultList result = personAttributeDao.getUnique("caste", "test");
		assertTrue(result.size() <= 20);
	}
}
