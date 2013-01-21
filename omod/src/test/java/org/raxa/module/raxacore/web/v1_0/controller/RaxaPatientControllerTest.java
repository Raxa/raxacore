/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.raxa.module.raxacore.web.v1_0.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author joman
 */
public class RaxaPatientControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private MockHttpServletRequest request = null;
	
	private MockHttpServletResponse response = null;
	
	private RaxaPatientController controller = null;
	
	@Before
	public void before() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.controller = new RaxaPatientController();
	}
	
	/**
	 * Test of createNewPatient method, of class RaxaPatientController.
	 */
	@Test
	public void testCreateNewPatient() throws Exception {
		String json = "{\"names\": [{\"givenName\":\"john\",\"familyName\":\"James\"}],\"gender\":\"M\", \"age\":23 }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		SimpleObject patient = (SimpleObject) (controller.createNewPatient(post, request, response));
		System.out.println(patient);
		System.out.println(patient.get("uuid"));
		Patient p = Context.getPatientService().getPatientByUuid(patient.get("uuid").toString());
		Assert.assertNotNull(p);
		Assert.assertEquals("James", p.getFamilyName());
		Assert.assertTrue(p.getAge() == 23);
	}
}
