package org.raxa.module.raxacore.db.hibernate;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.RaxaAlert;
import org.raxa.module.raxacore.db.RaxaAlertDAO;

/**
 * @author Tarang Mahajan
 */

public class HibernateRaxaAlertDAOTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private RaxaAlertDAO dao = null;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		dao = (HibernateRaxaAlertDAO) applicationContext
		        .getBean("org.raxa.module.raxacore.db.hibernate.HibernateRaxaAlertDAO");
	}
	
	/**
	 * Test of saveRaxaAlert method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testSaveRaxaAlert() {
		RaxaAlert rAlert = new RaxaAlert();
		//NOTE: never set Id, will be generated automatically (when saving)
		rAlert.setName("TestList3");
		rAlert.setDescription("Third Test List");
		rAlert.setCreator(Context.getUserContext().getAuthenticatedUser());
		rAlert.setDateCreated(new java.util.Date());
		rAlert.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d30");
		rAlert.setVoided(Boolean.FALSE);
		rAlert.setProviderSentId(1);
		rAlert.setProviderRecipientId(2);
		dao.saveRaxaAlert(rAlert);
		RaxaAlert result = dao.getRaxaAlertByName("TestList3");
		String name = result.getName();
		assertEquals(name, "TestList4");
	}
	
	/**
	 * Test of deleteRaxaAlert method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testDeleteRaxaAlert() {
		RaxaAlert rAlert = new RaxaAlert();
		rAlert.setName("TestList2");
		rAlert.setId(2);
		rAlert.setDescription("Second Test List");
		rAlert.setCreator(Context.getUserContext().getAuthenticatedUser());
		rAlert.setDateCreated(new java.util.Date());
		rAlert.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d30");
		rAlert.setVoided(Boolean.FALSE);
		rAlert.setProviderSentId(1);
		rAlert.setProviderRecipientId(2);
		dao.deleteRaxaAlert(rAlert);
		RaxaAlert result = dao.getRaxaAlert(2);
		assertEquals(null, result);
	}
	
	/**
	 * Test of getRaxaAlert method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetRaxaAlert() {
		Integer raxaAlertId = 1;
		RaxaAlert result = dao.getRaxaAlert(raxaAlertId);
		String name = result.getName();
		assertEquals("TestList1", name);
	}
	
	/**
	 * Test of getRaxaAlertByUuid method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetRaxaAlertByUuid() {
		String uuid = "68547121-1b70-465e-99ee-c9dfd95e7d30";
		String result = dao.getRaxaAlertByUuid(uuid).getName();
		assertEquals("TestList2", result);
	}
	
	/**
	 * Test of getRaxaAlertByName method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetRaxaAlertByName() {
		String name = "TestList1";
		String result = dao.getRaxaAlertByName(name).getName();
		assertEquals(name, result);
	}
	
	/**
	 * Test of getRaxaAlertByAlertType method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetRaxaAlertByAlertType() {
		System.out.println("getRaxaAlertByAlertType");
		String alertType = "";
		HibernateRaxaAlertDAO instance = new HibernateRaxaAlertDAO();
		List expResult = null;
		List result = instance.getRaxaAlertByAlertType(alertType);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
	
	/**
	 * Test of getRaxaAlertByPatientId method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetRaxaAlertByPatientId() {
		System.out.println("getRaxaAlertByPatientId");
		Integer patientId = null;
		HibernateRaxaAlertDAO instance = new HibernateRaxaAlertDAO();
		List expResult = null;
		List result = instance.getRaxaAlertByPatientId(patientId);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
	
	/**
	 * Test of getRaxaAlertByPatientUuid method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetRaxaAlertByPatientUuid() {
		System.out.println("getRaxaAlertByPatientUuid");
		String patientUuid = "";
		HibernateRaxaAlertDAO instance = new HibernateRaxaAlertDAO();
		List expResult = null;
		List result = instance.getRaxaAlertByPatientUuid(patientUuid);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
	
	/**
	 * Test of getRaxaAlertByProviderSentId method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetRaxaAlertByProviderSentId() {
		System.out.println("getRaxaAlertByProviderSentId");
		Integer providerSentId = null;
		HibernateRaxaAlertDAO instance = new HibernateRaxaAlertDAO();
		List expResult = null;
		List result = instance.getRaxaAlertByProviderSentId(providerSentId);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
	
	/**
	 * Test of getRaxaAlertByProviderSentUuid method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetRaxaAlertByProviderSentUuid() {
		System.out.println("getRaxaAlertByProviderSentUuid");
		String providerSentUuid = "";
		HibernateRaxaAlertDAO instance = new HibernateRaxaAlertDAO();
		List expResult = null;
		List result = instance.getRaxaAlertByProviderSentUuid(providerSentUuid);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
	
	/**
	 * Test of getRaxaAlertByProviderRecipientId method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetRaxaAlertByProviderRecipientId() {
		System.out.println("getRaxaAlertByProviderRecipientId");
		Integer providerRecipientId = null;
		HibernateRaxaAlertDAO instance = new HibernateRaxaAlertDAO();
		List expResult = null;
		List result = instance.getRaxaAlertByProviderRecipientId(providerRecipientId);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
	
	/**
	 * Test of getRaxaAlertByProviderRecipientUuid method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetRaxaAlertByProviderRecipientUuid() {
		System.out.println("getRaxaAlertByProviderRecipientUuid");
		String providerRecipientUuid = "";
		HibernateRaxaAlertDAO instance = new HibernateRaxaAlertDAO();
		List expResult = null;
		List result = instance.getRaxaAlertByProviderRecipientUuid(providerRecipientUuid);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
	
	/**
	 * Test of getAllRaxaAlerts method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetAllPatientList_shouldReturnAllRaxaAlertsIncludingVoided() {
		List<RaxaAlert> allRaxaAlert = dao.getAllRaxaAlerts(true);
		assertEquals(allRaxaAlert.size(), 3);
	}
	
	/**
	 * Test of getAllRaxaAlerts method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testGetAllPatientList_shouldReturnAllUnvoidedRaxaAlerts() {
		List<RaxaAlert> allRaxaAlert = dao.getAllRaxaAlerts(false);
		assertEquals(allRaxaAlert.size(), 3);
	}
	
	/**
	 * Test of updateRaxaAlert method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testUpdateRaxaAlert() {
		RaxaAlert raxaAlert = dao.getRaxaAlert(1);
		raxaAlert.setName("NewNameList");
		dao.updateRaxaAlert(raxaAlert);
		String name = dao.getRaxaAlert(1).getName();
		assertEquals(name, "NewNameList");
	}
	
	/**
	 * Test of markRaxaAlertAsSeen method, of class HibernateRaxaAlertDAO.
	 */
	@Test
	public void testMarkRaxaAlertAsSeen() {
		RaxaAlert raxaAlert = dao.getRaxaAlert(1);
		dao.markRaxaAlertAsSeen(raxaAlert);
		Boolean seen = dao.getRaxaAlert(1).seen();
		assertEquals(seen, "TRUE");
	}
}
