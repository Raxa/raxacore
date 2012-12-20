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

public interface BillingItemService extends OpenmrsService {
	
	BillingItem saveBillingItem(BillingItem item) throws DAOException; //saves a billingItem
	
	BillingItem getBillingItem(int billItemId) throws DAOException; //get billingitem by id 
	
	void deleteBillingItem(BillingItem item) throws DAOException; //delete billing item
	
	BillingItem getBillingItemByUuid(String uuid); // get billing item by uuid
	
	List<BillingItem> getAllBillingItems() throws DAOException; // get all billing items
	
	List<BillingItem> getAllBillingItemsByBill(Integer billid); //get all billing items in bill by billid
	
	BillingItem updateBillingItem(BillingItem item); //update billing item
	
	List<BillingItem> getAllBillingItemsByProvider(Integer providerId); //get billing item by providerId
	
	List<BillingItem> getAllBillingItemsByEncounter(Integer encounterId); //get billing items by encounterId
	
	List<BillingItem> getAllBillingItemsByConcept(Integer conceptId); // get billingItems by concept
	
	List<BillingItem> getAllBillingItemsByOrder(Integer orderId); //get billingItems by orderId
	
}
