package org.raxa.module.raxacore.web.v1_0.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class RaxaLoginControllerTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private MockHttpServletRequest request = null;
	
	private MockHttpServletResponse response = null;
	
	private RaxaLoginController controller = null;
	
	@Before
	public void before() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.controller = new RaxaLoginController();
	}
	
	/**
	 * Test of getLoginInfo method, of class RaxaLoginController.
	 */
	@Test
	public void testGetLoginInfo() throws Exception {
		String result = controller.getLoginInfo(request, response);
		SimpleObject loginInfo = SimpleObject.parseJson(result);
		Assert.assertEquals("ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562", loginInfo.get("personUuid"));
	}
	
}
