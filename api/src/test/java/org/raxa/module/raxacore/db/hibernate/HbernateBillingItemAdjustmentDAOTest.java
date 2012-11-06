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
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.BillingItem;
import org.raxa.module.raxacore.BillingItemAdjustment;
import org.raxa.module.raxacore.db.BillingItemAdjustmentDAO;

public class HbernateBillingItemAdjustmentDAOTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private BillingItemAdjustmentDAO dao = null;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		dao = (BillingItemAdjustmentDAO) applicationContext
		        .getBean("org.raxa.module.raxacore.db.hibernate.HibernateBillingItemAdjustmentDAO");
	}
	
	@Test
	public void testSaveBillingItemAdjustment() {
		BillingItemAdjustment billItemAdj = new BillingItemAdjustment();
		billItemAdj.setName("TestList9");
		billItemAdj.setDescription("testing");
		billItemAdj.setCreator(Context.getUserContext().getAuthenticatedUser());
		billItemAdj.setDateCreated(new java.util.Date());
		billItemAdj.setUuid("abc");
		billItemAdj.setDateCreated(new java.util.Date());
		billItemAdj.setRetired(Boolean.FALSE);
		billItemAdj.setReason("xyz");
		billItemAdj.setValue(10);
		billItemAdj.setBillItemId(8);
		dao.saveBillingItemAdjustment(billItemAdj);
		BillingItemAdjustment result = dao.getBillingItemAdjustmentByUuid("abc");
		String uuid = result.getUuid();
		assertEquals(uuid, "abc");
	}
	
	@Test
	public void testDeleteBillingItemAdjustment() {
		BillingItemAdjustment billItemAdj = new BillingItemAdjustment();
		billItemAdj.setName("TestList9");
		billItemAdj.setDescription("testing");
		billItemAdj.setCreator(Context.getUserContext().getAuthenticatedUser());
		billItemAdj.setDateCreated(new java.util.Date());
		billItemAdj.setUuid("abc");
		billItemAdj.setDateCreated(new java.util.Date());
		billItemAdj.setRetired(Boolean.FALSE);
		billItemAdj.setReason("xyz");
		billItemAdj.setValue(10);
		billItemAdj.setBillItemId(8);
		dao.deleteBillingItemAdjustment(billItemAdj);
		BillingItemAdjustment result = dao.getBillingItemAdjustmentByUuid("abc");
		//String uuid = result.getUuid();
		
		assertEquals(result, null);
	}
	
	@Test
	public void testBillingItemAdjustmentByUuid() {
		BillingItemAdjustment result = dao.getBillingItemAdjustmentByUuid("68547121-1b70-465c-99ee-c9dfd95e7d38");
		String name = result.getName();
		assertEquals(name, "TestList9");
		
	}
	
	@Test
	public void testGetAllBillingItemAdjustments() {
		
		List<BillingItemAdjustment> allBillingItemsAdj = dao.getAllBillingItemAdjustments();
		assertEquals(allBillingItemsAdj.size(), 1);
		
	}
	
	@Test
	public void testGetAllBillingItemAdjustmentsByBillingItem() {
		
		List<BillingItemAdjustment> allBillingItemsAdj = dao.getAllBillingItemAdjustmentsByBillingItem(8);
		assertEquals(allBillingItemsAdj.size(), 1);
		
	}
	
	@Test
	public void testUpdateBillingItemAdjustment() {
		BillingItemAdjustment billItemAdj = dao.getBillingItemAdjustmentByUuid("68547121-1b70-465c-99ee-c9dfd95e7d38");
		billItemAdj.setName("new test list");
		dao.updateBillingItemAdjustment(billItemAdj);
		String name = dao.getBillingItemAdjustmentByUuid("68547121-1b70-465c-99ee-c9dfd95e7d38").getName();
		assertEquals(name, "new test list");
		
	}
	
	@Test
	public void testGetAllBillingItemAdjustmentsByReason() {
		
		List<BillingItemAdjustment> allBillingItemsAdj = dao.getAllBillingItemAdjustmentsByReason("RSBY");
		assertEquals(allBillingItemsAdj.size(), 1);
		
	}
	
}
