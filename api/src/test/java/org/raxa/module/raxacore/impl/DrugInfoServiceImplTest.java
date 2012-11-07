package org.raxa.module.raxacore.impl;

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
import org.openmrs.User;
import org.openmrs.Drug;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.DrugInfo;
import org.raxa.module.raxacore.DrugInfoService;

/*
 * Testing the methods in DrugInfoServiceImpl
 */
public class DrugInfoServiceImplTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private DrugInfoService s = null;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		s = Context.getService(DrugInfoService.class);
		//removing system developer role to test our privileges
		
	}
	
	/**
	 * Test of saveDrugInfo method, of class DrugInfoServiceImpl.
	 */
	@Test
	public void testSaveDrugInfoShouldUsePrivileges() throws Exception {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Drug drug = new Drug();
		drug.setId(3);
		DrugInfo drugInfo = new DrugInfo();
		drugInfo.setDrug(drug);
		drugInfo.setName("TestDrugInfo3");
		drugInfo.setDescription("Third Test DrugInfo");
		drugInfo.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugInfo.setDateCreated(new java.util.Date());
		drugInfo.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d30");
		drugInfo.setRetired(Boolean.FALSE);
		try {
			s.saveDrugInfo(drugInfo);
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for saveDrugInfo");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("Add Drug Info");
			Context.getUserContext().addProxyPrivilege("View Drug Info");
			Context.getUserContext().addProxyPrivilege("View Users");
			s.saveDrugInfo(drugInfo);
		}
	}
	
	/**
	 * Test of saveDrugInfo method, of class DrugInfoServiceImpl.
	 */
	@Test
	public void testSaveDrugInfoShouldSaveDrugInfo() throws Exception {
		Drug drug = new Drug();
		drug.setId(3);
		DrugInfo drugInfo = new DrugInfo();
		drugInfo.setDrug(drug);
		drugInfo.setName("TestDrugInfo3");
		drugInfo.setDescription("Third Test DrugInfo");
		drugInfo.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugInfo.setDateCreated(new java.util.Date());
		drugInfo.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d30");
		drugInfo.setRetired(Boolean.FALSE);
		s.saveDrugInfo(drugInfo);
		DrugInfo result = s.getDrugInfoByUuid("68547121-1b70-465c-99ee-c9dfd95e7d30");
		String name = result.getName();
		assertEquals(name, "TestDrugInfo3");
	}
	
	/**
	 * Test of getDrugInfo method, of class DrugInfoServiceImpl.
	 */
	@Test
	public void testGetDrugInfoShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Context.getUserContext().removeProxyPrivilege("View Drug Info");
		Integer drugInfoId = 1;
		DrugInfo result = null;
		try {
			result = s.getDrugInfo(drugInfoId);
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for getDrugInfo");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("View Drug Info");
			result = s.getDrugInfo(drugInfoId);
		}
	}
	
	/**
	 * Test of getDrugInfo method, of class DrugInfoServiceImpl.
	 */
	@Test
	public void testGetDrugInfoShouldReturnDrugInfo() {
		Integer drugInfoId = 1;
		DrugInfo result = s.getDrugInfo(drugInfoId);
		String name = result.getName();
		assertEquals("TestDrugInfo1", name);
	}
	
	/**
	 * Test of getDrugInfosByName method, of class DrugInfoServiceImpl.
	 */
	@Test
	public void testGetDrugInfosByNameShouldReturnDrugInfos() {
		String drugName = "nyquil";
		List<DrugInfo> results = s.getDrugInfosByDrugName(drugName);
		String name = results.get(0).getName();
		assertEquals("TestDrugInfo1", name);
	}
	
	/**
	 * Test of getDrugInfoByUuid method, of class DrugInfoServiceImpl.
	 */
	@Test
	public void testGetDrugInfoByUuidShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Context.getUserContext().removeProxyPrivilege("View Drug Info");
		String uuid = "68547121-1b70-465e-99ee-c9dfd95e7d30";
		String result = null;
		try {
			result = s.getDrugInfoByUuid(uuid).getName();
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for getDrugInfoByUuid");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("View Drug Info");
			result = s.getDrugInfoByUuid(uuid).getName();
		}
	}
	
	/**
	 * Test of getDrugInfoByUuid method, of class DrugInfoServiceImpl.
	 */
	@Test
	public void testGetDrugInfoByUuidShouldReturnDrugInfo() {
		String uuid = "68547121-1b70-465e-99ee-c9dfd95e7d30";
		String result = s.getDrugInfoByUuid(uuid).getName();
		assertEquals("TestDrugInfo2", result);
	}
	
	/**
	 * Test of updateDrugInfo method, of class DrugInfoServiceImpl.
	 */
	@Test
	public void testUpdateDrugInfoShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Context.getUserContext().addProxyPrivilege("View Drug Info");
		DrugInfo drugInfo = s.getDrugInfo(1);
		drugInfo.setName("NewNameDrugInfo");
		try {
			s.updateDrugInfo(drugInfo);
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for updateDrugInfo");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("Edit Drug Info");
			s.updateDrugInfo(drugInfo);
		}
	}
	
	/**
	 * Test of updateDrugInfo method, of class DrugInfoServiceImpl.
	 */
	@Test
	public void testUpdateDrugInfoShouldChangeDrugInfo() {
		DrugInfo drugInfo = s.getDrugInfo(1);
		drugInfo.setName("NewNameDrugInfo");
		s.updateDrugInfo(drugInfo);
		String name = s.getDrugInfo(1).getName();
		assertEquals(name, "NewNameDrugInfo");
	}
	
	/**
	 * Test of deleteDrugInfo method, of class DrugInfoServiceImpl.
	 */
	@Test
	public void testDeleteDrugInfoShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Drug drug = new Drug();
		drug.setId(3);
		DrugInfo drugInfo = new DrugInfo();
		drugInfo.setDrug(drug);
		drugInfo.setId(2);
		drugInfo.setName("TestDrugInfo2");
		drugInfo.setDescription("Second Test DrugInfo");
		drugInfo.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugInfo.setDateCreated(new java.util.Date());
		drugInfo.setUuid("68547121-1b70-465e-99ee-c9dfd95e7d30");
		drugInfo.setRetired(Boolean.FALSE);
		try {
			s.deleteDrugInfo(drugInfo);
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for deleteDrugInfo");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("Delete Drug Info");
			Context.getUserContext().addProxyPrivilege("View Drug Info");
			s.deleteDrugInfo(drugInfo);
		}
	}
	
	/**
	 * Test of deleteDrugInfo method, of class DrugInfoServiceImpl.
	 */
	@Test
	public void testDeleteDrugInfoShouldDeleteDrugInfo() {
		Drug drug = new Drug();
		drug.setId(3);
		DrugInfo drugInfo = new DrugInfo();
		drugInfo.setDrug(drug);
		drugInfo.setId(2);
		drugInfo.setName("TestDrugInfo2");
		drugInfo.setDescription("Second Test DrugInfo");
		drugInfo.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugInfo.setDateCreated(new java.util.Date());
		drugInfo.setUuid("68547121-1b70-465e-99ee-c9dfd95e7d30");
		drugInfo.setRetired(Boolean.FALSE);
		s.deleteDrugInfo(drugInfo);
		DrugInfo result = s.getDrugInfo(2);
		assertEquals(null, result);
	}
	
	/**
	 * Test of getAllDrugInfo method, of class DrugInfoService.
	 */
	@Test
	public void testGetAllDrugInfo_shouldReturnUnretiredDrugInfo() {
		List<DrugInfo> allDrugInfo = s.getAllDrugInfo(false);
		assertEquals(allDrugInfo.size(), 2);
	}
	
	/**
	 * Test of getAllDrugInfo method, of class HibernateDrugInfoDAO.
	 */
	@Test
	public void testGetAllDrugInfo_shouldReturnAllDrugInfoIncludingRetired() {
		List<DrugInfo> allDrugInfo = s.getAllDrugInfo(true);
		assertEquals(allDrugInfo.size(), 3);
	}
}
