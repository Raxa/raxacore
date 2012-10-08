/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.raxa.module.raxacore.web.v1_0.controller;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.DrugGroupService;
import org.raxa.module.raxacore.DrugInfo;
import org.raxa.module.raxacore.DrugInfoService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author joman
 */
public class DrugInfoControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private MockHttpServletRequest request = null;
	
	private MockHttpServletResponse response = null;
	
	private DrugInfoController controller = null;
	
	private DrugInfoService service = null;
	
	@Before
	public void before() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.controller = new DrugInfoController();
		this.service = Context.getService(DrugInfoService.class);
	}
	
	/**
	 * Test of createNewDrugInfo method, of class DrugInfoController.
	 */
	@Test
	public void createNewDrugInfo_shouldSaveANewDrugInfo() throws Exception {
		int before = service.getAllDrugInfo(true).size();
		String json = "{ \"name\":\"Test DrugInfo\",\"description\":\"Test Drug Group\", \"drug\":\"3cfcf118-931c-46f7-8ff6-7b876f0d4202\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object drugInfo = controller.createNewDrugInfo(post, request, response);
		Assert.assertEquals(before + 1, service.getAllDrugInfo(false).size());
	}
	
	/**
	 * Test of updateDrugInfo method, of class DrugInfoController.
	 */
	@Test
	public void testUpdateDrugInfo_shouldUpdateDrugInfo() throws Exception {
		int before = service.getAllDrugInfo(false).size();
		String json = "{ \"name\":\"NameChange\",\"description\":\"Test Drug Info\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.updateDrugInfo(getUuid(), post, request, response);
		Assert.assertEquals(before, service.getAllDrugInfo(false).size());
		String result = controller.getAllDrugInfoByUuid(getUuid(), request);
		SimpleObject updatedDrugGroup = SimpleObject.parseJson(result);
		Assert.assertEquals(getUuid(), updatedDrugGroup.get("uuid"));
		Assert.assertEquals("NameChange", updatedDrugGroup.get("name"));
	}
	
	/**
	 * Test of getAllDrugInfo method, of class DrugInfoController.
	 */
	@Test
	public void testGetAllDrugInfo() throws Exception {
		String allDrugInfos = controller.getAllDrugInfo(request, response);
		Assert.assertEquals(2, ((ArrayList) SimpleObject.parseJson(allDrugInfos).get("results")).size());
	}
	
	/**
	 * Test of getAllDrugInfoByUuid method, of class DrugInfoController.
	 */
	@Test
	public void testGetAllDrugInfoByUuid() throws Exception {
		String result = controller.getAllDrugInfoByUuid(getUuid(), request);
		SimpleObject drugGroup = SimpleObject.parseJson(result);
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), drugGroup.get("uuid"));
		Assert.assertEquals("TestDrugInfo1", drugGroup.get("name"));
		Assert.assertNull(drugGroup.get("auditInfo"));
	}
	
	/**
	 * Test of getAllDrugInfoByUuidFull method, of class DrugInfoController.
	 */
	@Test
	public void testGetAllDrugInfoByUuidFull() throws Exception {
		String result = controller.getAllDrugInfoByUuidFull(getUuid(), "full", request);
		SimpleObject drugGroup = SimpleObject.parseJson(result);
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), drugGroup.get("uuid"));
		Assert.assertEquals("TestDrugInfo1", drugGroup.get("name"));
		Assert.assertNotNull(drugGroup.get("auditInfo"));
	}
	
	/**
	 * Test of voidDrugInfo method, of class DrugInfoController.
	 */
	@Test
	public void testRetireDrugInfo() throws Exception {
		DrugInfo di1 = service.getDrugInfoByUuid(getUuid());
		Assert.assertFalse(di1.isRetired());
		controller.retireDrugInfo(getUuid(), "testing", request, response);
		DrugInfo di2 = service.getDrugInfoByUuid(getUuid());
		Assert.assertTrue(di2.isRetired());
		Assert.assertEquals("testing", di2.getRetireReason());
	}
	
	/**
	 * Test of purgeDrugInfo method, of class DrugInfoController.
	 */
	@Test
	public void testPurgeDrugInfo() throws Exception {
		DrugInfo di1 = service.getDrugInfoByUuid(getUuid());
		Assert.assertFalse(di1.getRetired());
		controller.purgeDrugInfo(getUuid(), request, response);
		DrugInfo di2 = service.getDrugInfoByUuid(getUuid());
		Assert.assertNull(di2);
	}
	
	public String getUuid() {
		return "68547121-1b70-465d-99ee-dddfd95e7d21";
	}
}
