package org.raxa.module.raxacore.web.v1_0.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class RaxaEncounterControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private MockHttpServletRequest request = null;
	
	private MockHttpServletResponse response = null;
	
	private RaxaEncounterController controller = null;
	
	private EncounterService service = null;
	
	@Before
	public void before() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.controller = new RaxaEncounterController();
		this.service = Context.getService(EncounterService.class);
	}
	
	/**
	 * Test of createNewEncounter method, of class RaxaEncounterController.
	 */
	@Test
	public void testCreateNewEncounter() throws Exception {
		String json = "{ \"patient\":\"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\",\"encounterDatetime\":\"2013-01-07T12:40:20Z\", \"encounterType\": \"61ae96f4-6afe-4351-b6f8-cd4fc383cce1\", \"location\": \"9356400c-a5a2-4532-8f2b-2361b3446eb8\", \"obs\": [{\"person\":\"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\",\"concept\":\"92afda7c-78c9-47bd-a841-0de0817027d4\", \"obsDatetime\": \"2013-01-07T12:40:20Z\", \"value\": \"hello\", \"location\": \"9356400c-a5a2-4532-8f2b-2361b3446eb8\"}]}";
		
		//, \"obs\": [{\"person\":\"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\",\"concept\":\"b055abd8-a420-4a11-8b98-02ee170a7b54\", \"obsDatetime\": \"2013-01-07T12:40:20Z\", \"value\": \"500\", \"location\": \"9356400c-a5a2-4532-8f2b-2361b3446eb8\"}, {\"name\":\"Test inner Drug Inventory 2\",\"description\":\"Test drug inventory2\", \"drug\": \"05ec820a-d297-44e3-be6e-698531d9dd3f\", \"quantity\": 500, \"supplier\": \"test supplier\", \"expiryDate\":\"Sep 26, 2012 12:00:00 AM\"}]}
		
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.createNewEncounter(post, request, response);
		List<Encounter> encs = service.getEncountersByPatient(Context.getPatientService().getPatientByUuid(
		    "da7f524f-27ce-4bb2-86d6-6d1d05312bd5"));
		System.out.println(encs);
		//		Provider p = Context.getProviderService().getProviderByUuid("68547121-1b70-465e-99ee-c9df45jf9j32");
		//		List<DrugPurchaseOrder> dPOs = Context.getService(DrugPurchaseOrderService.class).getDrugPurchaseOrderByProvider(
		//		    p.getId());
		//		List<DrugInventory> dis = Context.getService(DrugInventoryService.class).getDrugInventoriesByLocation(2);
		//		Assert.assertNotNull(dis);
		//		Assert.assertEquals(2, dis.size());
		//		Assert.assertEquals("Test inner Drug Inventory", dis.get(0).getName());
		//		Assert.assertNotNull(dis.get(0).getExpiryDate());
		//		Assert.assertEquals(dis.get(1).getSupplier(), "test supplier");
		//		Assert.assertEquals(true, dPOs.get(0).isReceived());
	}
	
	/**
	 * Test of getEncounterByUuidFull method, of class RaxaEncounterController.
	 */
	@Test
	public void testGetEncounterByUuidFull() throws Exception {
		String result = controller.getEncounterByUuidFull("6519d653-393b-4118-9c83-a3715b82d4ac", request);
		SimpleObject encounter = SimpleObject.parseJson(result);
		System.out.println(result);
		Assert.assertNotNull(result);
		Assert.assertEquals("6519d653-393b-4118-9c83-a3715b82d4ac", encounter.get("uuid"));
	}
}
