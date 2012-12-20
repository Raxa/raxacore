package org.raxa.module.raxacore;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import java.util.List;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.db.DAOException;
import org.openmrs.util.PrivilegeConstants;
import org.raxa.module.raxacore.db.DrugInventoryDAO;
import org.springframework.transaction.annotation.Transactional;

public interface BillingService extends OpenmrsService {
	
	Billing saveBill(Billing bill) throws DAOException; //save a bill
	
	void deleteBill(Billing bill) throws DAOException; //delete a bill
	
	Billing getBillByPatientUuid(String uuid); // get all bills of a patient given its uuuid 
	
	List<Billing> getAllBills() throws DAOException; //get all bills
	
	List<Billing> getAllBillsByStatus(String status); //get all bills by status 
	
	Billing updateBill(Billing bill); // update a bill
	
	Billing getBill(int billId); // get a bill given its id 
	
	List<Billing> getAllBillsByProvider(Integer providerId); // get all bills for Provider given providerId
	
	List<Billing> getAllBillsByPatient(Integer patientId); //get all bills for patient given patientId
	
	List<Encounter> getEncountersByPatientId(Integer patientId);
	
}
