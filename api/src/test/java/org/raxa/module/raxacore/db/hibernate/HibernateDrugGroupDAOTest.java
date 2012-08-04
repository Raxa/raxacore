package org.raxa.module.raxacore.db.hibernate;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.db.DrugGroupDAO;

public class HibernateDrugGroupDAOTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private DrugGroupDAO dao = null;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		dao = (HibernateDrugGroupDAO) applicationContext
		        .getBean("org.raxa.module.raxacore.db.hibernate.HibernateDrugGroupDAO");
	}
	
	@Test
	public void testSaveDrugGroup() {
		DrugGroup drugGroup = new DrugGroup();
		drugGroup.setName("TestList3");
		drugGroup.setDescription("Third Test List");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		drugGroup.setSearchQuery("test Query");
		dao.saveDrugGroup(drugGroup);
		List<DrugGroup> result = dao.getDrugGroupByName("TestList3");
		String name = result.get(0).getName();
		assertEquals(name, "TestList3");
	}
	
	@Test
	public void testDeleteDrugGroup() {
		DrugGroup drugGroup = new DrugGroup();
		drugGroup.setId(2);
		drugGroup.setName("TestList2");
		drugGroup.setDescription("Second Test List");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465e-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		drugGroup.setSearchQuery("");
		dao.deleteDrugGroup(drugGroup);
		DrugGroup result = dao.getDrugGroup(2);
		assertEquals(null, result);
	}
	
	@Test
	public void testGetDrugGroup() {
		Integer drugGroupId = 1;
		DrugGroup result = dao.getDrugGroup(drugGroupId);
		String name = result.getName();
		assertEquals("TestList1", name);
	}
	
	@Test
	public void testGetDrugGroupByUuid() {
		String uuid = "68547121-1b70-465e-99ee-c9dfd95e7d30";
		String result = dao.getDrugGroupByUuid(uuid).getName();
		assertEquals("TestList2", result);
	}
	
	@Test
	public void testGetDrugGroupByName() {
		String name = "TestList1";
		String result = dao.getDrugGroupByName(name).get(0).getName();
		assertEquals(name, result);
	}
	
	@Test
	public void testUpdateDrugGroup() {
		String nameSet = "NewDrugGroupName";
		String nameRetrieved;
		DrugGroup drugGroup = dao.getDrugGroup(1);
		drugGroup.setName(nameSet);
		dao.updateDrugGroup(drugGroup);
		nameRetrieved = dao.getDrugGroup(1).getName();
		assertEquals(nameSet, nameRetrieved);
	}
}
