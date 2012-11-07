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
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.DrugInventory;
import org.raxa.module.raxacore.DrugInventoryService;
import org.raxa.module.raxacore.DrugPurchaseOrder;
import org.raxa.module.raxacore.DrugPurchaseOrderService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class DrugPurchaseOrderControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private MockHttpServletRequest request = null;
	
	private MockHttpServletResponse response = null;
	
	private DrugPurchaseOrderController controller = null;
	
	private DrugPurchaseOrderService service = null;
	
	@Before
	public void before() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.controller = new DrugPurchaseOrderController();
		this.service = Context.getService(DrugPurchaseOrderService.class);
	}
	
	/**
	 * Test of createNewDrugPurchaseOrder method, of class DrugPurchaseOrderController.
	 */
	@Test
	public void testCreateNewDrugPurchaseOrder() throws Exception {
		int before = service.getAllDrugPurchaseOrders().size();
		String json = "{ \"name\":\"Test purchase order\",\"description\":\"Test purchase order\", \"provider\": \"68547121-1b70-465e-99ee-c9df45jf9j32\", \"received\": \"true\", \"inventories\": [{\"name\":\"Test inner Drug Inventory\",\"description\":\"Test drug inventory\", \"drug\": \"05ec820a-d297-44e3-be6e-698531d9dd3f\", \"quantity\": 500, \"location\": \"9356400c-a5a2-4532-8f2b-2361b3446eb8\", \"expiryDate\":\"Wed Sep 19 2012 00:00:00 GMT+0530 (India Standard Time)\"}, {\"name\":\"Test inner Drug Inventory 2\",\"description\":\"Test drug inventory2\", \"drug\": \"05ec820a-d297-44e3-be6e-698531d9dd3f\", \"quantity\": 500, \"supplier\": \"test supplier\", \"location\": \"9356400c-a5a2-4532-8f2b-2361b3446eb8\", \"expiryDate\":\"Sep 26, 2012 12:00:00 AM\"}]}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.createNewDrugPurchaseOrder(post, request, response);
		int after = service.getAllDrugPurchaseOrders().size();
		Provider p = Context.getProviderService().getProviderByUuid("68547121-1b70-465e-99ee-c9df45jf9j32");
		List<DrugPurchaseOrder> dPOs = Context.getService(DrugPurchaseOrderService.class).getDrugPurchaseOrderByProvider(
		    p.getId());
		List<DrugInventory> dis = Context.getService(DrugInventoryService.class).getDrugInventoriesByLocation(2);
		Assert.assertNotNull(dis);
		Assert.assertEquals(2, dis.size());
		Assert.assertEquals("Test inner Drug Inventory", dis.get(0).getName());
		Assert.assertNotNull(dis.get(0).getExpiryDate());
		Assert.assertEquals(dis.get(1).getSupplier(), "test supplier");
		Assert.assertEquals(true, dPOs.get(0).isReceived());
		Assert.assertEquals(before + 1, after);
	}
	
	/**
	 * Test of getDrugPuchaseOrderByUuid method, of class DrugPurchaseOrderController.
	 */
	@Test
	public void testGetDrugPuchaseOrderByUuid() throws Exception {
		String uuid = "68547121-1b70-465c-99ee-c9dfd95e7d41";
		String result = controller.getDrugPuchaseOrderByUuid(uuid, request);
		SimpleObject dI = SimpleObject.parseJson(result);
		Assert.assertNotNull(result);
		assertEquals(uuid, dI.get("uuid"));
	}
	
	/**
	 * @see DrugPurchaseOrderController#getAllDrugPurchaseOrders(HttpServletRequest, HttpServletResponse)
	 * @verifies get all the Drug Invs in the system
	 */
	@Test
	public void shouldGetAll() throws Exception {
		String allDIs = controller.getAllDrugPurchaseOrders(request, response);
		Assert.assertEquals(2, ((ArrayList) SimpleObject.parseJson(allDIs).get("results")).size());
	}
	
	/**
	 * @see DrugPurchaseOrderController#searchByStockLocation(String, HttpServeletRequest, HttpServletResponse)
	 * @throws Exception
	 */
	@Test
	public void searchByStockLocation_shouldGetPurchaseOrdersByStockLocation() throws Exception {
		String results = controller.searchByStockLocation("9356400c-a5a2-4532-8f2b-2361b3446eb8", request);
		LinkedHashMap di = (LinkedHashMap) ((ArrayList) SimpleObject.parseJson(results).get("results")).get(0);
		Assert.assertEquals("Test drug PO", di.get("name"));
	}
	
	/**
	 * @see DrugPurchaseOrderController#updateDrugPurchaseOrder(String, SimpleObject, HttpServletRequest, HttpServletResponse)
	 * @verifies a new patient list is created
	 */
	@Test
	public void updateDrugPurchaseOrder_shouldUpdateADrugPurchaseOrder() throws Exception {
		int before = service.getAllDrugPurchaseOrders().size();
		String json = "{ \"name\":\"Test DrugPurchaseOrder Change\",\"description\":\"Test Alert\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.updateDrugPurchaseOrder("68547121-1b70-465c-99ee-c9dfd95e7d41", post, request, response);
		Assert.assertEquals(before, service.getAllDrugPurchaseOrders().size());
		String results = controller.getAllDrugPurchaseOrders(request, response);
		LinkedHashMap updatedDrugPurchaseOrder = (LinkedHashMap) ((ArrayList) SimpleObject.parseJson(results).get("results"))
		        .get(0);
		Assert.assertEquals("Test DrugPurchaseOrder Change", updatedDrugPurchaseOrder.get("name"));
	}
	
}
