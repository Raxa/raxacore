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
import org.openmrs.util.PrivilegeConstants;
import org.raxa.module.raxacore.db.DrugInfoDAO;
import org.springframework.transaction.annotation.Transactional;

/*
 * Interface for interacting with the DrugInfo
 */
@Transactional
public interface DrugInfoService extends OpenmrsService {
	
	/**
	 * Sets Patient List DAO
	 *
	 * @param dao
	 */
	public void setDrugInfoDAO(DrugInfoDAO dao);
	
	/**
	 * Saves DrugInfo
	 *
	 * @param drugInfo
	 * @return DrugInfo
	 */
	@Authorized( { "Add Drug Info" })
	public DrugInfo saveDrugInfo(DrugInfo drugInfo);
	
	/**
	 * Gets a DrugInfo by Id
	 *
	 * @param id
	 * @return DrugInfos
	 */
	@Authorized( { "View Drug Info" })
	public DrugInfo getDrugInfo(Integer id);
	
	/**
	 * Gets DrugInfo by uuid
	 *
	 * @param uuid
	 * @return DrugInfo
	 */
	@Authorized( { "View Drug Info" })
	public DrugInfo getDrugInfoByUuid(String uuid);
	
	/**
	 * Gets DrugInfo by drug uuid
	 *
	 * @param uuid
	 * @return DrugInfo
	 */
	@Authorized( { "View Drug Info" })
	public DrugInfo getDrugInfoByDrugUuid(String uuid);
	
	/**
	 * Gets DrugInfo by drug name
	 *
	 * @param uuid
	 * @return DrugInfo
	 */
	@Authorized( { "View Drug Info" })
	public List<DrugInfo> getDrugInfosByDrugName(String name);
	
	/**
	 * Gets all DrugInfos
	 *
	 * @return
	 */
	@Authorized( { "View Drug Info" })
	public List<DrugInfo> getAllDrugInfo(boolean includeRetired);
	
	/**
	 * Updates DrugInfo
	 *
	 * @param drugInfo
	 * @return DrugInfo
	 */
	@Authorized( { "Edit Drug Info" })
	DrugInfo updateDrugInfo(DrugInfo drugInfo);
	
	/**
	 * Deletes DrugInfo
	 *
	 * @param drugInfo
	 */
	@Authorized( { "Delete Drug Info" })
	public void deleteDrugInfo(DrugInfo drugInfo);
}
