package org.raxa.module.raxacore.impl;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.raxa.module.raxacore.Billing;
import org.raxa.module.raxacore.BillingItem;
import org.raxa.module.raxacore.BillingItemService;
import org.raxa.module.raxacore.BillingService;
import org.raxa.module.raxacore.db.BillingItemDAO;

public class BillingItemServiceImpl implements BillingItemService {
	
	private BillingItemDAO dao;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public void setBillingItemDAO(BillingItemDAO dao) {
		this.dao = dao;
		
	}
	
	public void onShutdown() {
		
		log.info("Starting drug purchase order service");
	}
	
	public void onStartup() {
		
		log.info("Starting drug purchase order service");
	}
	
	public BillingItem saveBillingItem(BillingItem item) {
		return dao.saveBillingItem(item);
	}
	
	public void deleteBillingItem(BillingItem item) {
		dao.deleteBillingItem(item);
	}
	
	public BillingItem getBillingItemByUuid(String uuid)

	{
		return dao.getBillingItemByUuid(uuid);
	}
	
	public BillingItem getBillingItem(int billItemId)

	{
		return dao.getBillingItem(billItemId);
	}
	
	public List<BillingItem> getAllBillingItems() {
		return dao.getAllBillingItems();
	}
	
	public List<BillingItem> getAllBillingItemsByBill(Integer billid) {
		return dao.getAllBillingItemsByBill(billid);
	}
	
	public BillingItem updateBillingItem(BillingItem item) {
		return dao.updateBillingItem(item);
	}
	
	public List<BillingItem> getAllBillingItemsByProvider(Integer providerId) {
		return dao.getAllBillingItemsByProvider(providerId);
	}
	
	public List<BillingItem> getAllBillingItemsByEncounter(Integer encounterId) {
		return dao.getAllBillingItemsByEncounter(encounterId);
	}
	
	public List<BillingItem> getAllBillingItemsByConcept(Integer conceptId) {
		return dao.getAllBillingItemsByConcept(conceptId);
	}
	
	public List<BillingItem> getAllBillingItemsByOrder(Integer orderId) {
		return dao.getAllBillingItemsByOrder(orderId);
	}
	
}
