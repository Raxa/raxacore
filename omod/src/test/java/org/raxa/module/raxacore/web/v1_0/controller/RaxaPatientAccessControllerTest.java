package org.raxa.module.raxacore.web.v1_0.controller;

import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author joman
 */
public class RaxaPatientAccessControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private MockHttpServletRequest request = null;
	
	private MockHttpServletResponse response = null;
	
	private RaxaPatientAccessController controller = null;
	
	private PersonService service = null;
	
	@Before
	public void before() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.controller = new RaxaPatientAccessController();
		this.service = Context.getPersonService();
	}
	
	/**
	 * Test of createNewRelationship method, of class RaxaPatientAccessController.
	 */
	@Test
	public void testCreateNewRelationship() throws Exception {
		int before = service.getAllRelationships(true).size();
		String json = "{\"fromPerson\":\"a7e04421-525f-442f-8138-05b619d16def\",\"toPerson\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"relationshipType\":\"Doctor/Patient\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object drug = controller.createNewRelationship(post, request, response);
		Assert.assertEquals(before + 1, service.getAllRelationships(true).size());
	}
	
	/**
	 * Test of getRelationships method, of class RaxaPatientAccessController.
	 */
	@Test
	public void testGetRelationships() throws Exception {
		String allRelationships = controller.getAllRelationships(request, response);
		Assert.assertEquals(2, ((ArrayList) SimpleObject.parseJson(allRelationships).get("results")).size());
	}
	
	/**
	 * Test of getRelationshipByUuid method, of class RaxaPatientAccessController.
	 */
	@Test
	public void testGetRelationshipByUuid() throws Exception {
		String result = controller.getRelationshipByUuid("c18717dd-5d78-4a0e-84fc-ee62c5f0676a", request);
		Assert.assertNotNull(result);
	}
	
	/**
	 * Test of getAllRelationships method, of class RaxaPatientAccessController.
	 */
	@Test
	public void testGetAllRelationships() throws Exception {
		String allRelationships = controller.getAllRelationships(request, response);
		Assert.assertEquals(2, ((ArrayList) SimpleObject.parseJson(allRelationships).get("results")).size());
	}
}
