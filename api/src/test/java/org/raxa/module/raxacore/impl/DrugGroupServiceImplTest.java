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
import org.junit.Test; // import org.openmrs.Encounter;
import org.openmrs.EncounterType; // import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.DrugGroupService;

/*
 * Testing the methods in PatientListServiceImpl
 */
public class DrugGroupServiceImplTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private DrugGroupService s = null;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		s = Context.getService(DrugGroupService.class);
		//removing system developer role to test our privileges
	}
	
	@Test
	public void testSaveDrugGroupShouldUsePrivileges() throws Exception {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		DrugGroup drugGroup = new DrugGroup();
		//NOTE: never set Id, will be generated automatically (when saving)
		drugGroup.setName("TestList3");
		drugGroup.setDescription("Third Test List");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		try {
			s.saveDrugGroup(drugGroup);
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for savePatientList");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("Add Patient Lists");
			Context.getUserContext().addProxyPrivilege("View Patient Lists");
			Context.getUserContext().addProxyPrivilege("View Users");
			s.saveDrugGroup(drugGroup);
		}
	}
	
	@Test
	public void testSaveDrugGroupShouldSaveDrugGroup() throws Exception {
		DrugGroup drugGroup = new DrugGroup();
		//NOTE: never set Id, will be generated automatically (when saving)
		drugGroup.setName("TestList3");
		drugGroup.setDescription("Third Test List");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		s.saveDrugGroup(drugGroup);
		List<DrugGroup> result = s.getDrugGroupByName("TestList3");
		String name = result.get(0).getName();
		assertEquals(name, "TestList3");
	}
	
	@Test
	public void testGetDrugGroupShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Context.getUserContext().removeProxyPrivilege("View Patient Lists");
		Integer drugGroupId = 1;
		DrugGroup result = null;
		try {
			result = s.getDrugGroup(drugGroupId);
			// if we don't throw exception fail - no privileges required!
			fail("No privileges required for getPatientList");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("View Patient Lists");
			result = s.getDrugGroup(drugGroupId);
		}
	}
	
	@Test
	public void testGetDrugGroupShouldReturnDrugGroup() {
		Integer patientListId = 1;
		DrugGroup result = s.getDrugGroup(patientListId);
		String name = result.getName();
		assertEquals("TestList1", name);
	}
	
	@Test
	public void testGetDrugGroupByNameShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Context.getUserContext().removeProxyPrivilege("View Patient Lists");
		String name = "TestList1";
		String result = null;
		try {
			result = s.getDrugGroupByName(name).get(0).getName();
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for getPatientListByName");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("View Patient Lists");
			result = s.getDrugGroupByName(name).get(0).getName();
		}
	}
	
	@Test
	public void testGetDrugGroupByNameShouldReturnDrugGroup() {
		String name = "TestList1";
		String result = s.getDrugGroupByName(name).get(0).getName();
		assertEquals(name, result);
	}
	
	@Test
	public void testGetDrugGroupByUuidShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Context.getUserContext().removeProxyPrivilege("View Patient Lists");
		String uuid = "68547121-1b70-465e-99ee-c9dfd95e7d30";
		String result = null;
		try {
			result = s.getDrugGroupByUuid(uuid).getName();
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for getPatientListByUuid");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("View Patient Lists");
			result = s.getDrugGroupByUuid(uuid).getName();
		}
	}
	
	@Test
	public void testGetPatientListByUuidShouldReturnPatientList() {
		String uuid = "68547121-1b70-465e-99ee-c9dfd95e7d30";
		String result = s.getDrugGroupByUuid(uuid).getName();
		assertEquals("TestList2", result);
	}
	
	@Test
	public void testUpdateDrugGroupShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Context.getUserContext().addProxyPrivilege("View Patient Lists");
		DrugGroup drugGroup = s.getDrugGroup(1);
		drugGroup.setName("NewNameList");
		try {
			s.updateDrugGroup(drugGroup);
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for updatePatientList");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("Edit Patient Lists");
			s.updateDrugGroup(drugGroup);
		}
	}
	
	@Test
	public void testUpdateDrugGroupShouldChangeDrugGroup() {
		DrugGroup drugGroup = s.getDrugGroup(1);
		drugGroup.setName("NewNameList");
		s.updateDrugGroup(drugGroup);
		String name = s.getDrugGroup(1).getName();
		assertEquals(name, "NewNameList");
	}
	
	@Test
	public void testDeleteDrugGroupShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		DrugGroup drugGroup = new DrugGroup();
		drugGroup.setId(2);
		drugGroup.setName("TestList2");
		drugGroup.setDescription("Second Test List");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465e-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		try {
			s.deleteDrugGroup(drugGroup);
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for deletePatientList");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("Delete Patient Lists");
			Context.getUserContext().addProxyPrivilege("View Patient Lists");
			s.deleteDrugGroup(drugGroup);
		}
	}
	
	@Test
	public void testDeleteDrugGroupShouldDeleteDrugGroup() {
		DrugGroup drugGroup = new DrugGroup();
		drugGroup.setId(2);
		drugGroup.setName("TestList2");
		drugGroup.setDescription("Second Test List");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465e-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		s.deleteDrugGroup(drugGroup);
		DrugGroup result = s.getDrugGroup(2);
		assertEquals(null, result);
	}
}
