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
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Encounter;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.Billing;
import org.raxa.module.raxacore.BillingItem;
import org.raxa.module.raxacore.db.BillingItemDAO;

public class HibernateBillingItemDAOTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private BillingItemDAO dao = null;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		dao = (BillingItemDAO) applicationContext.getBean("org.raxa.module.raxacore.db.hibernate.HibernateBillingItemDAO");
	}
	
	@Test
	public void testSaveBillItem() {
		BillingItem billItem = new BillingItem();
		//NOTE: never set Id, will be generated automatically (when saving)
		Patient pat = new Patient(2);
		Encounter enc = new Encounter(10);
		Concept con = new Concept(1);
		Order ord = new Order(1);
		Provider pro = new Provider(1);
		
		billItem.setName("TestList8");
		billItem.setDescription("testing");
		billItem.setCreator(Context.getUserContext().getAuthenticatedUser());
		billItem.setDateCreated(new java.util.Date());
		billItem.setUuid("abc");
		billItem.setDateCreated(new java.util.Date());
		billItem.setRetired(Boolean.FALSE);
		//bill.setBatch("batch 1");
		billItem.setQuantity(10);
		billItem.setValue(10);
		billItem.setProviderId(1);
		billItem.setConceptId(1);
		billItem.setEncounterId(10);
		billItem.setOrderId(1);
		billItem.setBillId(7);
		billItem.setProvider(pro);
		billItem.setConcept(con);
		billItem.setEncounter(enc);
		billItem.setOrder(ord);
		
		dao.saveBillingItem(billItem);
		BillingItem result = dao.getBillingItemByUuid("abc");
		String uuid = result.getUuid();
		assertEquals(uuid, "abc");
	}
	
	@Test
	public void testDeleteBill() {
		BillingItem billItem = new BillingItem();
		//NOTE: never set Id, will be generated automatically (when saving)
		Patient pat = new Patient(2);
		Encounter enc = new Encounter(10);
		Concept con = new Concept(1);
		Order ord = new Order(1);
		Provider pro = new Provider(1);
		
		billItem.setName("TestList8");
		billItem.setDescription("testing");
		billItem.setCreator(Context.getUserContext().getAuthenticatedUser());
		billItem.setDateCreated(new java.util.Date());
		billItem.setUuid("abc");
		billItem.setDateCreated(new java.util.Date());
		billItem.setRetired(Boolean.FALSE);
		//bill.setBatch("batch 1");
		billItem.setQuantity(10);
		billItem.setValue(10);
		billItem.setProviderId(1);
		billItem.setConceptId(1);
		billItem.setEncounterId(10);
		billItem.setOrderId(1);
		billItem.setBillId(7);
		billItem.setProvider(pro);
		billItem.setConcept(con);
		billItem.setEncounter(enc);
		billItem.setOrder(ord);
		
		dao.deleteBillingItem(billItem);
		BillingItem result = dao.getBillingItemByUuid("abc");
		
		assertEquals(result, null);
	}
	
	@Test
	public void testGetBillingItemByUuid() {
		BillingItem result = dao.getBillingItemByUuid("68547121-1b70-465c-99ee-c9dfd95e7d37");
		String name = result.getName();
		assertEquals(name, "TestList8");
		
	}
	
	@Test
	public void testGetAllBillingItems() {
		
		List<BillingItem> allBillingItems = dao.getAllBillingItems();
		assertEquals(allBillingItems.size(), 1);
		
	}
	
	@Test
	public void testGetAllBillingItemsByBill() {
		List<BillingItem> allBillingItems = dao.getAllBillingItemsByBill(7);
		assertEquals(allBillingItems.size(), 1);
	}
	
	@Test
	public void testUpdateBillingItem() {
		BillingItem billItem = dao.getBillingItemByUuid("68547121-1b70-465c-99ee-c9dfd95e7d37");
		billItem.setName("new test list");
		dao.updateBillingItem(billItem);
		String name = dao.getBillingItemByUuid("68547121-1b70-465c-99ee-c9dfd95e7d37").getName();
		assertEquals(name, "new test list");
		
	}
	
	@Test
	public void testGetAllBillingItemsByProvider() {
		List<BillingItem> allBillingItems = dao.getAllBillingItemsByProvider(1);
		assertEquals(allBillingItems.size(), 1);
		
	}
	
	@Test
	public void testGetAllBillingItemsByEncounter() {
		List<BillingItem> allBillingItems = dao.getAllBillingItemsByEncounter(10);
		assertEquals(allBillingItems.size(), 1);
		
	}
	
	@Test
	public void testGetAllBillingItemsByConcept() {
		List<BillingItem> allBillingItems = dao.getAllBillingItemsByConcept(1);
		assertEquals(allBillingItems.size(), 1);
		
	}
	
	@Test
	public void testGetAllBillingItemsByOrder() {
		List<BillingItem> allBillingItems = dao.getAllBillingItemsByOrder(1);
		assertEquals(allBillingItems.size(), 1);
		
	}
}
