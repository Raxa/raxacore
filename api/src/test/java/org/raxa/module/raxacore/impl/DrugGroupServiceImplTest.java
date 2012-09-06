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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.DrugGroupService;

/*
 * Testing the methods in DrugGroupServiceImpl
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
		drugGroup.setName("TestDrugGroup3");
		drugGroup.setDescription("Third Test Drug Group");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		try {
			s.saveDrugGroup(drugGroup);
			// if we don't throw exception fail - no privileges required!
			fail("No privileges required for saveDrugGroup");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("Add Drug Groups");
			Context.getUserContext().addProxyPrivilege("View Drug Groups");
			Context.getUserContext().addProxyPrivilege("View Users");
			s.saveDrugGroup(drugGroup);
		}
	}
	
	@Test
	public void testSaveDrugGroupShouldSaveDrugGroup() throws Exception {
		DrugGroup drugGroup = new DrugGroup();
		Set<Drug> drugs = new HashSet<Drug>();
		Drug drug1 = new Drug();
		Drug drug2 = new Drug();
		drug1.setId(1);
		drug1.setConcept(new Concept(792));
		drug2.setId(2);
		drug2.setConcept(new Concept(792));
		drugs.add(drug1);
		drugs.add(drug2);
		//NOTE: never set Id, will be generated automatically (when saving)
		drugGroup.setName("TestDrugGroup3");
		drugGroup.setDescription("Third Test Drug Group");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		drugGroup.setDrugs(drugs);
		s.saveDrugGroup(drugGroup);
		List<DrugGroup> result = s.getDrugGroupByName("TestDrugGroup3");
		String name = result.get(0).getName();
		Set<Drug> resDrugs = result.get(0).getDrugs();
		assertEquals(name, "TestDrugGroup3");
		assertEquals(resDrugs.contains(drug1), true);
		assertEquals(resDrugs.contains(drug2), true);
	}
	
	@Test
	public void testGetDrugGroupShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Context.getUserContext().removeProxyPrivilege("View Drug Groups");
		Integer drugGroupId = 1;
		DrugGroup result = null;
		try {
			result = s.getDrugGroup(drugGroupId);
			// if we don't throw exception fail - no privileges required!
			fail("No privileges required for getDrugGroup");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("View Drug Groups");
			result = s.getDrugGroup(drugGroupId);
		}
	}
	
	@Test
	public void testGetDrugGroupShouldReturnDrugGroup() {
		Integer DrugGroupId = 1;
		DrugGroup result = s.getDrugGroup(DrugGroupId);
		String name = result.getName();
		assertEquals("TestDrugGroup1", name);
	}
	
	@Test
	public void testGetDrugGroupByNameShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Context.getUserContext().removeProxyPrivilege("View Drug Groups");
		String name = "TestDrugGroup1";
		String result = null;
		try {
			result = s.getDrugGroupByName(name).get(0).getName();
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for getDrugGroupByName");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("View Drug Groups");
			result = s.getDrugGroupByName(name).get(0).getName();
		}
	}
	
	@Test
	public void testGetDrugGroupByNameShouldReturnDrugGroup() {
		String name = "TestDrugGroup1";
		String result = s.getDrugGroupByName(name).get(0).getName();
		assertEquals(name, result);
	}
	
	@Test
	public void testGetDrugGroupByUuidShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Context.getUserContext().removeProxyPrivilege("View Drug Groups");
		String uuid = "68547121-1b70-465e-99ee-c9dfd95e7d30";
		String result = null;
		try {
			result = s.getDrugGroupByUuid(uuid).getName();
			// if we don't throw exception fail - no privileges required!
			fail("No privileges required for getDrugGroupByUuid");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("View Drug Groups");
			result = s.getDrugGroupByUuid(uuid).getName();
		}
	}
	
	@Test
	public void testGetDrugGroupByUuidShouldReturnDrugGroup() {
		String uuid = "68547121-1b70-465e-99ee-c9dfd95e7d30";
		String result = s.getDrugGroupByUuid(uuid).getName();
		assertEquals("TestDrugGroup2", result);
	}
	
	@Test
	public void testUpdateDrugGroupShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		Context.getUserContext().addProxyPrivilege("View Drug Groups");
		DrugGroup drugGroup = s.getDrugGroup(1);
		drugGroup.setName("NewNameDrugGroup");
		try {
			s.updateDrugGroup(drugGroup);
			// if we don't throw exception fail - no privileges required!
			fail("No privileges required for updateDrugGroup");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("Edit Drug Groups");
			s.updateDrugGroup(drugGroup);
		}
	}
	
	@Test
	public void testUpdateDrugGroupShouldChangeDrugGroup() {
		DrugGroup drugGroup = s.getDrugGroup(1);
		drugGroup.setName("NewNameDrugGroup");
		s.updateDrugGroup(drugGroup);
		String name = s.getDrugGroup(1).getName();
		assertEquals(name, "NewNameDrugGroup");
	}
	
	@Test
	public void testDeleteDrugGroupShouldUsePrivileges() {
		Context.getUserContext().getAuthenticatedUser().removeRole(Context.getUserService().getRole("System Developer"));
		DrugGroup drugGroup = new DrugGroup();
		drugGroup.setId(2);
		drugGroup.setName("TestDrugGroup2");
		drugGroup.setDescription("Second Test Drug Group");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465e-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		try {
			s.deleteDrugGroup(drugGroup);
			//if we don't throw exception fail - no privileges required!
			fail("No privileges required for deleteDrugGroup");
		}
		catch (APIAuthenticationException e) {
			Context.getUserContext().addProxyPrivilege("Delete Drug Groups");
			Context.getUserContext().addProxyPrivilege("View Drug Groups");
			s.deleteDrugGroup(drugGroup);
		}
	}
	
	@Test
	public void testDeleteDrugGroupShouldDeleteDrugGroup() {
		DrugGroup drugGroup = new DrugGroup();
		Set<Drug> drugs = new HashSet<Drug>();
		Drug drug1 = new Drug();
		Drug drug2 = new Drug();
		drug1.setId(1);
		drug1.setConcept(new Concept(792));
		drug1.setDateCreated(new Date());
		drug1.setCreator(Context.getUserContext().getAuthenticatedUser());
		drug2.setId(2);
		drug2.setConcept(new Concept(792));
		drug2.setDateCreated(new Date());
		drug2.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugs.add(drug1);
		drugs.add(drug2);
		drugGroup.setId(2);
		drugGroup.setName("TestDrugGroup2");
		drugGroup.setDescription("Second Test Drug Group");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465e-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		drugGroup.setDrugs(drugs);
		s.deleteDrugGroup(drugGroup);
		DrugGroup result = s.getDrugGroup(2);
		assertEquals(null, result);
	}
}
