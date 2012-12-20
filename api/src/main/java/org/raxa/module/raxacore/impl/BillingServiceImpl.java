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
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.raxa.module.raxacore.Billing;
import org.raxa.module.raxacore.BillingService;
import org.raxa.module.raxacore.db.BillingDAO;

public class BillingServiceImpl implements BillingService {
	
	private BillingDAO dao;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public void setBillingDAO(BillingDAO dao) {
		this.dao = dao;
		
	}
	
	public void onShutdown() {
		
		log.info("Starting drug purchase order service");
	}
	
	public void onStartup() {
		
		log.info("Starting drug purchase order service");
	}
	
	public Billing saveBill(Billing bill) {
		return dao.saveBill(bill);
	}
	
	public void deleteBill(Billing bill) {
		dao.deleteBill(bill);
	}
	
	public Billing getBillByPatientUuid(String uuid) {
		return dao.getBillByPatientUuid(uuid);
	}
	
	public Billing getBill(int billId) {
		return dao.getBill(billId);
	}
	
	public List<Billing> getAllBills() {
		return dao.getAllBills();
	}
	
	public List<Billing> getAllBillsByStatus(String status) {
		return dao.getAllBillsByStatus(status);
	}
	
	public Billing updateBill(Billing bill) {
		return dao.updateBill(bill);
	}
	
	public List<Billing> getAllBillsByProvider(Integer providerId) {
		return dao.getAllBillsByProvider(providerId);
	}
	
	public List<Billing> getAllBillsByPatient(Integer patientId) {
		return dao.getAllBillsByPatient(patientId);
	}
	
	@Override
	public List<Encounter> getEncountersByPatientId(Integer patientId) {
		// TODO Auto-generated method stub
		return dao.getEncountersByPatientId(patientId);
		
	}
}
