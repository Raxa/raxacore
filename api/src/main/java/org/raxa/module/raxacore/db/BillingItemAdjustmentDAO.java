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
import org.raxa.module.raxacore.BillingItemAdjustment;

public interface BillingItemAdjustmentDAO {
	
	BillingItemAdjustment saveBillingItemAdjustment(BillingItemAdjustment adjustment) throws DAOException;
	
	void deleteBillingItemAdjustment(BillingItemAdjustment adjustment) throws DAOException;
	
	BillingItemAdjustment getBillingItemAdjustmentByUuid(String uuid);
	
	BillingItemAdjustment getBillingItemAdjustment(int billItemAdjustmentId);
	
	List<BillingItemAdjustment> getAllBillingItemAdjustments() throws DAOException;
	
	List<BillingItemAdjustment> getAllBillingItemAdjustmentsByReason(String reason);
	
	BillingItemAdjustment updateBillingItemAdjustment(BillingItemAdjustment adjustment);
	
	List<BillingItemAdjustment> getAllBillingItemAdjustmentsByBillingItem(Integer billingitemid);
}
