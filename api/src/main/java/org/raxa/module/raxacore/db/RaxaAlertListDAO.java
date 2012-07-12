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
import org.raxa.module.raxacore.RaxaAlertList;

/**
 * Interface for accessing raxacore_raxa_alert_list
 */
public interface RaxaAlertListDAO {
	
	/**
	 * Saves a RaxaAlertList
	 * 
	 * @param RaxaAlertList to be saved
	 * @throws DAOException
	 * @should save a raxa alert list
	 */
	public RaxaAlertList saveRaxaAlertList(RaxaAlertList raxaAlertList) throws DAOException;
	
	/**
	 * Purge a RaxaAlertList from database.
	 * 
	 * @param RaxaAlertList object to be purged
	 */
	public void deleteRaxaAlertList(RaxaAlertList raxaAlertList) throws DAOException;
	
	/**
	 * Get raxaAlertList by internal identifier
	 * 
	 * @param raxaAlertID raxaAlert id
	 * @return raxaAlertList with given internal identifier
	 * @throws DAOException
	 * @should get a raxa alert list
	 */
	public RaxaAlertList getRaxaAlertList(Integer raxaAlertID) throws DAOException;
	
	/**
	 * Find {@link RaxaAlertList} matching a patient
	 * 
	 * @param patient
	 * @return {@link RaxaAlertList}
	 * @should get a raxa alert list by patient
	 */
	public List<RaxaAlertList> getRaxaAlertListByPatientId(Integer patientId);
    
    /**
	 * Find {@link RaxaAlertList} matching a uuid
	 * 
	 * @param uuid
	 * @return {@link RaxaAlertList}
	 * @should get a raxa alert list by uuid
	 */
	public RaxaAlertList getRaxaAlertListByUuid(String uuid);
    
    /**
	 * Find {@link RaxaAlertList} matching a alertType
	 * 
	 * @param alertType
	 * @return {@link RaxaAlertList}
	 * @should get a raxa alert list by alertType
	 */
	public RaxaAlertList getRaxaAlertListByAlertType(String alertType);
	
	/**
	 * Find {@link RaxaAlertList} matching providerSent
	 * 
	 * @param providerSent
	 * @return List of RaxaAlertLists
	 * @should get a raxa alert list by providerSent
	 */
	public List<RaxaAlertList> getRaxaAlertListByProviderSentId(Integer providerSentId);
    
    /**
	 * Find {@link RaxaAlertList} matching providerRecipient
	 * 
	 * @param providerRecipient
	 * @return List of RaxaAlertLists
	 * @should get a raxa alert list by providerRecipient
	 */
	public List<RaxaAlertList> getRaxaAlertListByProviderRecipientId(String providerRecipientId);
	
	/**
	 * Update RaxaAlertList
	 * @return {@link RaxaAlertList}
	 * @should update a RaxaAlertList
	 */
	RaxaAlertList updateRaxaAlertList(RaxaAlertList raxaAlertList) throws DAOException;
	
}
