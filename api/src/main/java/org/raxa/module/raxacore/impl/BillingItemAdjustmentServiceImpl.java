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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.raxa.module.raxacore.BillingItemAdjustment;
import org.raxa.module.raxacore.BillingItemAdjustmentService;
import org.raxa.module.raxacore.db.BillingItemAdjustmentDAO;

public class BillingItemAdjustmentServiceImpl implements BillingItemAdjustmentService {
	
	private BillingItemAdjustmentDAO dao;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public void setBillingItemAdjustmentDAO(BillingItemAdjustmentDAO dao) {
		this.dao = dao;
		
	}
	
	public void onShutdown() {
		
		log.info("Starting drug purchase order service");
	}
	
	public void onStartup() {
		
		log.info("Starting drug purchase order service");
	}
	
	public BillingItemAdjustment saveBillingItemAdjustment(BillingItemAdjustment adjustment) {
		return dao.saveBillingItemAdjustment(adjustment);
	}
	
	public void deleteBillingItemAdjustment(BillingItemAdjustment adjustment) {
		dao.deleteBillingItemAdjustment(adjustment);
	}
	
	public BillingItemAdjustment getBillingItemAdjustmentByUuid(String uuid) {
		return dao.getBillingItemAdjustmentByUuid(uuid);
	}
	
	public BillingItemAdjustment getBillingItemAdjustment(int billItemAdjustmentId) {
		return dao.getBillingItemAdjustment(billItemAdjustmentId);
	}
	
	public List<BillingItemAdjustment> getAllBillingItemAdjustments() {
		return dao.getAllBillingItemAdjustments();
	}
	
	public List<BillingItemAdjustment> getAllBillingItemAdjustmentsByReason(String reason) {
		return dao.getAllBillingItemAdjustmentsByReason(reason);
	}
	
	public BillingItemAdjustment updateBillingItemAdjustment(BillingItemAdjustment adjustment) {
		return dao.updateBillingItemAdjustment(adjustment);
	}
	
	public List<BillingItemAdjustment> getAllBillingItemAdjustmentsByBillingItem(Integer billingitemid) {
		return dao.getAllBillingItemAdjustmentsByBillingItem(billingitemid);
	}
}
