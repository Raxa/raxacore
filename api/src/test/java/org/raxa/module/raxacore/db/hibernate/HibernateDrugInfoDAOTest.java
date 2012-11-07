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
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.DrugInfo;
import org.raxa.module.raxacore.db.DrugInfoDAO;

public class HibernateDrugInfoDAOTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private DrugInfoDAO dao = null;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		dao = (HibernateDrugInfoDAO) applicationContext
		        .getBean("org.raxa.module.raxacore.db.hibernate.HibernateDrugInfoDAO");
	}
	
	/**
	 * Test of saveDrugInfo method, of class HibernateDrugInfoDAO.
	 */
	@Test
	public void testSaveDrugInfo() {
		DrugInfo drugInfo = new DrugInfo();
		Drug drug = new Drug();
		drug.setId(3);
		drugInfo.setDrug(drug);
		drugInfo.setDrugId(drug.getId());
		drugInfo.setName("TestDrugInfo3");
		drugInfo.setDescription("Third Test DrugInfo");
		drugInfo.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugInfo.setDateCreated(new java.util.Date());
		drugInfo.setUuid("68547121-1b70-465c-99ef-c9dfd95e7d30");
		drugInfo.setRetired(Boolean.FALSE);
		dao.saveDrugInfo(drugInfo);
		DrugInfo result = dao.getDrugInfoByUuid("68547121-1b70-465c-99ef-c9dfd95e7d30");
		String name = result.getName();
		assertEquals(name, "TestDrugInfo3");
	}
	
	/**
	 * Test of deleteDrugInfo method, of class HibernateDrugInfoDAO.
	 */
	@Test
	public void testDeleteDrugInfo() {
		DrugInfo drugInfo = new DrugInfo();
		drugInfo.setId(2);
		Drug drug = new Drug();
		drug.setId(5);
		drug.setDrugId(5);
		drugInfo.setDrugId(5);
		drugInfo.setDrug(drug);
		drugInfo.setName("TestDrugInfo2");
		drugInfo.setDescription("Second Test DrugInfo");
		drugInfo.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugInfo.setDateCreated(new java.util.Date());
		drugInfo.setUuid("68547121-1b70-465e-99ee-c9dfd95e7d30");
		drugInfo.setRetired(Boolean.FALSE);
		dao.deleteDrugInfo(drugInfo);
		DrugInfo result = dao.getDrugInfo(2);
		assertEquals(null, result);
	}
	
	/**
	 * Test of getDrugInfo method, of class HibernateDrugInfoDAO.
	 */
	@Test
	public void testGetDrugInfo() {
		Integer drugInfoId = 1;
		DrugInfo result = dao.getDrugInfo(drugInfoId);
		String name = result.getName();
		assertEquals("TestDrugInfo1", name);
	}
	
	/**
	 * Test of getDrugInfoByUuid method, of class HibernateDrugInfoDAO.
	 */
	@Test
	public void testGetDrugInfoByUuid() {
		String uuid = "68547121-1b70-465e-99ee-c9dfd95e7d30";
		String result = dao.getDrugInfoByUuid(uuid).getName();
		assertEquals("TestDrugInfo2", result);
	}
	
	/**
	 * Test of getDrugInfoByName method, of class HibernateDrugInfoDAO.
	 */
	@Test
	public void testGetDrugInfoByName() {
		String name = "TestDrugInfo1";
		String result = dao.getDrugInfoByName(name).get(0).getName();
		assertEquals(name, result);
	}
	
	/**
	 * Test of updateDrugInfo method, of class HibernateDrugInfoDAO.
	 */
	@Test
	public void testUpdateDrugInfo() {
		DrugInfo drugInfo = dao.getDrugInfo(1);
		drugInfo.setName("NewNameDrugInfo");
		dao.updateDrugInfo(drugInfo);
		String name = dao.getDrugInfo(1).getName();
		assertEquals(name, "NewNameDrugInfo");
	}
	
	/**
	 * Test of getAllDrugInfo method, of class HibernateDrugInfoDAO.
	 */
	@Test
	public void testGetAllDrugInfo_shouldReturnUnretiredDrugInfo() {
		List<DrugInfo> allDrugInfo = dao.getAllDrugInfo(false);
		assertEquals(allDrugInfo.size(), 2);
	}
	
	/**
	 * Test of getAllDrugInfo method, of class HibernateDrugInfoDAO.
	 */
	@Test
	public void testGetAllDrugInfo_shouldReturnAllDrugInfoIncludingRetired() {
		List<DrugInfo> allDrugInfo = dao.getAllDrugInfo(true);
		assertEquals(allDrugInfo.size(), 3);
	}
}
