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

public interface BillingItemAdjustmentService extends OpenmrsService {
	
	BillingItemAdjustment saveBillingItemAdjustment(BillingItemAdjustment adjustment) throws DAOException;
	
	/**
	 * saves a billing adjustment
	 * @param adjustment
	 * @return
	 * @throws DAOException
	 */
	void deleteBillingItemAdjustment(BillingItemAdjustment adjustment) throws DAOException;
	
	/**
	 * delete a billing adjustment
	 * @param adjustment
	 * @return
	 * @throws DAOException
	 */
	BillingItemAdjustment getBillingItemAdjustmentByUuid(String uuid);
	
	/**
	 * get a billing adjustment by uuid
	 * @param adjustment
	 * @return
	 * @throws DAOException
	 */
	BillingItemAdjustment getBillingItemAdjustment(int billItemAdjustmentId);
	
	/**
	 * get a billing adjustment by its id
	 * @param adjustment
	 * @return
	 * @throws DAOException
	 */
	
	List<BillingItemAdjustment> getAllBillingItemAdjustments() throws DAOException;
	
	/**
	 * gets all  billing adjustment
	 * @param adjustment
	 * @return
	 * @throws DAOException
	 */
	
	BillingItemAdjustment updateBillingItemAdjustment(BillingItemAdjustment adjustment);
	
	/**
	 *update  billing adjustment given id 
	 * @param adjustment
	 * @return
	 * @throws DAOException
	 */
	
	List<BillingItemAdjustment> getAllBillingItemAdjustmentsByBillingItem(Integer billingitemid);
	
	/**
	 * gets all  billing adjustments for billing item
	 * @param adjustment
	 * @return
	 * @throws DAOException
	 */
}
