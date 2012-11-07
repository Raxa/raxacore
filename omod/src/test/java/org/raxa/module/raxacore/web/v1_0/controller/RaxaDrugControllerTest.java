/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.raxa.module.raxacore.web.v1_0.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.DrugInfo;
import org.raxa.module.raxacore.DrugInfoService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author joman
 */
public class RaxaDrugControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private MockHttpServletRequest request = null;
	
	private MockHttpServletResponse response = null;
	
	private RaxaDrugController controller = null;
	
	private ConceptService service = null;
	
	@Before
	public void before() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.controller = new RaxaDrugController();
		this.service = Context.getConceptService();
	}
	
	/**
	 * Test of createNewDrug method, of class RaxaDrugController.
	 */
	@Test
	public void createNewDrug_shouldCreateNewDrug() throws Exception {
		int before = service.getAllDrugs(true).size();
		String json = "{\"concept\":\"0cbe2ed3-cd5f-4f46-9459-26127c9265ab\",\"name\":\"New Drug name\",\"dosageForm\":\"0cbe2ed3-cd5f-4f46-9459-26127c9265ab\",\"minimumDailyDose\":\"10\",\"maximumDailyDose\":\"100\",\"units\":\"mg\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object drug = controller.createNewDrug(post, request, response);
		Assert.assertEquals(before + 1, service.getAllDrugs(true).size());
	}
	
	/**
	 * Test of createNewDrug method, of class RaxaDrugController.
	 */
	@Test
	public void createNewDrug_shouldCreateNewDrugInfo() throws Exception {
		String json = "{\"concept\":\"0cbe2ed3-cd5f-4f46-9459-26127c9265ab\",\"name\":\"New Drug name\",\"dosageForm\":\"0cbe2ed3-cd5f-4f46-9459-26127c9265ab\",\"minimumDailyDose\":\"10\",\"maximumDailyDose\":\"100\",\"units\":\"mg\", \"drugInfo\":{ \"name\":\"Inner DrugInfo\",\"description\":\"Test Drug Group\", \"drug\":\"3cfcf118-931c-46f7-8ff6-7b876f0d4202\"} }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object drug = controller.createNewDrug(post, request, response);
		List<DrugInfo> drugInfos = Context.getService(DrugInfoService.class).getAllDrugInfo(true);
		Boolean foundNewDrugInfo = false;
		for (int i = 0; i < drugInfos.size(); i++) {
			DrugInfo di = drugInfos.get(i);
			if (di.getName().equals("Inner DrugInfo")) {
				foundNewDrugInfo = true;
			}
		}
		Assert.assertEquals(true, foundNewDrugInfo);
	}
	
	/**
	 * Test of updateDrug method, of class RaxaDrugController.
	 */
	@Test
	public void updateDrug_shouldUpdateADrug() throws Exception {
		int before = service.getAllDrugs(true).size();
		String json = "{ \"name\":\"Changed name\",\"description\":\"Test\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.updateDrug(getUuid(), post, request, response);
		Assert.assertEquals(before, service.getAllDrugs(true).size());
		String results = controller.getAllDrugs(request, response);
		LinkedHashMap updatedDrugs = (LinkedHashMap) ((ArrayList) SimpleObject.parseJson(results).get("results")).get(0);
		Assert.assertEquals(getUuid(), updatedDrugs.get("uuid"));
		Assert.assertEquals("Changed name", updatedDrugs.get("name"));
	}
	
	/**
	 * Test of getAllDrug method, of class RaxaDrugController.
	 */
	@Test
	public void getAllDrugs_shouldGetDrugs() throws Exception {
		String allDrugs = controller.getAllDrugs(request, response);
		Assert.assertEquals(2, ((ArrayList) SimpleObject.parseJson(allDrugs).get("results")).size());
	}
	
	/**
	 * Test of getDrugByUuid method, of class RaxaDrugController.
	 */
	@Test
	public void getDrugByUuid_shouldGetDrug() throws Exception {
		String result = controller.getDrugByUuid(getUuid(), request);
		SimpleObject drug = SimpleObject.parseJson(result);
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), drug.get("uuid"));
		Assert.assertEquals("Triomune-30", drug.get("name"));
	}
	
	/**
	 * Test of getAllDrugByUuidFull method, of class RaxaDrugController.
	 */
	@Test
	public void getDrugByUuidFull_shouldGetFullDrug() throws Exception {
		String result = controller.getDrugByUuidFull(getUuid(), "full", request);
		SimpleObject drug = SimpleObject.parseJson(result);
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), drug.get("uuid"));
		Assert.assertEquals(false, drug.get("retired"));
	}
	
	/**
	 * Test of retireDrug method, of class RaxaDrugController.
	 */
	@Test
	public void testRetireDrug() throws Exception {
		Drug drug = service.getDrugByUuid(getUuid());
		Assert.assertFalse(drug.isRetired());
		controller.retireDrug(getUuid(), "testing", request, response);
		Drug drug2 = service.getDrugByUuid(getUuid());
		Assert.assertTrue(drug2.isRetired());
		Assert.assertEquals("testing", drug2.getRetireReason());
	}
	
	private String getUuid() {
		return "3cfcf118-931c-46f7-8ff6-7b876f0d4202";
	}
}
