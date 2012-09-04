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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.RaxaAlertService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class RaxaAlertControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private MockHttpServletRequest request = null;
	
	private MockHttpServletResponse response = null;
	
	private RaxaAlertController controller = null;
	
	private RaxaAlertService service = null;
	
	@Before
	public void before() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.controller = new RaxaAlertController();
		this.service = Context.getService(RaxaAlertService.class);
	}
	
	/**
	 * @see RaxaAlertController#updateRaxaAlert(String, SimpleObject, HttpServletRequest, HttpServletResponse)
	 * @verifies a new patient list is created
	 */
	@Test
	public void updateRaxaAlert_shouldUpdateARaxaAlert() throws Exception {
		int before = service.getAllRaxaAlerts(true).size();
		String json = "{ \"name\":\"Test RaxaAlert\",\"description\":\"Test Alert\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.updateRaxaAlert(getUuid(), post, request, response);
		Assert.assertEquals(before, service.getAllRaxaAlerts(true).size());
		String results = controller.getAllRaxaAlerts(request, response);
		//SimpleObject updatedRaxaAlert = SimpleObject.parseJson(results.substring(12, result.length() - 2));
		LinkedHashMap updatedRaxaAlert = (LinkedHashMap) ((ArrayList) SimpleObject.parseJson(results).get("results")).get(0);
		Assert.assertEquals(getUuid(), updatedRaxaAlert.get("uuid"));
		Assert.assertEquals("Test RaxaAlert", updatedRaxaAlert.get("name"));
	}
	
	/**
	 * @see RaxaAlertController#createNewRaxaAlert(SimpleObject, HttpServletRequest, HttpServletResponse)
	 * @verifies a new patient list is created
	 */
	@Test
	public void createNewRaxaAlert_shouldSaveANewRaxaAlert() throws Exception {
		int before = service.getAllRaxaAlerts(true).size();
		String json = "{ \"name\":\"Test RaxaAlert\",\"description\":\"Test Alert\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object RaxaAlert = controller.createNewRaxaAlert(post, request, response);
		Assert.assertEquals(before + 1, service.getAllRaxaAlerts(true).size());
	}
	
	/**
	 * @see RaxaAlertController#getAllRaxaAlertByUuid(String, HttpServletRequest)
	 * @verifies get a default representation of a patient list by its uuid
	 */
	@Test
	public void getRaxaAlertByUuid_shouldGetADefaultRepresentationOfARaxaAlert() throws Exception {
		String result = controller.getRaxaAlertByUuid(getUuid(), request);
		SimpleObject RaxaAlert = SimpleObject.parseJson(result);
		Assert.assertNotNull(result);
		Assert.assertEquals("68547121-1b70-465e-99ee-c9dfd95e7d31", RaxaAlert.get("uuid"));
		Assert.assertEquals("TestAlert1", RaxaAlert.get("name"));
	}
	
	/**
	 * @see RaxaAlertController#getAllRaxaAlerts(HttpServletRequest, HttpServletResponse)
	 * @verifies get all the RaxaAlert in the system
	 */
	@Test
	public void shouldGetAll() throws Exception {
		String allRaxaAlerts = controller.getAllRaxaAlerts(request, response);
		Assert.assertEquals(2, ((ArrayList) SimpleObject.parseJson(allRaxaAlerts).get("results")).size());
	}
	
	/**
	 * @see RaxaAlertController#searchByProviderRecipient(String, HttpServeletRequest, HttpServletResponse)
	 * @throws Exception
	 */
	@Test
	public void searchByProviderRecipient_shouldGetAlertsByProvider() throws Exception {
		String results = controller.searchByProviderRecipient("68547121-1b70-465e-99ee-c9df45jf9j32", request);
		LinkedHashMap raxaAlert = (LinkedHashMap) ((ArrayList) SimpleObject.parseJson(results).get("results")).get(0);
		System.out.println(Context.getProviderService().getProvider(1).getUuid());
		Assert.assertEquals("TestAlert1", raxaAlert.get("name"));
	}
	
	@Test
	public void searchByToLocation_shouldGetAlertsByLocation() throws Exception {
		String results = controller.searchByToLocation("9356400c-a5a2-4532-8f2b-2361b3446eb8", request);
		LinkedHashMap raxaAlert = (LinkedHashMap) ((ArrayList) SimpleObject.parseJson(results).get("results")).get(0);
		Assert.assertEquals("TestAlert2", raxaAlert.get("name"));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	public String getUuid() {
		return "68547121-1b70-465e-99ee-c9dfd95e7d31";
	}
}
