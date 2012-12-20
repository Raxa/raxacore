package org.raxa.module.raxacore.db;

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

import org.openmrs.Encounter;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.Billing;

public interface BillingDAO {
	
	public Billing saveBill(Billing bill) throws DAOException;
	
	public void deleteBill(Billing bill) throws DAOException;
	
	public Billing getBillByPatientUuid(String uuid);
	
	public List<Billing> getAllBills() throws DAOException;
	
	public List<Billing> getAllBillsByStatus(String status);
	
	public Billing updateBill(Billing bill);
	
	public Billing getBill(int billId);
	
	public List<Billing> getAllBillsByProvider(Integer providerId);
	
	public List<Billing> getAllBillsByPatient(Integer patientId);
	
	public List<Encounter> getEncountersByPatientId(Integer patientId);
	
}
