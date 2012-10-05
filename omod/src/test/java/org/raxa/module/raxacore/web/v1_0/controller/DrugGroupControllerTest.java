package org.raxa.module.raxacore.web.v1_0.controller;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.DrugGroupService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class DrugGroupControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private MockHttpServletRequest request = null;
	
	private MockHttpServletResponse response = null;
	
	private DrugGroupController controller = null;
	
	private DrugGroupService service = null;
	
	@Before
	public void before() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.controller = new DrugGroupController();
		this.service = Context.getService(DrugGroupService.class);
	}
	
	/**
	 * @see DrugGroupController#retireDrugGroup(String,String,HttpServletRequest, HttpServletResponse)
	 * @verifies void a location attribute type
	 */
	@Test
	public void retireDrugGroup_shouldRetireADrugGroup() throws Exception {
		DrugGroup dg1 = service.getDrugGroupByUuid(getUuid());
		Assert.assertFalse(dg1.isRetired());
		controller.retireDrugGroup(getUuid(), "testing", request, response);
		DrugGroup dg2 = service.getDrugGroupByUuid(getUuid());
		Assert.assertTrue(dg2.isRetired());
		Assert.assertEquals("testing", dg2.getRetireReason());
	}
	
	/**
	 * @see DrugGroupController#updateDrugGroup(String, SimpleObject, HttpServletRequest, HttpServletResponse)
	 * @verifies a new patient list is created
	 */
	@Test
	public void updateDrugGroup_shouldSaveANewDrugGroup() throws Exception {
		int before = service.getAllDrugGroup(false).size();
		String json = "{ \"name\":\"Test DrugGroup\",\"description\":\"Test Drug Group\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.updateDrugGroup(getUuid(), post, request, response);
		Assert.assertEquals(before, service.getAllDrugGroup(false).size());
		String result = controller.getAllDrugGroupByUuid(getUuid(), request);
		SimpleObject updatedDrugGroup = SimpleObject.parseJson(result);
		Util.log("Updated Drug Group", updatedDrugGroup);
		Assert.assertEquals(getUuid(), updatedDrugGroup.get("uuid"));
		Assert.assertEquals("Test DrugGroup", updatedDrugGroup.get("name"));
	}
	
	/**
	 * @see DrugGroupController#createNewDrugGroup(SimpleObject, HttpServletRequest, HttpServletResponse)
	 * @verifies a new patient list is created
	 */
	@Test
	public void createNewDrugGroup_shouldSaveANewDrugGroup() throws Exception {
		int before = service.getAllDrugGroup(false).size();
		String json = "{ \"name\":\"Test DrugGroup\",\"description\":\"Test Drug Group\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object drugGroup = controller.createNewDrugGroup(post, request, response);
		Util.log("Created Patient List", drugGroup);
		Assert.assertEquals(before + 1, service.getAllDrugGroup(false).size());
	}
	
	/**
	 * @see DrugGroupController#getDrugGroupsByName(String,HttpServletRequest)
	 * @verifies return no results because no matching druggroup name
	 */
	@Test
	public void getDrugGroupsByName_shouldReturnNoResultsIfThereAreNoMatchingDrugGroup() throws Exception {
		String results = controller.getDrugGroupsByName("zzzznotype", request);
		Assert.assertEquals(0, ((ArrayList) SimpleObject.parseJson(results).get("results")).size());
	}
	
	/**
	 * @see DrugGroupController#getDrugGroupsByName(String,HttpServletRequest)
	 * @verifies find matching patient list
	 */
	@Test
	public void getDrugGroupsByName_shouldFindMatchingDrugGroups() throws Exception {
		String results = controller.getDrugGroupsByName("TestDrugGroup2", request);
		LinkedHashMap drugGroup = (LinkedHashMap) ((ArrayList) SimpleObject.parseJson(results).get("results")).get(0);
		Util.log("Found DrugGroup(s)", drugGroup);
		Assert.assertEquals("68547121-1b70-465e-99ee-c9dfd95e7d30", drugGroup.get("uuid"));
		Assert.assertEquals("TestDrugGroup2", drugGroup.get("name"));
		Assert.assertNull(drugGroup.get("auditInfo"));
	}
	
	/**
	 * @see DrugGroupController#getAllDrugGroupByUuidFull(String, String, HttpServletRequest)
	 * @verifies get the full representation of a patient list by its uuid
	 */
	@Test
	public void getDrugGroupByUuidFull_shouldGetAFullRepresentationOfADrugGroup() throws Exception {
		String result = controller.getAllDrugGroupByUuidFull(getUuid(), "full", request);
		SimpleObject drugGroup = SimpleObject.parseJson(result);
		Assert.assertNotNull(result);
		Util.log("DrugGroup fetched (full)", result);
		Assert.assertEquals("68547121-1b70-465d-99ee-c9dfd95e7d30", drugGroup.get("uuid"));
		Assert.assertEquals("TestDrugGroup1", drugGroup.get("name"));
		Assert.assertNotNull(drugGroup.get("auditInfo"));
	}
	
	/**
	 * @see DrugGroupController#getAllDrugGroupByUuid(String, HttpServletRequest)
	 * @verifies get a default representation of a patient list by its uuid
	 */
	@Test
	public void getDrugGroupByUuid_shouldGetADefaultRepresentationOfADrugGroup() throws Exception {
		String result = controller.getAllDrugGroupByUuid(getUuid(), request);
		SimpleObject drugGroup = SimpleObject.parseJson(result);
		Assert.assertNotNull(result);
		Util.log("DrugGroup fetched (default)", result);
		Assert.assertEquals("68547121-1b70-465d-99ee-c9dfd95e7d30", drugGroup.get("uuid"));
		Assert.assertEquals("TestDrugGroup1", drugGroup.get("name"));
		Assert.assertNull(drugGroup.get("auditInfo"));
	}
	
	/**
	 * @see DrugGroupController#getAllDrugGroups(HttpServletRequest, HttpServletResponse)
	 * @verifies get all the DrugGroup in the system
	 */
	@Test
	public void shouldGetAll() throws Exception {
		String allDrugGroups = controller.getAllDrugGroups(request, response);
		Util.log("All Drug Groups", allDrugGroups);
		Assert.assertEquals(2, ((ArrayList) SimpleObject.parseJson(allDrugGroups).get("results")).size());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	public String getUuid() {
		return "68547121-1b70-465d-99ee-c9dfd95e7d30";
	}
}
