/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.raxa.module.raxacore.web.v1_0.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.Assert;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author joman
 */
public class RaxaUserControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private MockHttpServletRequest request = null;
	
	private MockHttpServletResponse response = null;
	
	private RaxaUserController controller = null;
	
	private UserService service = null;
	
	@Before
	public void before() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.controller = new RaxaUserController();
		this.service = Context.getUserService();
	}
	
	/**
	 * Test of createNewUser method, of class RaxaUserController.
	 */
	@Test
	public void testCreateNewPatientUser() throws Exception {
		String json = "{\"firstName\":\"john\",\"lastName\":\"James\",\"gender\":\"M\",\"userName\":\"johnJames\",\"password\":\"Hello123\",\"type\":\"patient\",\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\"} }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object user = controller.createNewUser(post, request, response);
		System.out.println(user);
		User u = Context.getUserService().getUserByUsername("johnJames");
		Assert.assertEquals("john", u.getGivenName());
		Assert.assertEquals("1", u.getPerson().getAttribute("Health Center").getValue());
	}
	
	/**
	 * Test of createNewUser method, of class RaxaUserController.
	 */
	@Test
	public void testCreateNewProviderUser() throws Exception {
		String json = "{\"firstName\":\"Darth\",\"lastName\":\"Vader\",\"gender\":\"M\",\"userName\":\"johnJames\",\"password\":\"Hello123\",\"type\":\"provider\",\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\"} }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object user = controller.createNewUser(post, request, response);
		System.out.println(user);
		User u = Context.getUserService().getUserByUsername("johnJames");
		Provider p = Context.getProviderService().getProviders("Darth Vader", null, null, null).iterator().next();
		Assert.assertEquals("Darth Vader", p.getName());
		Assert.assertEquals("1", u.getPerson().getAttribute("Health Center").getValue());
	}
}
