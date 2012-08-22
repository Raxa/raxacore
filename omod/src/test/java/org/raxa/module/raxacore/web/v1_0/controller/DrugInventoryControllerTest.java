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
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.DrugInventory;
import org.raxa.module.raxacore.DrugInventoryService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class DrugInventoryControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private MockHttpServletRequest request = null;
	
	private MockHttpServletResponse response = null;
	
	private DrugInventoryController controller = null;
	
	private DrugInventoryService service = null;
	
	@Before
	public void before() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.controller = new DrugInventoryController();
		this.service = Context.getService(DrugInventoryService.class);
	}
	
	/**
	 * Test of saveDrugInventory method, of class DrugInventoryController.
	 */
	@Test
	public void testSaveDrugInventory() throws Exception {
		int before = service.getAllDrugInventories().size();
		String json = "{ \"name\":\"Test Drug Inventory\",\"description\":\"Test drug inventory\", \"drugId\": 2, \"quantity\": 500 }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.saveDrugInventory(post, request, response);
		int after = service.getAllDrugInventories().size();
		Assert.assertEquals(before + 1, after);
		
	}
	
	/**
	 * Test of getDrugInventoryByUuid method, of class DrugInventoryController.
	 */
	@Test
	public void testGetDrugInventoryByUuid() throws Exception {
		String uuid = "68547121-1b70-465c-99ee-c9dfd95e7d36";
		String result = controller.getDrugInventoryByUuid(uuid, request);
		SimpleObject dI = SimpleObject.parseJson(result);
		Assert.assertNotNull(result);
		assertEquals(uuid, dI.get("uuid"));
	}
	
	/**
	 * @see DrugInventoryController#updateDrugInventory(String, SimpleObject, HttpServletRequest, HttpServletResponse)
	 * @verifies updates a drug inv
	 */
	@Test
	public void updateDrugInventory_shouldUpdateDrugInventory() throws Exception {
		int before = service.getAllDrugInventories().size();
		String json = "{ \"name\":\"Updated DrugInv\",\"description\":\"Update\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.updateDrugInventory("68547121-1b70-465c-99ee-c9dfd95e7d36", post, request, response);
		String results = controller.getAllDrugInventories(request, response);
		//SimpleObject updatedRaxaAlert = SimpleObject.parseJson(results.substring(12, result.length() - 2));
		LinkedHashMap updatedRaxaAlert = (LinkedHashMap) ((ArrayList) SimpleObject.parseJson(results).get("results")).get(0);
		Assert.assertEquals("68547121-1b70-465c-99ee-c9dfd95e7d36", updatedRaxaAlert.get("uuid"));
		Assert.assertEquals("Updated DrugInv", updatedRaxaAlert.get("name"));
	}
	
	/**
	 * @see DrugInventoryController#getAllDrugInventories(HttpServletRequest, HttpServletResponse)
	 * @verifies get all the Drug Invs in the system
	 */
	@Test
	public void shouldGetAll() throws Exception {
		String allDIs = controller.getAllDrugInventories(request, response);
		Assert.assertEquals(1, ((ArrayList) SimpleObject.parseJson(allDIs).get("results")).size());
	}
	
	/**
	 * @see RaxaAlertController#searchByProviderRecipient(String, HttpServeletRequest, HttpServletResponse)
	 * @throws Exception
	 */
	@Test
	public void searchByLocation_shouldGetInventoriesByLocation() throws Exception {
		String results = controller.searchByLocation("dc5c1fcc-0459-4201-bf70-0b90535ba362", request);
		LinkedHashMap di = (LinkedHashMap) ((ArrayList) SimpleObject.parseJson(results).get("results")).get(0);
		Assert.assertEquals("Test drug inventory", di.get("name"));
	}
}
