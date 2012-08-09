package org.raxa.module.raxacore;

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
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Drug;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.util.PrivilegeConstants;
import org.raxa.module.raxacore.db.DrugGroupDAO;
import org.springframework.transaction.annotation.Transactional;

/*
 * Interface for interacting with the DrugGroup
 */
@Transactional
public interface DrugGroupService extends OpenmrsService {
	
	/**
	 * Sets Drug Group DAO
	 *
	 * @param dao
	 */
	public void setDrugGroupDAO(DrugGroupDAO dao);
	
	/**
	 * Saves DrugGroup
	 *
	 * @param drugGroup
	 * @return DrugGroup
	 */
	@Authorized( { "Add Drug Groups" })
	public DrugGroup saveDrugGroup(DrugGroup drugGroup);
	
	/**
	 * Gets a DrugGroup by Id
	 *
	 * @param id
	 * @return DrugGroups
	 */
	@Authorized( { "View Drug Groups" })
	public DrugGroup getDrugGroup(Integer id);
	
	/**
	 * Gets a DrugGroup by Name
	 *
	 * @param name
	 * @return list of DrugGroups
	 */
	@Authorized( { "View Drug Groups" })
	public List<DrugGroup> getDrugGroupByName(String name);
	
	/**
	 * Gets DrugGroup by uuid
	 *
	 * @param uuid
	 * @return DrugGroup
	 */
	@Authorized( { "View Drug Groups" })
	public DrugGroup getDrugGroupByUuid(String uuid);
	
	/**
	 * Gets all DrugGroups
	 *
	 * @return list of DrugGroups
	 */
	@Authorized( { "View Drug Groups" })
	public List<DrugGroup> getAllDrugGroup(boolean includeRetired);
	
	/**
	 * Updates DrugGroup
	 *
	 * @param DrugGroup
	 * @return DrugGroup
	 */
	@Authorized( { "Edit Drug Groups" })
	DrugGroup updateDrugGroup(DrugGroup drugGroup);
	
	/**
	 * Deletes DrugGroup
	 *
	 * @param DrugGroup
	 */
	@Authorized( { "Delete Drug Groups" })
	public void deleteDrugGroup(DrugGroup drugGroup);
}
