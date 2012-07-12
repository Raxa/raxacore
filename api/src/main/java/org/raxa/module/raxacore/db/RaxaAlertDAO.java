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
import org.openmrs.EncounterType;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.RaxaAlert;

/**
 * Interface for accessing raxacore_raxa_alert_list
 */
public interface RaxaAlertDAO {
	
	/**
	 * Saves a RaxaAlert
	 * 
	 * @param RaxaAlert to be saved
	 * @throws DAOException
	 * @should save a raxa alert list
	 */
	public RaxaAlert saveRaxaAlert(RaxaAlert raxaAlert) throws DAOException;
	
	/**
	 * Purge a RaxaAlert from database.
	 * 
	 * @param RaxaAlert object to be purged
	 */
	public void deleteRaxaAlert(RaxaAlert raxaAlert) throws DAOException;
	
	/**
	 * Get raxaAlert by internal identifier
	 * 
	 * @param raxaAlertID raxaAlert id
	 * @return raxaAlert with given internal identifier
	 * @throws DAOException
	 * @should get a raxa alert list
	 */
	public RaxaAlert getRaxaAlert(Integer raxaAlertID) throws DAOException;
	
	/**
	 * Find {@link RaxaAlert} matching a patient
	 * 
	 * @param patient
	 * @return {@link RaxaAlert}
	 * @should get a raxa alert list by patient
	 */
	public List<RaxaAlert> getRaxaAlertByPatientId(Integer patientId);
    
    /**
	 * Find {@link RaxaAlert} matching a uuid
	 * 
	 * @param uuid
	 * @return {@link RaxaAlert}
	 * @should get a raxa alert list by uuid
	 */
	public RaxaAlert getRaxaAlertByUuid(String uuid);
    
    /**
	 * Find {@link RaxaAlert} matching a alertType
	 * 
	 * @param alertType
	 * @return {@link RaxaAlert}
	 * @should get a raxa alert list by alertType
	 */
	public RaxaAlert getRaxaAlertByAlertType(String alertType);
	
	/**
	 * Find {@link RaxaAlert} matching providerSent
	 * 
	 * @param providerSent
	 * @return List of RaxaAlerts
	 * @should get a raxa alert list by providerSent
	 */
	public List<RaxaAlert> getRaxaAlertByProviderSentId(Integer providerSentId);
    
    /**
	 * Find {@link RaxaAlert} matching providerRecipient
	 * 
	 * @param providerRecipient
	 * @return List of RaxaAlerts
	 * @should get a raxa alert list by providerRecipient
	 */
	public List<RaxaAlert> getRaxaAlertByProviderRecipientId(String providerRecipientId);
	
	/**
	 * Update RaxaAlert
	 * @return {@link RaxaAlert}
	 * @should update a RaxaAlert
	 */
	RaxaAlert updateRaxaAlert(RaxaAlert raxaAlert) throws DAOException;
	
}
