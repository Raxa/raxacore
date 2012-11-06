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
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.Billing;
import org.raxa.module.raxacore.DrugInventory;

import org.raxa.module.raxacore.db.BillingDAO;

public class HibernateBillingDAOTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private BillingDAO dao = null;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		dao = (BillingDAO) applicationContext.getBean("org.raxa.module.raxacore.db.hibernate.HibernateBillingDAO");
	}
	
	@Test
	public void testSaveBill() {
		Billing bill = new Billing();
		//NOTE: never set Id, will be generated automatically (when saving)
		Patient pat = new Patient(2);
		Provider pro = new Provider(1);
		bill.setName("TestList7");
		bill.setDescription("testing");
		bill.setCreator(Context.getUserContext().getAuthenticatedUser());
		bill.setDateCreated(new java.util.Date());
		bill.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d34");
		bill.setDateCreated(new java.util.Date());
		bill.setRetired(Boolean.FALSE);
		//bill.setBatch("batch 1");
		//bill.setQuantity(10);
		bill.setStatus("paid");
		//bill.setDrugId(2);
		//bill.setExpiryDate(new Date(2012 - 1 - 1));
		//bill.setValue(20);
		bill.setProviderId(1);
		bill.setPatientId(2);
		//bill.setDrugPurchaseOrderId(14);
		//bill.setOriginalQuantity(20);
		bill.setPatient(pat);
		bill.setProvider(pro);
		
		dao.saveBill(bill);
		Billing result = dao.getBillByPatientUuid("68547121-1b70-465c-99ee-c9dfd95e7d34");
		//DrugInventory result=dao.get
		String uuid = result.getUuid();
		assertEquals(uuid, "68547121-1b70-465c-99ee-c9dfd95e7d34");
	}
	
	@Test
	public void testDeleteBill() {
		Billing bill = new Billing();
		//NOTE: never set Id, will be generated automatically (when saving)
		Patient pat = new Patient(2);
		Provider pro = new Provider(1);
		bill.setName("TestList7");
		bill.setDescription("testing");
		bill.setCreator(Context.getUserContext().getAuthenticatedUser());
		bill.setDateCreated(new java.util.Date());
		bill.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d34");
		bill.setDateCreated(new java.util.Date());
		bill.setRetired(Boolean.FALSE);
		//bill.setBatch("batch 1");
		//bill.setQuantity(10);
		bill.setStatus("paid");
		//bill.setDrugId(2);
		//bill.setExpiryDate(new Date(2012 - 1 - 1));
		//bill.setValue(20);
		bill.setProviderId(1);
		bill.setPatientId(2);
		//bill.setDrugPurchaseOrderId(14);
		//bill.setOriginalQuantity(20);
		bill.setPatient(pat);
		bill.setProvider(pro);
		
		dao.deleteBill(bill);
		Billing result = dao.getBillByPatientUuid("68547121-1b70-465c-99ee-c9dfd95e7d34");
		
		assertEquals(result, null);
	}
	
	@Test
	public void testGetBillsByUuid() {
		Billing result = dao.getBillByPatientUuid("68547121-1b70-465c-99ee-c9dfd95e7d36");
		String name = result.getName();
		assertEquals(name, "TestList7");
		
	}
	
	@Test
	public void testGetAllBills() {
		
		List<Billing> allBills = dao.getAllBills();
		assertEquals(allBills.size(), 1);
		
	}
	
	@Test
	public void testGetAllBillsByStatus() {
		List<Billing> allBills = dao.getAllBillsByStatus("paid");
		assertEquals(allBills.size(), 1);
	}
	
	@Test
	public void testUpdateBill() {
		Billing bill = dao.getBillByPatientUuid("68547121-1b70-465c-99ee-c9dfd95e7d36");
		bill.setName("new test list");
		dao.updateBill(bill);
		String name = dao.getBillByPatientUuid("68547121-1b70-465c-99ee-c9dfd95e7d36").getName();
		assertEquals(name, "new test list");
		
	}
	
	@Test
	public void testGetBillsByProvider() {
		List<Billing> allBills = dao.getAllBillsByProvider(1);
		assertEquals(allBills.size(), 1);
		
	}
	
}
