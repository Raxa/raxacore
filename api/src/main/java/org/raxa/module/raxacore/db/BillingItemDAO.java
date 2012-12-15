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
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.BillingItem;

public interface BillingItemDAO {
	
	BillingItem saveBillingItem(BillingItem item) throws DAOException;
	
	void deleteBillingItem(BillingItem item) throws DAOException;
	
	BillingItem getBillingItemByUuid(String uuid);
	
	BillingItem getBillingItem(int billItemId);
	
	List<BillingItem> getAllBillingItems() throws DAOException;
	
	List<BillingItem> getAllBillingItemsByBill(Integer billid);
	
	BillingItem updateBillingItem(BillingItem item);
	
	List<BillingItem> getAllBillingItemsByProvider(Integer providerId);
	
	List<BillingItem> getAllBillingItemsByEncounter(Integer encounterId);
	
	List<BillingItem> getAllBillingItemsByConcept(Integer conceptId);
	
	List<BillingItem> getAllBillingItemsByOrder(Integer orderId);
	
}
