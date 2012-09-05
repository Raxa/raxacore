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
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.raxa.module.raxacore.db.RaxaAlertDAO;
import org.springframework.transaction.annotation.Transactional;

/*
 * Interface for interacting with the RaxaAlert
 */
@Transactional
public interface RaxaAlertService extends OpenmrsService {
	
	/**
	 * Sets Raxa Alert DAO
	 *
	 * @param dao
	 */
	public void setRaxaAlertDAO(RaxaAlertDAO dao);
	
	/**
	 * Saves RaxaAlert
	 *
	 * @param raxaAlert
	 * @return RaxaAlert
	 */
	@Authorized( { "Add Raxa Alerts" })
	public RaxaAlert saveRaxaAlert(RaxaAlert raxaAlert);
	
	/**
	 * Gets a RaxaAlert by Id
	 *
	 * @param id
	 * @return RaxaAlerts
	 */
	@Authorized( { "View Raxa Alerts" })
	public RaxaAlert getRaxaAlert(Integer id);
	
	/**
	 * Gets a RaxaAlert by PatientId
	 *
	 * @param patientId
	 * @return RaxaAlerts
	 */
	@Authorized( { "View Raxa Alerts" })
	public List<RaxaAlert> getRaxaAlertByPatientId(Integer patientId, boolean includeSeen);
	
	/**
	 * Gets a RaxaAlert by ProviderSentId
	 *
	 * @param providerSentId
	 * @return RaxaAlerts
	 */
	@Authorized( { "View Raxa Alerts" })
	public List<RaxaAlert> getRaxaAlertByProviderSentId(Integer providerSentId, boolean includeSeen);
	
	/**
	 * Gets a RaxaAlert by ProviderRecipientId
	 *
	 * @param providerRecipientId
	 * @return RaxaAlerts
	 */
	@Authorized( { "View Raxa Alerts" })
	public List<RaxaAlert> getRaxaAlertByProviderRecipientId(Integer providerRecipientId, boolean includeSeen);
	
	/**
	 * Gets a RaxaAlert by ProviderSentUuid
	 *
	 * @param providerSentUuid
	 * @return RaxaAlerts
	 */
	@Authorized( { "View Raxa Alerts" })
	public List<RaxaAlert> getRaxaAlertByProviderSentUuid(String providerSentUuid, boolean includeSeen);
	
	/**
	 * Gets a RaxaAlert by ProviderRecipientUuid
	 *
	 * @param providerSentUuid
	 * @return RaxaAlerts
	 */
	@Authorized( { "View Raxa Alerts" })
	public List<RaxaAlert> getRaxaAlertByProviderRecipientUuid(String providerRecipientUuid, boolean includeSeen);
	
	/**
	 * Gets a RaxaAlert by Name
	 *
	 * @param name
	 * @return list of RaxaAlerts
	 */
	@Authorized( { "View Raxa Alerts" })
	public List<RaxaAlert> getRaxaAlertsByName(String name, boolean includeSeen);
	
	/**
	 * Gets RaxaAlert by uuid
	 *
	 * @param uuid
	 * @return RaxaAlert
	 */
	@Authorized( { "View Raxa Alerts" })
	public RaxaAlert getRaxaAlertByUuid(String uuid);
	
	/**
	 * Gets RaxaAlert by alertType
	 *
	 * @param uuid
	 * @return RaxaAlert
	 */
	@Authorized( { "View Raxa Alerts" })
	public List<RaxaAlert> getRaxaAlertByAlertType(String alertType, boolean includeSeen);
	
	/**
	 * Gets all RaxaAlerts
	 *
	 * @return list of RaxaAlerts
	 */
	@Authorized( { "View Raxa Alerts" })
	public List<RaxaAlert> getAllRaxaAlerts(boolean includeSeen);
	
	/**
	 * Mark RaxaAlert as seen
	 *
	 * @param seen
	 */
	@Authorized( { "Edit Raxa Alerts" })
	RaxaAlert markRaxaAlertAsSeen(RaxaAlert raxaAlert);
	
	/**
	 * Updates RaxaAlert
	 *
	 * @param raxaAlert
	 * @return RaxaAlert
	 */
	@Authorized( { "Edit Raxa Alerts" })
	RaxaAlert updateRaxaAlert(RaxaAlert raxaAlert);
	
	/**
	 * Deletes RaxaAlert
	 *
	 * @param raxaAlert
	 */
	@Authorized( { "Delete Raxa Alerts" })
	public void deleteRaxaAlert(RaxaAlert raxaAlert);
	
	@Authorized( { "Delete Raxa Alerts" })
	public void voidRaxaAlert(RaxaAlert raxaAlert, String reason);
	
	@Authorized( { "Delete Raxa Alerts" })
	public void purgeRaxaAlert(RaxaAlert raxaAlert);
	
	public List<RaxaAlert> getRaxaAlertByToLocationUuid(String toLocation, boolean includeSeen);
}
